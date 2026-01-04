package net.mcreator.asterrisk.world.structure;

import net.mcreator.asterrisk.init.AsterRiskModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

/**
 * Eclipse Sanctum - 3階層の闘技場ダンジョン
 * 
 * 召喚条件: Eclipse Altar周囲にEclipse Stoneが8個以上
 * 
 * 構造:
 * - 地下2階: 牢獄エリア、スポナー
 * - 地下1階: 回廊、宝箱
 * - 1階: 闘技場、Eclipse Altar（Eclipse Stone床）
 */
public class EclipseSanctumPiece extends StructurePiece {
    
    private final BlockPos center;
    private static final ResourceLocation LOOT_TABLE = new ResourceLocation("aster_risk", "chests/eclipse_sanctum");
    
    // ダンジョンサイズ
    private static final int ARENA_RADIUS = 10;
    private static final int CORRIDOR_WIDTH = 3;
    private static final int FLOOR_HEIGHT = 5;
    
    public EclipseSanctumPiece(BlockPos center, RandomSource random) {
        super(ModStructurePieces.ECLIPSE_SANCTUM_PIECE.get(), 0, 
            new BoundingBox(center.getX() - ARENA_RADIUS - 4, center.getY() - FLOOR_HEIGHT * 2 - 2, center.getZ() - ARENA_RADIUS - 4,
                           center.getX() + ARENA_RADIUS + 4, center.getY() + FLOOR_HEIGHT + 4, center.getZ() + ARENA_RADIUS + 4));
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
        BlockState chiseledMoonstone = AsterRiskModBlocks.CHISELED_MOONSTONE.get().defaultBlockState();
        BlockState eclipseAltar = AsterRiskModBlocks.ECLIPSE_ALTAR.get().defaultBlockState();
        BlockState moonlightLantern = AsterRiskModBlocks.MOONLIGHT_LANTERN.get().defaultBlockState();
        BlockState ironBars = Blocks.IRON_BARS.defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockState ladder = Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.SOUTH);
        
        int baseY = center.getY();
        
        // === 地下2階（牢獄エリア） ===
        int prison_Y = baseY - FLOOR_HEIGHT * 2;
        buildRectRoom(level, box, prison_Y, 8, 8, eclipseStone, polishedMoonstone, air);
        
        // 牢屋（4つ）
        int[][] cellPositions = {{-5, -5}, {5, -5}, {-5, 5}, {5, 5}};
        for (int[] cell : cellPositions) {
            buildCell(level, box, prison_Y, center.getX() + cell[0], center.getZ() + cell[1], ironBars, eclipseStone);
            // 各牢屋に宝箱
            placeChest(level, box, new BlockPos(center.getX() + cell[0], prison_Y + 1, center.getZ() + cell[1]), 
                      cell[0] > 0 ? Direction.WEST : Direction.EAST, random);
        }
        // スポナー（地下2階中央）
        placeSpawner(level, box, new BlockPos(center.getX(), prison_Y + 1, center.getZ()), EntityType.WITHER_SKELETON, random);
        
        // === はしご（地下2階→地下1階） ===
        int ladderX1 = center.getX();
        int ladderZ1 = center.getZ() - 6;
        for (int dy = 1; dy <= FLOOR_HEIGHT; dy++) {
            BlockPos ladderPos = new BlockPos(ladderX1, prison_Y + dy, ladderZ1);
            if (box.isInside(ladderPos)) {
                level.setBlock(ladderPos, ladder, 2);
            }
        }
        // はしご穴
        BlockPos holePos1 = new BlockPos(ladderX1, prison_Y + FLOOR_HEIGHT, ladderZ1);
        if (box.isInside(holePos1)) {
            level.setBlock(holePos1, air, 2);
        }
        
        // === 地下1階（回廊） ===
        int corridor_Y = baseY - FLOOR_HEIGHT;
        // 十字型の回廊
        buildCrossCorridors(level, box, corridor_Y, eclipseStone, moonstoneBricks, air);
        
        // 回廊の角に宝箱
        placeChest(level, box, new BlockPos(center.getX() - 6, corridor_Y + 1, center.getZ() - 6), Direction.EAST, random);
        placeChest(level, box, new BlockPos(center.getX() + 6, corridor_Y + 1, center.getZ() + 6), Direction.WEST, random);
        // スポナー（回廊）
        placeSpawner(level, box, new BlockPos(center.getX() + 6, corridor_Y + 1, center.getZ()), EntityType.ZOMBIE, random);
        placeSpawner(level, box, new BlockPos(center.getX() - 6, corridor_Y + 1, center.getZ()), EntityType.ZOMBIE, random);
        
