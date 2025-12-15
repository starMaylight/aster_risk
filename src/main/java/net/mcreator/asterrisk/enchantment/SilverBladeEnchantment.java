package net.mcreator.asterrisk.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * 銀の刃エンチャント - アンデッドへの追加ダメージ
 * Smiteの強化版として機能
 */
public class SilverBladeEnchantment extends Enchantment {
    
    public SilverBladeEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }
    
    @Override
    public int getMinCost(int level) {
        return 8 + (level - 1) * 10;
    }
    
    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 20;
    }
    
    @Override
    public int getMaxLevel() {
        return 5;
    }
    
    /**
     * アンデッドへの追加ダメージを計算
     * @param level エンチャントレベル
     * @return 追加ダメージ
     */
    public static float getUndeadDamageBonus(int level) {
        if (level <= 0) return 0;
        return 3.0f * level; // Smiteより強力（レベル5で15ダメージ）
    }
}
