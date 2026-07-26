package net.mcreator.asterrisk.compat;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Curios連携（任意依存）。
 *
 * Curiosはコンパイル依存に含めていないため、すべてリフレクション経由で扱う。
 * 未導入環境やAPI変更時も例外を握りつぶして安全に無効化される。
 */
public final class CuriosCompat {

    private static Boolean loaded = null;
    /** 一度失敗したら以降は試行しない */
    private static boolean unavailable = false;

    private CuriosCompat() {
    }

    private static boolean isLoaded() {
        if (loaded == null) {
            try {
                loaded = ModList.get() != null && ModList.get().isLoaded("curios");
            } catch (Throwable t) {
                loaded = false;
            }
        }
        return loaded;
    }

    /**
     * 全Curiosスロットのアイテムを取り出してスロットを空にする。
     *
     * @return 取り出したアイテム（Curios未導入・失敗時は空リスト）
     */
    public static List<ItemStack> extractAll(LivingEntity entity) {
        if (!isLoaded() || unavailable) {
            return Collections.emptyList();
        }

        List<ItemStack> removed = new ArrayList<>();
        try {
            Class<?> apiClass = Class.forName("top.theillusivec4.curios.api.CuriosApi");
            Method getInventory = apiClass.getMethod("getCuriosInventory", LivingEntity.class);
            Object handler = unwrap(getInventory.invoke(null, entity));
            if (handler == null) {
                return removed;
            }

            Object curios = invoke(handler, "getCurios");
            if (!(curios instanceof Map<?, ?> slotMap)) {
                return removed;
            }

            for (Object stacksHandler : slotMap.values()) {
                if (stacksHandler == null) continue;
                Object stacks = invoke(stacksHandler, "getStacks");
                if (stacks == null) continue;

                Method getSlots = findMethod(stacks.getClass(), "getSlots");
                Method getStackInSlot = findMethod(stacks.getClass(), "getStackInSlot", int.class);
                Method setStackInSlot = findMethod(stacks.getClass(), "setStackInSlot", int.class, ItemStack.class);
                if (getSlots == null || getStackInSlot == null || setStackInSlot == null) continue;

                int slots = (int) getSlots.invoke(stacks);
                for (int i = 0; i < slots; i++) {
                    Object value = getStackInSlot.invoke(stacks, i);
                    if (!(value instanceof ItemStack stack) || stack.isEmpty()) continue;

                    removed.add(stack.copy());
                    setStackInSlot.invoke(stacks, i, ItemStack.EMPTY);
                }
            }
        } catch (Throwable t) {
            // API不一致など。以降は無効化する
            unavailable = true;
            return Collections.emptyList();
        }
        return removed;
    }

    /** Optional / LazyOptional のどちらでも中身を取り出す */
    private static Object unwrap(Object holder) throws Exception {
        if (holder == null) {
            return null;
        }
        if (holder instanceof Optional<?> optional) {
            return optional.orElse(null);
        }
        // Curios 5.x未満はLazyOptionalを返す
        Method resolve = findMethod(holder.getClass(), "resolve");
        if (resolve == null) {
            return holder;
        }
        Object resolved = resolve.invoke(holder);
        return resolved instanceof Optional<?> optional ? optional.orElse(null) : resolved;
    }

    private static Object invoke(Object target, String name) throws Exception {
        Method method = findMethod(target.getClass(), name);
        return method != null ? method.invoke(target) : null;
    }

    /**
     * クラス階層とインターフェースからメソッドを探す。
     * 実装クラスがpackage-privateでもインターフェース経由で呼べるようにする。
     */
    private static Method findMethod(Class<?> type, String name, Class<?>... params) {
        for (Class<?> current = type; current != null; current = current.getSuperclass()) {
            try {
                Method method = current.getDeclaredMethod(name, params);
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException | SecurityException ignored) {
                // 次を探す
            }
            for (Class<?> iface : current.getInterfaces()) {
                Method method = findMethod(iface, name, params);
                if (method != null) {
                    return method;
                }
            }
        }
        return null;
    }
}
