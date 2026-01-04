package net.mcreator.asterrisk.block.entity;

import net.mcreator.asterrisk.item.PhaseSigilItem;
import net.mcreator.asterrisk.mana.BlockManaCapability;
import net.mcreator.asterrisk.registry.ModBlockEntities;
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
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 月相の金床のBlockEntity
 */
public class PhaseAnvilBlockEntity extends BlockEntity {

    public static final float MAX_MANA = 300f;
    public static final float RECEIVE_RATE = 15f;
    public static final int PROCESS_TIME = 100; // 5秒
    public static final float MANA_COST = 200f;

    public static final String TAG_PHASE_SIGILS = "PhaseSigils";

    private final BlockManaCapability.BlockMana manaStorage;
    private final LazyOptional<BlockManaCapability.IBlockMana> manaHandler;

    private ItemStack equipment = ItemStack.EMPTY;
    private ItemStack sigil = ItemStack.EMPTY;
    
    private int processProgress = 0;
    private boolean processing = false;

    public PhaseAnvilBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PHASE_ANVIL.get(), pos, state);
        this.manaStorage = new BlockManaCapability.BlockMana(MAX_MANA, RECEIVE_RATE, 0, true, false);
        this.manaHandler = LazyOptional.of(() -> manaStorage);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, PhaseAnvilBlockEntity entity) {
        if (level.isClientSide()) return;

        // 処理開始条件チェック
        if (!entity.processing && !entity.equipment.isEmpty() && !entity.sigil.isEmpty()) {
            if (entity.canApplySigil()) {
                entity.processing = true;
                entity.processProgress = 0;
            }
        }

        // 処理中
        if (entity.processing) {
            float manaPerTick = MANA_COST / PROCESS_TIME;
            if (entity.manaStorage.getMana() >= manaPerTick) {
                entity.manaStorage.setMana(entity.manaStorage.getMana() - manaPerTick);
                entity.processProgress++;

                // パーティクル
                if (entity.processProgress % 10 == 0 && level instanceof ServerLevel serverLevel) {
                    double x = pos.getX() + 0.5;
                    double y = pos.getY() + 1.0;
                    double z = pos.getZ() + 0.5;
                    serverLevel.sendParticles(ModParticles.LUNAR_SPARKLE.get(), x, y, z, 3, 0.2, 0.2, 0.2, 0.02);
                    serverLevel.sendParticles(ParticleTypes.ENCHANT, x, y, z, 3, 0.3, 0.2, 0.3, 0.08);
                }
                
                // 金床打撃音
                if (entity.processProgress % 25 == 0) {
                    level.playSound(null, pos, ModSounds.PHASE_ANVIL_USE.get(), SoundSource.BLOCKS, 0.8f, 1.0f + level.random.nextFloat() * 0.2f);
                }

                // 完了
                if (entity.processProgress >= PROCESS_TIME) {
                    entity.completeImbue(pos);
                }
            }
        }

        entity.setChanged();
    }

    private boolean canApplySigil() {
        if (equipment.isEmpty() || sigil.isEmpty()) return false;
        if (!(sigil.getItem() instanceof PhaseSigilItem)) return false;
        
        // 装備品のみ刻印可能
        return equipment.getItem() instanceof ArmorItem || 
               equipment.getItem() instanceof SwordItem ||
               equipment.getItem() instanceof DiggerItem;
    }

    private void completeImbue(BlockPos pos) {
        if (level == null || equipment.isEmpty() || sigil.isEmpty()) return;
        if (!(sigil.getItem() instanceof PhaseSigilItem sigilItem)) return;

        // 刻印をNBTに追加
        CompoundTag tag = equipment.getOrCreateTag();
        CompoundTag sigils = tag.getCompound(TAG_PHASE_SIGILS);
        
        PhaseSigilItem.MoonPhase phase = sigilItem.getPhase();
        int currentLevel = sigils.getInt(phase.getName());
        sigils.putInt(phase.getName(), Math.min(currentLevel + 1, 3)); // 最大レベル3
        
        tag.put(TAG_PHASE_SIGILS, sigils);
        
        // 刻印消費
        sigil = ItemStack.EMPTY;
        
        // エフェクト
        if (level instanceof ServerLevel serverLevel) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 1.0;
            double z = pos.getZ() + 0.5;
            serverLevel.playSound(null, pos, ModSounds.RITUAL_COMPLETE.get(), SoundSource.BLOCKS, 1.0f, 1.2f);
            serverLevel.playSound(null, pos, ModSounds.LUNAR_MAGIC.get(), SoundSource.BLOCKS, 0.8f, 1.0f);
            serverLevel.sendParticles(ModParticles.LUNAR_SPARKLE.get(), x, y, z, 20, 0.3, 0.3, 0.3, 0.05);
            serverLevel.sendParticles(ModParticles.STAR_BURST.get(), x, y, z, 15, 0.2, 0.2, 0.2, 0.1);
            serverLevel.sendParticles(ParticleTypes.END_ROD, x, y, z, 15, 0.3, 0.3, 0.3, 0.05);
        }

        // リセット
        processProgress = 0;
        processing = false;
        
        level.sendBlockUpdated(pos, getBlockState(), getBlockState(), 3);
    }

    public boolean addItem(ItemStack stack) {
        if (processing) return false;

        // 刻印アイテムの場合
        if (stack.getItem() instanceof PhaseSigilItem) {
            if (sigil.isEmpty()) {
                sigil = stack.copyWithCount(1);
                setChanged();
                if (level != null) {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                }
                return true;
            }
            return false;
        }

        // 装備品の場合
        if (equipment.isEmpty() && (stack.getItem() instanceof ArmorItem || 
            stack.getItem() instanceof SwordItem || stack.getItem() instanceof DiggerItem)) {
            equipment = stack.copyWithCount(1);
            setChanged();
            if (level != null) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
            return true;
        }

        return false;
    }

    public ItemStack removeEquipment() {
        if (!equipment.isEmpty() && !processing) {
            ItemStack removed = equipment;
            equipment = ItemStack.EMPTY;
            setChanged();
            if (level != null) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
            return removed;
        }
        return ItemStack.EMPTY;
    }

    public ItemStack removeSigil() {
        if (!sigil.isEmpty() && !processing) {
            ItemStack removed = sigil;
            sigil = ItemStack.EMPTY;
            setChanged();
            if (level != null) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
            return removed;
        }
        return ItemStack.EMPTY;
    }

    public void dropContents() {
        if (level != null && !level.isClientSide()) {
            if (!equipment.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(level,
                    worldPosition.getX() + 0.5,
                    worldPosition.getY() + 0.5,
                    worldPosition.getZ() + 0.5,
                    equipment);
                level.addFreshEntity(itemEntity);
                equipment = ItemStack.EMPTY;
            }
            if (!sigil.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(level,
                    worldPosition.getX() + 0.5,
                    worldPosition.getY() + 0.5,
                    worldPosition.getZ() + 0.5,
                    sigil);
                level.addFreshEntity(itemEntity);
                sigil = ItemStack.EMPTY;
            }
        }
    }

    public float getMana() { return manaStorage.getMana(); }
    public float getMaxMana() { return manaStorage.getMaxMana(); }
    public ItemStack getEquipment() { return equipment; }
    public ItemStack getSigil() { return sigil; }
    public boolean isProcessing() { return processing; }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("manaStorage", manaStorage.serializeNBT());
        tag.put("equipment", equipment.save(new CompoundTag()));
        tag.put("sigil", sigil.save(new CompoundTag()));
        tag.putInt("processProgress", processProgress);
        tag.putBoolean("processing", processing);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("manaStorage")) {
            manaStorage.deserializeNBT(tag.getCompound("manaStorage"));
        }
        equipment = ItemStack.of(tag.getCompound("equipment"));
        sigil = ItemStack.of(tag.getCompound("sigil"));
        processProgress = tag.getInt("processProgress");
        processing = tag.getBoolean("processing");
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
