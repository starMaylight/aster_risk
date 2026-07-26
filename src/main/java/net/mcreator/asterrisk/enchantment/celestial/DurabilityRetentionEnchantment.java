package net.mcreator.asterrisk.enchantment.celestial;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * 耐久保持 - Durability Retention
 *
 * 装備の残り耐久が1/4以下になったとき、
 * エンチャントレベルを1つ消費して耐久を全回復する。
 * 最大レベル: 7（=7回まで自動修復できる）
 *
 * 入手方法はCelestial Enchanting Tableのみ
 * （isDiscoverable=falseによりStar Anvilの抽選・通常エンチャントからも除外）
 */
public class DurabilityRetentionEnchantment extends Enchantment {

    /** 発動する残り耐久の割合（1/4以下） */
    public static final float TRIGGER_RATIO = 0.25F;

    public DurabilityRetentionEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.BREAKABLE, new EquipmentSlot[]{
            EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND,
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
        });
    }

    /**
     * 残り耐久が発動条件（1/4以下）を満たしているか
     */
    public static boolean shouldTrigger(ItemStack stack) {
        if (!stack.isDamageableItem() || stack.getMaxDamage() <= 0) {
            return false;
        }
        int remaining = stack.getMaxDamage() - stack.getDamageValue();
        return remaining <= stack.getMaxDamage() * TRIGGER_RATIO;
    }

    @Override
    public int getMinCost(int level) {
        return 20 + (level - 1) * 10;
    }

    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 30;
    }

    @Override
    public int getMaxLevel() {
        return 7;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.isDamageableItem();
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public boolean isDiscoverable() {
        return false;
    }

    @Override
    public boolean isTradeable() {
        return false;
    }

    @Override
    public boolean isAllowedOnBooks() {
        return false;
    }
}
