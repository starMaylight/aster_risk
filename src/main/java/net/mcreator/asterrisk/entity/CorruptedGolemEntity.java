package net.mcreator.asterrisk.entity;

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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * 堕落した月光石ゴーレム - Corrupted Moonstone Golem
 * 強力な近接攻撃を持つ大型の敵。
 * 倒すと月光石や隕石の欠片をドロップ。
 */
public class CorruptedGolemEntity extends Monster {
    
    private int attackCooldown = 0;
    private int particleTimer = 0;
    private boolean isEnraged = false;

    public CorruptedGolemEntity(EntityType<? extends CorruptedGolemEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 80.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.22D)
            .add(Attributes.ATTACK_DAMAGE, 24.0D)
            .add(Attributes.FOLLOW_RANGE, 20.0D)
            .add(Attributes.ARMOR, 8.0D)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D)
            .add(Attributes.ATTACK_KNOCKBACK, 1.5D);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        
        if (attackCooldown > 0) {
            attackCooldown--;
        }
        
        // HP50%以下で激昂状態
        if (!this.level().isClientSide()) {
            boolean shouldBeEnraged = this.getHealth() < this.getMaxHealth() * 0.5F;
            if (shouldBeEnraged && !isEnraged) {
                isEnraged = true;
                // 激昂時にステータス上昇
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.28D);
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(16.0D);
            }
        }
        
        // パーティクル
        if (this.level().isClientSide()) {
            particleTimer++;
            if (particleTimer >= 10) {
                particleTimer = 0;
                
                // 通常時は紫のパーティクル
                this.level().addParticle(
                    ParticleTypes.WITCH,
                    this.getRandomX(0.8D),
                    this.getRandomY(),
                    this.getRandomZ(0.8D),
                    0, 0.02D, 0
                );
                
                // 激昂時は追加で赤いパーティクル
                if (isEnraged) {
                    this.level().addParticle(
                        ParticleTypes.ANGRY_VILLAGER,
                        this.getRandomX(0.5D),
                        this.getRandomY() + 0.5D,
                        this.getRandomZ(0.5D),
                        0, 0, 0
                    );
                }
            }
        }
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean result = super.doHurtTarget(target);
        
        if (result && target instanceof LivingEntity) {
            // 地面を叩く攻撃エフェクト
            for (int i = 0; i < 12; i++) {
                double angle = (Math.PI * 2.0D / 12.0D) * i;
                double px = this.getX() + Math.cos(angle) * 1.5D;
                double pz = this.getZ() + Math.sin(angle) * 1.5D;
                this.level().addParticle(
                    ParticleTypes.CRIT,
                    px, this.getY() + 0.1D, pz,
                    Math.cos(angle) * 0.2D, 0.3D, Math.sin(angle) * 0.2D
                );
            }
            
            this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 0.8F);
        }
        
        return result;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // 弱点：プロジェクタイルには通常ダメージ
        // 近接攻撃には強い耐性
        if (!source.isIndirect() && source.getEntity() instanceof LivingEntity) {
            amount *= 0.7F; // 近接ダメージ30%減少
        }
        
        return super.hurt(source, amount);
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        
        // 月光石をドロップ (1-3個)
        int moonstoneCount = 1 + this.random.nextInt(2) + looting;
        for (int i = 0; i < moonstoneCount; i++) {
            this.spawnAtLocation(new ItemStack(AsterRiskModItems.MOONSTONE.get()));
        }
        
        // 隕石の欠片をドロップ (0-2個)
        int meteoriteCount = this.random.nextInt(2 + looting);
        for (int i = 0; i < meteoriteCount; i++) {
            this.spawnAtLocation(new ItemStack(AsterRiskModItems.METEORITE_FRAGMENT.get()));
        }
        
        // 低確率で星の欠片 (10% + looting*5%)
        if (this.random.nextFloat() < 0.1F + looting * 0.05F) {
            this.spawnAtLocation(new ItemStack(AsterRiskModItems.STARFLAGMENT.get()));
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.IRON_GOLEM_STEP;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.IRON_GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }
    
    @Override
    protected boolean shouldDespawnInPeaceful() {
        return true;
    }

    public boolean isEnraged() {
        return isEnraged;
    }
}
