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
 * 星の守護 - Stellar Ward
 * 防具用：ダメージを受けた時に星の加護を付与
 * 最大レベル: 3
 */
public class StellarWardEnchantment extends Enchantment {
    
    public StellarWardEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.ARMOR, new EquipmentSlot[]{
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
        });
    }

    @Override
    public int getMinCost(int level) {
        return 10 + (level - 1) * 10;
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
        return stack.getItem() instanceof ArmorItem;
    }
    
    /**
     * ダメージを受けた時の処理（EnchantmentEventHandlerで呼び出す）
     */
    public static void onDamaged(LivingEntity entity, int totalLevel) {
        if (totalLevel > 0 && !entity.level().isClientSide()) {
            // 確率でStellar Blessingを付与
            if (entity.getRandom().nextFloat() < 0.15f * totalLevel) {
                entity.addEffect(new MobEffectInstance(AsterRiskModEffects.STELLAR_BLESSING.get(), 100, 0));
            }
        }
    }
}
