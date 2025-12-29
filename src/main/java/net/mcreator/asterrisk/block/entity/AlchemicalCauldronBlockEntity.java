package net.mcreator.asterrisk.block.entity;

import net.mcreator.asterrisk.init.AsterRiskModItems;
import net.mcreator.asterrisk.mana.BlockManaCapability;
import net.mcreator.asterrisk.recipe.AlchemyRecipe;
import net.mcreator.asterrisk.recipe.ModRecipes;
import net.mcreator.asterrisk.registry.ModBlockEntities;
import net.mcreator.asterrisk.block.AlchemicalCauldronBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 錬金釜のBlockEntity
 */
public class AlchemicalCauldronBlockEntity extends BlockEntity {

    public static final float MAX_MANA = 500f;
    public static final float RECEIVE_RATE = 20f;
    public static final int MAX_INGREDIENTS = 4;

    private final BlockManaCapability.BlockMana manaStorage;
    private final LazyOptional<BlockManaCapability.IBlockMana> manaHandler;

    // 材料スロット
    private final List<ItemStack> ingredients = new ArrayList<>();
    
    // 月水レベル (0.0 - 1.0)
    private float waterLevel = 0f;
    
    // 処理状態
    private int processProgress = 0;
    private int processTime = 0;
    private boolean processing = false;
    
    @Nullable
    private AlchemyRecipe currentRecipe = null;

    public AlchemicalCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ALCHEMICAL_CAULDRON.get(), pos, state);
        this.manaStorage = new BlockManaCapability.BlockMana(MAX_MANA, RECEIVE_RATE, 0, true, false);
        this.manaHandler = LazyOptional.of(() -> manaStorage);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AlchemicalCauldronBlockEntity entity) {
        if (level.isClientSide()) return;

        boolean wasActive = state.getValue(AlchemicalCauldronBlock.ACTIVE);
        boolean shouldBeActive = entity.processing;

        // レシピチェック
        if (!entity.processing && entity.waterLevel > 0 && !entity.ingredients.isEmpty()) {
            entity.findAndStartRecipe();
        }

        // 処理中
        if (entity.processing && entity.currentRecipe != null) {
            // マナ消費チェック
            float manaPerTick = entity.currentRecipe.getManaCost() / (float) entity.processTime;
            if (entity.manaStorage.getMana() >= manaPerTick && entity.waterLevel > 0) {
                entity.manaStorage.setMana(entity.manaStorage.getMana() - manaPerTick);
                entity.waterLevel -= 0.001f; // 水を少しずつ消費
                entity.processProgress++;

                // パーティクル
                if (entity.processProgress % 5 == 0 && level instanceof ServerLevel serverLevel) {
                    double x = pos.getX() + 0.5;
                    double y = pos.getY() + 0.8;
                    double z = pos.getZ() + 0.5;
                    serverLevel.sendParticles(ParticleTypes.WITCH, x, y, z, 2, 0.2, 0.1, 0.2, 0.01);
                }

                // 完了チェック
                if (entity.processProgress >= entity.processTime) {
                    entity.completeAlchemy(pos);
                }
            } else {
                // マナ不足で一時停止
                entity.processing = false;
            }
        }

        // ブロック状態更新
        if (wasActive != shouldBeActive) {
            level.setBlock(pos, state.setValue(AlchemicalCauldronBlock.ACTIVE, shouldBeActive), 3);
        }

        entity.setChanged();
    }

    private void findAndStartRecipe() {
        if (level == null) return;

        Optional<AlchemyRecipe> recipe = level.getRecipeManager()
            .getAllRecipesFor(ModRecipes.ALCHEMY_TYPE.get())
            .stream()
            .filter(r -> r.matches(ingredients))
            .findFirst();

        if (recipe.isPresent()) {
            currentRecipe = recipe.get();
            processTime = currentRecipe.getProcessTime();
            processProgress = 0;
            processing = true;
        }
    }

    private void completeAlchemy(BlockPos pos) {
        if (level == null || currentRecipe == null) return;

        // 材料消費
        ingredients.clear();
        
        // 結果アイテムをスポーン
        ItemStack result = currentRecipe.getResultItem();
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 1.0;
        double z = pos.getZ() + 0.5;
        
        ItemEntity itemEntity = new ItemEntity(level, x, y, z, result);
        itemEntity.setDeltaMovement(0, 0.2, 0);
        level.addFreshEntity(itemEntity);

        // エフェクト
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0f, 1.2f);
            serverLevel.sendParticles(ParticleTypes.END_ROD, x, y, z, 20, 0.3, 0.3, 0.3, 0.05);
        }

        // リセット
        currentRecipe = null;
        processProgress = 0;
        processTime = 0;
        processing = false;
        
        level.sendBlockUpdated(pos, getBlockState(), getBlockState(), 3);
    }

    public boolean addItem(ItemStack stack) {
        // 月水バケツの処理
        if (stack.is(AsterRiskModItems.MOONWATER_BUCKET.get())) {
            if (waterLevel < 1.0f) {
                waterLevel = 1.0f;
                setChanged();
                if (level != null) {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                }
                return true;
            }
            return false;
        }

        // 通常のアイテム追加
        if (ingredients.size() < MAX_INGREDIENTS && !processing) {
            ingredients.add(stack.copyWithCount(1));
            setChanged();
            if (level != null) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
            return true;
        }
        return false;
    }

    public ItemStack removeItem() {
        if (!ingredients.isEmpty() && !processing) {
            ItemStack removed = ingredients.remove(ingredients.size() - 1);
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
            for (ItemStack stack : ingredients) {
                ItemEntity itemEntity = new ItemEntity(level,
                    worldPosition.getX() + 0.5,
                    worldPosition.getY() + 0.5,
                    worldPosition.getZ() + 0.5,
                    stack);
                level.addFreshEntity(itemEntity);
            }
            ingredients.clear();
        }
    }

    public float getMana() { return manaStorage.getMana(); }
    public float getMaxMana() { return manaStorage.getMaxMana(); }
    public float getWaterLevel() { return waterLevel; }
    public List<ItemStack> getIngredients() { return new ArrayList<>(ingredients); }
    public boolean isProcessing() { return processing; }
    public int getProcessProgress() { return processProgress; }
    public int getProcessTime() { return processTime; }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("manaStorage", manaStorage.serializeNBT());
        tag.putFloat("waterLevel", waterLevel);
        tag.putInt("processProgress", processProgress);
        tag.putInt("processTime", processTime);
        tag.putBoolean("processing", processing);

        ListTag ingredientList = new ListTag();
        for (ItemStack stack : ingredients) {
            CompoundTag itemTag = new CompoundTag();
            stack.save(itemTag);
            ingredientList.add(itemTag);
        }
        tag.put("ingredients", ingredientList);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("manaStorage")) {
            manaStorage.deserializeNBT(tag.getCompound("manaStorage"));
        }
        waterLevel = tag.getFloat("waterLevel");
        processProgress = tag.getInt("processProgress");
        processTime = tag.getInt("processTime");
        processing = tag.getBoolean("processing");

        ingredients.clear();
        ListTag ingredientList = tag.getList("ingredients", 10);
        for (int i = 0; i < ingredientList.size(); i++) {
            ingredients.add(ItemStack.of(ingredientList.getCompound(i)));
        }
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
