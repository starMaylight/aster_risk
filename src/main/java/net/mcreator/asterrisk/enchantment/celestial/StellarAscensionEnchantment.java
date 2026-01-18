package net.mcreator.asterrisk.enchantment.celestial;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * 10. 星の昇華 - Stellar Ascension
 * このエンチャントがかかったツールに付与されたこのエンチャントを除いた
 * エンチャント全てのレベルをこのエンチャント分だけ上げる
 * 最大レベル: 3
 */
public class StellarAscensionEnchantment extends Enchantment {
    
    public StellarAscensionEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.BREAKABLE, new EquipmentSlot[]{
            EquipmentSlot.MAINHAND, EquipmentSlot.HEAD, EquipmentSlot.CHEST, 
            EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.OFFHAND
        });
    }
    
    @Override
    public int getMinCost(int level) {
        return 35 + (level - 1) * 20;
    }
    
    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 35;
    }
    
    @Override
    public int getMaxLevel() {
        return 3;
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
    
    public static int getLevelBonus(int ascensionLevel) {
        return ascensionLevel;
    }
}
