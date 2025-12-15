package net.mcreator.asterrisk.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * 月の引力エンチャント - Lunar Gravity
 * 防具用：近くのアイテムを自動で引き寄せる
 * 最大レベル: 3
 * レベル1: 3ブロック、レベル2: 5ブロック、レベル3: 8ブロック
 */
public class LunarGravityEnchantment extends Enchantment {
    
    public LunarGravityEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.ARMOR, 
            new EquipmentSlot[]{EquipmentSlot.CHEST}); // チェストプレートのみ
    }

    @Override
    public int getMinCost(int level) {
        return 15 + (level - 1) * 15;
    }

    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 25;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        if (stack.getItem() instanceof ArmorItem armor) {
            return armor.getEquipmentSlot() == EquipmentSlot.CHEST;
        }
        return false;
    }

    /**
     * アイテム吸引範囲を取得
     * @param level エンチャントレベル
     * @return 吸引範囲（ブロック）
     */
    public static double getAttractionRange(int level) {
        switch (level) {
            case 1: return 3.0;
            case 2: return 5.0;
            case 3: return 8.0;
            default: return 0.0;
        }
    }
}
