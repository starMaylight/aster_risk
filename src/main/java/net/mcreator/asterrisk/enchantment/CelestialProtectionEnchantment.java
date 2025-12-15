package net.mcreator.asterrisk.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;

/**
 * 天体の守護エンチャント - Celestial Protection
 * 防具用：全ダメージ軽減（特に魔法ダメージに強い）
 * 最大レベル: 4
 * 通常の防護と同等だが、魔法ダメージに対してボーナス
 */
public class CelestialProtectionEnchantment extends Enchantment {
    
    public CelestialProtectionEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentCategory.ARMOR, 
            new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET});
    }

    @Override
    public int getMinCost(int level) {
        return 1 + (level - 1) * 11;
    }

    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 11;
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem;
    }

    @Override
    public boolean checkCompatibility(Enchantment other) {
        // バニラの防護エンチャントとは非互換
        if (other instanceof ProtectionEnchantment) {
            return false;
        }
        return super.checkCompatibility(other);
    }

    /**
     * ダメージ軽減量を計算
     * @param level エンチャントレベル
     * @return 軽減量（0-20の範囲、1 = 4%軽減）
     */
    public static int getDamageProtection(int level) {
        return level * 2;
    }

    /**
     * 魔法ダメージに対する追加軽減を計算
     * @param level エンチャントレベル
     * @return 追加軽減量
     */
    public static int getMagicDamageProtection(int level) {
        return level * 3;
    }
}
