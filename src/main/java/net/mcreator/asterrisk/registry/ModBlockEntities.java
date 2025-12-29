package net.mcreator.asterrisk.registry;

import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.block.entity.AlchemicalCauldronBlockEntity;
import net.mcreator.asterrisk.block.entity.AltarCoreBlockEntity;
import net.mcreator.asterrisk.block.entity.LunarCollectorBlockEntity;
import net.mcreator.asterrisk.block.entity.LunarInfuserBlockEntity;
import net.mcreator.asterrisk.block.entity.ManaBatteryBlockEntity;
import net.mcreator.asterrisk.block.entity.MeteorSummoningBlockEntity;
import net.mcreator.asterrisk.block.entity.MoonlightBeaconBlockEntity;
import net.mcreator.asterrisk.block.entity.ObeliskBlockEntity;
import net.mcreator.asterrisk.block.entity.PhaseAnvilBlockEntity;
import net.mcreator.asterrisk.block.entity.ResonatorBlockEntity;
import net.mcreator.asterrisk.block.entity.RitualPedestalBlockEntity;
import net.mcreator.asterrisk.block.entity.StarAnvilBlockEntity;
import net.mcreator.asterrisk.init.AsterRiskModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> REGISTRY = 
        DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AsterRiskMod.MODID);

    @SuppressWarnings("ConstantConditions")
    public static final RegistryObject<BlockEntityType<LunarCollectorBlockEntity>> LUNAR_COLLECTOR = 
        REGISTRY.register("lunar_collector", () -> 
            BlockEntityType.Builder.of(LunarCollectorBlockEntity::create, 
                AsterRiskModBlocks.LUNAR_COLLECTOR.get()).build(null));

    @SuppressWarnings("ConstantConditions")
    public static final RegistryObject<BlockEntityType<ResonatorBlockEntity>> RESONATOR_TIER1 = 
        REGISTRY.register("resonator_tier1", () -> 
            BlockEntityType.Builder.of(ResonatorBlockEntity::createTier1,
                AsterRiskModBlocks.RESONATOR_TIER1.get()).build(null));

    @SuppressWarnings("ConstantConditions")
    public static final RegistryObject<BlockEntityType<ResonatorBlockEntity>> RESONATOR_TIER2 = 
        REGISTRY.register("resonator_tier2", () -> 
            BlockEntityType.Builder.of(ResonatorBlockEntity::createTier2,
                AsterRiskModBlocks.RESONATOR_TIER2.get()).build(null));

    @SuppressWarnings("ConstantConditions")
    public static final RegistryObject<BlockEntityType<ResonatorBlockEntity>> RESONATOR_TIER3 = 
        REGISTRY.register("resonator_tier3", () -> 
            BlockEntityType.Builder.of(ResonatorBlockEntity::createTier3,
                AsterRiskModBlocks.RESONATOR_TIER3.get()).build(null));

    @SuppressWarnings("ConstantConditions")
    public static final RegistryObject<BlockEntityType<ManaBatteryBlockEntity>> MANA_BATTERY = 
        REGISTRY.register("mana_battery", () -> 
            BlockEntityType.Builder.of(ManaBatteryBlockEntity::create,
                AsterRiskModBlocks.MANA_BATTERY.get()).build(null));

    @SuppressWarnings("ConstantConditions")
    public static final RegistryObject<BlockEntityType<RitualPedestalBlockEntity>> RITUAL_PEDESTAL = 
        REGISTRY.register("ritual_pedestal", () -> 
            BlockEntityType.Builder.of(RitualPedestalBlockEntity::create,
                AsterRiskModBlocks.RITUAL_PEDESTAL.get()).build(null));

    @SuppressWarnings("ConstantConditions")
    public static final RegistryObject<BlockEntityType<AltarCoreBlockEntity>> ALTAR_CORE = 
        REGISTRY.register("altar_core", () -> 
            BlockEntityType.Builder.of(AltarCoreBlockEntity::create,
                AsterRiskModBlocks.ALTAR_CORE.get()).build(null));

    @SuppressWarnings("ConstantConditions")
    public static final RegistryObject<BlockEntityType<MoonlightBeaconBlockEntity>> MOONLIGHT_BEACON = 
        REGISTRY.register("moonlight_beacon", () -> 
            BlockEntityType.Builder.of(MoonlightBeaconBlockEntity::create,
                AsterRiskModBlocks.MOONLIGHT_BEACON.get()).build(null));

    @SuppressWarnings("ConstantConditions")
    public static final RegistryObject<BlockEntityType<LunarInfuserBlockEntity>> LUNAR_INFUSER = 
        REGISTRY.register("lunar_infuser", () -> 
            BlockEntityType.Builder.of(LunarInfuserBlockEntity::create,
                AsterRiskModBlocks.LUNAR_INFUSER.get()).build(null));

    @SuppressWarnings("ConstantConditions")
    public static final RegistryObject<BlockEntityType<StarAnvilBlockEntity>> STAR_ANVIL = 
        REGISTRY.register("star_anvil", () -> 
            BlockEntityType.Builder.of(StarAnvilBlockEntity::create,
                AsterRiskModBlocks.STAR_ANVIL.get()).build(null));

    @SuppressWarnings("ConstantConditions")
    public static final RegistryObject<BlockEntityType<ObeliskBlockEntity>> OBELISK = 
        REGISTRY.register("obelisk", () -> 
            BlockEntityType.Builder.of(ObeliskBlockEntity::new,
                AsterRiskModBlocks.LUNAR_OBELISK.get(),
                AsterRiskModBlocks.STELLAR_OBELISK.get(),
                AsterRiskModBlocks.SOLAR_OBELISK.get(),
                AsterRiskModBlocks.VOID_OBELISK.get()).build(null));

    // === 新システム ===

    @SuppressWarnings("ConstantConditions")
    public static final RegistryObject<BlockEntityType<AlchemicalCauldronBlockEntity>> ALCHEMICAL_CAULDRON = 
        REGISTRY.register("alchemical_cauldron", () -> 
            BlockEntityType.Builder.of(AlchemicalCauldronBlockEntity::new,
                AsterRiskModBlocks.ALCHEMICAL_CAULDRON.get()).build(null));

    @SuppressWarnings("ConstantConditions")
    public static final RegistryObject<BlockEntityType<PhaseAnvilBlockEntity>> PHASE_ANVIL = 
        REGISTRY.register("phase_anvil", () -> 
            BlockEntityType.Builder.of(PhaseAnvilBlockEntity::new,
                AsterRiskModBlocks.PHASE_ANVIL.get()).build(null));

    @SuppressWarnings("ConstantConditions")
    public static final RegistryObject<BlockEntityType<MeteorSummoningBlockEntity>> METEOR_SUMMONING = 
        REGISTRY.register("meteor_summoning", () -> 
            BlockEntityType.Builder.of(MeteorSummoningBlockEntity::new,
                AsterRiskModBlocks.METEOR_SUMMONING.get()).build(null));
}
