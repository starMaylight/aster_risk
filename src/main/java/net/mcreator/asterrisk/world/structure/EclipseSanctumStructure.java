package net.mcreator.asterrisk.world.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

import java.util.Optional;

/**
 * Eclipse Sanctum - Eclipse Monarchを召喚する祭壇がある聖域
 * Eclipse Wastesバイオームにのみ生成
 */
public class EclipseSanctumStructure extends Structure {
    
    public static final Codec<EclipseSanctumStructure> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(settingsCodec(instance))
            .apply(instance, EclipseSanctumStructure::new));
    
    public EclipseSanctumStructure(StructureSettings settings) {
        super(settings);
    }
    
    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        int x = context.chunkPos().getMiddleBlockX();
        int z = context.chunkPos().getMiddleBlockZ();
        int y = context.chunkGenerator().getFirstOccupiedHeight(
            x, z, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
        
        BlockPos pos = new BlockPos(x, y, z);
        
        return Optional.of(new GenerationStub(pos, builder -> {
            builder.addPiece(new EclipseSanctumPiece(pos, context.random()));
        }));
    }
    
    @Override
    public GenerationStep.Decoration step() {
        return GenerationStep.Decoration.SURFACE_STRUCTURES;
    }
    
    @Override
    public StructureType<?> type() {
        return ModStructures.ECLIPSE_SANCTUM.get();
    }
}
