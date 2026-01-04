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
 * 隕石クレーターの構造ピース
 * すり鉢状のクレーターで、中央に隕石ブロック
 */
public class MeteoriteCraterPiece extends StructurePiece {
    
    private final BlockPos center;
    private static final ResourceLocation LOOT_TABLE = new ResourceLocation("aster_risk", "chests/meteorite_crater");
    
    public MeteoriteCraterPiece(BlockPos center, RandomSource random) {
        super(ModStructurePieces.METEORITE_CRATER_PIECE.get(), 0, 
            new BoundingBox(center.getX() - 12, center.getY() - 8, center.getZ() - 12,
                           center.getX() + 12, center.getY() + 5, center.getZ() + 12));
        this.center = center;
    }
    
    public MeteoriteCraterPiece(StructurePieceSerializationContext context, CompoundTag tag) {
        super(ModStructurePieces.METEORITE_CRATER_PIECE.get(), tag);
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
        
        BlockState meteoriteBlock = AsterRiskModBlocks.METEORITE_BLOCK.get().defaultBlockState();
        BlockState meteoriteOre = AsterRiskModBlocks.METEORITE_ORE.get().defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockState magma = Blocks.MAGMA_BLOCK.defaultBlockState();
        BlockState obsidian = Blocks.OBSIDIAN.defaultBlockState();
        BlockState blackstone = Blocks.BLACKSTONE.defaultBlockState();
        
        int baseY = center.getY();
        int craterRadius = 8;
        int craterDepth = 6;
        
        // クレーターを掘る（すり鉢状）
        for (int dx = -craterRadius; dx <= craterRadius; dx++) {
            for (int dz = -craterRadius; dz <= craterRadius; dz++) {
                double dist = Math.sqrt(dx * dx + dz * dz);
                
                if (dist <= craterRadius) {
                    // 距離に応じた深さ
                    int depth = (int) (craterDepth * (1 - dist / craterRadius));
                    
                    // 空気で埋める（クレーターの穴）
                    for (int dy = 0; dy >= -depth; dy--) {
                        BlockPos p = new BlockPos(center.getX() + dx, baseY + dy, center.getZ() + dz);
                        if (box.isInside(p)) {
                            level.setBlock(p, air, 2);
                        }
                    }
                    
                    // クレーターの底
                    BlockPos bottom = new BlockPos(center.getX() + dx, baseY - depth - 1, center.getZ() + dz);
                    if (box.isInside(bottom)) {
                        if (dist < 2) {
                            // 中心付近はマグマ
                            level.setBlock(bottom, random.nextFloat() < 0.3 ? magma : obsidian, 2);
                        } else if (dist < 4) {
                            // 中間はオブシディアン
                            level.setBlock(bottom, obsidian, 2);
                        } else {
                            // 外側はブラックストーン
                            level.setBlock(bottom, blackstone, 2);
                        }
                    }
                }
            }
        }
        
        // 隕石本体（中央）
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 1; dy++) {
                for (int dz = -2; dz <= 2; dz++) {
                    double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                    if (dist <= 2.5) {
                        BlockPos p = new BlockPos(center.getX() + dx, baseY - craterDepth + dy, center.getZ() + dz);
                        if (box.isInside(p)) {
                            // ランダムで鉱石ブロックを混ぜる
                            if (random.nextFloat() < 0.2) {
                                level.setBlock(p, meteoriteOre, 2);
                            } else {
                                level.setBlock(p, meteoriteBlock, 2);
                            }
                        }
                    }
                }
            }
        }
        
        // クレーター周囲のリム（盛り上がり）
        for (int dx = -craterRadius - 2; dx <= craterRadius + 2; dx++) {
            for (int dz = -craterRadius - 2; dz <= craterRadius + 2; dz++) {
                double dist = Math.sqrt(dx * dx + dz * dz);
                
                if (dist >= craterRadius && dist <= craterRadius + 2) {
                    int rimHeight = (int) (2 * (1 - (dist - craterRadius) / 2));
                    for (int dy = 0; dy <= rimHeight; dy++) {
                        BlockPos p = new BlockPos(center.getX() + dx, baseY + dy, center.getZ() + dz);
                        if (box.isInside(p)) {
                            level.setBlock(p, blackstone, 2);
                        }
                    }
                }
            }
        }
        
        // 散らばった隕石の破片（周囲）
        for (int i = 0; i < 8; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = 5 + random.nextDouble() * 5;
            int dx = (int) (Math.cos(angle) * dist);
            int dz = (int) (Math.sin(angle) * dist);
            
            BlockPos fragmentPos = new BlockPos(center.getX() + dx, baseY - (int)(craterDepth * (1 - dist / craterRadius)), center.getZ() + dz);
            if (box.isInside(fragmentPos)) {
                level.setBlock(fragmentPos, meteoriteBlock, 2);
            }
        }
        
        // 宝箱（隕石の横）
        BlockPos chestPos = new BlockPos(center.getX() + 3, baseY - craterDepth + 1, center.getZ());
        placeChest(level, box, chestPos, Direction.WEST, random);
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
