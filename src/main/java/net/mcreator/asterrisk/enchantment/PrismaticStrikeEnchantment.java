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
 * 虹色の一撃 - Prismatic Strike
 * 武器用：攻撃時に確率で虹色の輝きを付与（自分に）
 * 最大レベル: 3
 */
public class PrismaticStrikeEnchantment extends Enchantment {
    
    public PrismaticStrikeEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
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

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof SwordItem || stack.getItem() instanceof AxeItem;
    }
    
    @Override
    public void doPostAttack(LivingEntity attacker, Entity target, int level) {
        if (!attacker.level().isClientSide()) {
            // 確率で虹色の輝きを付与
            float chance = 0.08f + (level * 0.04f); // レベル1: 12%, レベル2: 16%, レベル3: 20%
            if (attacker.getRandom().nextFloat() < chance) {
                int duration = 100 + (level * 40); // 5秒 + レベルごとに2秒
                attacker.addEffect(new MobEffectInstance(AsterRiskModEffects.PRISMATIC_GLOW.get(), duration, level - 1));
            }
        }
    }
}
