package net.mcreator.asterrisk.entity;

import net.mcreator.asterrisk.init.AsterRiskModEffects;
import net.mcreator.asterrisk.init.AsterRiskModItems;
import net.mcreator.asterrisk.registry.ModEntities;
import net.minecraft.world.entity.EntityType;
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
import net.minecraft.core.BlockPos;

import java.util.List;

/**
 * 星喰らい - Star Devourer
 * 宇宙の深淵から来た巨大な存在。
 * 星の力を吸収し、強力な重力攻撃を行う。
 */
public class StarDevourerEntity extends Monster {
    
    private final ServerBossEvent bossEvent = new ServerBossEvent(
        Component.translatable("entity.aster_risk.star_devourer"),
        BossEvent.BossBarColor.BLUE,
        BossEvent.BossBarOverlay.NOTCHED_12
    );
    
    private int attackCooldown = 0;
    private int specialAttackTimer = 0;
    private int phase = 1;
    private int particleTimer = 0;
    private int gravityPullTimer = 0;
    private int meteorTimer = 0;
    private boolean isCharging = false;
    private int chargeTimer = 0;

    public StarDevourerEntity(EntityType<? extends StarDevourerEntity> type, Level level) {
        super(type, level);
        this.xpReward = 800;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 0.8D, false));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 20.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 500.0D)  // ボスはHP維持
            .add(Attributes.MOVEMENT_SPEED, 0.55D)  // 速度増
            .add(Attributes.ATTACK_DAMAGE, 50.0D)  // 攻撃力増
            .add(Attributes.FOLLOW_RANGE, 64.0D)
            .add(Attributes.ARMOR, 10.0D)  // 防御弱体化
            .add(Attributes.ARMOR_TOUGHNESS, 6.0D)
            .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        
        updatePhase();
        
        if (this.level().isClientSide()) {
            spawnParticles();
        }
        
        if (!this.level().isClientSide()) {
            attackCooldown = Math.max(0, attackCooldown - 1);
            specialAttackTimer++;
            gravityPullTimer++;
            meteorTimer++;
            
            // チャージ中の処理
            if (isCharging) {
                chargeTimer++;
                if (chargeTimer >= 60) {
                    releaseChargedAttack();
                    isCharging = false;
                    chargeTimer = 0;
                }
            }
            
            // 常時重力引き寄せ（フェーズ2以降）
            if (phase >= 2 && gravityPullTimer >= 20) {
                passiveGravityPull();
                gravityPullTimer = 0;
            }
            
            // 隕石召喚（フェーズ3）
            if (phase >= 3 && meteorTimer >= 100) {
                summonMeteors();
                meteorTimer = 0;
            }
            
            // 特殊攻撃
            if (specialAttackTimer >= getSpecialAttackInterval() && !isCharging) {
                performSpecialAttack();
                specialAttackTimer = 0;
            }
        }
    }
    
    private void updatePhase() {
        float healthPercent = this.getHealth() / this.getMaxHealth();
        
        if (healthPercent <= 0.25F && phase < 3) {
            phase = 3;
            onPhaseChange(3);
        } else if (healthPercent <= 0.55F && phase < 2) {
            phase = 2;
            onPhaseChange(2);
        }
    }
    
    private void onPhaseChange(int newPhase) {
        if (!this.level().isClientSide()) {
            // 衝撃波
            AABB area = this.getBoundingBox().inflate(10.0D);
            List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, area, e -> e != this);
            
            for (LivingEntity target : targets) {
                Vec3 knockback = target.position().subtract(this.position()).normalize().scale(2.0D);
                target.setDeltaMovement(target.getDeltaMovement().add(knockback.x, 0.5D, knockback.z));
                target.hurt(this.damageSources().mobAttack(this), 8.0F);
            }
            
            // パーティクル
            for (int i = 0; i < 50; i++) {
                double angle = i * Math.PI * 2 / 50;
                for (int r = 1; r <= 10; r++) {
                    this.level().addParticle(
                        ParticleTypes.END_ROD,
                        this.getX() + Math.cos(angle) * r,
                        this.getY() + 1.5D,
                        this.getZ() + Math.sin(angle) * r,
                        0, 0.1D, 0
                    );
                }
            }
            
            // フェーズ3で強化
            if (newPhase == 3) {
                this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 1));
                this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 400, 0));
            }
        }
    }
    
    private int getSpecialAttackInterval() {
        return switch (phase) {
            case 1 -> 60;  // 3秒（高速化）
            case 2 -> 40;  // 2秒
            case 3 -> 25;  // 1.25秒
            default -> 80;
        };
    }
    
    private void performSpecialAttack() {
        if (this.getTarget() == null) return;
        
        int attackType = this.random.nextInt(phase == 3 ? 5 : (phase == 2 ? 4 : 3));
        
        switch (attackType) {
            case 0 -> gravitySlam();
            case 1 -> starVortex();
            case 2 -> voidBreath();
            case 3 -> startCharging();
            case 4 -> blackHole();
        }
    }
    
    /**
     * 重力スラム - 地面を叩きつけて周囲にダメージ
     */
    private void gravitySlam() {
        double range = 8.0D;
        AABB area = this.getBoundingBox().inflate(range);
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, area, e -> e != this);
        
        for (LivingEntity target : targets) {
            double distance = this.distanceTo(target);
            float damage = (float)(12.0D * (1.0D - distance / range)) + phase * 2;
            target.hurt(this.damageSources().mobAttack(this), damage);
            target.setDeltaMovement(target.getDeltaMovement().add(0, -0.5D, 0));
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 2));
        }
        
        // 地面のパーティクル
        for (int i = 0; i < 60; i++) {
            double angle = i * Math.PI * 2 / 60;
            this.level().addParticle(
                ParticleTypes.CAMPFIRE_COSY_SMOKE,
                this.getX() + Math.cos(angle) * range * this.random.nextDouble(),
                this.getY(),
                this.getZ() + Math.sin(angle) * range * this.random.nextDouble(),
                0, 0.2D, 0
            );
        }
    }
    
    /**
     * 星の渦 - 周囲のエンティティを引き寄せる
     */
    private void starVortex() {
        double range = 12.0D;
        AABB area = this.getBoundingBox().inflate(range);
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, area, e -> e != this);
        
        for (LivingEntity target : targets) {
            Vec3 pull = this.position().subtract(target.position()).normalize().scale(0.8D);
            target.setDeltaMovement(target.getDeltaMovement().add(pull));
            target.addEffect(new MobEffectInstance(AsterRiskModEffects.STARLESS_NIGHT.get(), 100, 0));
        }
        
        // 渦のパーティクル
        for (int i = 0; i < 40; i++) {
            double angle = i * Math.PI * 2 / 40 + this.tickCount * 0.1D;
            double radius = 2.0D + (i % 10) * 0.8D;
            this.level().addParticle(
                ParticleTypes.END_ROD,
                this.getX() + Math.cos(angle) * radius,
                this.getY() + 1.0D + (i % 5) * 0.5D,
                this.getZ() + Math.sin(angle) * radius,
                -Math.cos(angle) * 0.2D, 0, -Math.sin(angle) * 0.2D
            );
        }
    }
    
    /**
     * 虚空のブレス - 前方に扇状のダメージ
     */
    private void voidBreath() {
        LivingEntity target = this.getTarget();
        if (target == null) return;
        
        Vec3 direction = target.position().subtract(this.position()).normalize();
        double baseAngle = Math.atan2(direction.z, direction.x);
        
        for (int i = -3; i <= 3; i++) {
            double angle = baseAngle + i * Math.PI / 12;
            for (double d = 1; d <= 10; d += 0.5D) {
                double x = this.getX() + Math.cos(angle) * d;
                double z = this.getZ() + Math.sin(angle) * d;
                
                this.level().addParticle(
                    ParticleTypes.DRAGON_BREATH,
                    x, this.getY() + 1.5D, z,
                    Math.cos(angle) * 0.1D, 0, Math.sin(angle) * 0.1D
                );
                
                AABB hitbox = new AABB(x - 0.5D, this.getY(), z - 0.5D, x + 0.5D, this.getY() + 3.0D, z + 0.5D);
                List<LivingEntity> hit = this.level().getEntitiesOfClass(LivingEntity.class, hitbox, e -> e != this);
                for (LivingEntity entity : hit) {
                    entity.hurt(this.damageSources().mobAttack(this), 6.0F + phase);
                    entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 0));
                }
            }
        }
    }
    
    /**
     * チャージ開始 - 強力な攻撃の溜め
     */
    private void startCharging() {
        isCharging = true;
        chargeTimer = 0;
        
        // チャージ開始パーティクル
        for (int i = 0; i < 20; i++) {
            this.level().addParticle(
                ParticleTypes.WITCH,
                this.getRandomX(1.5D),
                this.getRandomY(),
                this.getRandomZ(1.5D),
                0, 0.1D, 0
            );
        }
    }
    
    /**
     * チャージ攻撃解放
     */
    private void releaseChargedAttack() {
        double range = 15.0D;
        AABB area = this.getBoundingBox().inflate(range);
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, area, e -> e != this);
        
        for (LivingEntity target : targets) {
            target.hurt(this.damageSources().mobAttack(this), 20.0F);
            target.addEffect(new MobEffectInstance(AsterRiskModEffects.LUNAR_ECLIPSE_CURSE.get(), 200, 1));
            target.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 40, 1));
            
            Vec3 knockback = target.position().subtract(this.position()).normalize().scale(3.0D);
            target.setDeltaMovement(knockback.x, 1.0D, knockback.z);
        }
        
        // 大爆発パーティクル
        for (int i = 0; i < 100; i++) {
            double angle = this.random.nextDouble() * Math.PI * 2;
            double radius = this.random.nextDouble() * range;
            this.level().addParticle(
                ParticleTypes.EXPLOSION,
                this.getX() + Math.cos(angle) * radius,
                this.getY() + this.random.nextDouble() * 3.0D,
                this.getZ() + Math.sin(angle) * radius,
                0, 0.1D, 0
            );
        }
    }
    
    /**
     * ブラックホール - フェーズ3専用
     */
    private void blackHole() {
        LivingEntity target = this.getTarget();
        if (target == null) return;
        
        Vec3 pos = target.position();
        
        // 5秒間持続する重力場を作成
        for (int tick = 0; tick < 100; tick += 5) {
            // この処理はシンプル化のためtickごとではなく即時で近くの敵にダメージ
        }
        
        double range = 8.0D;
        AABB area = new AABB(pos.x - range, pos.y - range, pos.z - range, 
                            pos.x + range, pos.y + range, pos.z + range);
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, area, e -> e != this);
        
        for (LivingEntity entity : targets) {
            Vec3 pull = pos.subtract(entity.position()).normalize().scale(1.5D);
            entity.setDeltaMovement(pull);
            entity.hurt(this.damageSources().mobAttack(this), 4.0F);
        }
        
        // ブラックホールパーティクル
        for (int i = 0; i < 50; i++) {
            double angle = i * Math.PI * 2 / 50;
            double radius = 3.0D + this.random.nextDouble() * 5.0D;
            this.level().addParticle(
                ParticleTypes.PORTAL,
                pos.x + Math.cos(angle) * radius,
                pos.y + 1.0D,
                pos.z + Math.sin(angle) * radius,
                -Math.cos(angle) * 0.5D, 0, -Math.sin(angle) * 0.5D
            );
        }
        
        // 中心の闇
        for (int i = 0; i < 20; i++) {
            this.level().addParticle(
                ParticleTypes.SQUID_INK,
                pos.x + (this.random.nextDouble() - 0.5D) * 2.0D,
                pos.y + 1.0D + (this.random.nextDouble() - 0.5D) * 2.0D,
                pos.z + (this.random.nextDouble() - 0.5D) * 2.0D,
                0, 0, 0
            );
        }
    }
    
    /**
     * パッシブ重力引き寄せ
     */
    private void passiveGravityPull() {
        double range = 16.0D;
        AABB area = this.getBoundingBox().inflate(range);
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, area, 
            e -> e != this && e instanceof Player);
        
        for (LivingEntity target : targets) {
            double distance = this.distanceTo(target);
            double strength = 0.1D * (1.0D - distance / range) * phase;
            Vec3 pull = this.position().subtract(target.position()).normalize().scale(strength);
            target.setDeltaMovement(target.getDeltaMovement().add(pull));
        }
    }
    
    /**
     * 隕石召喚
     */
    private void summonMeteors() {
        LivingEntity target = this.getTarget();
        if (target == null) return;
        
        for (int i = 0; i < 3; i++) {
            double offsetX = (this.random.nextDouble() - 0.5D) * 10.0D;
            double offsetZ = (this.random.nextDouble() - 0.5D) * 10.0D;
            double x = target.getX() + offsetX;
            double z = target.getZ() + offsetZ;
            
            // 落下警告パーティクル
            for (int j = 0; j < 10; j++) {
                this.level().addParticle(
                    ParticleTypes.FLAME,
                    x, target.getY() + 10.0D - j, z,
                    0, -0.5D, 0
                );
            }
            
            // 着弾ダメージ
            AABB impactArea = new AABB(x - 2, target.getY() - 1, z - 2, x + 2, target.getY() + 3, z + 2);
            List<LivingEntity> hit = this.level().getEntitiesOfClass(LivingEntity.class, impactArea, e -> e != this);
            for (LivingEntity entity : hit) {
                entity.hurt(this.damageSources().mobAttack(this), 10.0F);
                entity.setSecondsOnFire(3);
            }
            
            // 爆発パーティクル
            this.level().addParticle(ParticleTypes.EXPLOSION_EMITTER, x, target.getY(), z, 0, 0, 0);
        }
    }
    
    private void spawnParticles() {
        particleTimer++;
        if (particleTimer >= 2) {
            particleTimer = 0;
            
            // 体を覆う星のパーティクル
            this.level().addParticle(
                ParticleTypes.END_ROD,
                this.getRandomX(1.2D),
                this.getRandomY(),
                this.getRandomZ(1.2D),
                (this.random.nextDouble() - 0.5D) * 0.05D,
                this.random.nextDouble() * 0.05D,
                (this.random.nextDouble() - 0.5D) * 0.05D
            );
            
            // チャージ中は追加パーティクル
            if (isCharging) {
                for (int i = 0; i < 5; i++) {
                    double angle = this.random.nextDouble() * Math.PI * 2;
                    double radius = 2.0D + this.random.nextDouble() * 3.0D;
                    this.level().addParticle(
                        ParticleTypes.ENCHANT,
                        this.getX() + Math.cos(angle) * radius,
                        this.getY() + 2.0D,
                        this.getZ() + Math.sin(angle) * radius,
                        -Math.cos(angle) * 0.3D,
                        0.2D,
                        -Math.sin(angle) * 0.3D
                    );
                }
            }
            
            if (phase >= 2) {
                this.level().addParticle(
                    ParticleTypes.REVERSE_PORTAL,
                    this.getRandomX(0.8D),
                    this.getRandomY(),
                    this.getRandomZ(0.8D),
                    0, -0.05D, 0
                );
            }
            
            if (phase >= 3) {
                this.level().addParticle(
                    ParticleTypes.SOUL_FIRE_FLAME,
                    this.getRandomX(1.0D),
                    this.getRandomY(),
                    this.getRandomZ(1.0D),
                    0, 0.02D, 0
                );
            }
        }
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean result = super.doHurtTarget(target);
        
        if (result && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
            if (phase >= 2) {
                living.addEffect(new MobEffectInstance(AsterRiskModEffects.STARLESS_NIGHT.get(), 100, 0));
            }
        }
        
        return result;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // チャージ中はダメージ軽減
        if (isCharging) {
            amount *= 0.5F;
        }
        
        // フェーズ3では追加軽減
        if (phase == 3 && this.random.nextFloat() < 0.25F) {
            amount *= 0.6F;
        }
        
        return super.hurt(source, amount);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.STELLAR_MAGIC.get();
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
        tag.putBoolean("IsCharging", this.isCharging);
        tag.putInt("ChargeTimer", this.chargeTimer);
    }
    
    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Phase")) {
            this.phase = tag.getInt("Phase");
        }
        if (tag.contains("IsCharging")) {
            this.isCharging = tag.getBoolean("IsCharging");
        }
        if (tag.contains("ChargeTimer")) {
            this.chargeTimer = tag.getInt("ChargeTimer");
        }
    }

    public int getPhase() {
        return this.phase;
    }
    
    public boolean isCharging() {
        return this.isCharging;
    }
    
    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        
        // Stellar Heart - 確定ドロップ 1-2個
        int heartCount = 1 + this.random.nextInt(2) + looting;
        for (int i = 0; i < heartCount; i++) {
            this.spawnAtLocation(AsterRiskModItems.STELLAR_HEART.get());
        }
        
        // Star Fragment - 3-5個
        int fragmentCount = 3 + this.random.nextInt(3) + looting;
        for (int i = 0; i < fragmentCount; i++) {
            this.spawnAtLocation(AsterRiskModItems.STARFLAGMENT.get());
        }
        
        // Stardust - 5-10個
        int dustCount = 5 + this.random.nextInt(6) + looting;
        for (int i = 0; i < dustCount; i++) {
            this.spawnAtLocation(AsterRiskModItems.STARDUST.get());
        }
    }
}
