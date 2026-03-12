package net.mcreator.asterrisk.block.entity;

import net.mcreator.asterrisk.registry.ModBlocks;
import net.mcreator.asterrisk.registry.ModBlockEntities;
import net.mcreator.asterrisk.registry.ModBlocks;
import net.mcreator.asterrisk.recipe.FocusChamberRecipe;
import net.mcreator.asterrisk.recipe.FocusChamberRecipeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * 集光チャンバーコア ブロックエンティティ
 * 
 * 仕様:
 * 1. CoreとMoonstone Brick、CelestialTileで3x3x3のストラクチャを構築
 *    (2:2:1, 1:2:2, 3:2:2, 2:2:3は空気 = レーザー入射口)
 * 2. 空気の場所からFocusのレーザーを照射してマナを供給
 * 3. マナが供給され、アイテムが正しければクラフト開始
 * 4. 内容物のアイテムは視点を合わせた時に表示
 */
public class FocusChamberCoreBlockEntity extends BlockEntity {
    
    // マルチブロック構造
    // コアはY=2の中央 (2,2,2)
    // 空気穴: 北(2,2,1), 西(1,2,2), 東(3,2,2), 南(2,2,3) - コアと同じY座標
    
    private float storedMana = 0;
    private static final float MAX_MANA = 1000;
    
    private boolean isStructureValid = false;
    private boolean isProcessing = false;
    private int processProgress = 0;
    private static final int PROCESS_TIME = 200; // 10秒
    
    @Nullable
    private FocusChamberRecipe currentRecipe = null;
    private List<ItemStack> storedItems = new ArrayList<>();
    private static final int MAX_ITEMS = 9;
    
