package net.mcreator.asterrisk.block.entity;

import net.mcreator.asterrisk.mana.IManaReceiver;
import net.mcreator.asterrisk.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * 月光フォーカスブロックエンティティ
 * 
 * 仕様:
 * 1. 夜にマナを蓄積
 * 2. リンク杖で他ブロック(Focus, RitualCircle, ChamberCore, CelestialEnchantTable)と接続可能
 * 3. Focus, ChamberCoreと接続時はレーザーパーティクルを表示、その他は表示しない
 * 4. リンク先に蓄積済みマナを送信
 * 5. 複数のブロックとリンク可能
 */
public class MoonlightFocusBlockEntity extends BlockEntity {
    
    private float storedMana = 0;
    private static final float MAX_MANA = 500;
    private static final float MANA_PER_TICK = 0.25f;
    private static final float SEND_RATE = 5.0f; // 1ティックあたりの送信量
    
    // リンク先のブロック位置（複数可能）
    private Set<BlockPos> linkedPositions = new HashSet<>();
    
    // パーティクルカウンター
    private int tickCounter = 0;
    
    public MoonlightFocusBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MOONLIGHT_FOCUS.get(), pos, state);
    }
    
    public static void serverTick(Level level, BlockPos pos, BlockState state, MoonlightFocusBlockEntity entity) {
        entity.tickCounter++;
        
        // 夜間かつ空が見える場合にマナを蓄積
        boolean isNight = level.isNight();
        boolean canSeeSky = level.canSeeSkyFromBelowWater(pos.above());
        
        if (isNight && canSeeSky) {
            float moonPhaseMultiplier = getMoonPhaseMultiplier(level);
            entity.storedMana = Math.min(MAX_MANA, 
                entity.storedMana + MANA_PER_TICK * moonPhaseMultiplier);
            
            // 蓄積パーティクル（20ティックごと）
            if (entity.tickCounter % 20 == 0 && level instanceof ServerLevel serverLevel) {
                entity.spawnCollectingParticles(serverLevel);
            }
        }
        
        // リンク先にマナを送信
        if (entity.storedMana > 0 && !entity.linkedPositions.isEmpty()) {
            entity.sendManaToLinkedBlocks(level);
        }
        
        entity.setChanged();
    }
    
    /**
     * 月相による倍率
     */
    private static float getMoonPhaseMultiplier(Level level) {
        int phase = level.getMoonPhase();
        return switch (phase) {
            case 0 -> 2.0f;  // 満月
            case 1, 7 -> 1.5f;
            case 2, 6 -> 1.0f;
            case 3, 5 -> 0.5f;
            case 4 -> 0.2f;  // 新月
            default -> 1.0f;
        };
    }
    
    /**
     * リンク先にマナを送信
     */
    private void sendManaToLinkedBlocks(Level level) {
        if (linkedPositions.isEmpty() || storedMana <= 0) return;
        
        // 送信量を分配
        float sendPerTarget = Math.min(SEND_RATE / linkedPositions.size(), storedMana / linkedPositions.size());
        
        Set<BlockPos> toRemove = new HashSet<>();
        
        for (BlockPos linkedPos : linkedPositions) {
            BlockEntity be = level.getBlockEntity(linkedPos);
            
            if (be == null) {
                toRemove.add(linkedPos);
                continue;
            }
            
            float sent = 0;
            boolean showLaser = false;
            
            // リンク先の種類に応じて処理
            if (be instanceof MoonlightFocusBlockEntity focus) {
                // Focus同士のリンク - レーザー表示あり
                sent = focus.receiveManaFromFocus(sendPerTarget);
                showLaser = true;
            } else if (be instanceof FocusChamberCoreBlockEntity chamber) {
                // ChamberCore - レーザー表示あり
                sent = chamber.receiveMana(sendPerTarget);
                showLaser = true;
            } else if (be instanceof RitualCircleBlockEntity circle) {
                // RitualCircle - レーザー表示なし
                sent = circle.receiveMana(sendPerTarget);
                showLaser = false;
            } else if (be instanceof CelestialEnchantingTableBlockEntity enchantTable) {
                // CelestialEnchantTable - レーザー表示なし
                sent = enchantTable.receiveMana(sendPerTarget);
                showLaser = false;
            } else {
                // 不明なブロック - リンク解除
                toRemove.add(linkedPos);
                continue;
            }
            
            storedMana -= sent;
            
            // レーザーパーティクル表示（FocusとChamberCoreのみ）
            if (showLaser && sent > 0 && tickCounter % 3 == 0 && level instanceof ServerLevel serverLevel) {
                spawnLaserParticles(serverLevel, worldPosition, linkedPos);
            }
        }
        
        // 無効なリンクを削除
        linkedPositions.removeAll(toRemove);
    }
    
    /**
     * 他のFocusからマナを受け取る
     */
    public float receiveManaFromFocus(float amount) {
        float received = Math.min(amount, MAX_MANA - storedMana);
        storedMana += received;
        setChanged();
        return received;
    }
    
    /**
     * 月光収集パーティクル
     */
    private void spawnCollectingParticles(ServerLevel level) {
        double x = worldPosition.getX() + 0.5;
        double y = worldPosition.getY() + 1.5;
        double z = worldPosition.getZ() + 0.5;
        
        // 上から降り注ぐ月光
        level.sendParticles(ParticleTypes.END_ROD, x, y, z, 3, 0.3, 0.5, 0.3, 0.01);
    }
    
    /**
     * レーザーパーティクル描画
     */
    private void spawnLaserParticles(ServerLevel level, BlockPos from, BlockPos to) {
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        double dz = to.getZ() - from.getZ();
        double distance = Math.sqrt(dx*dx + dy*dy + dz*dz);
        
        if (distance < 0.1) return;
        
        int steps = Math.max(5, (int)(distance * 4));
        
        for (int i = 0; i <= steps; i++) {
            double t = (double)i / steps;
            double x = from.getX() + 0.5 + dx * t;
            double y = from.getY() + 0.5 + dy * t;
            double z = from.getZ() + 0.5 + dz * t;
            
            // メインビーム
            level.sendParticles(ParticleTypes.END_ROD, x, y, z, 1, 0.02, 0.02, 0.02, 0);
            
            // サブビーム
            if (i % 2 == 0) {
                level.sendParticles(ParticleTypes.ENCHANT, x, y, z, 1, 0.05, 0.05, 0.05, 0.02);
            }
        }
        
        // 始点の輝き
        level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, 
            from.getX() + 0.5, from.getY() + 0.5, from.getZ() + 0.5, 
            2, 0.1, 0.1, 0.1, 0.01);
    }
    
    // ===== リンク機能 =====
    
    /**
     * ブロックへのリンクを追加
     */
    public boolean addLink(BlockPos targetPos, Player player) {
        if (level == null) return false;
        if (targetPos.equals(worldPosition)) return false; // 自分自身
        if (linkedPositions.contains(targetPos)) {
            player.displayClientMessage(Component.translatable("message.aster_risk.focus.already_linked"), true);
            return false;
        }

        BlockEntity be = level.getBlockEntity(targetPos);

        // リンク可能なブロックかチェック
        if (be instanceof MoonlightFocusBlockEntity ||
            be instanceof FocusChamberCoreBlockEntity ||
            be instanceof RitualCircleBlockEntity ||
            be instanceof CelestialEnchantingTableBlockEntity) {

            linkedPositions.add(targetPos);
            setChanged();

            String blockName = be.getClass().getSimpleName().replace("BlockEntity", "");
            player.displayClientMessage(
                Component.translatable("message.aster_risk.focus.linked",
                    blockName, targetPos.getX(), targetPos.getY(), targetPos.getZ()),
                true
            );
            return true;
        }

        player.displayClientMessage(Component.translatable("message.aster_risk.focus.cannot_link"), true);
        return false;
    }
    
    /**
     * 特定のリンクを解除
     */
    public void removeLink(BlockPos targetPos, Player player) {
        if (linkedPositions.remove(targetPos)) {
            setChanged();
            player.displayClientMessage(
                Component.translatable("message.aster_risk.focus.unlinked",
                    targetPos.getX(), targetPos.getY(), targetPos.getZ()),
                true
            );
        }
    }
    
    /**
     * 全リンクを解除
     */
    public void clearAllLinks(Player player) {
        int count = linkedPositions.size();
        linkedPositions.clear();
        setChanged();
        player.displayClientMessage(Component.translatable("message.aster_risk.focus.cleared", count), true);
    }
    
    /**
     * リンク情報を表示
     */
    public void showLinkInfo(Player player) {
        if (linkedPositions.isEmpty()) {
            player.displayClientMessage(Component.translatable("message.aster_risk.focus.no_links"), true);
        } else {
            player.displayClientMessage(
                Component.translatable("message.aster_risk.focus.status",
                    (int)storedMana, (int)MAX_MANA, linkedPositions.size()),
                true
            );
        }
    }
    
    // Getters
    public float getStoredMana() { return storedMana; }
    public float getMaxMana() { return MAX_MANA; }
    public Set<BlockPos> getLinkedPositions() { return linkedPositions; }
    public int getLinkCount() { return linkedPositions.size(); }
    
    public boolean hasLinkTo(BlockPos pos) {
        return linkedPositions.contains(pos);
    }
    
    // NBT
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putFloat("Mana", storedMana);
        
        // リンク位置保存
        ListTag linksTag = new ListTag();
        for (BlockPos pos : linkedPositions) {
            linksTag.add(NbtUtils.writeBlockPos(pos));
        }
        tag.put("Links", linksTag);
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        storedMana = tag.getFloat("Mana");
        
        // リンク位置読み込み
        linkedPositions.clear();
        ListTag linksTag = tag.getList("Links", Tag.TAG_COMPOUND);
        for (int i = 0; i < linksTag.size(); i++) {
            linkedPositions.add(NbtUtils.readBlockPos(linksTag.getCompound(i)));
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
