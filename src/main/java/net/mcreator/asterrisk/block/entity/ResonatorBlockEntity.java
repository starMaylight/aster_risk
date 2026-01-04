package net.mcreator.asterrisk.block.entity;

import net.mcreator.asterrisk.mana.BlockManaCapability;
import net.mcreator.asterrisk.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.mcreator.asterrisk.registry.ModParticles;
import net.mcreator.asterrisk.registry.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * 共振器のBlockEntity
 * 他の共振器とリンクしてマナを転送する
 * 
 * 動作:
 * - 隣接ブロックからマナを抽出可能な場合、リンク先にマナを送る
 * - 一方向のみ動作（入力と出力は同時に行わない）
 */
public class ResonatorBlockEntity extends BlockEntity {

    private final int tier;
    private final int maxRange;
    private final float transferRate;
    
    // リンク先の座標リスト
    private final Set<BlockPos> linkedPositions = new HashSet<>();
    
    // tick管理
    private int tickCounter = 0;

    public ResonatorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int tier) {
        super(type, pos, state);
        this.tier = tier;
        
        // Tier別の設定
        switch (tier) {
            case 1 -> { maxRange = 16; transferRate = 25f; }
            case 2 -> { maxRange = 32; transferRate = 50f; }
            case 3 -> { maxRange = 64; transferRate = 150f; }
            default -> { maxRange = 16; transferRate = 25f; }
        }
    }

    /**
     * BlockEntityType.Builder用のファクトリメソッド
     */
    public static ResonatorBlockEntity createTier1(BlockPos pos, BlockState state) {
        return new ResonatorBlockEntity(ModBlockEntities.RESONATOR_TIER1.get(), pos, state, 1);
    }

    public static ResonatorBlockEntity createTier2(BlockPos pos, BlockState state) {
        return new ResonatorBlockEntity(ModBlockEntities.RESONATOR_TIER2.get(), pos, state, 2);
    }

    public static ResonatorBlockEntity createTier3(BlockPos pos, BlockState state) {
        return new ResonatorBlockEntity(ModBlockEntities.RESONATOR_TIER3.get(), pos, state, 3);
    }

    /**
     * 別の共振器とリンク
     */
    public boolean linkTo(BlockPos targetPos) {
        if (targetPos.equals(worldPosition)) return false;
        
        // 距離チェック
        double distance = Math.sqrt(targetPos.distSqr(worldPosition));
        if (distance > maxRange) return false;
        
        // リンク追加
        linkedPositions.add(targetPos);
        setChanged();
        
        // 相手側にも追加
        if (level != null) {
            BlockEntity be = level.getBlockEntity(targetPos);
            if (be instanceof ResonatorBlockEntity other) {
                other.linkedPositions.add(worldPosition);
                other.setChanged();
            }
        }
        
        return true;
    }

    /**
     * リンク解除
     */
    public void unlinkFrom(BlockPos targetPos) {
        linkedPositions.remove(targetPos);
        setChanged();
        
        if (level != null) {
            BlockEntity be = level.getBlockEntity(targetPos);
            if (be instanceof ResonatorBlockEntity other) {
                other.linkedPositions.remove(worldPosition);
                other.setChanged();
            }
        }
    }

    /**
     * 全リンク解除
     */
    public void unlinkAll() {
        for (BlockPos pos : new HashSet<>(linkedPositions)) {
            unlinkFrom(pos);
        }
    }

    public Set<BlockPos> getLinkedPositions() {
        return new HashSet<>(linkedPositions);
    }

    public int getTier() {
        return tier;
    }

    public int getMaxRange() {
        return maxRange;
    }

    public float getTransferRate() {
        return transferRate;
    }

    /**
     * 毎tick処理：隣接ブロックからマナを吸い上げ、リンク先に送信
     */
    public static void serverTick(Level level, BlockPos pos, BlockState state, ResonatorBlockEntity entity) {
        if (level.isClientSide()) return;
        
        entity.tickCounter++;
        if (entity.tickCounter < 10) return; // 0.5秒ごとに処理
        entity.tickCounter = 0;
        
        // 隣接ブロックからマナを取得できるか確認
        ManaSourceResult sourceResult = entity.findAdjacentManaSource();
        if (sourceResult == null) return;
        
        // リンク先にマナを転送
        for (BlockPos linkedPos : entity.linkedPositions) {
            BlockEntity linkedBE = level.getBlockEntity(linkedPos);
            if (linkedBE instanceof ResonatorBlockEntity linkedResonator) {
                // リンク先共振器の隣接ブロックにマナを送る
                ManaReceiverResult receiverResult = linkedResonator.findAdjacentManaReceiver();
                if (receiverResult != null) {
                    float toTransfer = Math.min(entity.transferRate / 2f, sourceResult.storage.getMana());
                    toTransfer = Math.min(toTransfer, receiverResult.storage.getMaxMana() - receiverResult.storage.getMana());
                    
                    if (toTransfer > 0.1f) {
                        float extracted = sourceResult.storage.extractMana(toTransfer);
                        if (extracted > 0) {
                            receiverResult.storage.addMana(extracted);
                            
                            // パーティクルエフェクト
                            if (level instanceof ServerLevel serverLevel) {
                                spawnTransferParticles(serverLevel, pos, linkedPos);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * マナソース検索結果
     */
    private static class ManaSourceResult {
        final BlockManaCapability.IBlockMana storage;
        final BlockPos pos;
        
        ManaSourceResult(BlockManaCapability.IBlockMana storage, BlockPos pos) {
            this.storage = storage;
            this.pos = pos;
        }
    }

    /**
     * マナ受信先検索結果
     */
    private static class ManaReceiverResult {
        final BlockManaCapability.IBlockMana storage;
        final BlockPos pos;
        
        ManaReceiverResult(BlockManaCapability.IBlockMana storage, BlockPos pos) {
            this.storage = storage;
            this.pos = pos;
        }
    }

    /**
     * 隣接するマナストレージを探す（抽出元）
     * 他のResonatorは除外
     */
    @Nullable
    private ManaSourceResult findAdjacentManaSource() {
        if (level == null) return null;
        
        for (Direction dir : Direction.values()) {
            BlockPos adjacentPos = worldPosition.relative(dir);
            BlockEntity be = level.getBlockEntity(adjacentPos);
            
            // 他のResonatorは除外
            if (be instanceof ResonatorBlockEntity) continue;
            
            if (be != null) {
                LazyOptional<BlockManaCapability.IBlockMana> cap = be.getCapability(BlockManaCapability.BLOCK_MANA, dir.getOpposite());
                if (cap.isPresent()) {
                    BlockManaCapability.IBlockMana storage = cap.resolve().orElse(null);
                    if (storage != null && storage.canExtract() && storage.getMana() > 0) {
                        return new ManaSourceResult(storage, adjacentPos);
                    }
                }
            }
        }
        return null;
    }

    /**
     * 隣接するマナ受信可能ブロックを探す（転送先）
     * 他のResonatorは除外
     */
    @Nullable
    private ManaReceiverResult findAdjacentManaReceiver() {
        if (level == null) return null;
        
        for (Direction dir : Direction.values()) {
            BlockPos adjacentPos = worldPosition.relative(dir);
            BlockEntity be = level.getBlockEntity(adjacentPos);
            
            // 他のResonatorは除外
            if (be instanceof ResonatorBlockEntity) continue;
            
            if (be != null) {
                LazyOptional<BlockManaCapability.IBlockMana> cap = be.getCapability(BlockManaCapability.BLOCK_MANA, dir.getOpposite());
                if (cap.isPresent()) {
                    BlockManaCapability.IBlockMana storage = cap.resolve().orElse(null);
                    if (storage != null && storage.canReceive() && storage.getMana() < storage.getMaxMana()) {
                        return new ManaReceiverResult(storage, adjacentPos);
                    }
                }
            }
        }
        return null;
    }

    private static void spawnTransferParticles(ServerLevel level, BlockPos from, BlockPos to) {
        double dx = (to.getX() - from.getX()) / 10.0;
        double dy = (to.getY() - from.getY()) / 10.0;
        double dz = (to.getZ() - from.getZ()) / 10.0;
        
        for (int i = 0; i < 5; i++) {
            double x = from.getX() + 0.5 + dx * (i + Math.random() * 2);
            double y = from.getY() + 0.5 + dy * (i + Math.random() * 2);
            double z = from.getZ() + 0.5 + dz * (i + Math.random() * 2);
            level.sendParticles(ModParticles.MANA_FLOW.get(), x, y, z, 1, dx * 0.5, dy * 0.5, dz * 0.5, 0.02);
        }
        
        // 送信元に光
        level.sendParticles(ModParticles.LUNAR_SPARKLE.get(), from.getX() + 0.5, from.getY() + 0.8, from.getZ() + 0.5, 2, 0.1, 0.1, 0.1, 0.01);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("tier", tier);
        
        ListTag linkList = new ListTag();
        for (BlockPos pos : linkedPositions) {
            linkList.add(NbtUtils.writeBlockPos(pos));
        }
        tag.put("links", linkList);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        
        linkedPositions.clear();
        if (tag.contains("links")) {
            ListTag linkList = tag.getList("links", Tag.TAG_COMPOUND);
            for (int i = 0; i < linkList.size(); i++) {
                linkedPositions.add(NbtUtils.readBlockPos(linkList.getCompound(i)));
            }
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
