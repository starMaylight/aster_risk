package net.mcreator.asterrisk.item.weapon;

import net.minecraft.core.particles.ParticleTypes;
import net.mcreator.asterrisk.registry.ModParticles;
import net.mcreator.asterrisk.registry.ModSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.mcreator.asterrisk.item.tool.ModToolTiers;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * 虹色の大剣 - 攻撃時にランダムな追加効果
 */
public class PrismaticGreatswordItem extends SwordItem {

    private final Random random = new Random();

    public PrismaticGreatswordItem(Properties properties) {
        super(ModToolTiers.PRISMATIC, 6, -2.8f, properties.rarity(Rarity.EPIC));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        Level level = attacker.level();
        
        // ランダムな追加効果（20%確率）
        if (random.nextFloat() < 0.2f) {
            int effect = random.nextInt(5);
            
            switch (effect) {
                case 0 -> {
                    // 炎上
                    target.setSecondsOnFire(3);
                    spawnParticles(level, target, ParticleTypes.FLAME);
                }
                case 1 -> {
                    // 凍結（スローネス）
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 2));
                    spawnParticles(level, target, ParticleTypes.SNOWFLAKE);
                }
                case 2 -> {
                    // 毒
                    target.addEffect(new MobEffectInstance(MobEffects.POISON, 80, 1));
                    spawnParticles(level, target, ParticleTypes.ITEM_SLIME);
                }
                case 3 -> {
                    // 雷撃（追加ダメージ）
                    target.hurt(attacker.damageSources().magic(), 5.0f);
                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.playSound(null, target.getX(), target.getY(), target.getZ(),
                            SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 0.5f, 1.5f);
                    }
                    spawnParticles(level, target, ParticleTypes.ELECTRIC_SPARK);
                }
                case 4 -> {
                    // ノックバック強化
                    double dx = target.getX() - attacker.getX();
                    double dz = target.getZ() - attacker.getZ();
                    target.knockback(1.5f, dx, dz);
                    spawnParticles(level, target, ParticleTypes.EXPLOSION);
                }
            }
        }

        // 常に虹色パーティクルとサウンド
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ModParticles.PRISMATIC_SPARKLE.get(),
                target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(),
                5, 0.3, 0.3, 0.3, 0.05);
            serverLevel.playSound(null, target.getX(), target.getY(), target.getZ(),
                ModSounds.PRISMATIC_HIT.get(), SoundSource.PLAYERS, 0.8f, 1.0f + random.nextFloat() * 0.2f);
        }
        spawnParticles(level, target, ParticleTypes.END_ROD);

        return super.hurtEnemy(stack, target, attacker);
    }

    private void spawnParticles(Level level, LivingEntity target, net.minecraft.core.particles.ParticleOptions particle) {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(particle, 
                target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(),
                8, 0.3, 0.3, 0.3, 0.05);
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, 
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§d✦ Prismatic Blade"));
        tooltip.add(Component.literal("§720% chance for random effect:"));
        tooltip.add(Component.literal("§c  Fire §7/ §bIce §7/ §2Poison §7/ §eLightning §7/ §fKnockback"));
    }
}
