package net.mcreator.asterrisk.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * 流星エンチャント - 弓矢に貫通効果を付与
 */
public class MeteorEnchantment extends Enchantment {
    
    public MeteorEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.BOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }
    
    @Override
    public int getMinCost(int level) {
        return 15 + (level - 1) * 12;
    }
    
    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 25;
    }
    
    @Override
    public int getMaxLevel() {
        return 3;
    }
    
    /**
     * 貫通できるエンティティ数を計算
     * @param level エンチャントレベル
     * @return 貫通数
     */
    public static int getPierceCount(int level) {
        if (level <= 0) return 0;
        return level; // レベル1: 1体, レベル2: 2体, レベル3: 3体
    }
    
    /**
     * 貫通後のダメージ減衰を計算
     * @param level エンチャントレベル
     * @return 減衰率（0.0-1.0、低いほど減衰が少ない）
     */
    public static float getDamageDecay(int level) {
        if (level <= 0) return 1.0f;
        return 1.0f - (0.1f * level); // レベル1: 10%減衰, レベル2: 20%減衰, レベル3: 30%減衰
    }
}
