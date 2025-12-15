package net.mcreator.asterrisk.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;

/**
 * 星屑の幸運エンチャント - Stardust Fortune
 * 道具用：追加の幸運効果（バニラの幸運と重複可能）
 * 最大レベル: 2
 * レベル1: 幸運+1相当、レベル2: 幸運+2相当
 */
public class StardustFortuneEnchantment extends Enchantment {
    
    public StardustFortuneEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinCost(int level) {
        return 15 + (level - 1) * 15;
    }

    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 30;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof PickaxeItem || 
               stack.getItem() instanceof ShovelItem ||
               stack.getItem() instanceof AxeItem ||
               stack.getItem() instanceof HoeItem;
    }

    @Override
    public boolean checkCompatibility(Enchantment other) {
        // シルクタッチとは非互換
        return other != Enchantments.SILK_TOUCH && super.checkCompatibility(other);
    }
}
