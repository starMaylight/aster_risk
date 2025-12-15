package net.mcreator.asterrisk.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * 月の引力エンチャント - アイテムを吸引する
 * 全装備用エンチャント
 */
public class LunarAttractionEnchantment extends Enchantment {
    
    public LunarAttractionEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentCategory.ARMOR_CHEST, 
            new EquipmentSlot[]{EquipmentSlot.CHEST});
    }
    
    @Override
    public int getMinCost(int level) {
        return 12 + (level - 1) * 10;
    }
    
    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 20;
    }
    
    @Override
    public int getMaxLevel() {
        return 3;
    }
    
    /**
     * アイテム吸引範囲を計算
     * @param level エンチャントレベル
     * @return 吸引範囲（ブロック単位）
     */
    public static double getAttractionRange(int level) {
        if (level <= 0) return 0;
        return 3.0 + (level - 1) * 2.0; // レベル1: 3ブロック, レベル2: 5ブロック, レベル3: 7ブロック
    }
}
