package net.mcreator.asterrisk.enchantment;

import net.mcreator.asterrisk.init.AsterRiskModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * 影吸収 - Shadow Drain
 * 武器用：攻撃時に相手の体力を吸収
 * 最大レベル: 2
 */
public class ShadowDrainEnchantment extends Enchantment {
    
    public ShadowDrainEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinCost(int level) {
        return 20 + (level - 1) * 15;
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
        return stack.getItem() instanceof SwordItem || stack.getItem() instanceof AxeItem;
    }
    
    @Override
    public void doPostAttack(LivingEntity attacker, Entity target, int level) {
        if (target instanceof LivingEntity living && !attacker.level().isClientSide()) {
            // 確率で体力吸収
            if (attacker.getRandom().nextFloat() < 0.2f + (level * 0.1f)) {
                float healAmount = 1.0f + (level * 0.5f);
                attacker.heal(healAmount);
                
                // 自分に短時間の影の抱擁を付与（速度ボーナス）
                attacker.addEffect(new MobEffectInstance(AsterRiskModEffects.SHADOW_EMBRACE.get(), 60, 0));
            }
        }
    }
}
