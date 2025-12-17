package net.mcreator.asterrisk.world;

import net.mcreator.asterrisk.block.LunarPortalBlock;
import net.mcreator.asterrisk.init.AsterRiskModBlocks;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;

/**
 * 月の領域用テレポーター
 */
public class LunarRealmTeleporter implements ITeleporter {
    
    private final ServerLevel level;
    
    public LunarRealmTeleporter(ServerLevel level) {
        this.level = level;
    }
    
    @Nullable
    @Override
    public PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
        // 既存のポータルを検索
        Optional<BlockUtil.FoundRectangle> existingPortal = findExistingPortal(entity.blockPosition());
        
        if (existingPortal.isPresent()) {
            BlockUtil.FoundRectangle rect = existingPortal.get();
            return new PortalInfo(
                new Vec3(rect.minCorner.getX() + 0.5, rect.minCorner.getY(), rect.minCorner.getZ() + 0.5),
                Vec3.ZERO, entity.getYRot(), entity.getXRot()
            );
        }
        
        // ポータルが見つからない場合は新しく作成
        BlockPos destPos = createNewPortal(entity.blockPosition());
        return new PortalInfo(
            new Vec3(destPos.getX() + 0.5, destPos.getY(), destPos.getZ() + 0.5),
            Vec3.ZERO, entity.getYRot(), entity.getXRot()
        );
    }
    
    @Override
    public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
        return repositionEntity.apply(false);
    }
    
    private Optional<BlockUtil.FoundRectangle> findExistingPortal(BlockPos pos) {
        WorldBorder worldBorder = this.level.getWorldBorder();
        int searchRadius = 128;
        
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        
        for (int x = -searchRadius; x <= searchRadius; x += 8) {
            for (int z = -searchRadius; z <= searchRadius; z += 8) {
                for (int y = this.level.getMinBuildHeight(); y < this.level.getMaxBuildHeight(); y += 8) {
                    mutablePos.set(pos.getX() + x, y, pos.getZ() + z);
                    
                    if (!worldBorder.isWithinBounds(mutablePos)) continue;
                    
                    BlockState state = this.level.getBlockState(mutablePos);
                    if (state.is(AsterRiskModBlocks.LUNAR_PORTAL.get())) {
                        // ポータルの角を見つける
                        BlockPos corner = findPortalCorner(mutablePos);
                        return Optional.of(new BlockUtil.FoundRectangle(corner, 2, 3));
                    }
                }
            }
        }
        
        return Optional.empty();
    }
    
    private BlockPos findPortalCorner(BlockPos portalPos) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        mutablePos.set(portalPos);
        
        // 下に移動
        while (this.level.getBlockState(mutablePos.below()).is(AsterRiskModBlocks.LUNAR_PORTAL.get())) {
            mutablePos.move(Direction.DOWN);
        }
        
        // 北に移動
        while (this.level.getBlockState(mutablePos.north()).is(AsterRiskModBlocks.LUNAR_PORTAL.get())) {
            mutablePos.move(Direction.NORTH);
        }
        
        // 西に移動
        while (this.level.getBlockState(mutablePos.west()).is(AsterRiskModBlocks.LUNAR_PORTAL.get())) {
            mutablePos.move(Direction.WEST);
        }
        
        return mutablePos.immutable();
    }
    
    private BlockPos createNewPortal(BlockPos pos) {
        WorldBorder worldBorder = this.level.getWorldBorder();
        int x = pos.getX();
        int z = pos.getZ();
        
        // 地表の高さを取得
        int y = this.level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
        if (y < this.level.getMinBuildHeight() + 5) {
            y = 64;
        }
        
        BlockPos basePos = new BlockPos(x, y, z);
        
        // フレームを構築
        Block frameBlock = LunarPortalBlock.getFrameBlock();
        BlockState frameState = frameBlock.defaultBlockState();
        BlockState portalState = AsterRiskModBlocks.LUNAR_PORTAL.get().defaultBlockState()
            .setValue(LunarPortalBlock.AXIS, Direction.Axis.X);
        
        // 4x5のポータルフレームを作成
        // 底部
        for (int dx = 0; dx < 4; dx++) {
            this.level.setBlock(basePos.offset(dx, 0, 0), frameState, 3);
        }
        
        // 上部
        for (int dx = 0; dx < 4; dx++) {
            this.level.setBlock(basePos.offset(dx, 4, 0), frameState, 3);
        }
        
        // 左側
        for (int dy = 0; dy < 5; dy++) {
            this.level.setBlock(basePos.offset(0, dy, 0), frameState, 3);
        }
        
        // 右側
        for (int dy = 0; dy < 5; dy++) {
            this.level.setBlock(basePos.offset(3, dy, 0), frameState, 3);
        }
        
        // ポータルブロック
        for (int dx = 1; dx < 3; dx++) {
            for (int dy = 1; dy < 4; dy++) {
                this.level.setBlock(basePos.offset(dx, dy, 0), portalState, 3);
            }
        }
        
        // プラットフォーム
        for (int dx = -1; dx < 5; dx++) {
            for (int dz = -2; dz < 3; dz++) {
                if (dz != 0) {
                    BlockPos platformPos = basePos.offset(dx, 0, dz);
                    if (this.level.getBlockState(platformPos).isAir()) {
                        this.level.setBlock(platformPos, frameState, 3);
                    }
                }
            }
        }
        
        return basePos.offset(1, 1, 0);
    }
}
