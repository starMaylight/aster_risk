package net.mcreator.asterrisk.entity;

import net.mcreator.asterrisk.init.AsterRiskModEffects;
import net.mcreator.asterrisk.init.AsterRiskModItems;
import net.mcreator.asterrisk.registry.ModEntities;
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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.particles.ParticleTypes;
import net.mcreator.asterrisk.registry.ModParticles;
import net.mcreator.asterrisk.registry.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;

import java.util.List;

/**
 * 月蝕の王 - Eclipse Monarch
 * Aster Riskのボスエンティティ。
 * 複数のフェーズを持ち、様々な攻撃パターンを使用する。
 */
public class EclipseMonarchEntity extends Monster {
    
    private final ServerBossEvent bossEvent = new ServerBossEvent(
        Component.translatable("entity.aster_risk.eclipse_monarch"),
        BossEvent.BossBarColor.PURPLE,
        BossEvent.BossBarOverlay.NOTCHED_10
    );
    
    private int attackCooldown = 0;
    private int specialAttackTimer = 0;
    private int phase = 1;
    private int particleTimer = 0;
    private int summonCooldown = 0;

    public EclipseMonarchEntity(EntityType<? extends EclipseMonarchEntity> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
        this.xpReward = 500;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 450.0D)  // ボスはHP維持
            .add(Attributes.MOVEMENT_SPEED, 0.85D)  // 速度増
            .add(Attributes.ATTACK_DAMAGE, 50.0D)  // 攻撃力増
            .add(Attributes.FOLLOW_RANGE, 56.0D)  // 索敵範囲増
            .add(Attributes.ARMOR, 10.0D)  // 防御弱体化
            .add(Attributes.ARMOR_TOUGHNESS, 6.0D)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.9D);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        
        // フェーズ更新
        updatePhase();
        
        // 浮遊
        if (!this.onGround() && this.getDeltaMovement().y < 0) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D));
        }
        
        // パーティクル
        if (this.level().isClientSide()) {
            spawnParticles();
        }
        
        // サーバー側の攻撃処理
        if (!this.level().isClientSide()) {
            attackCooldown = Math.max(0, attackCooldown - 1);
            specialAttackTimer++;
            summonCooldown = Math.max(0, summonCooldown - 1);
            
            // 特殊攻撃
            if (specialAttackTimer >= getSpecialAttackInterval()) {
                performSpecialAttack();
                specialAttackTimer = 0;
            }
            
            // フェーズ2以降：召喚（頻度増）
            if (phase >= 2 && summonCooldown <= 0) {
                summonMinions();
                summonCooldown = 300; // 15秒
            }
        }
    }
    
    private void updatePhase() {
        float healthPercent = this.getHealth() / this.getMaxHealth();
        
        if (healthPercent <= 0.3F && phase < 3) {
            phase = 3;
            onPhaseChange(3);
        } else if (healthPercent <= 0.6F && phase < 2) {
            phase = 2;
            onPhaseChange(2);
        }
    }
    
    private void onPhaseChange(int newPhase) {
        if (!this.level().isClientSide()) {
            // フェーズ変更時のエフェクト
            for (int i = 0; i < 30; i++) {
                this.level().addParticle(
                    ParticleTypes.SOUL_FIRE_FLAME,
                    this.getX() + (this.random.nextDouble() - 0.5D) * 3.0D,
                    this.getY() + this.random.nextDouble() * 2.5D,
                    this.getZ() + (this.random.nextDouble() - 0.5D) * 3.0D,
                    0, 0.1D, 0
                );
            }
            
            // フェーズ3で回復
            if (newPhase == 3) {
                this.heal(20.0F);
            }
        }
    }
    
    private int getSpecialAttackInterval() {
        return switch (phase) {
            case 1 -> 70;  // 3.5秒（高速化）
            case 2 -> 50;  // 2.5秒
            case 3 -> 35;  // 1.75秒
            default -> 70;
        };
    }
    
    private void performSpecialAttack() {
        if (this.getTarget() == null) return;
        
        int attackType = this.random.nextInt(phase == 3 ? 4 : 3);
        
        switch (attackType) {
            case 0 -> darkPulse();
            case 1 -> eclipseBeam();
            case 2 -> voidTeleport();
            case 3 -> lunarCurse();
        }
    }
    
    /**
     * 闇の波動 - 周囲にダメージ
     */
    private void darkPulse() {
        double range = 6.0D + phase;
        AABB area = this.getBoundingBox().inflate(range);
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, area, 
            e -> e != this && !(e instanceof EclipsePhantomEntity));
        
        for (LivingEntity target : targets) {
            target.hurt(this.damageSources().mobAttack(this), 6.0F + phase * 2);
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, phase - 1));
            
            // ノックバック
            Vec3 knockback = target.position().subtract(this.position()).normalize().scale(1.5D);
            target.setDeltaMovement(target.getDeltaMovement().add(knockback));
        }
        
        // パーティクル
        for (int i = 0; i < 40; i++) {
            double angle = i * Math.PI * 2 / 40;
            this.level().addParticle(
                ParticleTypes.SOUL,
                this.getX() + Math.cos(angle) * range,
                this.getY() + 1.0D,
                this.getZ() + Math.sin(angle) * range,
                0, 0.05D, 0
            );
        }
    }
    
    /**
     * 月蝕ビーム - ターゲットに向かって直線攻撃
     */
    private void eclipseBeam() {
        LivingEntity target = this.getTarget();
        if (target == null) return;
        
        Vec3 direction = target.position().subtract(this.position()).normalize();
        double distance = this.distanceTo(target);
        
        for (double d = 0; d < distance; d += 0.5D) {
            double x = this.getX() + direction.x * d;
            double y = this.getY() + 1.5D + direction.y * d;
            double z = this.getZ() + direction.z * d;
            
            this.level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 0, 0, 0);
            
            // ヒット判定
            AABB hitbox = new AABB(x - 0.5D, y - 0.5D, z - 0.5D, x + 0.5D, y + 0.5D, z + 0.5D);
            List<LivingEntity> hit = this.level().getEntitiesOfClass(LivingEntity.class, hitbox,
                e -> e != this);
            for (LivingEntity entity : hit) {
                entity.hurt(this.damageSources().mobAttack(this), 8.0F + phase * 2);
                entity.addEffect(new MobEffectInstance(AsterRiskModEffects.STARLESS_NIGHT.get(), 200, 0));
            }
        }
    }
    
    /**
     * 虚空テレポート - ターゲットの背後にテレポート
     */
    private void voidTeleport() {
        LivingEntity target = this.getTarget();
        if (target == null) return;
        
        // テレポート元のパーティクル
        for (int i = 0; i < 20; i++) {
            this.level().addParticle(
                ParticleTypes.PORTAL,
                this.getX(), this.getY() + 1.0D, this.getZ(),
                (this.random.nextDouble() - 0.5D) * 2.0D,
                this.random.nextDouble(),
                (this.random.nextDouble() - 0.5D) * 2.0D
            );
        }
        
        // ターゲットの背後に移動
        Vec3 targetLook = target.getLookAngle().normalize();
        double newX = target.getX() - targetLook.x * 3.0D;
        double newY = target.getY();
        double newZ = target.getZ() - targetLook.z * 3.0D;
        
        this.teleportTo(newX, newY, newZ);
        
        // テレポート先のパーティクル
        for (int i = 0; i < 20; i++) {
            this.level().addParticle(
                ParticleTypes.REVERSE_PORTAL,
                this.getX(), this.getY() + 1.0D, this.getZ(),
                (this.random.nextDouble() - 0.5D) * 2.0D,
                this.random.nextDouble(),
                (this.random.nextDouble() - 0.5D) * 2.0D
            );
        }
        
        // すぐに攻撃
        if (this.distanceTo(target) < 4.0D) {
            target.hurt(this.damageSources().mobAttack(this), 10.0F);
        }
    }
    
    /**
     * 月の呪い - ターゲットにデバフ
     */
    private void lunarCurse() {
        LivingEntity target = this.getTarget();
        if (target == null) return;
        
        target.addEffect(new MobEffectInstance(AsterRiskModEffects.LUNAR_ECLIPSE_CURSE.get(), 300, 1));
        target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1));
        
        // パーティクル
        for (int i = 0; i < 15; i++) {
            this.level().addParticle(
                ParticleTypes.ENCHANT,
                target.getX() + (this.random.nextDouble() - 0.5D) * 2.0D,
                target.getY() + this.random.nextDouble() * 2.0D,
                target.getZ() + (this.random.nextDouble() - 0.5D) * 2.0D,
                0, -0.5D, 0
            );
        }
    }
    
    /**
     * ミニオン召喚
     */
    private void summonMinions() {
        if (!(this.level() instanceof ServerLevel serverLevel)) return;
        
        int count = phase;
        for (int i = 0; i < count; i++) {
            double angle = i * Math.PI * 2 / count;
            double x = this.getX() + Math.cos(angle) * 4.0D;
            double z = this.getZ() + Math.sin(angle) * 4.0D;
            
            EclipsePhantomEntity phantom = ModEntities.ECLIPSE_PHANTOM.get().create(serverLevel);
            if (phantom != null) {
                phantom.moveTo(x, this.getY(), z, this.random.nextFloat() * 360F, 0F);
                serverLevel.addFreshEntity(phantom);
                
                // 召喚パーティクル
                for (int j = 0; j < 10; j++) {
                    this.level().addParticle(
                        ParticleTypes.SOUL,
                        x, this.getY() + 1.0D, z,
                        (this.random.nextDouble() - 0.5D) * 0.5D,
                        this.random.nextDouble() * 0.5D,
                        (this.random.nextDouble() - 0.5D) * 0.5D
                    );
                }
            }
        }
    }
    
    private void spawnParticles() {
        particleTimer++;
        if (particleTimer >= 2) {
            particleTimer = 0;
            
            // 常時パーティクル
            this.level().addParticle(
                ParticleTypes.SOUL_FIRE_FLAME,
                this.getRandomX(1.0D),
                this.getRandomY(),
                this.getRandomZ(1.0D),
                0, 0.02D, 0
            );
            
            // フェーズに応じた追加パーティクル
            if (phase >= 2) {
                this.level().addParticle(
                    ParticleTypes.PORTAL,
                    this.getRandomX(0.8D),
                    this.getRandomY(),
                    this.getRandomZ(0.8D),
                    0, 0, 0
                );
            }
            
            if (phase >= 3) {
                this.level().addParticle(
                    ParticleTypes.DRAGON_BREATH,
                    this.getRandomX(0.6D),
                    this.getRandomY(),
                    this.getRandomZ(0.6D),
                    0, 0.01D, 0
                );
            }
        }
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean result = super.doHurtTarget(target);
        
        if (result && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(
                AsterRiskModEffects.LUNAR_ECLIPSE_CURSE.get(),
                100,
                0
            ));
        }
        
        return result;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // フェーズ3では一定確率でダメージ軽減
        if (phase == 3 && this.random.nextFloat() < 0.3F) {
            amount *= 0.5F;
        }
        
        return super.hurt(source, amount);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.VOID_WHISPER.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.BOSS_ATTACK.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.BOSS_DEATH.get();
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
    }

    @Override
    public boolean isPushable() {
        return false;
    }
    
    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
    }
    
    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }
    
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Phase", this.phase);
    }
    
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Phase")) {
            this.phase = tag.getInt("Phase");
        }
    }

    public int getPhase() {
        return this.phase;
    }
    
    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        
        // Eclipse Core - 確定ドロップ 1-2個
        int coreCount = 1 + this.random.nextInt(2) + looting;
        for (int i = 0; i < coreCount; i++) {
            this.spawnAtLocation(AsterRiskModItems.ECLIPSE_CORE.get());
        }
        
        // Meteorite Fragment - 3-5個
        int fragmentCount = 3 + this.random.nextInt(3) + looting;
        for (int i = 0; i < fragmentCount; i++) {
            this.spawnAtLocation(AsterRiskModItems.METEORITE_FRAGMENT.get());
        }
        
        // Shadow Essence - 2-4個
        int essenceCount = 2 + this.random.nextInt(3);
        for (int i = 0; i < essenceCount; i++) {
            this.spawnAtLocation(AsterRiskModItems.SHADOW_ESSENCE.get());
        }
    }
}
