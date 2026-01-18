package net.mcreator.asterrisk.block.entity;

import net.mcreator.asterrisk.pattern.PatternManager;
import net.mcreator.asterrisk.pattern.PedestalPattern;
import net.mcreator.asterrisk.registry.ModBlockEntities;
import net.mcreator.asterrisk.recipe.RitualCircleRecipe;
import net.mcreator.asterrisk.recipe.RitualCircleRecipeManager;
import net.mcreator.asterrisk.mana.IManaReceiver;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.*;

/**
 * 魔法陣ブロックエンティティ
 * 周囲の台座（RitualPedestal）の配置パターンとアイテムを検出し、
 * レシピに一致すれば儀式を実行してアイテムを変換する
 * 
 * パターンはPatternManagerからデータ駆動で読み込まれる
 */
public class RitualCircleBlockEntity extends BlockEntity implements IManaReceiver {
    
    private float mana = 0;
    private static final float MAX_MANA = 5000;
    
    private boolean isActive = false;
    private int ritualProgress = 0;
    private static final int RITUAL_TIME = 100;
    
    @Nullable
    private RitualCircleRecipe currentRecipe = null;
    private String currentPattern = "";
    
    public RitualCircleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RITUAL_CIRCLE.get(), pos, state);
    }
    
    public static void serverTick(Level level, BlockPos pos, BlockState state, RitualCircleBlockEntity entity) {
        if (entity.isActive && entity.currentRecipe != null) {
            float manaPerTick = (float) entity.currentRecipe.getManaCost() / RITUAL_TIME;
            if (entity.mana >= manaPerTick) {
                entity.mana -= manaPerTick;
                entity.ritualProgress++;
                
                if (level instanceof ServerLevel serverLevel) {
                    entity.spawnRitualParticles(serverLevel);
                }
                
                if (entity.ritualProgress >= RITUAL_TIME) {
                    entity.completeRitual();
                }
            } else {
                entity.cancelRitual();
            }
            entity.setChanged();
        }
    }
    
    public boolean tryStartRitual() {
        if (level == null || level.isClientSide || isActive) return false;
        
        // PatternManagerから全パターンを取得
        for (PedestalPattern pattern : PatternManager.getInstance().getAllPedestalPatterns()) {
            String patternName = pattern.getName();
            List<BlockPos> positions = pattern.getPositions();
            
            List<ItemStack> pedestalItems = getPedestalItems(positions);
            if (pedestalItems == null) continue;
            
            RitualCircleRecipe recipe = RitualCircleRecipeManager.findRecipe(patternName, pedestalItems);
            if (recipe != null && mana >= recipe.getManaCost()) {
                currentRecipe = recipe;
                currentPattern = patternName;
                isActive = true;
                ritualProgress = 0;
                
                level.playSound(null, worldPosition, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0f, 1.0f);
                setChanged();
                return true;
            }
        }
        return false;
    }
    
    @Nullable
    private List<ItemStack> getPedestalItems(List<BlockPos> relativePositions) {
        if (level == null) return null;
        
        List<ItemStack> items = new ArrayList<>();
        for (BlockPos relPos : relativePositions) {
            BlockPos pedestalPos = worldPosition.offset(relPos);
            BlockEntity be = level.getBlockEntity(pedestalPos);
            
            if (be instanceof RitualPedestalBlockEntity pedestal) {
                items.add(pedestal.getItem().copy());
            } else {
                return null;
            }
        }
        return items;
    }
    
    private void completeRitual() {
        if (level == null || currentRecipe == null) return;
        
        // パターンの位置を取得
        PedestalPattern pattern = PatternManager.getInstance().getPedestalPatternByName(currentPattern);
        if (pattern != null) {
            for (BlockPos relPos : pattern.getPositions()) {
                BlockPos pedestalPos = worldPosition.offset(relPos);
                BlockEntity be = level.getBlockEntity(pedestalPos);
                if (be instanceof RitualPedestalBlockEntity pedestal) {
                    pedestal.removeItem();
                }
            }
        }
        
        ItemStack result = currentRecipe.getResultItem().copy();
        double x = worldPosition.getX() + 0.5;
        double y = worldPosition.getY() + 1.5;
        double z = worldPosition.getZ() + 0.5;
        
        ItemEntity itemEntity = new ItemEntity(level, x, y, z, result);
        itemEntity.setDeltaMovement(0, 0.2, 0);
        level.addFreshEntity(itemEntity);
        
        level.playSound(null, worldPosition, SoundEvents.BEACON_POWER_SELECT, SoundSource.BLOCKS, 1.0f, 1.5f);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.END_ROD, x, y, z, 50, 0.5, 0.5, 0.5, 0.1);
        }
        
        resetRitual();
    }
    
    private void cancelRitual() {
        if (level != null) {
            level.playSound(null, worldPosition, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 1.0f, 0.5f);
        }
        resetRitual();
    }
    
    private void resetRitual() {
        isActive = false;
        ritualProgress = 0;
        currentRecipe = null;
        currentPattern = "";
        setChanged();
    }
    
    private void spawnRitualParticles(ServerLevel level) {
        PedestalPattern pattern = PatternManager.getInstance().getPedestalPatternByName(currentPattern);
        if (pattern == null) return;
        
        for (BlockPos relPos : pattern.getPositions()) {
            BlockPos pedestalPos = worldPosition.offset(relPos);
            double px = pedestalPos.getX() + 0.5;
            double py = pedestalPos.getY() + 1.2;
            double pz = pedestalPos.getZ() + 0.5;
            
            double dx = (worldPosition.getX() + 0.5 - px) * 0.05;
            double dz = (worldPosition.getZ() + 0.5 - pz) * 0.05;
            
            level.sendParticles(ParticleTypes.ENCHANT, px, py, pz, 2, dx, 0.1, dz, 0.5);
        }
        
        double angle = (ritualProgress * 0.2) % (Math.PI * 2);
        double radius = 1.0;
        double cx = worldPosition.getX() + 0.5 + Math.cos(angle) * radius;
        double cz = worldPosition.getZ() + 0.5 + Math.sin(angle) * radius;
        level.sendParticles(ParticleTypes.PORTAL, cx, worldPosition.getY() + 0.5, cz, 1, 0, 0.5, 0, 0);
    }
    
    // IManaReceiver implementation
    @Override
    public float receiveMana(float amount) {
        float received = Math.min(amount, MAX_MANA - mana);
        mana += received;
        setChanged();
        return received;
    }
    
    @Override
    public float getMana() { return mana; }
    
    @Override
    public float getMaxMana() { return MAX_MANA; }
    
    @Override
    public boolean canReceiveMana() { return mana < MAX_MANA; }
    
    // Getters
    public boolean isActive() { return isActive; }
    public int getRitualProgress() { return ritualProgress; }
    public int getRitualTime() { return RITUAL_TIME; }
    public String getCurrentPattern() { return currentPattern; }
    
    @Nullable
    public RitualCircleRecipe getCurrentRecipe() { return currentRecipe; }
    
    // NBT
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putFloat("Mana", mana);
        tag.putBoolean("Active", isActive);
        tag.putInt("Progress", ritualProgress);
        tag.putString("Pattern", currentPattern);
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        mana = tag.getFloat("Mana");
        isActive = tag.getBoolean("Active");
        ritualProgress = tag.getInt("Progress");
        currentPattern = tag.getString("Pattern");
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
