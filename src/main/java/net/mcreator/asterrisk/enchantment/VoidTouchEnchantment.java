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
 * 虚空の触手 - Void Touch
 * 武器用：攻撃時に虚空の腐食を付与
 * 最大レベル: 2
 */
public class VoidTouchEnchantment extends Enchantment {
    
    public VoidTouchEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
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
        return stack.getItem() instanceof SwordItem || stack.getItem() instanceof AxeItem;
    }
    
    @Override
    public void doPostAttack(LivingEntity attacker, Entity target, int level) {
        if (target instanceof LivingEntity living && !attacker.level().isClientSide()) {
            // 虚空の腐食を付与（レベルに応じて持続時間増加）
            int duration = 100 + (level * 60); // 5秒 + レベルごとに3秒
            living.addEffect(new MobEffectInstance(AsterRiskModEffects.VOID_CORRUPTION.get(), duration, level - 1));
        }
    }
}
