package net.mcreator.asterrisk.entity;

import net.mcreator.asterrisk.registry.ModEntities;
import net.mcreator.asterrisk.init.AsterRiskModItems;
import net.mcreator.asterrisk.init.AsterRiskModEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * 月光の妖精 - Moonlight Fairy
 * 友好的な小さな浮遊Mob。プレイヤーについてきて、
 * 暗闘時に光を提供し、マナ回復を助ける。
 * ルナーダストで懐く。
 */
public class MoonlightFairyEntity extends PathfinderMob {
    
    private static final EntityDataAccessor<Boolean> TAMED = 
        SynchedEntityData.defineId(MoonlightFairyEntity.class, EntityDataSerializers.BOOLEAN);
    
    @Nullable
    private UUID ownerUUID;
    private int particleTimer = 0;
    private int healTimer = 0;

    public MoonlightFairyEntity(EntityType<? extends MoonlightFairyEntity> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TAMED, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 2.0D));
        this.goalSelector.addGoal(2, new FollowOwnerGoal());
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 10.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.35D)
            .add(Attributes.FOLLOW_RANGE, 16.0D)
            .add(Attributes.FLYING_SPEED, 0.5D);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        
        // 浮遊動作
        if (!this.onGround() && this.getDeltaMovement().y < 0) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.5D, 1.0D));
        }
        
        // パーティクル（月光）
        if (this.level().isClientSide()) {
            particleTimer++;
            if (particleTimer >= 3) {
                particleTimer = 0;
                this.level().addParticle(
                    ParticleTypes.END_ROD,
                    this.getRandomX(0.3D),
                    this.getRandomY(),
                    this.getRandomZ(0.3D),
                    0, -0.02D, 0
                );
            }
        }
        
        // 懐いている場合、オーナーにバフを与える
        if (!this.level().isClientSide() && isTamed() && ownerUUID != null) {
            Player owner = this.level().getPlayerByUUID(ownerUUID);
            if (owner != null && this.distanceTo(owner) < 10.0D) {
                healTimer++;
                if (healTimer >= 100) { // 5秒ごと
                    healTimer = 0;
                    
                    // マナ回復
                    owner.getCapability(net.mcreator.asterrisk.mana.LunarManaCapability.LUNAR_MANA).ifPresent(mana -> {
                        mana.addMana(2.0f);
                    });
                    
                    // 夜間は月光の祝福を付与
                    long dayTime = this.level().getDayTime() % 24000;
                    boolean isNight = dayTime >= 13000 && dayTime < 23000;
                    if (isNight) {
                        owner.addEffect(new MobEffectInstance(
                            AsterRiskModEffects.LUNAR_BLESSING.get(),
                            20 * 10, // 10秒
                            0
                        ));
                    }
                }
            }
        }
    }

    public boolean isTamed() {
        return this.entityData.get(TAMED);
    }

    public void setTamed(boolean tamed) {
        this.entityData.set(TAMED, tamed);
    }

    @Nullable
    public UUID getOwnerUUID() {
        return this.ownerUUID;
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.ownerUUID = uuid;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Tamed", this.isTamed());
        if (this.ownerUUID != null) {
            tag.putUUID("Owner", this.ownerUUID);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setTamed(tag.getBoolean("Tamed"));
        if (tag.hasUUID("Owner")) {
            this.ownerUUID = tag.getUUID("Owner");
        }
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ALLAY_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ALLAY_DEATH;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        // ルナーダストで懐かせる
        if (stack.is(AsterRiskModItems.LUNAR_DUST.get()) && !isTamed()) {
            if (!this.level().isClientSide()) {
                if (this.random.nextFloat() < 0.33F) { // 33%の確率で懐く
                    this.setTamed(true);
                    this.setOwnerUUID(player.getUUID());
                    
                    // ハートのパーティクル
                    for (int i = 0; i < 7; i++) {
                        this.level().addParticle(
                            ParticleTypes.HEART,
                            this.getRandomX(1.0D),
                            this.getRandomY() + 0.5D,
                            this.getRandomZ(1.0D),
                            0, 0, 0
                        );
                    }
                } else {
                    // 煙のパーティクル（失敗）
                    for (int i = 0; i < 3; i++) {
                        this.level().addParticle(
                            ParticleTypes.SMOKE,
                            this.getRandomX(0.5D),
                            this.getRandomY() + 0.5D,
                            this.getRandomZ(0.5D),
                            0, 0.05D, 0
                        );
                    }
                }
                
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }
        
        // 懐いている妖精にスターダストを与えると一時的に強化
        if (stack.is(AsterRiskModItems.STARDUST.get()) && isTamed()) {
            if (!this.level().isClientSide()) {
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 20 * 60 * 3, 0)); // 3分
                player.addEffect(new MobEffectInstance(AsterRiskModEffects.LUNAR_BLESSING.get(), 20 * 60, 1)); // 1分、レベル2
                
                for (int i = 0; i < 15; i++) {
                    this.level().addParticle(
                        ParticleTypes.END_ROD,
                        this.getX(), this.getY() + 0.5D, this.getZ(),
                        (this.random.nextDouble() - 0.5D) * 0.5D,
                        this.random.nextDouble() * 0.3D,
                        (this.random.nextDouble() - 0.5D) * 0.5D
                    );
                }
                
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }
        
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    /**
     * オーナーを追従するゴール
     */
    private class FollowOwnerGoal extends Goal {
        @Override
        public boolean canUse() {
            if (!isTamed() || ownerUUID == null) return false;
            Player owner = level().getPlayerByUUID(ownerUUID);
            return owner != null && distanceTo(owner) > 4.0D;
        }

        @Override
        public void tick() {
            if (ownerUUID == null) return;
            Player owner = level().getPlayerByUUID(ownerUUID);
            if (owner == null) return;
            
            if (distanceTo(owner) > 4.0D) {
                getNavigation().moveTo(owner, 1.2D);
            }
            
            // 離れすぎたらテレポート
            if (distanceTo(owner) > 20.0D) {
                teleportTo(owner.getX(), owner.getY() + 1.0D, owner.getZ());
            }
        }
    }
}
