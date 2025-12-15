package net.mcreator.asterrisk.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * 月光エンチャント - Moonlight
 * 武器用：夜間のダメージが増加
 * 最大レベル: 3
 * レベル1: +20%、レベル2: +35%、レベル3: +50%
 */
public class MoonlightEnchantment extends Enchantment {
    
    public MoonlightEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinCost(int level) {
        return 10 + (level - 1) * 10;
    }

    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 20;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof SwordItem || stack.getItem() instanceof AxeItem;
    }
    
    /**
     * 夜間ダメージボーナスを計算
     * @param level エンチャントレベル
     * @return ダメージ倍率（1.0 = 100%、1.5 = 150%など）
     */
    public static float getNightDamageMultiplier(int level) {
        if (level <= 0) return 1.0f;
        switch (level) {
            case 1: return 1.20f;
            case 2: return 1.35f;
            case 3: return 1.50f;
            default: return 1.50f;
        }
    }
}
