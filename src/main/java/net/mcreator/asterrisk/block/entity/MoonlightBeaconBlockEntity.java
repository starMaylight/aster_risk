package net.mcreator.asterrisk.block.entity;

import net.mcreator.asterrisk.mana.BlockManaCapability;
import net.mcreator.asterrisk.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * 月光ビーコン
 * マナを消費して範囲内のプレイヤーにバフ効果を付与
 */
public class MoonlightBeaconBlockEntity extends BlockEntity {

    // マナ設定
    public static final float MAX_MANA = 1000f;
    public static final float RECEIVE_RATE = 50f;
    public static final float MANA_PER_TICK = 0.5f; // 1秒あたり10マナ消費

    // 効果範囲
    public static final int EFFECT_RADIUS = 16;
    public static final int EFFECT_DURATION = 220; // 11秒（10秒 + バッファ）

    private final BlockManaCapability.BlockMana manaStorage;
    private final LazyOptional<BlockManaCapability.IBlockMana> manaHandler;

    private boolean active = false;
    private int tickCounter = 0;

    public MoonlightBeaconBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.manaStorage = new BlockManaCapability.BlockMana(MAX_MANA, RECEIVE_RATE, 0, true, false);
        this.manaHandler = LazyOptional.of(() -> manaStorage);
    }

    public static MoonlightBeaconBlockEntity create(BlockPos pos, BlockState state) {
        return new MoonlightBeaconBlockEntity(ModBlockEntities.MOONLIGHT_BEACON.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MoonlightBeaconBlockEntity entity) {
        if (level.isClientSide()) return;

        entity.tickCounter++;

        // マナがあればアクティブ
        boolean wasActive = entity.active;
        entity.active = entity.manaStorage.getMana() >= MANA_PER_TICK;

        if (entity.active) {
            // マナ消費
            entity.manaStorage.setMana(entity.manaStorage.getMana() - MANA_PER_TICK);

            // 2秒ごとに効果付与（40tick）
            if (entity.tickCounter % 40 == 0) {
                entity.applyEffectsToNearbyPlayers();
            }

            // パーティクル（10tickごと）
            if (entity.tickCounter % 10 == 0 && level instanceof ServerLevel serverLevel) {
                entity.spawnBeaconParticles(serverLevel);
            }
        }

        // 状態変更時に同期
        if (wasActive != entity.active) {
            entity.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
        }
    }

    private void applyEffectsToNearbyPlayers() {
        if (level == null) return;

        AABB area = new AABB(worldPosition).inflate(EFFECT_RADIUS);
        List<Player> players = level.getEntitiesOfClass(Player.class, area);

        for (Player player : players) {
            // 暗視
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, EFFECT_DURATION, 0, true, false));
            
            // 再生（弱）
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, EFFECT_DURATION, 0, true, false));
            
            // 月の満ち欠けによる追加効果
            if (level instanceof ServerLevel serverLevel) {
                int moonPhase = serverLevel.getMoonPhase();
                if (moonPhase == 0) { // 満月
                    // 満月時は強化効果
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, EFFECT_DURATION, 0, true, false));
                }
            }
        }
    }

    private void spawnBeaconParticles(ServerLevel level) {
        double x = worldPosition.getX() + 0.5;
        double y = worldPosition.getY() + 1.0;
        double z = worldPosition.getZ() + 0.5;

        // 上向きの光線パーティクル
        for (int i = 0; i < 3; i++) {
            level.sendParticles(ParticleTypes.END_ROD,
                x, y + i * 0.5, z,
                1, 0.1, 0.1, 0.1, 0.01);
        }

        // 周囲の光パーティクル
        level.sendParticles(ParticleTypes.ENCHANT,
            x, y + 0.5, z,
            5, 0.3, 0.2, 0.3, 0.1);
    }

    public float getMana() {
        return manaStorage.getMana();
    }

    public float getMaxMana() {
        return manaStorage.getMaxMana();
    }

    public boolean isActive() {
        return active;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("manaStorage", manaStorage.serializeNBT());
        tag.putBoolean("active", active);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("manaStorage")) {
            manaStorage.deserializeNBT(tag.getCompound("manaStorage"));
        }
        active = tag.getBoolean("active");
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putFloat("mana", manaStorage.getMana());
        tag.putBoolean("active", active);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        if (tag.contains("mana")) {
            manaStorage.setMana(tag.getFloat("mana"));
        }
        active = tag.getBoolean("active");
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == BlockManaCapability.BLOCK_MANA) {
            return manaHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        manaHandler.invalidate();
    }
}
