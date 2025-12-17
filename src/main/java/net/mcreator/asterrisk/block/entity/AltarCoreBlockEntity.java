package net.mcreator.asterrisk.block.entity;

import net.mcreator.asterrisk.mana.BlockManaCapability;
import net.mcreator.asterrisk.recipe.RitualRecipe;
import net.mcreator.asterrisk.recipe.RitualRecipeManager;
import net.mcreator.asterrisk.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * 祭壇コアのBlockEntity
 * 周囲の台座を検出し、儀式を実行する
 * オベリスクとリンクして特殊エネルギーを使用可能
 */
public class AltarCoreBlockEntity extends BlockEntity {

    // マナ設定
    public static final float MAX_MANA = 2000f;
    public static final float RECEIVE_RATE = 100f;

    // 台座の検出位置（十字 + 斜め = 8方向、距離2）
    private static final BlockPos[] PEDESTAL_OFFSETS = {
        new BlockPos(2, 0, 0),   // 東
        new BlockPos(-2, 0, 0),  // 西
        new BlockPos(0, 0, 2),   // 南
        new BlockPos(0, 0, -2),  // 北
        new BlockPos(2, 0, 2),   // 南東
        new BlockPos(-2, 0, 2),  // 南西
        new BlockPos(2, 0, -2),  // 北東
        new BlockPos(-2, 0, -2)  // 北西
    };

    private final BlockManaCapability.BlockMana manaStorage;
    private final LazyOptional<BlockManaCapability.IBlockMana> manaHandler;

    // 儀式状態
    private boolean ritualInProgress = false;
    private int ritualProgress = 0;
    private static final int RITUAL_DURATION = 60; // 3秒

    // 現在の儀式レシピ（進行中のみ）
    @Nullable
    private RitualRecipe currentRecipe = null;
    
    // リンクされたオベリスク
    private Set<BlockPos> linkedObelisks = new HashSet<>();

