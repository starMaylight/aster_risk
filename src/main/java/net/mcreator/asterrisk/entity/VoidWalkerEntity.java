package net.mcreator.asterrisk.entity;

import net.mcreator.asterrisk.init.AsterRiskModEffects;
import net.mcreator.asterrisk.init.AsterRiskModItems;
import net.minecraft.world.entity.EntityType;
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
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

/**
 * 虚空の歩行者 - Void Walker
 * エンダーマンのようにテレポートする敵。
 * 攻撃時に「月蝕の呪い」デバフを付与。
 */
public class VoidWalkerEntity extends Monster {
    
    private int teleportCooldown = 0;
    private int particleTimer = 0;
    private static final int TELEPORT_COOLDOWN_MAX = 40; // 2秒（高速化）

    public VoidWalkerEntity(EntityType<? extends VoidWalkerEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.3D, false));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 28.0D)  // HP減
            .add(Attributes.MOVEMENT_SPEED, 0.42D)  // 速度増
            .add(Attributes.ATTACK_DAMAGE, 16.0D)  // 攻撃力増
            .add(Attributes.FOLLOW_RANGE, 40.0D)  // 索敵範囲増
            .add(Attributes.ARMOR, 2.0D);  // 防御減
    }

    @Override
    public void aiStep() {
        super.aiStep();
        
        if (teleportCooldown > 0) {
            teleportCooldown--;
        }
        
        // ターゲットがいる場合、一定確率でテレポート
        if (!this.level().isClientSide()) {
            LivingEntity target = this.getTarget();
            if (target != null && teleportCooldown <= 0) {
                double distance = this.distanceTo(target);
                
                // 遠すぎる場合はターゲットの近くにテレポート
                if (distance > 10.0D && this.random.nextFloat() < 0.3F) {
                    teleportToTarget(target);
                }
                // ダメージを受けた時もテレポート
                else if (this.getHealth() < this.getMaxHealth() * 0.5F && this.random.nextFloat() < 0.2F) {
                    teleportRandomly();
                }
            }
        }
        
        // パーティクル
        if (this.level().isClientSide()) {
            particleTimer++;
            if (particleTimer >= 5) {
                particleTimer = 0;
                this.level().addParticle(
                    ParticleTypes.PORTAL,
                    this.getRandomX(0.5D),
                    this.getRandomY(),
                    this.getRandomZ(0.5D),
                    (this.random.nextDouble() - 0.5D) * 0.5D,
                    -this.random.nextDouble(),
                    (this.random.nextDouble() - 0.5D) * 0.5D
                );
            }
        }
    }

    private void teleportToTarget(LivingEntity target) {
        double offsetX = (this.random.nextDouble() - 0.5D) * 4.0D;
        double offsetZ = (this.random.nextDouble() - 0.5D) * 4.0D;
        
        double newX = target.getX() + offsetX;
        double newY = target.getY();
        double newZ = target.getZ() + offsetZ;
        
        if (this.randomTeleport(newX, newY, newZ, true)) {
            teleportCooldown = TELEPORT_COOLDOWN_MAX;
            spawnTeleportParticles();
            this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
        }
    }

    private void teleportRandomly() {
        double newX = this.getX() + (this.random.nextDouble() - 0.5D) * 16.0D;
        double newY = this.getY() + (this.random.nextDouble() - 0.5D) * 8.0D;
        double newZ = this.getZ() + (this.random.nextDouble() - 0.5D) * 16.0D;
        
        if (this.randomTeleport(newX, newY, newZ, true)) {
            teleportCooldown = TELEPORT_COOLDOWN_MAX;
            spawnTeleportParticles();
            this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
        }
    }

    private void spawnTeleportParticles() {
        for (int i = 0; i < 20; i++) {
            this.level().addParticle(
                ParticleTypes.PORTAL,
                this.getX(), this.getY() + 1.0D, this.getZ(),
                (this.random.nextDouble() - 0.5D) * 2.0D,
                -this.random.nextDouble(),
                (this.random.nextDouble() - 0.5D) * 2.0D
            );
        }
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean result = super.doHurtTarget(target);
        
        if (result && target instanceof LivingEntity living) {
            // 月蝕の呪いを付与
            living.addEffect(new MobEffectInstance(
                AsterRiskModEffects.LUNAR_ECLIPSE_CURSE.get(),
                20 * 8, // 8秒
                0
            ));
            
            // 攻撃後にテレポートで逃げる
            if (this.random.nextFloat() < 0.4F && teleportCooldown <= 0) {
                teleportRandomly();
            }
        }
        
        return result;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // ダメージを受けた時にテレポートで回避
        if (!this.level().isClientSide() && teleportCooldown <= 0 && this.random.nextFloat() < 0.3F) {
            teleportRandomly();
        }
        return super.hurt(source, amount);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENDERMAN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENDERMAN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENDERMAN_DEATH;
    }
    
    @Override
    protected boolean shouldDespawnInPeaceful() {
        return true;
    }
    
    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        
        // Void Shard - 60%確率で1個
        if (this.random.nextFloat() < 0.6F + looting * 0.1F) {
            this.spawnAtLocation(AsterRiskModItems.VOID_SHARD.get());
        }
    }
}
