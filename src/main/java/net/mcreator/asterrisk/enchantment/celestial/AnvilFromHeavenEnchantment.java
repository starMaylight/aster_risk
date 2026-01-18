package net.mcreator.asterrisk.enchantment.celestial;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * 6. 天罰の鉄槌 - Anvil From Heaven
 * 攻撃時エンチャントレベル*5%の確率で敵の頭上2ブロック上から金床が降ってくる（落下後消失）
 * 最大レベル: 5
 */
public class AnvilFromHeavenEnchantment extends Enchantment {
    
    public AnvilFromHeavenEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
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
    
    public static float getTriggerChance(int level) {
        return level * 0.05f; // レベル*5%
    }
    
    public static int getDropHeight() {
        return 2;
    }
}
