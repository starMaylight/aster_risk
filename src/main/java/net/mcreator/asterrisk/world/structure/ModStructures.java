package net.mcreator.asterrisk.world.structure;

import net.mcreator.asterrisk.AsterRiskMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * 構造物タイプの登録
 */
public class ModStructures {
    
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = 
        DeferredRegister.create(Registries.STRUCTURE_TYPE, AsterRiskMod.MODID);
    
    public static final RegistryObject<StructureType<EclipseSanctumStructure>> ECLIPSE_SANCTUM = 
        STRUCTURE_TYPES.register("eclipse_sanctum", () -> () -> EclipseSanctumStructure.CODEC);
    
    public static final RegistryObject<StructureType<StellarSpireStructure>> STELLAR_SPIRE = 
        STRUCTURE_TYPES.register("stellar_spire", () -> () -> StellarSpireStructure.CODEC);
    
    public static final RegistryObject<StructureType<LunarTempleStructure>> LUNAR_TEMPLE = 
        STRUCTURE_TYPES.register("lunar_temple", () -> () -> LunarTempleStructure.CODEC);
    
    public static final RegistryObject<StructureType<MeteoriteCraterStructure>> METEORITE_CRATER = 
        STRUCTURE_TYPES.register("meteorite_crater", () -> () -> MeteoriteCraterStructure.CODEC);
    
    public static void register(IEventBus eventBus) {
        STRUCTURE_TYPES.register(eventBus);
    }
}
