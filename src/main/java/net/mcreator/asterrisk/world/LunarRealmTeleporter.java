package net.mcreator.asterrisk.world;

import net.mcreator.asterrisk.block.LunarPortalBlock;
import net.mcreator.asterrisk.init.AsterRiskModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;

/**
 * 月の領域用テレポーター
 * Minecraftのネザーポータルを参考にした実装
 */
public class LunarRealmTeleporter implements ITeleporter {
    
    private final ServerLevel level;
    private static final int PORTAL_SEARCH_RADIUS = 128;
    
    public LunarRealmTeleporter(ServerLevel level) {
        this.level = level;
    }
    
    @Nullable
    @Override
    public PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
        BlockPos destPos = entity.blockPosition();
        
        // チャンクをロード
        this.level.getChunkSource().addRegionTicket(TicketType.PORTAL, new ChunkPos(destPos), 3, destPos);
        
        // 既存のポータルを検索
        Optional<BlockPos> existingPortal = findExistingPortal(destPos);
        
        BlockPos spawnPos;
        if (existingPortal.isPresent()) {
            spawnPos = existingPortal.get();
        } else {
            // 新しいポータルを作成
            spawnPos = createNewPortal(destPos);
        }
        
        return new PortalInfo(
            new Vec3(spawnPos.getX() + 0.5, spawnPos.getY() + 0.1, spawnPos.getZ() + 0.5),
            Vec3.ZERO, entity.getYRot(), entity.getXRot()
        );
    }
    
    @Override
    public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
        return repositionEntity.apply(false);
    }
    
    /**
     * 既存のポータルを検索
     */
    private Optional<BlockPos> findExistingPortal(BlockPos searchCenter) {
        WorldBorder worldBorder = this.level.getWorldBorder();
        int minY = this.level.getMinBuildHeight();
        int maxY = this.level.getMaxBuildHeight();
        
        BlockPos closestPortal = null;
        double closestDistSq = Double.MAX_VALUE;
        
        // 検索範囲内を探索
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        
        for (int x = searchCenter.getX() - PORTAL_SEARCH_RADIUS; x <= searchCenter.getX() + PORTAL_SEARCH_RADIUS; x++) {
            for (int z = searchCenter.getZ() - PORTAL_SEARCH_RADIUS; z <= searchCenter.getZ() + PORTAL_SEARCH_RADIUS; z++) {
                if (!worldBorder.isWithinBounds(new BlockPos(x, 0, z))) {
                    continue;
                }
                
                for (int y = minY; y < maxY; y++) {
                    mutablePos.set(x, y, z);
                    
                    if (this.level.getBlockState(mutablePos).is(AsterRiskModBlocks.LUNAR_PORTAL.get())) {
                        // ポータルの安全なスポーン地点を見つける
                        BlockPos safePos = findSafeSpawnInPortal(mutablePos.immutable());
                        if (safePos != null) {
                            double distSq = safePos.distToCenterSqr(searchCenter.getX(), searchCenter.getY(), searchCenter.getZ());
                            if (distSq < closestDistSq) {
                                closestDistSq = distSq;
                                closestPortal = safePos;
                            }
                        }
                        
                        // 近いポータルが見つかったら早期終了
                        if (closestDistSq < 64 * 64) {
                            return Optional.ofNullable(closestPortal);
                        }
                    }
                }
            }
        }
        
        return Optional.ofNullable(closestPortal);
    }
    
    /**
     * ポータル内で安全なスポーン地点を見つける
     */
    @Nullable
    private BlockPos findSafeSpawnInPortal(BlockPos portalPos) {
        // ポータルの最下部を見つける
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        mutablePos.set(portalPos);
        
        while (this.level.getBlockState(mutablePos.below()).is(AsterRiskModBlocks.LUNAR_PORTAL.get())) {
            mutablePos.move(Direction.DOWN);
        }
        
        // ポータルの隣の安全な場所を見つける
        BlockState portalState = this.level.getBlockState(mutablePos);
        Direction.Axis axis = portalState.getValue(LunarPortalBlock.AXIS);
        
        // ポータルの横（通過方向）に立てる場所を探す
        Direction[] checkDirs = (axis == Direction.Axis.X) 
            ? new Direction[]{Direction.NORTH, Direction.SOUTH}
            : new Direction[]{Direction.EAST, Direction.WEST};
        
        for (Direction dir : checkDirs) {
            BlockPos sidePos = mutablePos.relative(dir);
            if (isSafeSpawnLocation(sidePos)) {
                return sidePos;
            }
        }
        
        // 横が安全でない場合はポータルの位置をそのまま返す
        return mutablePos.immutable();
    }
    
    /**
     * スポーン地点が安全かどうかを確認
     */
    private boolean isSafeSpawnLocation(BlockPos pos) {
        BlockState below = this.level.getBlockState(pos.below());
        BlockState at = this.level.getBlockState(pos);
        BlockState above = this.level.getBlockState(pos.above());
        
        // 足元に固体ブロック、プレイヤーの位置と頭上が空いている
        return below.isSolid() && !at.isSolid() && !above.isSolid();
    }
    
    /**
     * 新しいポータルを作成
     */
    private BlockPos createNewPortal(BlockPos targetPos) {
        WorldBorder worldBorder = this.level.getWorldBorder();
        
        // ワールドボーダー内に収める
        int x = (int) Math.max(worldBorder.getMinX() + 16, Math.min(targetPos.getX(), worldBorder.getMaxX() - 16));
        int z = (int) Math.max(worldBorder.getMinZ() + 16, Math.min(targetPos.getZ(), worldBorder.getMaxZ() - 16));
        
        // 安全な設置場所を見つける
        BlockPos safePos = findSuitablePortalLocation(x, z);
        
        // ポータルを構築
        return buildPortalStructure(safePos);
    }
    
    /**
     * ポータルの設置に適した場所を見つける
     */
    private BlockPos findSuitablePortalLocation(int x, int z) {
        int surfaceY = this.level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        
        // 地表が取得できない場合のフォールバック
        if (surfaceY <= this.level.getMinBuildHeight()) {
            // Lunar Realmのような空洞ディメンションの場合
            // 下から上に向かって最初の固体ブロックを探す
            for (int y = this.level.getMinBuildHeight() + 1; y < this.level.getMaxBuildHeight() - 10; y++) {
                BlockPos checkPos = new BlockPos(x, y, z);
                if (this.level.getBlockState(checkPos).isSolid() && 
                    !this.level.getBlockState(checkPos.above()).isSolid() &&
                    !this.level.getBlockState(checkPos.above(2)).isSolid()) {
                    surfaceY = y + 1;
                    break;
                }
            }
            
            // それでも見つからない場合は固定高さ
            if (surfaceY <= this.level.getMinBuildHeight()) {
                surfaceY = 64;
            }
        }
        
        // 高さの制限
        surfaceY = Math.max(this.level.getMinBuildHeight() + 5, Math.min(surfaceY, this.level.getMaxBuildHeight() - 10));
        
        return new BlockPos(x, surfaceY, z);
    }
    
    /**
     * ポータル構造物を構築
     */
    private BlockPos buildPortalStructure(BlockPos basePos) {
        Block frameBlock = LunarPortalBlock.getFrameBlock();
        BlockState frameState = frameBlock.defaultBlockState();
        BlockState portalState = AsterRiskModBlocks.LUNAR_PORTAL.get().defaultBlockState()
            .setValue(LunarPortalBlock.AXIS, Direction.Axis.X);
        
        // 設置場所を確保（空気に置き換え）
        for (int dx = -1; dx <= 4; dx++) {
            for (int dy = 0; dy <= 5; dy++) {
                for (int dz = -2; dz <= 2; dz++) {
                    BlockPos clearPos = basePos.offset(dx, dy, dz);
                    BlockState currentState = this.level.getBlockState(clearPos);
                    if (!currentState.is(frameBlock) && !currentState.is(AsterRiskModBlocks.LUNAR_PORTAL.get())) {
                        this.level.setBlock(clearPos, Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }
        }
        
        // 土台プラットフォームを先に作成（足場）
        for (int dx = -1; dx <= 4; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos platformPos = basePos.offset(dx, -1, dz);
                this.level.setBlock(platformPos, frameState, 3);
            }
        }
        
        // ポータルフレーム構築
        // 底部（y=0）
        for (int dx = 0; dx <= 3; dx++) {
            this.level.setBlock(basePos.offset(dx, 0, 0), frameState, 3);
        }
        
        // 上部（y=4）
        for (int dx = 0; dx <= 3; dx++) {
            this.level.setBlock(basePos.offset(dx, 4, 0), frameState, 3);
        }
        
        // 左側（x=0）
        for (int dy = 0; dy <= 4; dy++) {
            this.level.setBlock(basePos.offset(0, dy, 0), frameState, 3);
        }
        
        // 右側（x=3）
        for (int dy = 0; dy <= 4; dy++) {
            this.level.setBlock(basePos.offset(3, dy, 0), frameState, 3);
        }
        
        // ポータルブロック（内部 2x3）
        for (int dx = 1; dx <= 2; dx++) {
            for (int dy = 1; dy <= 3; dy++) {
                this.level.setBlock(basePos.offset(dx, dy, 0), portalState, 3);
            }
        }
        
        // スポーン地点を返す（ポータルの手前）
        return basePos.offset(1, 0, -1);
    }
}
