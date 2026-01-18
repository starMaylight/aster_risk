package net.mcreator.asterrisk.entity;

import net.mcreator.asterrisk.AsterRiskMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;

/**
 * 天罰の鉄槌で召喚される金床エンティティ
 * 落下後に消滅し、ブロックを設置しない
 */
public class HeavenlyAnvilEntity extends Entity {
    
    private static final EntityDataAccessor<Integer> DATA_TICK = SynchedEntityData.defineId(
        HeavenlyAnvilEntity.class, EntityDataSerializers.INT);
    
    private static final float DAMAGE = 40.0f;
    private static final float GRAVITY = 0.04f;
    private int lifetime = 0;
    private boolean hasLanded = false;
    
    public HeavenlyAnvilEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.blocksBuilding = true;
    }
    
    public HeavenlyAnvilEntity(Level level, double x, double y, double z) {
        this(ModEntityTypes.HEAVENLY_ANVIL.get(), level);
        this.setPos(x, y, z);
        this.setDeltaMovement(Vec3.ZERO);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }
    
    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_TICK, 0);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        lifetime++;
        
        // 最大生存時間（10秒）
        if (lifetime > 200) {
            this.discard();
            return;
        }
        
        // 重力適用
        Vec3 motion = this.getDeltaMovement();
        this.setDeltaMovement(motion.x, motion.y - GRAVITY, motion.z);
        
        // 移動
        this.move(MoverType.SELF, this.getDeltaMovement());
        
        // 地面との衝突チェック
        if (this.onGround() && !hasLanded) {
            onLand();
        }
        
        // ブロックとの衝突チェック
        BlockPos belowPos = this.blockPosition().below();
        BlockState belowState = this.level().getBlockState(belowPos);
        if (!belowState.isAir() && !hasLanded && this.getDeltaMovement().y <= 0) {
            BlockPos currentPos = this.blockPosition();
            BlockState currentState = this.level().getBlockState(currentPos);
            if (!currentState.isAir() || this.position().y - belowPos.getY() < 1.1) {
                onLand();
            }
        }
        
        // 落下中のパーティクル
        if (!hasLanded && this.level() instanceof ServerLevel serverLevel) {
            if (lifetime % 2 == 0) {
                serverLevel.sendParticles(ParticleTypes.CLOUD,
                    this.getX(), this.getY() + 0.5, this.getZ(),
                    1, 0.1, 0.1, 0.1, 0.01);
            }
        }
    }
    
    private void onLand() {
        if (hasLanded) return;
        hasLanded = true;
        
        if (!this.level().isClientSide) {
            // 周囲のエンティティにダメージ
            AABB damageArea = new AABB(
                this.getX() - 1.5, this.getY() - 0.5, this.getZ() - 1.5,
                this.getX() + 1.5, this.getY() + 1.5, this.getZ() + 1.5
            );
            
            List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, damageArea);
            for (LivingEntity entity : entities) {
                // 防御無視のダメージ
                entity.hurt(this.damageSources().anvil(this), DAMAGE);
            }
            
            // エフェクト
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.CLOUD,
                    this.getX(), this.getY(), this.getZ(),
                    20, 0.5, 0.3, 0.5, 0.05);
                serverLevel.sendParticles(ParticleTypes.CRIT,
                    this.getX(), this.getY(), this.getZ(),
                    15, 0.5, 0.3, 0.5, 0.2);
            }
            
            // サウンド
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
        
        // 消滅
        this.discard();
    }
    
    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.lifetime = tag.getInt("Lifetime");
        this.hasLanded = tag.getBoolean("HasLanded");
    }
    
    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Lifetime", this.lifetime);
        tag.putBoolean("HasLanded", this.hasLanded);
    }
    
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
    
    @Override
    public boolean isPickable() {
        return false;
    }
    
    @Override
    public boolean isPushable() {
        return false;
    }
    
    /**
     * 金床の見た目を取得（レンダラー用）
     */
    public BlockState getBlockState() {
        return Blocks.ANVIL.defaultBlockState();
    }
}