    public FocusChamberCoreBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FOCUS_CHAMBER_CORE.get(), pos, state);
    }
    
    public static void serverTick(Level level, BlockPos pos, BlockState state, FocusChamberCoreBlockEntity entity) {
        // マルチブロック構造のチェック
        entity.isStructureValid = entity.checkMultiblockStructure(level);
        
        if (!entity.isStructureValid) {
            entity.cancelProcessing();
            return;
        }
        
        // 処理中
        if (entity.isProcessing && entity.currentRecipe != null) {
            float manaPerTick = (float) entity.currentRecipe.getMoonlightCost() / PROCESS_TIME;
            
            if (entity.storedMana >= manaPerTick) {
                entity.storedMana -= manaPerTick;
                entity.processProgress++;
                
                // パーティクル
                if (level instanceof ServerLevel serverLevel && entity.processProgress % 10 == 0) {
                    entity.spawnProcessingParticles(serverLevel);
                }
                
                // 完了
                if (entity.processProgress >= PROCESS_TIME) {
                    entity.completeProcessing(level);
                }
            }
        } else if (entity.isStructureValid && !entity.storedItems.isEmpty() && entity.storedMana > 0) {
            // レシピ検索
            entity.tryStartProcessing();
        }
        
        entity.setChanged();
        
        // クライアント同期
        level.sendBlockUpdated(pos, state, state, 3);
    }
    
    /**
     * 3x3x3マルチブロック構造をチェック
     * コアを中心とし、4方向に空気穴を持つ構造
     */
    private boolean checkMultiblockStructure(Level level) {
        // 空気穴の位置（コアと同じY座標、4方向）
        BlockPos[] airHoles = {
            worldPosition.north(),  // 北
            worldPosition.south(),  // 南
            worldPosition.east(),   // 東
            worldPosition.west()    // 西
        };
        
        // 空気穴のチェック
        for (BlockPos hole : airHoles) {
            if (!level.getBlockState(hole).isAir()) {
                return false;
            }
        }
        
        // フレームブロックのチェック（コアを中心とした3x3x3の外殻）
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    // コア自身はスキップ
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    
                    // 4方向の空気穴はスキップ
                    if (dy == 0 && ((dx == 0 && Math.abs(dz) == 1) || (dz == 0 && Math.abs(dx) == 1))) {
                        continue;
                    }
                    
                    BlockPos checkPos = worldPosition.offset(dx, dy, dz);
                    BlockState checkState = level.getBlockState(checkPos);
                    
                    if (!isValidFrameBlock(checkState)) {
                        return false;
                    }
                }
            }
        }
        
        return true;
    }
    
    /**
     * フレームとして有効なブロックかチェック
     */
    private boolean isValidFrameBlock(BlockState state) {
        Block block = state.getBlock();
        String blockId = block.getDescriptionId();
        
        // Moonstone Brick, Celestial Tile, または関連ブロック
        return blockId.contains("moonstone") || 
               blockId.contains("celestial") ||
               blockId.contains("lunar") ||
               blockId.contains("aster_risk");
    }
    
    /**
     * Focusからマナを受け取る
     */
    public float receiveMana(float amount) {
        float received = Math.min(amount, MAX_MANA - storedMana);
        storedMana += received;
        setChanged();
        return received;
    }
    
    /**
     * 月光を受け取る（後方互換性）
     */
    public float receiveMoonlight(float amount) {
        return receiveMana(amount);
    }
    
    /**
     * アイテムを追加
     */
    public boolean addItem(ItemStack stack) {
        if (storedItems.size() >= MAX_ITEMS) return false;
        if (isProcessing) return false;
        
        storedItems.add(stack.copy());
        setChanged();
        return true;
    }
    
    /**
     * 最後のアイテムを取り出す
     */
    public ItemStack removeLastItem() {
        if (storedItems.isEmpty() || isProcessing) return ItemStack.EMPTY;
        
        ItemStack removed = storedItems.remove(storedItems.size() - 1);
        setChanged();
        return removed;
    }
    
    /**
     * 処理開始を試みる
     */
    private void tryStartProcessing() {
        FocusChamberRecipe recipe = FocusChamberRecipeManager.findRecipe(storedItems);
        if (recipe != null && storedMana >= recipe.getMoonlightCost() * 0.1f) {
            currentRecipe = recipe;
            isProcessing = true;
            processProgress = 0;
            
            if (level != null) {
                level.playSound(null, worldPosition, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0f, 1.2f);
            }
        }
    }
    
    /**
     * 処理完了
     */
    private void completeProcessing(Level level) {
        if (currentRecipe == null) return;
        
        // アイテム消費
        storedItems.clear();
        
        // 結果アイテムをドロップ
        ItemStack result = currentRecipe.getResultItem().copy();
        double x = worldPosition.getX() + 0.5;
        double y = worldPosition.getY() + 1.5;
        double z = worldPosition.getZ() + 0.5;
        
        ItemEntity entity = new ItemEntity(level, x, y, z, result);
        entity.setDeltaMovement(0, 0.2, 0);
        level.addFreshEntity(entity);
        
        // エフェクト
        level.playSound(null, worldPosition, SoundEvents.BEACON_POWER_SELECT, SoundSource.BLOCKS, 1.0f, 1.5f);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.END_ROD, x, y, z, 100, 1.0, 1.0, 1.0, 0.1);
        }
        
        // リセット
        isProcessing = false;
        processProgress = 0;
        currentRecipe = null;
    }
    
    private void cancelProcessing() {
        isProcessing = false;
        processProgress = 0;
        currentRecipe = null;
    }
    
    private void spawnProcessingParticles(ServerLevel level) {
        double cx = worldPosition.getX() + 0.5;
        double cy = worldPosition.getY() + 0.5;
        double cz = worldPosition.getZ() + 0.5;
        
        // 回転するエンチャントパーティクル
        for (int i = 0; i < 4; i++) {
            double angle = (processProgress * 0.1 + i * Math.PI / 2) % (Math.PI * 2);
            double radius = 0.8;
            double px = cx + Math.cos(angle) * radius;
            double pz = cz + Math.sin(angle) * radius;
            level.sendParticles(ParticleTypes.ENCHANT, px, cy, pz, 2, 0, 0.3, 0, 0.1);
        }
        
        // 中央の輝き
        level.sendParticles(ParticleTypes.END_ROD, cx, cy, cz, 3, 0.2, 0.2, 0.2, 0.02);
    }
    
    /**
     * コンテンツをドロップ
     */
    public void dropContents() {
        if (level != null && !storedItems.isEmpty()) {
            for (ItemStack stack : storedItems) {
                Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), stack);
            }
            storedItems.clear();
        }
    }
    
    // Getters
    public float getStoredMana() { return storedMana; }
    public float getMaxMana() { return MAX_MANA; }
    public float getStoredMoonlight() { return storedMana; } // 後方互換
    public float getMaxMoonlight() { return MAX_MANA; } // 後方互換
    public boolean isStructureValid() { return isStructureValid; }
    public boolean isProcessing() { return isProcessing; }
    public int getProcessProgress() { return processProgress; }
    public int getProcessTime() { return PROCESS_TIME; }
    public List<ItemStack> getStoredItems() { return storedItems; }
    
    // NBT
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putFloat("Mana", storedMana);
        tag.putBoolean("Processing", isProcessing);
        tag.putInt("Progress", processProgress);
        tag.putBoolean("StructureValid", isStructureValid);
        
        ListTag itemsTag = new ListTag();
        for (ItemStack stack : storedItems) {
            itemsTag.add(stack.save(new CompoundTag()));
        }
        tag.put("Items", itemsTag);
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        storedMana = tag.getFloat("Mana");
        isProcessing = tag.getBoolean("Processing");
        processProgress = tag.getInt("Progress");
        isStructureValid = tag.getBoolean("StructureValid");
        
        storedItems.clear();
        ListTag itemsTag = tag.getList("Items", Tag.TAG_COMPOUND);
        for (int i = 0; i < itemsTag.size(); i++) {
            storedItems.add(ItemStack.of(itemsTag.getCompound(i)));
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
}
