package net.mcreator.asterrisk.util;

import net.mcreator.asterrisk.compat.CuriosCompat;
import net.mcreator.asterrisk.entity.SunIncarnateEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;

/**
 * 太陽の力による共通戦闘ロジック。
 *
 * 陽の化身（SunIncarnateEntity）、星霊の笏、太陽の剣で共有する:
 * - 装備劣化（耐久削り／削れない場合は強制ドロップ）
 * - 貫通ダメージ（最大体力を一時的に下げて体力を直接削る）
 */
public final class SolarCombatHelper {

    /** 1回の劣化で失う耐久の割合（最大耐久比） */
    private static final float DURABILITY_LOSS_RATIO = 0.1F;

    private SolarCombatHelper() {
    }

    // ===== 装備劣化 =====

    /**
     * 全装備スロットの耐久値を最大値の10%分減らす。
     * 耐久値を持たない防具・武器（Unbreakable含む）は装備から外してその場にドロップさせる。
     *
     * @param attacker 演出音の発生源（nullならターゲット自身の位置で鳴らす）
     */
    public static void degradeEquipment(LivingEntity target, LivingEntity attacker) {
        if (target instanceof Player player && (player.isCreative() || player.isSpectator())) {
            return;
        }

        boolean droppedAny = false;

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = target.getItemBySlot(slot);
            if (stack.isEmpty()) continue;

            if (stack.isDamageableItem()) {
                int loss = Math.max(1, (int) (stack.getMaxDamage() * DURABILITY_LOSS_RATIO));
                stack.hurtAndBreak(loss, target, e -> e.broadcastBreakEvent(slot));
            } else if (isArmorOrWeapon(stack, slot)) {
                // 耐久値を持たない防具・武器は強制的に装備解除してその場にドロップ
                ItemStack dropped = stack.copy();
                target.setItemSlot(slot, ItemStack.EMPTY);
                target.spawnAtLocation(dropped);
                droppedAny = true;
            }
        }

        // Curiosスロットの装飾品も同様に強制ドロップ（Curios未導入時は何もしない）
        for (ItemStack curio : CuriosCompat.extractAll(target)) {
            if (curio.isEmpty()) continue;
            target.spawnAtLocation(curio);
            droppedAny = true;
        }

        if (droppedAny) {
            SoundSource source = attacker != null ? attacker.getSoundSource() : target.getSoundSource();
            target.level().playSound(null, target.blockPosition(),
                SoundEvents.ITEM_BREAK, source, 1.0F, 0.7F);
        }
    }

    /** 劣化・ドロップ対象（防具または武器）かどうかの判定 */
    public static boolean isArmorOrWeapon(ItemStack stack, EquipmentSlot slot) {
        // 防具スロットに装備している物は防具とみなす
        if (slot.isArmor()) {
            return true;
        }
        Item item = stack.getItem();
        if (item instanceof SwordItem || item instanceof DiggerItem
                || item instanceof TridentItem || item instanceof ProjectileWeaponItem) {
            return true;
        }
        // 攻撃力修飾子を持つアイテムも武器とみなす
        return !stack.getAttributeModifiers(slot).get(Attributes.ATTACK_DAMAGE).isEmpty();
    }

    // ===== 貫通ダメージ =====

    /**
     * 通常のダメージ処理（hurt）を通さずにSynchedEntityData上の体力を直接削る。
     *
     * 1. ターゲットの現在体力から攻撃力を引いた値を、最大体力に一時的に設定しなおす
     * 2. setHealthで上限までの回復を要求すると、下げた最大体力にクランプされて体力が切り下がる
     *    （setHealthはSynchedEntityDataのDATA_HEALTH_IDへ書き込む）
     * 3. 最大体力だけを元の値に戻す
     *
     * これにより防具・耐性・被ダメージ上限・ダメージイベントのいずれも介さずに体力を削る。
     *
     * @param fallbackSource 属性が取得できない場合や止めを刺す際に使うダメージソース
     */
    public static void dealPiercingDamage(LivingEntity target, DamageSource fallbackSource, float damage) {
        AttributeInstance maxHealthAttr = target.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealthAttr == null) {
            target.hurt(fallbackSource, damage);
            return;
        }

        SunIncarnateEntity sun = target instanceof SunIncarnateEntity s ? s : null;
        if (sun != null) {
            sun.setPierced(true);
        }

        try {
            double originalMaxHealth = maxHealthAttr.getBaseValue();
            float remaining = target.getHealth() - damage;
            // 最大体力は1未満に設定できないため、1を下回るなら致死扱いとする
            boolean lethal = remaining < 1.0F;
            float clampTarget = lethal ? 1.0F : remaining;

            // 最大体力を下げて現在体力を強制的に切り下げる
            maxHealthAttr.setBaseValue(clampTarget);
            target.setHealth(Float.MAX_VALUE);
            // 最大体力だけ元に戻す（体力は切り下がったまま）
            maxHealthAttr.setBaseValue(originalMaxHealth);

            // 陽の化身には正規ダメージとして通知（無敵中の巻き戻し対象から除外）
            if (sun != null) {
                sun.acceptPiercingDamage();
            }

            // 残り1で止まった場合は通常ダメージで止めを刺す
            if (lethal) {
                target.hurt(fallbackSource, Math.max(1.0F, damage));
            }
        } finally {
            if (sun != null) {
                sun.setPierced(false);
            }
        }
    }
}
