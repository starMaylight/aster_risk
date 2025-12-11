package net.mcreator.asterrisk.block.entity;

import net.mcreator.asterrisk.mana.BlockManaCapability;
import net.mcreator.asterrisk.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * マナバッテリーのBlockEntity
 * 大容量マナ貯蔵、モード切替可能（入力/出力/双方向）
 */
public class ManaBatteryBlockEntity extends BlockEntity {

    // マナ設定
    public static final float MAX_MANA = 5000f;
    public static final float TRANSFER_RATE = 100f;

    // モード定義
    public enum TransferMode {
        INPUT_ONLY("Input Only", true, false),
        OUTPUT_ONLY("Output Only", false, true),
        BIDIRECTIONAL("Bidirectional", true, true);

        private final String displayName;
        private final boolean canReceive;
        private final boolean canExtract;

        TransferMode(String displayName, boolean canReceive, boolean canExtract) {
            this.displayName = displayName;
            this.canReceive = canReceive;
            this.canExtract = canExtract;
        }

        public String getDisplayName() { return displayName; }
        public boolean canReceive() { return canReceive; }
        public boolean canExtract() { return canExtract; }

        public TransferMode next() {
            return values()[(ordinal() + 1) % values().length];
        }
    }

    private TransferMode mode = TransferMode.BIDIRECTIONAL;
    private final BlockManaCapability.BlockMana manaStorage;
    private LazyOptional<BlockManaCapability.IBlockMana> manaHandler;

    public ManaBatteryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.manaStorage = new BlockManaCapability.BlockMana(MAX_MANA, TRANSFER_RATE, TRANSFER_RATE, true, true);
        this.manaHandler = LazyOptional.of(() -> new ModeAwareManaStorage());
    }

    /**
     * ファクトリメソッド
     */
    public static ManaBatteryBlockEntity create(BlockPos pos, BlockState state) {
        return new ManaBatteryBlockEntity(ModBlockEntities.MANA_BATTERY.get(), pos, state);
    }

    /**
     * モード切替
     */
    public void cycleMode() {
        mode = mode.next();
        // Capabilityを再作成してモード変更を反映
        manaHandler.invalidate();
        manaHandler = LazyOptional.of(() -> new ModeAwareManaStorage());
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public TransferMode getMode() {
        return mode;
    }

    public Component getModeDisplayName() {
        String color = switch (mode) {
            case INPUT_ONLY -> "§a";    // 緑
            case OUTPUT_ONLY -> "§c";   // 赤
            case BIDIRECTIONAL -> "§b"; // 水色
        };
        return Component.literal(color + mode.getDisplayName());
    }

    public float getMana() {
        return manaStorage.getMana();
    }

    public float getMaxMana() {
        return manaStorage.getMaxMana();
    }

    /**
     * モードに応じた発光レベル
     */
    public int getLightLevel() {
        float fillRatio = manaStorage.getMana() / manaStorage.getMaxMana();
        int baseLightLevel = (int) (fillRatio * 10);
        return Math.min(15, baseLightLevel + 3);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("manaStorage", manaStorage.serializeNBT());
        tag.putInt("mode", mode.ordinal());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("manaStorage")) {
            manaStorage.deserializeNBT(tag.getCompound("manaStorage"));
        }
        if (tag.contains("mode")) {
            int modeOrdinal = tag.getInt("mode");
            if (modeOrdinal >= 0 && modeOrdinal < TransferMode.values().length) {
                mode = TransferMode.values()[modeOrdinal];
            }
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putFloat("mana", manaStorage.getMana());
        tag.putFloat("maxMana", manaStorage.getMaxMana());
        tag.putInt("mode", mode.ordinal());
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        if (tag.contains("mana")) {
            manaStorage.setMana(tag.getFloat("mana"));
        }
        if (tag.contains("mode")) {
            mode = TransferMode.values()[tag.getInt("mode")];
        }
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

    /**
     * モードに応じてcanReceive/canExtractを制御するラッパー
     */
    private class ModeAwareManaStorage implements BlockManaCapability.IBlockMana {
        @Override
        public float getMana() {
            return manaStorage.getMana();
        }

        @Override
        public void setMana(float mana) {
            manaStorage.setMana(mana);
        }

        @Override
        public float getMaxMana() {
            return manaStorage.getMaxMana();
        }

        @Override
        public void setMaxMana(float maxMana) {
            manaStorage.setMaxMana(maxMana);
        }

        @Override
        public float addMana(float amount) {
            if (!mode.canReceive()) return 0;
            float added = manaStorage.addMana(amount);
            if (added > 0) setChanged();
            return added;
        }

        @Override
        public float extractMana(float amount) {
            if (!mode.canExtract()) return 0;
            float extracted = manaStorage.extractMana(amount);
            if (extracted > 0) setChanged();
            return extracted;
        }

        @Override
        public boolean canReceive() {
            return mode.canReceive() && manaStorage.getMana() < manaStorage.getMaxMana();
        }

        @Override
        public boolean canExtract() {
            return mode.canExtract() && manaStorage.getMana() > 0;
        }

        @Override
        public float getReceiveRate() {
            return manaStorage.getReceiveRate();
        }

        @Override
        public float getExtractRate() {
            return manaStorage.getExtractRate();
        }

        @Override
        public CompoundTag serializeNBT() {
            return manaStorage.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            manaStorage.deserializeNBT(tag);
        }
    }
}
