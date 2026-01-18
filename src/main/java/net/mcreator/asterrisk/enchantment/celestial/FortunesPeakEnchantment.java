package net.mcreator.asterrisk.enchantment.celestial;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * 4. 幸運の極み - Fortune's Peak
 * 幸運の影響するブロックを採掘時にエンチャントレベル*2%の確率で幸運判定を5回行う
 * 最大レベル: 5
 */
public class FortunesPeakEnchantment extends Enchantment {
    
    public FortunesPeakEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }
    
    @Override
    public int getMinCost(int level) {
        return 25 + (level - 1) * 12;
    }
    
    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 25;
    }
    
    @Override
    public int getMaxLevel() {
        return 5;
    }
    
    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof DiggerItem;
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
    
    public static float getTriggerChance(int level) {
        return level * 0.02f; // レベル*2%
    }
    
    public static int getFortuneRolls() {
        return 5;
    }
}
