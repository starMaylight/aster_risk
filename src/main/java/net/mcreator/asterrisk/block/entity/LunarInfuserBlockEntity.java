package net.mcreator.asterrisk.block.entity;

import net.mcreator.asterrisk.mana.BlockManaCapability;
import net.mcreator.asterrisk.recipe.InfuserRecipe;
import net.mcreator.asterrisk.recipe.ModRecipes;
import net.mcreator.asterrisk.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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
import java.util.Optional;

/**
 * 月光注入機
 * JSONレシピでアイテムをマナで変換する
 */
public class LunarInfuserBlockEntity extends BlockEntity {

    // マナ設定
    public static final float MAX_MANA = 500f;
    public static final float RECEIVE_RATE = 30f;

    // デフォルト変換時間
    public static final int DEFAULT_INFUSION_TIME = 100; // 5秒

    private final BlockManaCapability.BlockMana manaStorage;
    private final LazyOptional<BlockManaCapability.IBlockMana> manaHandler;

    // インベントリ（1スロット）
    private final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    };
    private final LazyOptional<IItemHandler> inventoryHandler = LazyOptional.of(() -> inventory);

    // 変換進行状況
    private int infusionProgress = 0;
    private int currentProcessTime = DEFAULT_INFUSION_TIME;
    private boolean infusing = false;
    
    // 現在のレシピ（キャッシュ）
    @Nullable
    private InfuserRecipe currentRecipe = null;

    public LunarInfuserBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.manaStorage = new BlockManaCapability.BlockMana(MAX_MANA, RECEIVE_RATE, 0, true, false);
        this.manaHandler = LazyOptional.of(() -> manaStorage);
    }

    public static LunarInfuserBlockEntity create(BlockPos pos, BlockState state) {
        return new LunarInfuserBlockEntity(ModBlockEntities.LUNAR_INFUSER.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, LunarInfuserBlockEntity entity) {
        if (level.isClientSide()) return;

        ItemStack input = entity.inventory.getStackInSlot(0);
        
        if (input.isEmpty()) {
            entity.resetProgress();
            return;
        }

        // レシピ検索（キャッシュがないか、アイテムが変わった場合）
        if (entity.currentRecipe == null || !entity.currentRecipe.matches(input)) {
            entity.currentRecipe = entity.findRecipe(input);
        }

        if (entity.currentRecipe == null) {
            entity.resetProgress();
            return;
        }

        // マナチェック
        if (entity.manaStorage.getMana() < entity.currentRecipe.getManaCost()) {
            entity.infusing = false;
            return;
        }

        // 変換処理
        entity.infusing = true;
        entity.currentProcessTime = entity.currentRecipe.getProcessTime();
        entity.infusionProgress++;

        // パーティクル
        if (entity.infusionProgress % 10 == 0 && level instanceof ServerLevel serverLevel) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 1.0;
            double z = pos.getZ() + 0.5;
            serverLevel.sendParticles(ParticleTypes.ENCHANT, x, y, z, 5, 0.3, 0.2, 0.3, 0.1);
        }

        // 変換完了
        if (entity.infusionProgress >= entity.currentProcessTime) {
            entity.completeInfusion(pos);
        }
    }

    @Nullable
    private InfuserRecipe findRecipe(ItemStack input) {
        if (level == null) return null;
        
        Optional<InfuserRecipe> recipe = level.getRecipeManager()
            .getAllRecipesFor(ModRecipes.INFUSER_TYPE.get())
            .stream()
            .filter(r -> r.matches(input))
            .findFirst();
        
        return recipe.orElse(null);
    }

    private void completeInfusion(BlockPos pos) {
        if (level == null || currentRecipe == null) return;

        // マナ消費
        manaStorage.setMana(manaStorage.getMana() - currentRecipe.getManaCost());
        
        // 入力アイテム消費
        inventory.extractItem(0, 1, false);
        
        // 出力アイテムスポーン
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 1.2;
        double z = pos.getZ() + 0.5;
        ItemEntity itemEntity = new ItemEntity(level, x, y, z, currentRecipe.getOutput());
        itemEntity.setDeltaMovement(0, 0.15, 0);
        level.addFreshEntity(itemEntity);

        // エフェクト
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0f, 1.2f);
            serverLevel.sendParticles(ParticleTypes.END_ROD, x, y, z, 15, 0.3, 0.3, 0.3, 0.05);
        }

        resetProgress();
        setChanged();
    }

    private void resetProgress() {
        infusionProgress = 0;
        infusing = false;
        currentRecipe = null;
        currentProcessTime = DEFAULT_INFUSION_TIME;
    }

    public ItemStack getItem() {
        return inventory.getStackInSlot(0);
    }

    public void setItem(ItemStack stack) {
        inventory.setStackInSlot(0, stack);
        currentRecipe = null; // レシピキャッシュをリセット
    }

    public ItemStack removeItem() {
        ItemStack stack = inventory.extractItem(0, 64, false);
        resetProgress();
        return stack;
    }

    public float getMana() {
        return manaStorage.getMana();
    }

    public float getMaxMana() {
        return manaStorage.getMaxMana();
    }

    public int getInfusionProgress() {
        return infusionProgress;
    }

    public int getCurrentProcessTime() {
        return currentProcessTime;
    }

    public boolean isInfusing() {
        return infusing;
    }

    public void dropContents() {
        if (level != null && !level.isClientSide()) {
            ItemStack stack = inventory.getStackInSlot(0);
            if (!stack.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(level,
                    worldPosition.getX() + 0.5,
                    worldPosition.getY() + 0.5,
                    worldPosition.getZ() + 0.5,
                    stack);
                level.addFreshEntity(itemEntity);
                inventory.setStackInSlot(0, ItemStack.EMPTY);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("manaStorage", manaStorage.serializeNBT());
        tag.put("inventory", inventory.serializeNBT());
        tag.putInt("infusionProgress", infusionProgress);
        tag.putInt("currentProcessTime", currentProcessTime);
        tag.putBoolean("infusing", infusing);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("manaStorage")) {
            manaStorage.deserializeNBT(tag.getCompound("manaStorage"));
        }
        if (tag.contains("inventory")) {
            inventory.deserializeNBT(tag.getCompound("inventory"));
        }
        infusionProgress = tag.getInt("infusionProgress");
        currentProcessTime = tag.getInt("currentProcessTime");
        if (currentProcessTime <= 0) currentProcessTime = DEFAULT_INFUSION_TIME;
        infusing = tag.getBoolean("infusing");
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putFloat("mana", manaStorage.getMana());
        tag.put("inventory", inventory.serializeNBT());
        tag.putInt("infusionProgress", infusionProgress);
        tag.putInt("currentProcessTime", currentProcessTime);
        tag.putBoolean("infusing", infusing);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        if (tag.contains("mana")) {
            manaStorage.setMana(tag.getFloat("mana"));
        }
        if (tag.contains("inventory")) {
            inventory.deserializeNBT(tag.getCompound("inventory"));
        }
        infusionProgress = tag.getInt("infusionProgress");
        currentProcessTime = tag.getInt("currentProcessTime");
        if (currentProcessTime <= 0) currentProcessTime = DEFAULT_INFUSION_TIME;
        infusing = tag.getBoolean("infusing");
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
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return inventoryHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        manaHandler.invalidate();
        inventoryHandler.invalidate();
    }
}
