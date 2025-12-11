package net.mcreator.asterrisk.block.entity;

import net.mcreator.asterrisk.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 儀式台座のBlockEntity
 * アイテムを1つ保持し、祭壇コアと連携する
 */
public class RitualPedestalBlockEntity extends BlockEntity {

    private final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1; // 1アイテムのみ
        }
    };

    private final LazyOptional<IItemHandler> inventoryHandler = LazyOptional.of(() -> inventory);

    public RitualPedestalBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /**
     * ファクトリメソッド
     */
    public static RitualPedestalBlockEntity create(BlockPos pos, BlockState state) {
        return new RitualPedestalBlockEntity(ModBlockEntities.RITUAL_PEDESTAL.get(), pos, state);
    }

    /**
     * アイテムを取得
     */
    public ItemStack getItem() {
        return inventory.getStackInSlot(0);
    }

    /**
     * アイテムを設定
     */
    public void setItem(ItemStack stack) {
        inventory.setStackInSlot(0, stack);
    }

    /**
     * アイテムがあるか
     */
    public boolean hasItem() {
        return !inventory.getStackInSlot(0).isEmpty();
    }

    /**
     * アイテムを取り出す
     */
    public ItemStack removeItem() {
        ItemStack stack = inventory.getStackInSlot(0).copy();
        inventory.setStackInSlot(0, ItemStack.EMPTY);
        return stack;
    }

    /**
     * アイテムを消費（儀式成功時）
     */
    public void consumeItem() {
        inventory.setStackInSlot(0, ItemStack.EMPTY);
    }

    /**
     * ブロック破壊時にアイテムをドロップ
     */
    public void dropContents() {
        if (level != null && !level.isClientSide()) {
            ItemStack stack = getItem();
            if (!stack.isEmpty()) {
                Containers.dropItemStack(level, worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5, stack);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("inventory", inventory.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("inventory")) {
            inventory.deserializeNBT(tag.getCompound("inventory"));
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.put("inventory", inventory.serializeNBT());
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        if (tag.contains("inventory")) {
            inventory.deserializeNBT(tag.getCompound("inventory"));
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
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return inventoryHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        inventoryHandler.invalidate();
    }
}
