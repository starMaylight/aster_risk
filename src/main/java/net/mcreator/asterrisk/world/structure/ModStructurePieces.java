package net.mcreator.asterrisk.world.structure;

import net.mcreator.asterrisk.AsterRiskMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * 構造物ピースタイプの登録
 */
public class ModStructurePieces {
    
    public static final DeferredRegister<StructurePieceType> PIECE_TYPES = 
        DeferredRegister.create(Registries.STRUCTURE_PIECE, AsterRiskMod.MODID);
    
    public static final RegistryObject<StructurePieceType> ECLIPSE_SANCTUM_PIECE = 
        PIECE_TYPES.register("eclipse_sanctum_piece", () -> EclipseSanctumPiece::new);
    
    public static final RegistryObject<StructurePieceType> STELLAR_SPIRE_PIECE = 
        PIECE_TYPES.register("stellar_spire_piece", () -> StellarSpirePiece::new);
    
    public static final RegistryObject<StructurePieceType> LUNAR_TEMPLE_PIECE = 
        PIECE_TYPES.register("lunar_temple_piece", () -> LunarTemplePiece::new);
    
    public static final RegistryObject<StructurePieceType> METEORITE_CRATER_PIECE = 
        PIECE_TYPES.register("meteorite_crater_piece", () -> MeteoriteCraterPiece::new);
    
    public static void register(IEventBus eventBus) {
        PIECE_TYPES.register(eventBus);
    }
}
