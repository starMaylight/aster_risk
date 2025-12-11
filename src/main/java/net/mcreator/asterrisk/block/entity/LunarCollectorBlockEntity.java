package net.mcreator.asterrisk.block.entity;

import net.mcreator.asterrisk.mana.BlockManaCapability;
import net.mcreator.asterrisk.network.AsterRiskNetwork;
import net.mcreator.asterrisk.network.BlockManaSyncPacket;
import net.mcreator.asterrisk.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 月光収集器のBlockEntity
 * 夜間にマナを自動収集する
 */
public class LunarCollectorBlockEntity extends BlockEntity {

    // マナ設定
    public static final float MAX_MANA = 1000f;
    public static final float RECEIVE_RATE = 50f;
    public static final float EXTRACT_RATE = 50f;
    
    // 夜間収集量（tick毎）
    private static final float BASE_COLLECTION_RATE = 0.5f;
    
    // 月齢ボーナス
    private static final float[] MOON_PHASE_MULTIPLIER = {
        2.0f,  // 満月
        1.5f,  // 更待月
        1.0f,  // 下弦
        0.5f,  // 有明月
        0.25f, // 新月
        0.5f,  // 三日月
        1.0f,  // 上弦
        1.5f   // 十三夜
    };

    private final BlockManaCapability.BlockMana manaStorage;
    private final LazyOptional<BlockManaCapability.IBlockMana> manaHandler;
    
    // クライアント用キャッシュ
    private float clientMana = 0f;
    private float clientMaxMana = MAX_MANA;
    
    // 同期用カウンター
    private int syncCounter = 0;

    public LunarCollectorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.manaStorage = new BlockManaCapability.BlockMana(MAX_MANA, RECEIVE_RATE, EXTRACT_RATE, true, true);
        this.manaHandler = LazyOptional.of(() -> manaStorage);
    }

    /**
     * BlockEntityType.Builder用のファクトリメソッド
     */
    public static LunarCollectorBlockEntity create(BlockPos pos, BlockState state) {
        return new LunarCollectorBlockEntity(ModBlockEntities.LUNAR_COLLECTOR.get(), pos, state);
    }

    /**
     * 毎tick呼ばれる処理
     */
    public static void serverTick(Level level, BlockPos pos, BlockState state, LunarCollectorBlockEntity entity) {
        if (level == null || level.isClientSide()) return;

        // 時間取得
        long dayTime = level.getDayTime() % 24000;
        
        // 夜間判定（13000-23000が夜）
        boolean isNight = dayTime >= 13000 && dayTime < 23000;
        
        float collectionRate = 0f;
        
        if (isNight) {
            // 月齢ボーナス
            int moonPhase = level.getMoonPhase();
            float multiplier = MOON_PHASE_MULTIPLIER[moonPhase];
            
            // 空が見えているかチェック
            boolean canSeeSky = level.canSeeSky(pos.above());
            if (canSeeSky) {
                multiplier *= 1.5f;
            }
            
            collectionRate = BASE_COLLECTION_RATE * multiplier;
        } else {
            // 昼間は少量のみ（テスト用）
            collectionRate = 0.1f;
        }
        
        // マナ収集
        if (collectionRate > 0 && entity.manaStorage.getMana() < MAX_MANA) {
            float newMana = Math.min(entity.manaStorage.getMana() + collectionRate, MAX_MANA);
            entity.manaStorage.setMana(newMana);
            entity.setChanged();
        }
        
        // 定期的にクライアントに同期（1秒毎）
        entity.syncCounter++;
        if (entity.syncCounter >= 20) {
            entity.syncCounter = 0;
            entity.syncToClients();
        }
    }

    private void syncToClients() {
        if (level instanceof ServerLevel serverLevel) {
            BlockManaSyncPacket packet = new BlockManaSyncPacket(worldPosition, manaStorage.getMana(), manaStorage.getMaxMana());
            for (ServerPlayer player : serverLevel.players()) {
                if (player.blockPosition().distSqr(worldPosition) < 64 * 64) {
                    AsterRiskNetwork.sendToPlayer(packet, player);
                }
            }
        }
    }

    public void setClientMana(float mana, float maxMana) {
        this.clientMana = mana;
        this.clientMaxMana = maxMana;
    }

    public float getClientMana() {
        return clientMana;
    }

    public float getClientMaxMana() {
        return clientMaxMana;
    }

    public float getMana() {
        return manaStorage.getMana();
    }

    public float getMaxMana() {
        return manaStorage.getMaxMana();
    }

    public float extractMana(float amount) {
        float extracted = manaStorage.extractMana(amount);
        if (extracted > 0) {
            setChanged();
        }
        return extracted;
    }

    public float addMana(float amount) {
        float added = manaStorage.addMana(amount);
        if (added > 0) {
            setChanged();
        }
        return added;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("manaStorage", manaStorage.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("manaStorage")) {
            manaStorage.deserializeNBT(tag.getCompound("manaStorage"));
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putFloat("mana", manaStorage.getMana());
        tag.putFloat("maxMana", manaStorage.getMaxMana());
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        clientMana = tag.getFloat("mana");
        clientMaxMana = tag.getFloat("maxMana");
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
