package net.mcreator.asterrisk.enchantment.celestial;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * 最後の英雄 - Last Hero
 *
 * ダメージによって death する際、経験値レベルを消費して食いしばる。
 * エンチャントレベルが上がるほど消費する経験値レベルが減少する。
 * 最大レベル: 2
 *
 * 入手方法はCelestial Enchanting Tableのみ
 * （isDiscoverable=falseによりStar Anvilの抽選・通常エンチャントからも除外）
 */
public class LastHeroEnchantment extends Enchantment {

    /** レベル1で消費する経験値レベル */
    private static final int BASE_XP_LEVEL_COST = 30;
    /** レベルごとの軽減量 */
    private static final int COST_REDUCTION_PER_LEVEL = 15;

    public LastHeroEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.ARMOR, new EquipmentSlot[]{
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
        });
    }

    /**
     * 食いしばりに必要な経験値レベル
     */
    public static int getXpLevelCost(int enchantLevel) {
        return Math.max(1, BASE_XP_LEVEL_COST - (enchantLevel - 1) * COST_REDUCTION_PER_LEVEL);
    }

    @Override
    public int getMinCost(int level) {
        return 35 + (level - 1) * 20;
    }

    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 40;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem;
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
