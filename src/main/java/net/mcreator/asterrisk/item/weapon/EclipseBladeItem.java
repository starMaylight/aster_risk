package net.mcreator.asterrisk.item.weapon;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.core.particles.ParticleTypes;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Eclipse Blade - 最終武器（日蝕の剣）
 * HP吸収、闇属性攻撃
 */
public class EclipseBladeItem extends SwordItem {
    
    private static final Tier ECLIPSE_TIER = new Tier() {
        @Override public int getUses() { return 3500; }
        @Override public float getSpeed() { return 11.0F; }
        @Override public float getAttackDamageBonus() { return 12.0F; }  // 基礎4 + 12 = 16ダメージ
        @Override public int getLevel() { return 5; }
        @Override public int getEnchantmentValue() { return 25; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.EMPTY; }
    };
    
    public EclipseBladeItem(Properties properties) {
        super(ECLIPSE_TIER, 5, -2.2F, properties.fireResistant());
    }
    
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        
        if (result) {
            // HP吸収（与ダメージの20%）
            float healAmount = 3.0F;  // 回復量増
            attacker.heal(healAmount);
            
            // 敵に闇属性デバフ
            target.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 1));
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 1));
            
            // パーティクル
            if (!attacker.level().isClientSide()) {
                for (int i = 0; i < 5; i++) {
                    attacker.level().addParticle(
                        ParticleTypes.SOUL,
                        target.getX() + (Math.random() - 0.5) * 0.5,
                        target.getY() + target.getBbHeight() / 2,
                        target.getZ() + (Math.random() - 0.5) * 0.5,
                        0, 0.05, 0
                    );
                }
            }
        }
        
        return result;
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§5§l[Eclipse Blade]"));
        tooltip.add(Component.literal("§7Lifesteal: Heals on hit"));
        tooltip.add(Component.literal("§7Dark Curse: Applies Wither"));
        tooltip.add(Component.literal("§8Forged from the heart of darkness"));
    }
    
    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
