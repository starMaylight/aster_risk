package net.mcreator.asterrisk.effect;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

/**
 * 月光浮遊 - Lunar Levitation
 * ポジティブエフェクト
 * - 落下ダメージ無効
 * - 緩やかに浮遊
 * - 月光の杖や特定のアイテムで付与
 */
public class LunarLevitationEffect extends MobEffect {
    
    public LunarLevitationEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xADD8E6); // ライトブルー
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide()) {
            // 落下速度を緩やかに
            if (entity.getDeltaMovement().y < -0.1) {
                entity.setDeltaMovement(
                    entity.getDeltaMovement().x,
                    Math.max(entity.getDeltaMovement().y, -0.1 - (amplifier * 0.02)),
                    entity.getDeltaMovement().z
                );
                entity.fallDistance = 0;
            }
            
            // パーティクル効果
            if (entity.level() instanceof ServerLevel serverLevel && entity.tickCount % 5 == 0) {
                serverLevel.sendParticles(ParticleTypes.END_ROD,
                    entity.getX(), entity.getY(), entity.getZ(),
                    1, 0.2, 0.1, 0.2, 0.01);
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true; // 毎tick発動
    }
}
