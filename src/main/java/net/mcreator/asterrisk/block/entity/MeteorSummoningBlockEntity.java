package net.mcreator.asterrisk.block.entity;

import net.mcreator.asterrisk.block.MeteorSummoningBlock;
import net.mcreator.asterrisk.init.AsterRiskModItems;
import net.mcreator.asterrisk.item.MeteorSummonCoreItem;
import net.mcreator.asterrisk.mana.BlockManaCapability;
import net.mcreator.asterrisk.registry.ModBlockEntities;
import net.mcreator.asterrisk.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.mcreator.asterrisk.registry.ModParticles;
import net.mcreator.asterrisk.registry.ModSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

/**
 * 流星召喚陣のBlockEntity
 */
public class MeteorSummoningBlockEntity extends BlockEntity {

    public static final float MAX_MANA = 2000f;
    public static final float RECEIVE_RATE = 50f;
    
    // 各流星タイプのマナコスト
    private static final float SMALL_COST = 300f;
    private static final float STARDUST_COST = 600f;
    private static final float PRISMATIC_COST = 1500f;
    private static final float OMINOUS_COST = 1000f;

    private final BlockManaCapability.BlockMana manaStorage;
    private final LazyOptional<BlockManaCapability.IBlockMana> manaHandler;

    private ItemStack core = ItemStack.EMPTY;
    private int summonProgress = 0;
    private int summonTime = 0;
    private boolean summoning = false;
    
    private final Random random = new Random();

