package net.mcreator.asterrisk.world.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.Optional;

/**
 * 隕石クレーター構造物
 * 平原や砂漠に生成、隕石素材が取れる
 */
public class MeteoriteCraterStructure extends Structure {
    
    public static final Codec<MeteoriteCraterStructure> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(settingsCodec(instance))
            .apply(instance, MeteoriteCraterStructure::new)
    );
    
    public MeteoriteCraterStructure(StructureSettings settings) {
        super(settings);
    }
    
    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        BlockPos pos = context.chunkPos().getMiddleBlockPosition(0);
        
        int y = context.chunkGenerator().getFirstOccupiedHeight(
            pos.getX(), pos.getZ(),
            Heightmap.Types.WORLD_SURFACE_WG,
            context.heightAccessor(),
            context.randomState()
        );
        
        // 高すぎる場所や低すぎる場所は除外
        if (y < 60 || y > 100) {
            return Optional.empty();
        }
        
        BlockPos spawnPos = new BlockPos(pos.getX(), y, pos.getZ());
        
        return Optional.of(new GenerationStub(spawnPos, builder -> 
            generatePieces(builder, context, spawnPos)));
    }
    
    private void generatePieces(StructurePiecesBuilder builder, GenerationContext context, BlockPos pos) {
        builder.addPiece(new MeteoriteCraterPiece(pos, context.random()));
    }
    
    @Override
    public StructureType<?> type() {
        return ModStructures.METEORITE_CRATER.get();
    }
}