    public AltarCoreBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.manaStorage = new BlockManaCapability.BlockMana(MAX_MANA, RECEIVE_RATE, 0, true, false);
        this.manaHandler = LazyOptional.of(() -> manaStorage);
    }

    /**
     * ファクトリメソッド
     */
    public static AltarCoreBlockEntity create(BlockPos pos, BlockState state) {
        return new AltarCoreBlockEntity(ModBlockEntities.ALTAR_CORE.get(), pos, state);
    }
    
    // ===== オベリスクリンク機能 =====
    
    public void addLinkedObelisk(BlockPos pos) {
        linkedObelisks.add(pos);
        setChanged();
    }
    
    public void removeLinkedObelisk(BlockPos pos) {
        linkedObelisks.remove(pos);
        setChanged();
    }
    
    public void clearLinkedObelisks() {
        linkedObelisks.clear();
        setChanged();
    }
    
    public Set<BlockPos> getLinkedObelisks() {
        return linkedObelisks;
    }
    
    public boolean hasLinkedObeliskOfType(ObeliskEnergyType type) {
        if (level == null) return false;
        for (BlockPos pos : linkedObelisks) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ObeliskBlockEntity obelisk) {
                if (obelisk.getEnergyType() == type) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * 特定タイプのオベリスクからエネルギーを取得
     */
    @Nullable
    public ObeliskBlockEntity getLinkedObelisk(ObeliskEnergyType type) {
        if (level == null) return null;
        for (BlockPos pos : linkedObelisks) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ObeliskBlockEntity obelisk) {
                if (obelisk.getEnergyType() == type) {
                    return obelisk;
                }
            }
        }
        return null;
    }
    
    /**
     * リンクされた全オベリスクの情報を取得
     */
    public Map<ObeliskEnergyType, Integer> getAvailableEnergies() {
        Map<ObeliskEnergyType, Integer> energies = new EnumMap<>(ObeliskEnergyType.class);
        if (level == null) return energies;
        
        for (BlockPos pos : linkedObelisks) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ObeliskBlockEntity obelisk && obelisk.getEnergyType() != null) {
                energies.merge(obelisk.getEnergyType(), obelisk.getStoredEnergy(), Integer::sum);
            }
        }
        return energies;
    }
    
    /**
     * 特定タイプのエネルギーを消費
     */
    public boolean consumeObeliskEnergy(ObeliskEnergyType type, int amount) {
        if (level == null) return false;
        
        int remaining = amount;
        for (BlockPos pos : linkedObelisks) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ObeliskBlockEntity obelisk && obelisk.getEnergyType() == type) {
                int available = obelisk.getStoredEnergy();
                int toConsume = Math.min(available, remaining);
                if (toConsume > 0) {
                    obelisk.consumeEnergy(toConsume);
                    remaining -= toConsume;
                    if (remaining <= 0) return true;
                }
            }
        }
        return remaining <= 0;
    }
    
    /**
     * 特定タイプのエネルギーが十分にあるか確認
     */
    public boolean hasEnoughObeliskEnergy(ObeliskEnergyType type, int amount) {
        Map<ObeliskEnergyType, Integer> energies = getAvailableEnergies();
        return energies.getOrDefault(type, 0) >= amount;
    }

    /**
     * 周囲の台座を検出
     */
    public List<RitualPedestalBlockEntity> findPedestals() {
        List<RitualPedestalBlockEntity> pedestals = new ArrayList<>();
        if (level == null) return pedestals;

        for (BlockPos offset : PEDESTAL_OFFSETS) {
            BlockPos pedestalPos = worldPosition.offset(offset);
            BlockEntity be = level.getBlockEntity(pedestalPos);
            if (be instanceof RitualPedestalBlockEntity pedestal) {
                pedestals.add(pedestal);
            }
        }
        return pedestals;
    }

    /**
     * アイテムが置かれた台座を検出
     */
    public List<RitualPedestalBlockEntity> findPedestalsWithItems() {
        List<RitualPedestalBlockEntity> result = new ArrayList<>();
        for (RitualPedestalBlockEntity pedestal : findPedestals()) {
            if (pedestal.hasItem()) {
                result.add(pedestal);
            }
        }
        return result;
    }

    /**
     * 台座上のアイテムリストを取得
     */
    private List<ItemStack> getIngredients(List<RitualPedestalBlockEntity> pedestals) {
        List<ItemStack> items = new ArrayList<>();
        for (RitualPedestalBlockEntity pedestal : pedestals) {
            items.add(pedestal.getItem());
        }
        return items;
    }

    /**
     * 儀式を開始
     */
    public boolean startRitual(Player player) {
        if (level == null || level.isClientSide()) return false;
        if (ritualInProgress) {
            player.displayClientMessage(Component.literal("§c Ritual already in progress!"), true);
            return false;
        }

        List<RitualPedestalBlockEntity> pedestalsWithItems = findPedestalsWithItems();
        if (pedestalsWithItems.isEmpty()) {
            player.displayClientMessage(Component.literal("§c No items on pedestals!"), true);
            return false;
        }

        // JSONレシピから検索
        List<ItemStack> ingredients = getIngredients(pedestalsWithItems);
        RitualRecipe recipe = RitualRecipeManager.findRecipe(level, ingredients);

        if (recipe == null) {
            player.displayClientMessage(Component.literal("§c Invalid ritual combination!"), true);
            return false;
        }

        // マナチェック
        if (manaStorage.getMana() < recipe.getManaCost()) {
            player.displayClientMessage(
                Component.literal("§c Not enough mana! Need: " + (int)recipe.getManaCost() + ", Have: " + (int)manaStorage.getMana()),
                true
            );
            return false;
        }
        
        // オベリスクエネルギーチェック
        ObeliskEnergyType requiredType = recipe.getRequiredEnergyType();
        int requiredAmount = recipe.getRequiredEnergyAmount();
        
        if (requiredType != null && requiredAmount > 0) {
            if (!hasEnoughObeliskEnergy(requiredType, requiredAmount)) {
                String typeName = requiredType.getName();
                typeName = typeName.substring(0, 1).toUpperCase() + typeName.substring(1);
                player.displayClientMessage(
                    Component.literal("§c Not enough " + typeName + " energy! Need: " + requiredAmount + 
                        ", Have: " + getAvailableEnergies().getOrDefault(requiredType, 0)),
                    true
                );
                return false;
            }
        }

        // 儀式開始
        ritualInProgress = true;
        ritualProgress = 0;
        currentRecipe = recipe;
        player.displayClientMessage(Component.literal("§d✦ Ritual started..."), true);
        
        // 開始エフェクト
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, worldPosition, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0f, 1.5f);
        }

        setChanged();
        return true;
    }

    /**
     * サーバーティック処理
     */
    public static void serverTick(Level level, BlockPos pos, BlockState state, AltarCoreBlockEntity entity) {
        if (level.isClientSide()) return;

        if (entity.ritualInProgress) {
            entity.ritualProgress++;

            // パーティクルエフェクト
            if (level instanceof ServerLevel serverLevel && entity.ritualProgress % 5 == 0) {
                entity.spawnRitualParticles(serverLevel);
            }

            // 儀式完了
            if (entity.ritualProgress >= RITUAL_DURATION) {
                entity.completeRitual();
            }
        }
    }

    /**
     * 儀式完了処理
     */
    private void completeRitual() {
        if (level == null || currentRecipe == null) {
            ritualInProgress = false;
            ritualProgress = 0;
            currentRecipe = null;
            return;
        }

        List<RitualPedestalBlockEntity> pedestalsWithItems = findPedestalsWithItems();

        if (manaStorage.getMana() >= currentRecipe.getManaCost()) {
            // オベリスクエネルギー消費
            ObeliskEnergyType requiredType = currentRecipe.getRequiredEnergyType();
            int requiredAmount = currentRecipe.getRequiredEnergyAmount();
            
            if (requiredType != null && requiredAmount > 0) {
                if (!consumeObeliskEnergy(requiredType, requiredAmount)) {
                    // エネルギー不足で失敗
                    ritualInProgress = false;
                    ritualProgress = 0;
                    currentRecipe = null;
                    return;
                }
                
                // オベリスクエネルギー消費エフェクト
                spawnObeliskEnergyEffect(requiredType);
            }
            
            // マナ消費
            manaStorage.setMana(manaStorage.getMana() - currentRecipe.getManaCost());

            // 素材消費
            for (RitualPedestalBlockEntity pedestal : pedestalsWithItems) {
                pedestal.consumeItem();
            }

            // 結果アイテムをスポーン
            ItemStack result = currentRecipe.getResultItem();
            double x = worldPosition.getX() + 0.5;
            double y = worldPosition.getY() + 1.5;
            double z = worldPosition.getZ() + 0.5;
            ItemEntity itemEntity = new ItemEntity(level, x, y, z, result);
            itemEntity.setDeltaMovement(0, 0.2, 0);
            level.addFreshEntity(itemEntity);

            // 完了エフェクト
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.playSound(null, worldPosition, SoundEvents.BEACON_POWER_SELECT, SoundSource.BLOCKS, 1.0f, 1.2f);
                serverLevel.sendParticles(ParticleTypes.END_ROD, x, y, z, 30, 0.5, 0.5, 0.5, 0.1);
            }
        }

        ritualInProgress = false;
        ritualProgress = 0;
        currentRecipe = null;
        setChanged();
    }
    
    /**
     * オベリスクエネルギー消費時のエフェクト
     */
    private void spawnObeliskEnergyEffect(ObeliskEnergyType type) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        
        var particleType = switch (type) {
            case LUNAR -> ParticleTypes.END_ROD;
            case STELLAR -> ParticleTypes.FIREWORK;
            case SOLAR -> ParticleTypes.FLAME;
            case VOID -> ParticleTypes.PORTAL;
        };
        
        // リンクされたオベリスクから祭壇への光線
        for (BlockPos obeliskPos : linkedObelisks) {
            BlockEntity be = level.getBlockEntity(obeliskPos);
            if (be instanceof ObeliskBlockEntity obelisk && obelisk.getEnergyType() == type) {
                double ox = obeliskPos.getX() + 0.5;
                double oy = obeliskPos.getY() + 1.5;
                double oz = obeliskPos.getZ() + 0.5;
                double ax = worldPosition.getX() + 0.5;
                double ay = worldPosition.getY() + 1.0;
                double az = worldPosition.getZ() + 0.5;
                
                for (int i = 0; i < 10; i++) {
                    double t = i / 10.0;
                    double x = ox + (ax - ox) * t;
                    double y = oy + (ay - oy) * t;
                    double z = oz + (az - oz) * t;
                    serverLevel.sendParticles(particleType, x, y, z, 2, 0.1, 0.1, 0.1, 0.02);
                }
            }
        }
    }

    /**
     * 儀式パーティクル
     */
    private void spawnRitualParticles(ServerLevel level) {
        for (RitualPedestalBlockEntity pedestal : findPedestalsWithItems()) {
            BlockPos pPos = pedestal.getBlockPos();
            double px = pPos.getX() + 0.5;
            double py = pPos.getY() + 1.2;
            double pz = pPos.getZ() + 0.5;

            // 台座から祭壇への光線
            double dx = (worldPosition.getX() + 0.5 - px) / 10;
            double dy = (worldPosition.getY() + 1.0 - py) / 10;
            double dz = (worldPosition.getZ() + 0.5 - pz) / 10;

            for (int i = 0; i < 5; i++) {
                level.sendParticles(ParticleTypes.ENCHANT,
                    px + dx * i * 2, py + dy * i * 2, pz + dz * i * 2,
                    1, 0.05, 0.05, 0.05, 0);
            }
        }

        // コア周囲のパーティクル
        level.sendParticles(ParticleTypes.PORTAL,
            worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5,
            5, 0.3, 0.3, 0.3, 0.5);
    }

    public float getMana() {
        return manaStorage.getMana();
    }

    public float getMaxMana() {
        return manaStorage.getMaxMana();
    }

    public boolean isRitualInProgress() {
        return ritualInProgress;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("manaStorage", manaStorage.serializeNBT());
        tag.putBoolean("ritualInProgress", ritualInProgress);
        tag.putInt("ritualProgress", ritualProgress);
        
        // オベリスクリンク保存
        int[] obeliskData = new int[linkedObelisks.size() * 3];
        int i = 0;
        for (BlockPos pos : linkedObelisks) {
            obeliskData[i++] = pos.getX();
            obeliskData[i++] = pos.getY();
            obeliskData[i++] = pos.getZ();
        }
        tag.putIntArray("LinkedObelisks", obeliskData);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("manaStorage")) {
            manaStorage.deserializeNBT(tag.getCompound("manaStorage"));
        }
        ritualInProgress = tag.getBoolean("ritualInProgress");
        ritualProgress = tag.getInt("ritualProgress");
        
        // オベリスクリンク読み込み
        linkedObelisks.clear();
        if (tag.contains("LinkedObelisks")) {
            int[] obeliskData = tag.getIntArray("LinkedObelisks");
            for (int i = 0; i < obeliskData.length; i += 3) {
                linkedObelisks.add(new BlockPos(obeliskData[i], obeliskData[i + 1], obeliskData[i + 2]));
            }
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putFloat("mana", manaStorage.getMana());
        tag.putBoolean("ritualInProgress", ritualInProgress);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        if (tag.contains("mana")) {
            manaStorage.setMana(tag.getFloat("mana"));
        }
        ritualInProgress = tag.getBoolean("ritualInProgress");
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
