package net.mcreator.asterrisk.world.structure;

import net.mcreator.asterrisk.init.AsterRiskModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

/**
 * Eclipse Sanctumの構造ピース
 * 円形の闘技場風の聖域で、中央にEclipse Altarがある
 */
public class EclipseSanctumPiece extends StructurePiece {
    
    private final BlockPos center;
    
    public EclipseSanctumPiece(BlockPos center, RandomSource random) {
        super(ModStructurePieces.ECLIPSE_SANCTUM_PIECE.get(), 0, 
            new BoundingBox(center.getX() - 12, center.getY() - 3, center.getZ() - 12,
                           center.getX() + 12, center.getY() + 10, center.getZ() + 12));
        this.center = center;
    }
    
    public EclipseSanctumPiece(StructurePieceSerializationContext context, CompoundTag tag) {
        super(ModStructurePieces.ECLIPSE_SANCTUM_PIECE.get(), tag);
        this.center = new BlockPos(tag.getInt("cx"), tag.getInt("cy"), tag.getInt("cz"));
    }
    
    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        tag.putInt("cx", center.getX());
        tag.putInt("cy", center.getY());
        tag.putInt("cz", center.getZ());
    }
    
    @Override
    public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator,
                           RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos pos) {
        
        BlockState eclipseStone = AsterRiskModBlocks.ECLIPSESTONE.get().defaultBlockState();
        BlockState moonstoneBricks = AsterRiskModBlocks.MOONSTONE_BRICKS.get().defaultBlockState();
        BlockState polishedMoonstone = AsterRiskModBlocks.POLISHED_MOONSTONE.get().defaultBlockState();
        BlockState eclipseAltar = AsterRiskModBlocks.ECLIPSE_ALTAR.get().defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();
        
        int baseY = center.getY();
        
        // 地面を平らにする（半径10）
        for (int dx = -10; dx <= 10; dx++) {
            for (int dz = -10; dz <= 10; dz++) {
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist <= 10) {
                    BlockPos p = new BlockPos(center.getX() + dx, baseY - 1, center.getZ() + dz);
                    if (box.isInside(p)) {
                        level.setBlock(p, eclipseStone, 2);
                    }
                    // 上の空間をクリア
                    for (int dy = 0; dy <= 8; dy++) {
                        BlockPos pAir = new BlockPos(center.getX() + dx, baseY + dy, center.getZ() + dz);
                        if (box.isInside(pAir)) {
                            level.setBlock(pAir, air, 2);
                        }
                    }
                }
            }
        }
        
        // 外壁（円形、半径9-10）
        for (int dx = -10; dx <= 10; dx++) {
            for (int dz = -10; dz <= 10; dz++) {
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist >= 9 && dist <= 10) {
                    for (int dy = 0; dy <= 4; dy++) {
                        BlockPos p = new BlockPos(center.getX() + dx, baseY + dy, center.getZ() + dz);
                        if (box.isInside(p)) {
                            level.setBlock(p, moonstoneBricks, 2);
                        }
                    }
                }
            }
        }
        
        // 4つの柱（各隅）
        int[][] pillarPositions = {{-7, -7}, {7, -7}, {-7, 7}, {7, 7}};
        for (int[] pillarPos : pillarPositions) {
            for (int dy = 0; dy <= 6; dy++) {
                BlockPos p = new BlockPos(center.getX() + pillarPos[0], baseY + dy, center.getZ() + pillarPos[1]);
                if (box.isInside(p)) {
                    level.setBlock(p, polishedMoonstone, 2);
                }
            }
            // 柱の上に光源（ランタン代わり）
            BlockPos top = new BlockPos(center.getX() + pillarPos[0], baseY + 7, center.getZ() + pillarPos[1]);
            if (box.isInside(top)) {
                level.setBlock(top, AsterRiskModBlocks.MOONLIGHT_LANTERN.get().defaultBlockState(), 2);
            }
        }
        
        // 中央プラットフォーム（半径3）
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist <= 3) {
                    BlockPos p = new BlockPos(center.getX() + dx, baseY, center.getZ() + dz);
                    if (box.isInside(p)) {
                        level.setBlock(p, eclipseStone, 2);
                    }
                }
            }
        }
        
        // 中央祭壇
        BlockPos altarPos = new BlockPos(center.getX(), baseY + 1, center.getZ());
        if (box.isInside(altarPos)) {
            level.setBlock(altarPos, eclipseAltar, 2);
        }
        
        // 祭壇周囲の装飾（十字パターン）
        int[][] crossPositions = {{-2, 0}, {2, 0}, {0, -2}, {0, 2}};
        for (int[] crossPos : crossPositions) {
            BlockPos p = new BlockPos(center.getX() + crossPos[0], baseY + 1, center.getZ() + crossPos[1]);
            if (box.isInside(p)) {
                level.setBlock(p, polishedMoonstone, 2);
            }
        }
    }
}
