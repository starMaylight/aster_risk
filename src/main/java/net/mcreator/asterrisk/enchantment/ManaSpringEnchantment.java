package net.mcreator.asterrisk.enchantment;

import net.mcreator.asterrisk.init.AsterRiskModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * マナの泉 - Mana Spring
 * 防具用：継続的にマナを回復
 * 最大レベル: 3
 */
public class ManaSpringEnchantment extends Enchantment {
    
    public ManaSpringEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentCategory.ARMOR_CHEST, new EquipmentSlot[]{EquipmentSlot.CHEST});
    }

    @Override
    public int getMinCost(int level) {
        return 8 + (level - 1) * 8;
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
        return stack.getItem() instanceof ArmorItem armor && 
               armor.getEquipmentSlot() == EquipmentSlot.CHEST;
    }
    
    /**
     * マナ回復量を取得
     * @param level エンチャントレベル
     * @return 1秒あたりのマナ回復量
     */
    public static float getManaRegenBonus(int level) {
        return level * 0.5f; // レベル1: 0.5/秒, レベル2: 1.0/秒, レベル3: 1.5/秒
    }
}
