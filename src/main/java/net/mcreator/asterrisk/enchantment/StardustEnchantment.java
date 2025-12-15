package net.mcreator.asterrisk.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;

/**
 * 星屑エンチャント - 幸運効果を強化
 * ツール用エンチャント
 */
public class StardustEnchantment extends Enchantment {
    
    public StardustEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }
    
    @Override
    public int getMinCost(int level) {
        return 15 + (level - 1) * 12;
    }
    
    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 25;
    }
    
    @Override
    public int getMaxLevel() {
        return 3;
    }
    
    @Override
    protected boolean checkCompatibility(Enchantment other) {
        // Fortuneと共存可能だが、Silk Touchとは不可
        return super.checkCompatibility(other) && other != Enchantments.SILK_TOUCH;
    }
    
    /**
     * 追加ドロップのチャンスを計算
     * @param level エンチャントレベル
     * @return 追加ドロップ確率（0.0-1.0）
     */
    public static float getExtraDropChance(int level) {
        if (level <= 0) return 0;
        return 0.1f * level; // レベル1: 10%, レベル2: 20%, レベル3: 30%
    }
}
