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
import java.util.ArrayList;
import java.util.List;

/**
 * 祭壇コアのBlockEntity
 * 周囲の台座を検出し、儀式を実行する
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
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("manaStorage")) {
            manaStorage.deserializeNBT(tag.getCompound("manaStorage"));
        }
        ritualInProgress = tag.getBoolean("ritualInProgress");
        ritualProgress = tag.getInt("ritualProgress");
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
