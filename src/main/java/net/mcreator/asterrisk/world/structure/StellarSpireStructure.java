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
 * Stellar Spire - Star Devourerを召喚する尖塔
 * Starfall Hillsバイオームにのみ生成
 */
public class StellarSpireStructure extends Structure {
    
    public static final Codec<StellarSpireStructure> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(settingsCodec(instance))
            .apply(instance, StellarSpireStructure::new));
    
    public StellarSpireStructure(StructureSettings settings) {
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
            builder.addPiece(new StellarSpirePiece(pos, context.random()));
        }));
    }
    
    @Override
    public GenerationStep.Decoration step() {
        return GenerationStep.Decoration.SURFACE_STRUCTURES;
    }
    
    @Override
    public StructureType<?> type() {
        return ModStructures.STELLAR_SPIRE.get();
    }
}
