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

/**
 * Stellar Spireの構造ピース
 * 高い尖塔で、頂上にStellar Spire Coreがある
 */
public class StellarSpirePiece extends StructurePiece {
    
    private final BlockPos center;
    
    public StellarSpirePiece(BlockPos center, RandomSource random) {
        super(ModStructurePieces.STELLAR_SPIRE_PIECE.get(), 0, 
            new BoundingBox(center.getX() - 8, center.getY() - 2, center.getZ() - 8,
                           center.getX() + 8, center.getY() + 25, center.getZ() + 8));
        this.center = center;
    }
    
    public StellarSpirePiece(StructurePieceSerializationContext context, CompoundTag tag) {
        super(ModStructurePieces.STELLAR_SPIRE_PIECE.get(), tag);
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
        
        BlockState starfallSand = AsterRiskModBlocks.STARFALLSAND.get().defaultBlockState();
        BlockState moonstoneBricks = AsterRiskModBlocks.MOONSTONE_BRICKS.get().defaultBlockState();
        BlockState polishedMoonstone = AsterRiskModBlocks.POLISHED_MOONSTONE.get().defaultBlockState();
        BlockState stardustBlock = AsterRiskModBlocks.STARDUST_BLOCK.get().defaultBlockState();
        BlockState stellarSpireCore = AsterRiskModBlocks.STELLAR_SPIRE_CORE.get().defaultBlockState();
        BlockState starryGlass = AsterRiskModBlocks.STARRY_GLASS.get().defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();
        
        int baseY = center.getY();
        
        // 基礎プラットフォーム（半径6）
        for (int dx = -6; dx <= 6; dx++) {
            for (int dz = -6; dz <= 6; dz++) {
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist <= 6) {
                    BlockPos p = new BlockPos(center.getX() + dx, baseY - 1, center.getZ() + dz);
                    if (box.isInside(p)) {
                        level.setBlock(p, starfallSand, 2);
                    }
                }
            }
        }
        
        // 尖塔本体（段々と細くなる）
        int spireHeight = 20;
        for (int dy = 0; dy < spireHeight; dy++) {
            double radius = Math.max(1, 4.0 - (dy * 0.18));
            
            for (int dx = -5; dx <= 5; dx++) {
                for (int dz = -5; dz <= 5; dz++) {
                    double dist = Math.sqrt(dx * dx + dz * dz);
                    
                    if (dist <= radius) {
                        BlockPos p = new BlockPos(center.getX() + dx, baseY + dy, center.getZ() + dz);
                        if (box.isInside(p)) {
                            // 外周はムーンストーンブリック、内側は空気
                            if (dist >= radius - 1.0) {
                                level.setBlock(p, moonstoneBricks, 2);
                            } else if (dy > 0 && dy < spireHeight - 1) {
                                level.setBlock(p, air, 2);
                            } else {
                                level.setBlock(p, polishedMoonstone, 2);
                            }
                        }
                    }
                }
            }
            
            // 螺旋状の窓（スターリーグラス）
            if (dy % 4 == 2 && dy < spireHeight - 2) {
                double angle = dy * 0.5;
                int wx = (int) Math.round(Math.cos(angle) * (radius - 0.5));
                int wz = (int) Math.round(Math.sin(angle) * (radius - 0.5));
                BlockPos windowPos = new BlockPos(center.getX() + wx, baseY + dy, center.getZ() + wz);
                if (box.isInside(windowPos)) {
                    level.setBlock(windowPos, starryGlass, 2);
                }
            }
        }
        
        // 頂上プラットフォーム（Starfall Sandで召喚条件を満たす）
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist <= 2) {
                    BlockPos p = new BlockPos(center.getX() + dx, baseY + spireHeight, center.getZ() + dz);
                    if (box.isInside(p)) {
                        // 中央以外はStarfall Sand、装飾としてStardust Blockを角に
                        if (Math.abs(dx) == 2 && Math.abs(dz) == 2) {
                            level.setBlock(p, stardustBlock, 2);
                        } else {
                            level.setBlock(p, starfallSand, 2);
                        }
                    }
                }
            }
        }
        
        // 頂上の核（Stellar Spire Core）
        BlockPos corePos = new BlockPos(center.getX(), baseY + spireHeight + 1, center.getZ());
        if (box.isInside(corePos)) {
            level.setBlock(corePos, stellarSpireCore, 2);
        }
        
        // コア直下もStarfall Sandで確実に召喚条件を満たす
        BlockPos underCore = new BlockPos(center.getX(), baseY + spireHeight, center.getZ());
        if (box.isInside(underCore)) {
            level.setBlock(underCore, starfallSand, 2);
        }
        
        // 頂上の装飾柱
        int[][] topPillars = {{-1, -1}, {1, -1}, {-1, 1}, {1, 1}};
        for (int[] pillar : topPillars) {
            for (int dy = 1; dy <= 3; dy++) {
                BlockPos p = new BlockPos(center.getX() + pillar[0], baseY + spireHeight + dy, center.getZ() + pillar[1]);
                if (box.isInside(p)) {
                    level.setBlock(p, polishedMoonstone, 2);
                }
            }
        }
    }
}
