package net.mcreator.asterrisk.entity;

import net.mcreator.asterrisk.init.AsterRiskModEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

/**
 * 日食の亡霊 - Eclipse Phantom
 * 浮遊する幽霊タイプの敵。夜間に出現し、
 * 攻撃時に「星なき夜」デバフを付与する。
 */
public class EclipsePhantomEntity extends Monster {
    
    private int particleTimer = 0;

    public EclipsePhantomEntity(EntityType<? extends EclipsePhantomEntity> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 30.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.28D)
            .add(Attributes.ATTACK_DAMAGE, 6.0D)
            .add(Attributes.FOLLOW_RANGE, 24.0D)
            .add(Attributes.FLYING_SPEED, 0.35D)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.3D);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        
        // 浮遊動作
        if (!this.onGround() && this.getDeltaMovement().y < 0) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D));
        }
        
        // 暗いパーティクル
        if (this.level().isClientSide()) {
            particleTimer++;
            if (particleTimer >= 3) {
                particleTimer = 0;
                this.level().addParticle(
                    ParticleTypes.SMOKE,
                    this.getRandomX(0.6D),
                    this.getRandomY(),
                    this.getRandomZ(0.6D),
                    0, -0.05D, 0
                );
                if (this.random.nextFloat() < 0.3F) {
                    this.level().addParticle(
                        ParticleTypes.SOUL,
                        this.getRandomX(0.4D),
                        this.getRandomY(),
                        this.getRandomZ(0.4D),
                        0, 0.02D, 0
                    );
                }
            }
        }
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean result = super.doHurtTarget(target);
        
        if (result && target instanceof LivingEntity living) {
            // 星なき夜のデバフを付与
            living.addEffect(new MobEffectInstance(
                AsterRiskModEffects.STARLESS_NIGHT.get(),
                20 * 10, // 10秒
                0
            ));
            
            // 攻撃時のパーティクル
            for (int i = 0; i < 8; i++) {
                this.level().addParticle(
                    ParticleTypes.SOUL,
                    target.getX(), target.getY() + 1.0D, target.getZ(),
                    (this.random.nextDouble() - 0.5D) * 0.5D,
                    this.random.nextDouble() * 0.3D,
                    (this.random.nextDouble() - 0.5D) * 0.5D
                );
            }
        }
        
        return result;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false; // 落下ダメージなし
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PHANTOM_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.PHANTOM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PHANTOM_DEATH;
    }

    @Override
    public boolean isPushable() {
        return false;
    }
    
    @Override
    protected boolean shouldDespawnInPeaceful() {
        return true;
    }
}