        // === はしご（地下1階→1階） ===
        int ladderX2 = center.getX();
        int ladderZ2 = center.getZ() + 6;
        for (int dy = 1; dy <= FLOOR_HEIGHT; dy++) {
            BlockPos ladderPos = new BlockPos(ladderX2, corridor_Y + dy, ladderZ2);
            if (box.isInside(ladderPos)) {
                level.setBlock(ladderPos, ladder, 2);
            }
        }
        // はしご穴
        BlockPos holePos2 = new BlockPos(ladderX2, corridor_Y + FLOOR_HEIGHT, ladderZ2);
        if (box.isInside(holePos2)) {
            level.setBlock(holePos2, air, 2);
        }
        
        // === 1階（闘技場 - ボス召喚部屋） ===
        buildArena(level, box, baseY, eclipseStone, moonstoneBricks, polishedMoonstone, chiseledMoonstone, air);
        
        // ★ Eclipse Stone床（召喚条件：Altar周囲に8個以上）
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos stonePos = new BlockPos(center.getX() + dx, baseY, center.getZ() + dz);
                if (box.isInside(stonePos)) {
                    level.setBlock(stonePos, eclipseStone, 2);
                }
            }
        }
        
        // 中央祭壇（Eclipse Altar）
        BlockPos altarPos = new BlockPos(center.getX(), baseY + 1, center.getZ());
        if (box.isInside(altarPos)) {
            level.setBlock(altarPos, eclipseAltar, 2);
        }
        
        // 柱（8本、円周上）
        for (int i = 0; i < 8; i++) {
            double angle = i * Math.PI / 4;
            int px = (int) Math.round(Math.cos(angle) * 7);
            int pz = (int) Math.round(Math.sin(angle) * 7);
            for (int dy = 1; dy <= FLOOR_HEIGHT; dy++) {
                BlockPos pillarPos = new BlockPos(center.getX() + px, baseY + dy, center.getZ() + pz);
                if (box.isInside(pillarPos)) {
                    level.setBlock(pillarPos, moonstoneBricks, 2);
                }
            }
            // 柱上にランタン
            BlockPos lanternPos = new BlockPos(center.getX() + px, baseY + FLOOR_HEIGHT + 1, center.getZ() + pz);
            if (box.isInside(lanternPos)) {
                level.setBlock(lanternPos, moonlightLantern, 2);
            }
        }
        
        // 入り口（4方向）
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            int ex = center.getX() + dir.getStepX() * ARENA_RADIUS;
            int ez = center.getZ() + dir.getStepZ() * ARENA_RADIUS;
            for (int dy = 1; dy <= 3; dy++) {
                for (int w = -1; w <= 1; w++) {
                    BlockPos doorPos;
                    if (dir.getAxis() == Direction.Axis.X) {
                        doorPos = new BlockPos(ex, baseY + dy, center.getZ() + w);
                    } else {
                        doorPos = new BlockPos(center.getX() + w, baseY + dy, ez);
                    }
                    if (box.isInside(doorPos)) {
                        level.setBlock(doorPos, air, 2);
                    }
                }
            }
        }
    }
    
    private void buildRectRoom(WorldGenLevel level, BoundingBox box, int floorY, int halfWidth, int halfDepth,
                               BlockState wall, BlockState floor, BlockState air) {
        for (int dx = -halfWidth; dx <= halfWidth; dx++) {
            for (int dz = -halfDepth; dz <= halfDepth; dz++) {
                boolean isEdge = Math.abs(dx) == halfWidth || Math.abs(dz) == halfDepth;
                
                // 床
                BlockPos floorPos = new BlockPos(center.getX() + dx, floorY, center.getZ() + dz);
                if (box.isInside(floorPos)) {
                    level.setBlock(floorPos, floor, 2);
                }
                
                // 空間と壁
                for (int dy = 1; dy < FLOOR_HEIGHT; dy++) {
                    BlockPos pos = new BlockPos(center.getX() + dx, floorY + dy, center.getZ() + dz);
                    if (box.isInside(pos)) {
                        level.setBlock(pos, isEdge ? wall : air, 2);
                    }
                }
                
                // 天井
                BlockPos ceilingPos = new BlockPos(center.getX() + dx, floorY + FLOOR_HEIGHT, center.getZ() + dz);
                if (box.isInside(ceilingPos)) {
                    level.setBlock(ceilingPos, floor, 2);
                }
            }
        }
    }
    
    private void buildCell(WorldGenLevel level, BoundingBox box, int floorY, int cx, int cz, 
                          BlockState bars, BlockState wall) {
        // 小部屋（3x3）
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                boolean isEdge = Math.abs(dx) == 1 || Math.abs(dz) == 1;
                for (int dy = 1; dy < FLOOR_HEIGHT - 1; dy++) {
                    BlockPos pos = new BlockPos(cx + dx, floorY + dy, cz + dz);
                    if (box.isInside(pos)) {
                        if (isEdge && dy < 3) {
                            level.setBlock(pos, bars, 2);
                        }
                    }
                }
            }
        }
    }
    
    private void buildCrossCorridors(WorldGenLevel level, BoundingBox box, int floorY,
                                     BlockState wall, BlockState floor, BlockState air) {
        // 東西回廊
        for (int dx = -8; dx <= 8; dx++) {
            for (int dz = -CORRIDOR_WIDTH / 2; dz <= CORRIDOR_WIDTH / 2; dz++) {
                buildCorridorSegment(level, box, floorY, center.getX() + dx, center.getZ() + dz, wall, floor, air);
            }
        }
        // 南北回廊
        for (int dz = -8; dz <= 8; dz++) {
            for (int dx = -CORRIDOR_WIDTH / 2; dx <= CORRIDOR_WIDTH / 2; dx++) {
                buildCorridorSegment(level, box, floorY, center.getX() + dx, center.getZ() + dz, wall, floor, air);
            }
        }
    }
    
    private void buildCorridorSegment(WorldGenLevel level, BoundingBox box, int floorY, int x, int z,
                                      BlockState wall, BlockState floor, BlockState air) {
        // 床
        BlockPos floorPos = new BlockPos(x, floorY, z);
        if (box.isInside(floorPos)) {
            level.setBlock(floorPos, floor, 2);
        }
        // 空間
        for (int dy = 1; dy < FLOOR_HEIGHT; dy++) {
            BlockPos pos = new BlockPos(x, floorY + dy, z);
            if (box.isInside(pos)) {
                level.setBlock(pos, air, 2);
            }
        }
        // 天井
        BlockPos ceilingPos = new BlockPos(x, floorY + FLOOR_HEIGHT, z);
        if (box.isInside(ceilingPos)) {
            level.setBlock(ceilingPos, floor, 2);
        }
    }
    
    private void buildArena(WorldGenLevel level, BoundingBox box, int floorY,
                           BlockState wall, BlockState wall2, BlockState floor, BlockState decorFloor, BlockState air) {
        for (int dx = -ARENA_RADIUS; dx <= ARENA_RADIUS; dx++) {
            for (int dz = -ARENA_RADIUS; dz <= ARENA_RADIUS; dz++) {
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist <= ARENA_RADIUS) {
                    boolean isEdge = dist >= ARENA_RADIUS - 1;
                    
                    // 床（中央は装飾）
                    BlockPos floorPos = new BlockPos(center.getX() + dx, floorY, center.getZ() + dz);
                    if (box.isInside(floorPos)) {
                        if (dist <= 2) {
                            level.setBlock(floorPos, decorFloor, 2);
                        } else {
                            level.setBlock(floorPos, floor, 2);
                        }
                    }
                    
                    // 空間と壁
                    for (int dy = 1; dy <= FLOOR_HEIGHT; dy++) {
                        BlockPos pos = new BlockPos(center.getX() + dx, floorY + dy, center.getZ() + dz);
                        if (box.isInside(pos)) {
                            if (isEdge) {
                                level.setBlock(pos, wall, 2);
                            } else {
                                level.setBlock(pos, air, 2);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void placeSpawner(WorldGenLevel level, BoundingBox box, BlockPos pos, EntityType<?> entityType, RandomSource random) {
        if (box.isInside(pos)) {
            level.setBlock(pos, Blocks.SPAWNER.defaultBlockState(), 2);
            if (level.getBlockEntity(pos) instanceof SpawnerBlockEntity spawner) {
                spawner.setEntityId(entityType, random);
            }
        }
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
