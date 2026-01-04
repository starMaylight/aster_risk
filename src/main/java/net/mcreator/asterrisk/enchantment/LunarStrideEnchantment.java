package net.mcreator.asterrisk.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * 月光の歩み - Lunar Stride
 * ブーツ用：落下ダメージ軽減、夜間に移動速度上昇
 * 最大レベル: 3
 */
public class LunarStrideEnchantment extends Enchantment {
    
    public LunarStrideEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentCategory.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET});
    }

    @Override
    public int getMinCost(int level) {
        return 5 + (level - 1) * 8;
    }

    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 15;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem armor && 
               armor.getEquipmentSlot() == EquipmentSlot.FEET;
    }
    
    /**
     * 落下ダメージ軽減率を取得
     * @param level エンチャントレベル
     * @return 軽減率（0.0 ~ 1.0）
     */
    public static float getFallDamageReduction(int level) {
        return Math.min(0.25f * level, 0.75f); // 最大75%軽減
    }
    
    /**
     * 夜間の移動速度ボーナスを取得
     * @param level エンチャントレベル
     * @return 速度倍率
     */
    public static float getNightSpeedBonus(int level) {
        return 1.0f + (0.05f * level); // レベル1: +5%, レベル2: +10%, レベル3: +15%
    }
}