    public MeteorSummoningBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.METEOR_SUMMONING.get(), pos, state);
        this.manaStorage = new BlockManaCapability.BlockMana(MAX_MANA, RECEIVE_RATE, 0, true, false);
        this.manaHandler = LazyOptional.of(() -> manaStorage);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MeteorSummoningBlockEntity entity) {
        if (level.isClientSide()) return;

        boolean wasActive = state.getValue(MeteorSummoningBlock.ACTIVE);

        // 召喚開始条件チェック
        if (!entity.summoning && !entity.core.isEmpty() && entity.canStartSummoning(level)) {
            entity.startSummoning();
        }

        // 召喚中
        if (entity.summoning) {
            float manaPerTick = entity.getManaCost() / entity.summonTime;
            if (entity.manaStorage.getMana() >= manaPerTick) {
                entity.manaStorage.setMana(entity.manaStorage.getMana() - manaPerTick);
                entity.summonProgress++;

                // パーティクル
                if (entity.summonProgress % 5 == 0 && level instanceof ServerLevel serverLevel) {
                    double x = pos.getX() + 0.5;
                    double y = pos.getY() + 1.0;
                    double z = pos.getZ() + 0.5;
                    
                    // 儀式ルーン
                    float rotation = entity.summonProgress * 0.1f;
                    for (int i = 0; i < 8; i++) {
                        double angle = rotation + (i / 8.0) * Math.PI * 2;
                        double rx = x + Math.cos(angle) * 2.0;
                        double rz = z + Math.sin(angle) * 2.0;
                        serverLevel.sendParticles(ModParticles.RITUAL_RUNE.get(), rx, y + 0.1, rz, 1, 0, 0.02, 0, 0.01);
                    }
                    
                    serverLevel.sendParticles(ModParticles.STARDUST_SPARKLE.get(), x, y, z, 5, 0.5, 0.3, 0.5, 0.02);
                    serverLevel.sendParticles(ParticleTypes.PORTAL, x, y, z, 5, 0.5, 0.5, 0.5, 0.1);
                    
                    // 上空にもパーティクル（流星の軌跡）
                    if (entity.summonProgress % 20 == 0) {
                        serverLevel.sendParticles(ModParticles.METEOR_TRAIL.get(), x, y + 15 + entity.random.nextInt(15), z, 
                            3, 3, 0, 3, 0.05);
                        serverLevel.sendParticles(ParticleTypes.END_ROD, x, y + 10 + entity.random.nextInt(20), z, 
                            3, 2, 0, 2, 0.05);
                        
                        // サウンド
                        level.playSound(null, pos, ModSounds.METEOR_FALL.get(), SoundSource.BLOCKS, 0.5f, 0.8f + entity.random.nextFloat() * 0.4f);
                    }
                }

                // 完了
                if (entity.summonProgress >= entity.summonTime) {
                    entity.completeSummoning(pos);
                }
            }
        }

        // ブロック状態更新
        boolean shouldBeActive = entity.summoning;
        if (wasActive != shouldBeActive) {
            level.setBlock(pos, state.setValue(MeteorSummoningBlock.ACTIVE, shouldBeActive), 3);
        }

        entity.setChanged();
    }

    private boolean canStartSummoning(Level level) {
        // 夜間のみ
        long dayTime = level.getDayTime() % 24000;
        return dayTime >= 13000 && dayTime <= 23000;
    }

    private void startSummoning() {
        if (!(core.getItem() instanceof MeteorSummonCoreItem coreItem)) return;
        
        summoning = true;
        summonProgress = 0;
        
        // タイプによって召喚時間が異なる
        summonTime = switch (coreItem.getMeteorType()) {
            case SMALL -> 100;      // 5秒
            case STARDUST -> 150;   // 7.5秒
            case PRISMATIC -> 200;  // 10秒
            case OMINOUS -> 180;    // 9秒
        };
    }

    private float getManaCost() {
        if (!(core.getItem() instanceof MeteorSummonCoreItem coreItem)) return 0;
        
        return switch (coreItem.getMeteorType()) {
            case SMALL -> SMALL_COST;
            case STARDUST -> STARDUST_COST;
            case PRISMATIC -> PRISMATIC_COST;
            case OMINOUS -> OMINOUS_COST;
        };
    }

    private void completeSummoning(BlockPos pos) {
        if (level == null || !(level instanceof ServerLevel serverLevel)) return;
        if (!(core.getItem() instanceof MeteorSummonCoreItem coreItem)) return;

        MeteorSummonCoreItem.MeteorType type = coreItem.getMeteorType();
        
        // コア消費
        core = ItemStack.EMPTY;
        
        // 落下位置（少しランダム）
        double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 4;
        double y = pos.getY() + 1.0;
        double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 4;

        // エフェクト
        serverLevel.playSound(null, pos, ModSounds.METEOR_IMPACT.get(), SoundSource.WEATHER, 2.0f, 0.8f);
        serverLevel.playSound(null, pos, ModSounds.RITUAL_COMPLETE.get(), SoundSource.BLOCKS, 1.5f, 1.0f);
        serverLevel.sendParticles(ModParticles.STAR_BURST.get(), x, y + 2, z, 30, 1, 1, 1, 0.15);
        serverLevel.sendParticles(ModParticles.METEOR_TRAIL.get(), x, y + 3, z, 20, 0.5, 0.5, 0.5, 0.1);
        serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, x, y + 5, z, 2, 1, 1, 1, 0);
        
        // ドロップアイテム
        switch (type) {
            case SMALL -> {
                dropItem(serverLevel, x, y, z, new ItemStack(AsterRiskModItems.METEORITE_FRAGMENT.get(), 3 + random.nextInt(4)));
                if (random.nextFloat() < 0.3f) {
                    dropItem(serverLevel, x, y, z, new ItemStack(AsterRiskModItems.STARDUST.get(), 1 + random.nextInt(2)));
                }
            }
            case STARDUST -> {
                dropItem(serverLevel, x, y, z, new ItemStack(AsterRiskModItems.STARDUST.get(), 8 + random.nextInt(8)));
                dropItem(serverLevel, x, y, z, new ItemStack(AsterRiskModItems.METEORITE_FRAGMENT.get(), 2 + random.nextInt(3)));
                if (random.nextFloat() < 0.2f) {
                    dropItem(serverLevel, x, y, z, new ItemStack(AsterRiskModItems.STARFLAGMENT.get(), 1));
                }
            }
            case PRISMATIC -> {
                dropItem(serverLevel, x, y, z, new ItemStack(AsterRiskModItems.PRISMATIC_METEORITE.get(), 1 + random.nextInt(2)));
                dropItem(serverLevel, x, y, z, new ItemStack(AsterRiskModItems.STARFLAGMENT.get(), 2 + random.nextInt(3)));
                dropItem(serverLevel, x, y, z, new ItemStack(AsterRiskModItems.STARDUST.get(), 5 + random.nextInt(5)));
                // 虹色のパーティクル
                for (int i = 0; i < 50; i++) {
                    serverLevel.sendParticles(ParticleTypes.END_ROD, 
                        x + (random.nextDouble() - 0.5) * 3, 
                        y + random.nextDouble() * 3, 
                        z + (random.nextDouble() - 0.5) * 3, 
                        1, 0, 0, 0, 0.1);
                }
            }
            case OMINOUS -> {
                // 敵Mobスポーン
                for (int i = 0; i < 3 + random.nextInt(3); i++) {
                    double spawnX = x + (random.nextDouble() - 0.5) * 6;
                    double spawnZ = z + (random.nextDouble() - 0.5) * 6;
                    
                    // Eclipse PhantomかVoid Walkerをスポーン
                    if (random.nextBoolean()) {
                        ModEntities.ECLIPSE_PHANTOM.get().spawn(serverLevel, 
                            BlockPos.containing(spawnX, y + 2, spawnZ), MobSpawnType.EVENT);
                    } else {
                        ModEntities.VOID_WALKER.get().spawn(serverLevel, 
                            BlockPos.containing(spawnX, y, spawnZ), MobSpawnType.EVENT);
                    }
                }
                // 高レアドロップ
                dropItem(serverLevel, x, y, z, new ItemStack(AsterRiskModItems.VOID_SHARD.get(), 3 + random.nextInt(3)));
                dropItem(serverLevel, x, y, z, new ItemStack(AsterRiskModItems.SHADOW_ESSENCE.get(), 2 + random.nextInt(3)));
                if (random.nextFloat() < 0.15f) {
                    dropItem(serverLevel, x, y, z, new ItemStack(AsterRiskModItems.PRISMATIC_METEORITE.get(), 1));
                }
            }
        }

        // リセット
        summonProgress = 0;
        summonTime = 0;
        summoning = false;
        
        level.sendBlockUpdated(pos, getBlockState(), getBlockState(), 3);
    }

    private void dropItem(ServerLevel level, double x, double y, double z, ItemStack stack) {
        ItemEntity itemEntity = new ItemEntity(level, x, y, z, stack);
        itemEntity.setDeltaMovement(
            (random.nextDouble() - 0.5) * 0.3, 
            0.3 + random.nextDouble() * 0.2, 
            (random.nextDouble() - 0.5) * 0.3);
        level.addFreshEntity(itemEntity);
    }

    public boolean setCore(ItemStack stack) {
        if (summoning) return false;
        if (!(stack.getItem() instanceof MeteorSummonCoreItem)) return false;
        
        if (core.isEmpty()) {
            core = stack.copyWithCount(1);
            setChanged();
            if (level != null) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
            return true;
        }
        return false;
    }

    public ItemStack removeCore() {
        if (!core.isEmpty() && !summoning) {
            ItemStack removed = core;
            core = ItemStack.EMPTY;
            setChanged();
            if (level != null) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
            return removed;
        }
        return ItemStack.EMPTY;
    }

    public void dropContents() {
        if (level != null && !level.isClientSide() && !core.isEmpty()) {
            ItemEntity itemEntity = new ItemEntity(level,
                worldPosition.getX() + 0.5,
                worldPosition.getY() + 0.5,
                worldPosition.getZ() + 0.5,
                core);
            level.addFreshEntity(itemEntity);
            core = ItemStack.EMPTY;
        }
    }

    public float getMana() { return manaStorage.getMana(); }
    public float getMaxMana() { return manaStorage.getMaxMana(); }
    public boolean hasCore() { return !core.isEmpty(); }
    public ItemStack getCore() { return core; }
    public boolean isSummoning() { return summoning; }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("manaStorage", manaStorage.serializeNBT());
        tag.put("core", core.save(new CompoundTag()));
        tag.putInt("summonProgress", summonProgress);
        tag.putInt("summonTime", summonTime);
        tag.putBoolean("summoning", summoning);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("manaStorage")) {
            manaStorage.deserializeNBT(tag.getCompound("manaStorage"));
        }
        core = ItemStack.of(tag.getCompound("core"));
        summonProgress = tag.getInt("summonProgress");
        summonTime = tag.getInt("summonTime");
        summoning = tag.getBoolean("summoning");
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
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
