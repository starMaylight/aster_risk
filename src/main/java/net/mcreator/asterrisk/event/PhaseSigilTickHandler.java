package net.mcreator.asterrisk.event;

import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.block.entity.PhaseAnvilBlockEntity;
import net.mcreator.asterrisk.item.PhaseSigilItem;
import net.mcreator.asterrisk.mana.LunarManaCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 月相刻印のTick系効果を適用するハンドラー
 * - 三日月: 移動速度増加
 * - 新月: ステルス（透明化）
 * - 有明月: マナ回復増加
 * - 更待月: 魔法ダメージ増加（武器側で処理）
 */
@Mod.EventBusSubscriber(modid = AsterRiskMod.MODID)
public class PhaseSigilTickHandler {

    // 属性修飾子UUID
    private static final UUID SPEED_MODIFIER_UUID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    
    // 効果係数
    private static final float WAXING_CRESCENT_SPEED_BONUS = 0.10f; // +10% 移動速度/レベル
    private static final float WANING_CRESCENT_MANA_BONUS = 0.08f;  // +8% マナ回復/レベル

    // キャッシュ（毎tick計算を避ける）
    private static final Map<UUID, Integer> lastSpeedLevel = new HashMap<>();
    private static final Map<UUID, Long> lastTickTime = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level().isClientSide()) return;

        Player player = event.player;
        
        // 20tick毎に処理（1秒間隔）
        long gameTime = player.level().getGameTime();
        Long lastTick = lastTickTime.get(player.getUUID());
        if (lastTick != null && gameTime - lastTick < 20) return;
        lastTickTime.put(player.getUUID(), gameTime);

        Map<PhaseSigilItem.MoonPhase, Integer> levels = getTotalSigilLevels(player);

        // 三日月: 移動速度
        applySpeedBonus(player, levels.get(PhaseSigilItem.MoonPhase.WAXING_CRESCENT));

        // 新月: ステルス（透明化）
        applyStealthEffect(player, levels.get(PhaseSigilItem.MoonPhase.NEW_MOON));

        // 有明月: マナ回復
        applyManaRegenBonus(player, levels.get(PhaseSigilItem.MoonPhase.WANING_CRESCENT));
    }

    private static void applySpeedBonus(Player player, int level) {
        AttributeInstance speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr == null) return;

        UUID playerId = player.getUUID();
        Integer lastLevel = lastSpeedLevel.get(playerId);

        // レベルが変わった場合のみ更新
        if (lastLevel == null || lastLevel != level) {
            // 既存の修飾子を削除
            AttributeModifier existing = speedAttr.getModifier(SPEED_MODIFIER_UUID);
            if (existing != null) {
                speedAttr.removeModifier(SPEED_MODIFIER_UUID);
            }

            // 新しい修飾子を追加
            if (level > 0) {
                double bonus = WAXING_CRESCENT_SPEED_BONUS * level;
                speedAttr.addTransientModifier(new AttributeModifier(
                    SPEED_MODIFIER_UUID,
                    "Phase Sigil Speed",
                    bonus,
                    AttributeModifier.Operation.MULTIPLY_TOTAL
                ));
            }

            lastSpeedLevel.put(playerId, level);
        }
    }

    private static void applyStealthEffect(Player player, int level) {
        if (level <= 0) return;

        // 透明化効果を付与（レベルに応じて効果時間と強度を調整）
        int amplifier = Math.min(level - 1, 2); // 最大レベル2（かなり透明）
        int duration = 30; // 1.5秒（次のチェックまで持続）

        // 既に効果があれば更新のみ
        MobEffectInstance currentEffect = player.getEffect(MobEffects.INVISIBILITY);
        if (currentEffect == null || currentEffect.getDuration() < 10) {
            player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, duration, amplifier, true, false, true));
        }
    }

    private static void applyManaRegenBonus(Player player, int level) {
        if (level <= 0) return;

        // マナ回復ボーナスを適用
        player.getCapability(LunarManaCapability.LUNAR_MANA).ifPresent(mana -> {
            float bonusRegen = mana.getMaxMana() * WANING_CRESCENT_MANA_BONUS * level * 0.05f; // 1秒あたりの追加回復
            float newMana = Math.min(mana.getMana() + bonusRegen, mana.getMaxMana());
            mana.setMana(newMana);
        });
    }

    /**
     * 装備から全刻印レベルを取得
     */
    private static Map<PhaseSigilItem.MoonPhase, Integer> getTotalSigilLevels(Player player) {
        Map<PhaseSigilItem.MoonPhase, Integer> totals = new HashMap<>();
        
        for (PhaseSigilItem.MoonPhase phase : PhaseSigilItem.MoonPhase.values()) {
            totals.put(phase, 0);
        }

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.isEmpty()) continue;

            CompoundTag tag = stack.getTag();
            if (tag == null || !tag.contains(PhaseAnvilBlockEntity.TAG_PHASE_SIGILS)) continue;

            CompoundTag sigils = tag.getCompound(PhaseAnvilBlockEntity.TAG_PHASE_SIGILS);
            for (PhaseSigilItem.MoonPhase phase : PhaseSigilItem.MoonPhase.values()) {
                int sigilLevel = sigils.getInt(phase.getName());
                if (sigilLevel > 0) {
                    totals.put(phase, totals.get(phase) + sigilLevel);
                }
            }
        }

        return totals;
    }

    /**
     * プレイヤーログアウト時にキャッシュをクリア
     */
    @SubscribeEvent
    public static void onPlayerLogout(net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent event) {
        UUID playerId = event.getEntity().getUUID();
        lastSpeedLevel.remove(playerId);
        lastTickTime.remove(playerId);
    }
}
