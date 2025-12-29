package net.mcreator.asterrisk.util;

import net.mcreator.asterrisk.block.entity.PhaseAnvilBlockEntity;
import net.mcreator.asterrisk.item.PhaseSigilItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * 月相刻印のユーティリティクラス
 */
public class PhaseSigilUtil {

    // 効果係数
    public static final float FULL_MOON_ATTACK_BONUS = 0.15f;
    public static final float WANING_GIBBOUS_MAGIC_BONUS = 0.10f;
    public static final float LAST_QUARTER_DEFENSE_BONUS = 0.12f;
    public static final float WANING_CRESCENT_MANA_BONUS = 0.08f;
    public static final float WAXING_CRESCENT_SPEED_BONUS = 0.10f;
    public static final float FIRST_QUARTER_MINING_BONUS = 0.15f;
    public static final float WAXING_GIBBOUS_XP_BONUS = 0.10f;

    /**
     * 装備から全刻印レベルを取得
     */
    public static Map<PhaseSigilItem.MoonPhase, Integer> getTotalSigilLevels(Player player) {
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
                int level = sigils.getInt(phase.getName());
                if (level > 0) {
                    totals.put(phase, totals.get(phase) + level);
                }
            }
        }

        return totals;
    }

    /**
     * 特定の刻印レベルを取得
     */
    public static int getSigilLevel(Player player, PhaseSigilItem.MoonPhase phase) {
        return getTotalSigilLevels(player).get(phase);
    }

    /**
     * 魔法ダメージボーナスを計算（更待月）
     */
    public static float getMagicDamageMultiplier(Player player) {
        int level = getSigilLevel(player, PhaseSigilItem.MoonPhase.WANING_GIBBOUS);
        return 1.0f + (WANING_GIBBOUS_MAGIC_BONUS * level);
    }

    /**
     * 攻撃ダメージボーナスを計算（満月）
     */
    public static float getAttackDamageMultiplier(Player player) {
        int level = getSigilLevel(player, PhaseSigilItem.MoonPhase.FULL_MOON);
        return 1.0f + (FULL_MOON_ATTACK_BONUS * level);
    }

    /**
     * 防御ボーナスを計算（下弦）
     */
    public static float getDamageReductionMultiplier(Player player) {
        int level = getSigilLevel(player, PhaseSigilItem.MoonPhase.LAST_QUARTER);
        float reduction = 1.0f - (LAST_QUARTER_DEFENSE_BONUS * level);
        return Math.max(0.1f, reduction);
    }

    /**
     * 採掘速度ボーナスを計算（上弦）
     */
    public static float getMiningSpeedMultiplier(Player player) {
        int level = getSigilLevel(player, PhaseSigilItem.MoonPhase.FIRST_QUARTER);
        return 1.0f + (FIRST_QUARTER_MINING_BONUS * level);
    }

    /**
     * 経験値ボーナスを計算（十三夜）
     */
    public static float getXpMultiplier(Player player) {
        int level = getSigilLevel(player, PhaseSigilItem.MoonPhase.WAXING_GIBBOUS);
        return 1.0f + (WAXING_GIBBOUS_XP_BONUS * level);
    }

    /**
     * アイテムに刻印があるか確認
     */
    public static boolean hasSigil(ItemStack stack, PhaseSigilItem.MoonPhase phase) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(PhaseAnvilBlockEntity.TAG_PHASE_SIGILS)) return false;
        
        CompoundTag sigils = tag.getCompound(PhaseAnvilBlockEntity.TAG_PHASE_SIGILS);
        return sigils.getInt(phase.getName()) > 0;
    }

    /**
     * アイテムから特定の刻印レベルを取得
     */
    public static int getSigilLevel(ItemStack stack, PhaseSigilItem.MoonPhase phase) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(PhaseAnvilBlockEntity.TAG_PHASE_SIGILS)) return 0;
        
        CompoundTag sigils = tag.getCompound(PhaseAnvilBlockEntity.TAG_PHASE_SIGILS);
        return sigils.getInt(phase.getName());
    }
}
