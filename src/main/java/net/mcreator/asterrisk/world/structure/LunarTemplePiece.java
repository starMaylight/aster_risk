package net.mcreator.asterrisk.world.structure;

import net.mcreator.asterrisk.init.AsterRiskModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

/**
 * Lunar Templeの構造ピース
 * 神殿風の建物で、宝箱を含む
 */
public class LunarTemplePiece extends StructurePiece {
    
    private final BlockPos center;
    private static final ResourceLocation LOOT_TABLE = new ResourceLocation("aster_risk", "chests/lunar_temple");
    
    public LunarTemplePiece(BlockPos center, RandomSource random) {
        super(ModStructurePieces.LUNAR_TEMPLE_PIECE.get(), 0, 
            new BoundingBox(center.getX() - 10, center.getY() - 2, center.getZ() - 10,
                           center.getX() + 10, center.getY() + 12, center.getZ() + 10));
        this.center = center;
    }
    
    public LunarTemplePiece(StructurePieceSerializationContext context, CompoundTag tag) {
        super(ModStructurePieces.LUNAR_TEMPLE_PIECE.get(), tag);
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
        
        BlockState moonstoneBricks = AsterRiskModBlocks.MOONSTONE_BRICKS.get().defaultBlockState();
        BlockState polishedMoonstone = AsterRiskModBlocks.POLISHED_MOONSTONE.get().defaultBlockState();
        BlockState chiseledMoonstone = AsterRiskModBlocks.CHISELED_MOONSTONE.get().defaultBlockState();
        BlockState lunarPillar = AsterRiskModBlocks.LUNAR_PILLAR.get().defaultBlockState();
        BlockState starryGlass = AsterRiskModBlocks.STARRY_GLASS.get().defaultBlockState();
        BlockState moonlightLantern = AsterRiskModBlocks.MOONLIGHT_LANTERN.get().defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();
        
        int baseY = center.getY();
        
        // 基礎（8x8）
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                BlockPos p = new BlockPos(center.getX() + dx, baseY - 1, center.getZ() + dz);
                if (box.isInside(p)) {
                    level.setBlock(p, moonstoneBricks, 2);
                }
                // 床
                BlockPos floor = new BlockPos(center.getX() + dx, baseY, center.getZ() + dz);
                if (box.isInside(floor)) {
                    level.setBlock(floor, polishedMoonstone, 2);
                }
                // 空間クリア
                for (int dy = 1; dy <= 8; dy++) {
                    BlockPos pAir = new BlockPos(center.getX() + dx, baseY + dy, center.getZ() + dz);
                    if (box.isInside(pAir)) {
                        level.setBlock(pAir, air, 2);
                    }
                }
            }
        }
        
        // 壁（外周）
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                boolean isEdge = Math.abs(dx) == 4 || Math.abs(dz) == 4;
                boolean isCorner = Math.abs(dx) == 4 && Math.abs(dz) == 4;
                boolean isEntrance = (Math.abs(dx) <= 1 && Math.abs(dz) == 4) || (Math.abs(dz) <= 1 && Math.abs(dx) == 4);
                
                if (isEdge && !isEntrance) {
                    for (int dy = 1; dy <= 4; dy++) {
                        BlockPos p = new BlockPos(center.getX() + dx, baseY + dy, center.getZ() + dz);
                        if (box.isInside(p)) {
                            if (isCorner) {
                                level.setBlock(p, lunarPillar, 2);
                            } else if (dy == 3) {
                                level.setBlock(p, starryGlass, 2);
                            } else {
                                level.setBlock(p, moonstoneBricks, 2);
                            }
                        }
                    }
                }
            }
        }
        
        // 角の柱を高く
        int[][] corners = {{-4, -4}, {4, -4}, {-4, 4}, {4, 4}};
        for (int[] corner : corners) {
            for (int dy = 5; dy <= 7; dy++) {
                BlockPos p = new BlockPos(center.getX() + corner[0], baseY + dy, center.getZ() + corner[1]);
                if (box.isInside(p)) {
                    level.setBlock(p, lunarPillar, 2);
                }
            }
            // 頂上にランタン
            BlockPos lanternPos = new BlockPos(center.getX() + corner[0], baseY + 8, center.getZ() + corner[1]);
            if (box.isInside(lanternPos)) {
                level.setBlock(lanternPos, moonlightLantern, 2);
            }
        }
        
        // 屋根（ピラミッド型）
        for (int layer = 0; layer <= 3; layer++) {
            int size = 4 - layer;
            for (int dx = -size; dx <= size; dx++) {
                for (int dz = -size; dz <= size; dz++) {
                    BlockPos p = new BlockPos(center.getX() + dx, baseY + 5 + layer, center.getZ() + dz);
                    if (box.isInside(p)) {
                        if (layer == 3) {
                            level.setBlock(p, chiseledMoonstone, 2);
                        } else {
                            level.setBlock(p, moonstoneBricks, 2);
                        }
                    }
                }
            }
        }
        
        // 中央の祭壇台
        BlockPos altarBase = new BlockPos(center.getX(), baseY + 1, center.getZ());
        if (box.isInside(altarBase)) {
            level.setBlock(altarBase, chiseledMoonstone, 2);
        }
        
        // 宝箱（2つ）
        BlockPos chest1 = new BlockPos(center.getX() - 2, baseY + 1, center.getZ() - 2);
        BlockPos chest2 = new BlockPos(center.getX() + 2, baseY + 1, center.getZ() + 2);
        
        placeChest(level, box, chest1, Direction.SOUTH, random);
        placeChest(level, box, chest2, Direction.NORTH, random);
    }
    
    private void placeChest(WorldGenLevel level, BoundingBox box, BlockPos pos, Direction facing, RandomSource random) {
        if (box.isInside(pos)) {
            BlockState chest = Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, facing);
            level.setBlock(pos, chest, 2);
            
            if (level.getBlockEntity(pos) instanceof ChestBlockEntity chestEntity) {
                chestEntity.setLootTable(LOOT_TABLE, random.nextLong());
            }
        }
    }
}
