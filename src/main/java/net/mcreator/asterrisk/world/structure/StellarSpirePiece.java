package net.mcreator.asterrisk.world.structure;

import net.mcreator.asterrisk.init.AsterRiskModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
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
import net.minecraft.world.entity.EntityType;

/**
 * Stellar Spire - 3階層の星の塔ダンジョン
 * 
 * 召喚条件: Stellar Spire Core周囲にStarfall Sandが8個以上
 * 
 * 構造:
 * - 地下1階: エントランス、スポナー
 * - 1階: メインホール、宝箱
 * - 2階: 星の間、Stellar Spire Core（Starfall Sand床）
 * - 屋上: 展望台、ボーナス宝箱
 */
public class StellarSpirePiece extends StructurePiece {
    
    private final BlockPos center;
    private static final ResourceLocation LOOT_TABLE = new ResourceLocation("aster_risk", "chests/stellar_spire");
    
    // ダンジョンサイズ
    private static final int RADIUS = 7;
    private static final int FLOOR_HEIGHT = 6;
    
    public StellarSpirePiece(BlockPos center, RandomSource random) {
        super(ModStructurePieces.STELLAR_SPIRE_PIECE.get(), 0, 
            new BoundingBox(center.getX() - RADIUS - 2, center.getY() - FLOOR_HEIGHT, center.getZ() - RADIUS - 2,
                           center.getX() + RADIUS + 2, center.getY() + FLOOR_HEIGHT * 3 + 5, center.getZ() + RADIUS + 2));
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
        
        BlockState moonstoneBricks = AsterRiskModBlocks.MOONSTONE_BRICKS.get().defaultBlockState();
        BlockState polishedMoonstone = AsterRiskModBlocks.POLISHED_MOONSTONE.get().defaultBlockState();
        BlockState chiseledMoonstone = AsterRiskModBlocks.CHISELED_MOONSTONE.get().defaultBlockState();
        BlockState stardustBlock = AsterRiskModBlocks.STARDUST_BLOCK.get().defaultBlockState();
        BlockState starfallSand = AsterRiskModBlocks.STARFALLSAND.get().defaultBlockState();
        BlockState stellarSpireCore = AsterRiskModBlocks.STELLAR_SPIRE_CORE.get().defaultBlockState();
        BlockState starryGlass = AsterRiskModBlocks.STARRY_GLASS.get().defaultBlockState();
        BlockState moonlightLantern = AsterRiskModBlocks.MOONLIGHT_LANTERN.get().defaultBlockState();
        BlockState lunarPillar = AsterRiskModBlocks.LUNAR_PILLAR.get().defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockState ladder = Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.SOUTH);
        
        int baseY = center.getY();
        
