package net.mcreator.asterrisk.enchantment.celestial;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * 5. 死の連鎖 - Death Chain
 * 敵の撃破時、その敵と同じHP*エンチャントレベル/10だけ自身を除く周囲のmobにダメージをばらまく
 * 最大レベル: 10
 */
public class DeathChainEnchantment extends Enchantment {
    
    public DeathChainEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }
    
    @Override
    public int getMinCost(int level) {
        return 20 + (level - 1) * 10;
    }
    
    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 25;
    }
    
    @Override
    public int getMaxLevel() {
        return 10;
    }
    
    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof SwordItem || stack.getItem() instanceof AxeItem;
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
    
    public static float getDamageMultiplier(int level) {
        return level / 10.0f;
    }
    
    public static double getRange() {
        return 8.0;
    }
}