        // === 地下1階（エントランス） ===
        buildFloor(level, box, baseY - FLOOR_HEIGHT, RADIUS, moonstoneBricks, polishedMoonstone, air);
        // 入り口（南側）
        for (int dy = 0; dy < 3; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                BlockPos doorPos = new BlockPos(center.getX() + dx, baseY - FLOOR_HEIGHT + dy + 1, center.getZ() + RADIUS);
                if (box.isInside(doorPos)) {
                    level.setBlock(doorPos, air, 2);
                }
            }
        }
        // スポナー（地下）
        placeSpawner(level, box, new BlockPos(center.getX(), baseY - FLOOR_HEIGHT + 1, center.getZ()), 
                    EntityType.SKELETON, random);
        // 地下宝箱
        placeChest(level, box, new BlockPos(center.getX() + 3, baseY - FLOOR_HEIGHT + 1, center.getZ() + 3), Direction.WEST, random);
        
        // === はしご（地下→1階） ===
        int ladderX = center.getX() - 3;
        int ladderZ = center.getZ() - 3;
        for (int dy = 1; dy <= FLOOR_HEIGHT; dy++) {
            BlockPos ladderPos = new BlockPos(ladderX, baseY - FLOOR_HEIGHT + dy, ladderZ);
            if (box.isInside(ladderPos)) {
                level.setBlock(ladderPos, ladder, 2);
            }
        }
        // はしご穴（1階の床）
        BlockPos holePos1 = new BlockPos(ladderX, baseY, ladderZ);
        if (box.isInside(holePos1)) {
            level.setBlock(holePos1, air, 2);
        }
        
        // === 1階（メインホール） ===
        buildFloor(level, box, baseY, RADIUS, moonstoneBricks, polishedMoonstone, air);
        // 中央装飾
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos floorPos = new BlockPos(center.getX() + dx, baseY, center.getZ() + dz);
                if (box.isInside(floorPos)) {
                    level.setBlock(floorPos, stardustBlock, 2);
                }
            }
        }
        // 柱
        int[][] pillarPositions = {{-4, -4}, {4, -4}, {-4, 4}, {4, 4}};
        for (int[] pillar : pillarPositions) {
            for (int dy = 1; dy <= FLOOR_HEIGHT - 1; dy++) {
                BlockPos pillarPos = new BlockPos(center.getX() + pillar[0], baseY + dy, center.getZ() + pillar[1]);
                if (box.isInside(pillarPos)) {
                    level.setBlock(pillarPos, lunarPillar, 2);
                }
            }
        }
        // 1階宝箱
        placeChest(level, box, new BlockPos(center.getX() - 4, baseY + 1, center.getZ()), Direction.EAST, random);
        
        // === はしご（1階→2階） ===
        int ladderX2 = center.getX() + 3;
        int ladderZ2 = center.getZ() - 3;
        for (int dy = 1; dy <= FLOOR_HEIGHT; dy++) {
            BlockPos ladderPos = new BlockPos(ladderX2, baseY + dy, ladderZ2);
            if (box.isInside(ladderPos)) {
                level.setBlock(ladderPos, ladder, 2);
            }
        }
        // はしご穴（2階の床）
        BlockPos holePos2 = new BlockPos(ladderX2, baseY + FLOOR_HEIGHT, ladderZ2);
        if (box.isInside(holePos2)) {
            level.setBlock(holePos2, air, 2);
        }
        
        // === 2階（星の間 - ボス召喚部屋） ===
        buildFloor(level, box, baseY + FLOOR_HEIGHT, RADIUS - 1, moonstoneBricks, polishedMoonstone, air);
        
        // ★ Starfall Sand床（召喚条件：Core周囲に8個以上）
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos sandPos = new BlockPos(center.getX() + dx, baseY + FLOOR_HEIGHT, center.getZ() + dz);
                if (box.isInside(sandPos)) {
                    level.setBlock(sandPos, starfallSand, 2);
                }
            }
        }
        
        // Stellar Spire Core（中央、床から1ブロック上）
        BlockPos corePos = new BlockPos(center.getX(), baseY + FLOOR_HEIGHT + 1, center.getZ());
        if (box.isInside(corePos)) {
            level.setBlock(corePos, stellarSpireCore, 2);
        }
        
        // 星のガラス窓
        for (int i = 0; i < 8; i++) {
            double angle = i * Math.PI / 4;
            int wx = (int) Math.round(Math.cos(angle) * (RADIUS - 1));
            int wz = (int) Math.round(Math.sin(angle) * (RADIUS - 1));
            for (int dy = 2; dy <= 4; dy++) {
                BlockPos windowPos = new BlockPos(center.getX() + wx, baseY + FLOOR_HEIGHT + dy, center.getZ() + wz);
                if (box.isInside(windowPos)) {
                    level.setBlock(windowPos, starryGlass, 2);
                }
            }
        }
        // 2階宝箱
        placeChest(level, box, new BlockPos(center.getX() + 3, baseY + FLOOR_HEIGHT + 1, center.getZ() + 3), Direction.SOUTH, random);
        // スポナー（2階）
        placeSpawner(level, box, new BlockPos(center.getX() - 3, baseY + FLOOR_HEIGHT + 1, center.getZ() + 3), 
                    EntityType.STRAY, random);
        
        // === はしご（2階→屋上） ===
        int ladderX3 = center.getX() - 3;
        int ladderZ3 = center.getZ() + 3;
        for (int dy = 1; dy <= FLOOR_HEIGHT; dy++) {
            BlockPos ladderPos = new BlockPos(ladderX3, baseY + FLOOR_HEIGHT + dy, ladderZ3);
            if (box.isInside(ladderPos)) {
                level.setBlock(ladderPos, ladder, 2);
            }
        }
        
        // === 屋上（展望台） ===
        int roofY = baseY + FLOOR_HEIGHT * 2;
        for (int dx = -RADIUS + 1; dx <= RADIUS - 1; dx++) {
            for (int dz = -RADIUS + 1; dz <= RADIUS - 1; dz++) {
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist <= RADIUS - 1) {
                    BlockPos roofPos = new BlockPos(center.getX() + dx, roofY, center.getZ() + dz);
                    if (box.isInside(roofPos)) {
                        level.setBlock(roofPos, polishedMoonstone, 2);
                    }
                }
            }
        }
        // 屋上の手すり
        for (int dx = -RADIUS + 1; dx <= RADIUS - 1; dx++) {
            for (int dz = -RADIUS + 1; dz <= RADIUS - 1; dz++) {
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist >= RADIUS - 2 && dist <= RADIUS - 1) {
                    BlockPos railPos = new BlockPos(center.getX() + dx, roofY + 1, center.getZ() + dz);
                    if (box.isInside(railPos)) {
                        level.setBlock(railPos, moonstoneBricks, 2);
                    }
                }
            }
        }
        // 角にランタン
        for (int[] corner : pillarPositions) {
            BlockPos lanternPos = new BlockPos(center.getX() + corner[0], roofY + 2, center.getZ() + corner[1]);
            if (box.isInside(lanternPos)) {
                level.setBlock(lanternPos, moonlightLantern, 2);
                level.setBlock(lanternPos.below(), lunarPillar, 2);
            }
        }
        // 屋上宝箱（レア）
        placeChest(level, box, new BlockPos(center.getX(), roofY + 1, center.getZ()), Direction.SOUTH, random);
    }
    
    private void buildFloor(WorldGenLevel level, BoundingBox box, int floorY, int radius,
                           BlockState wall, BlockState floor, BlockState air) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist <= radius) {
                    // 床
                    BlockPos floorPos = new BlockPos(center.getX() + dx, floorY, center.getZ() + dz);
                    if (box.isInside(floorPos)) {
                        level.setBlock(floorPos, floor, 2);
                    }
                    // 空間クリア
                    for (int dy = 1; dy < FLOOR_HEIGHT; dy++) {
                        BlockPos airPos = new BlockPos(center.getX() + dx, floorY + dy, center.getZ() + dz);
                        if (box.isInside(airPos)) {
                            level.setBlock(airPos, air, 2);
                        }
                    }
                    // 天井
                    BlockPos ceilingPos = new BlockPos(center.getX() + dx, floorY + FLOOR_HEIGHT, center.getZ() + dz);
                    if (box.isInside(ceilingPos)) {
                        level.setBlock(ceilingPos, floor, 2);
                    }
                    // 壁
                    if (dist >= radius - 1) {
                        for (int dy = 1; dy < FLOOR_HEIGHT; dy++) {
                            BlockPos wallPos = new BlockPos(center.getX() + dx, floorY + dy, center.getZ() + dz);
                            if (box.isInside(wallPos)) {
                                level.setBlock(wallPos, wall, 2);
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
