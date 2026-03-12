package net.mcreator.asterrisk.registry;

import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.block.*;
import net.mcreator.asterrisk.block.entity.ObeliskEnergyType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {

    public static final DeferredRegister<Block> REGISTRY =
        DeferredRegister.create(ForgeRegistries.BLOCKS, AsterRiskMod.MODID);

    // === 鉱石 ===
    public static final RegistryObject<Block> MOONSTONE_ORE = REGISTRY.register("moonstone_ore", () -> new MoonstoneOreBlock());
    public static final RegistryObject<Block> METEORITE_ORE = REGISTRY.register("meteorite_ore", () -> new MeteoriteOreBlock());
    public static final RegistryObject<Block> SILVER_ORE = REGISTRY.register("silver_ore", () -> new SilveroreBlock());

    // === 鉱石ブロック ===
    public static final RegistryObject<Block> MOONSTONE_BLOCK = REGISTRY.register("moonstone_block", () -> new MoonstoneblockBlock());
    public static final RegistryObject<Block> SILVER_BLOCK = REGISTRY.register("silver_block", () -> new SilverBlockBlock());
    public static final RegistryObject<Block> STARDUST_BLOCK = REGISTRY.register("stardust_block", () -> new StardustBlockBlock());
    public static final RegistryObject<Block> METEORITE_BLOCK = REGISTRY.register("meteorite_block", () -> new MeteoriteBlockBlock());

    // === 装飾ブロック ===
    public static final RegistryObject<Block> MOONSTONE_BRICKS = REGISTRY.register("moonstone_bricks", () -> new MoonstoneBricksBlock());
    public static final RegistryObject<Block> POLISHED_MOONSTONE = REGISTRY.register("polished_moonstone", () -> new PolishedMoonstoneBlock());
    public static final RegistryObject<Block> MOONSTONE_TILES = REGISTRY.register("moonstone_tiles", () -> new MoonstoneTilesBlock());
    public static final RegistryObject<Block> CRACKED_MOONSTONE_BRICKS = REGISTRY.register("cracked_moonstone_bricks", () -> new CrackedMoonstoneBricksBlock());
    public static final RegistryObject<Block> MOSSY_MOONSTONE_BRICKS = REGISTRY.register("mossy_moonstone_bricks", () -> new MossyMoonstoneBricksBlock());
    public static final RegistryObject<Block> SILVER_BRICKS = REGISTRY.register("silver_bricks", () -> new SilverBricksBlock());
    public static final RegistryObject<Block> CHISELED_MOONSTONE = REGISTRY.register("chiseled_moonstone", () -> new ChiseledMoonstoneBlock());
    public static final RegistryObject<Block> STARRY_GLASS = REGISTRY.register("starry_glass", () -> new StarryGlassBlock());
    public static final RegistryObject<Block> LUNAR_PILLAR = REGISTRY.register("lunar_pillar", () -> new LunarPillarBlock());
    public static final RegistryObject<Block> CELESTIAL_TILE = REGISTRY.register("celestial_tile", () -> new CelestialTileBlock());
    public static final RegistryObject<Block> MOONLIGHT_LANTERN = REGISTRY.register("moonlight_lantern", () -> new MoonlightLanternBlock());

    // === ルナルディメンション用ブロック ===
    public static final RegistryObject<Block> LUNARGRASS = REGISTRY.register("lunargrass", () -> new LunargrassBlock());
    public static final RegistryObject<Block> LUNARDIRT = REGISTRY.register("lunardirt", () -> new LunardirtBlock());
    public static final RegistryObject<Block> LUNARSTONE = REGISTRY.register("lunarstone", () -> new LunarstoneBlock());
    public static final RegistryObject<Block> LUNARLOG = REGISTRY.register("lunarlog", () -> new LunarlogBlock());
    public static final RegistryObject<Block> LUNARLEAVES = REGISTRY.register("lunarleaves", () -> new LunarleavesBlock());
    public static final RegistryObject<Block> LUNARPLANKS = REGISTRY.register("lunarplanks", () -> new LunarplanksBlock());
    public static final RegistryObject<Block> STARFALLSAND = REGISTRY.register("starfallsand", () -> new StarfallsandBlock());
    public static final RegistryObject<Block> ECLIPSESTONE = REGISTRY.register("eclipsestone", () -> new EclipsestoneBlock());
    public static final RegistryObject<Block> MOONWATER = REGISTRY.register("moonwater", () -> new MoonwaterBlock());
    public static final RegistryObject<Block> MOONLIGHT = REGISTRY.register("moonlight", () -> new MoonlightBlock());

    // === 機能ブロック ===
    public static final RegistryObject<Block> LUNAR_COLLECTOR = REGISTRY.register("lunar_collector", () -> new LunarCollectorBlock());
    public static final RegistryObject<Block> RESONATOR_TIER1 = REGISTRY.register("resonator_tier1", () -> ResonatorBlock.createTier1());
    public static final RegistryObject<Block> RESONATOR_TIER2 = REGISTRY.register("resonator_tier2", () -> ResonatorBlock.createTier2());
    public static final RegistryObject<Block> RESONATOR_TIER3 = REGISTRY.register("resonator_tier3", () -> ResonatorBlock.createTier3());
    public static final RegistryObject<Block> MANA_BATTERY = REGISTRY.register("mana_battery", () -> new ManaBatteryBlock());
    public static final RegistryObject<Block> MOONLIGHT_BEACON = REGISTRY.register("moonlight_beacon", () -> new MoonlightBeaconBlock());
    public static final RegistryObject<Block> LUNAR_INFUSER = REGISTRY.register("lunar_infuser", () -> new LunarInfuserBlock());
    public static final RegistryObject<Block> STAR_ANVIL = REGISTRY.register("star_anvil", () -> new StarAnvilBlock());

    // === 儀式システム ===
    public static final RegistryObject<Block> RITUAL_PEDESTAL = REGISTRY.register("ritual_pedestal", () -> new RitualPedestalBlock());
    public static final RegistryObject<Block> ALTAR_CORE = REGISTRY.register("altar_core", () -> new AltarCoreBlock());

    // === オベリスク ===
    public static final RegistryObject<Block> LUNAR_OBELISK = REGISTRY.register("lunar_obelisk",
        () -> new ObeliskBlock(Block.Properties.of().strength(3.0F, 6.0F).requiresCorrectToolForDrops().lightLevel(s -> 8).noOcclusion(), ObeliskEnergyType.LUNAR));
    public static final RegistryObject<Block> STELLAR_OBELISK = REGISTRY.register("stellar_obelisk",
        () -> new ObeliskBlock(Block.Properties.of().strength(3.0F, 6.0F).requiresCorrectToolForDrops().lightLevel(s -> 10).noOcclusion(), ObeliskEnergyType.STELLAR));
    public static final RegistryObject<Block> SOLAR_OBELISK = REGISTRY.register("solar_obelisk",
        () -> new ObeliskBlock(Block.Properties.of().strength(3.0F, 6.0F).requiresCorrectToolForDrops().lightLevel(s -> 12).noOcclusion(), ObeliskEnergyType.SOLAR));
    public static final RegistryObject<Block> VOID_OBELISK = REGISTRY.register("void_obelisk",
        () -> new ObeliskBlock(Block.Properties.of().strength(3.0F, 6.0F).requiresCorrectToolForDrops().lightLevel(s -> 4).noOcclusion(), ObeliskEnergyType.VOID));

    // === ポータル・苗木 ===
    public static final RegistryObject<Block> LUNAR_PORTAL = REGISTRY.register("lunar_portal", () -> new LunarPortalBlock());
    public static final RegistryObject<Block> LUNAR_SAPLING = REGISTRY.register("lunar_sapling", () -> new LunarSaplingBlock());

    // === ボス召喚祭壇 ===
    public static final RegistryObject<Block> ECLIPSE_ALTAR = REGISTRY.register("eclipse_altar", () -> new EclipseAltarBlock());
    public static final RegistryObject<Block> STELLAR_SPIRE_CORE = REGISTRY.register("stellar_spire_core", () -> new StellarSpireCoreBlock());

    // === 新システム: 流星召喚・月相鍛冶・錬金術 ===
    public static final RegistryObject<Block> ALCHEMICAL_CAULDRON = REGISTRY.register("alchemical_cauldron", () -> new AlchemicalCauldronBlock());
    public static final RegistryObject<Block> PHASE_ANVIL = REGISTRY.register("phase_anvil", () -> new PhaseAnvilBlock());
    public static final RegistryObject<Block> METEOR_SUMMONING = REGISTRY.register("meteor_summoning", () -> new MeteorSummoningBlock());

    // === 魔法陣クラフトシステム ===
    public static final RegistryObject<Block> RITUAL_CIRCLE = REGISTRY.register("ritual_circle",
        () -> new RitualCircleBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_PURPLE)
            .strength(3.0f, 6.0f)
            .sound(SoundType.STONE)
            .lightLevel(state -> 7)
            .noOcclusion()));

    public static final RegistryObject<Block> MOONLIGHT_FOCUS = REGISTRY.register("moonlight_focus",
        () -> new MoonlightFocusBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_LIGHT_BLUE)
            .strength(4.0f, 6.0f)
            .sound(SoundType.GLASS)
            .lightLevel(state -> 10)
            .noOcclusion()));

    public static final RegistryObject<Block> FOCUS_CHAMBER_CORE = REGISTRY.register("focus_chamber_core",
        () -> new FocusChamberCoreBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_CYAN)
            .strength(5.0f, 8.0f)
            .sound(SoundType.METAL)
            .lightLevel(state -> 5)
            .noOcclusion()));

    public static final RegistryObject<Block> CELESTIAL_ENCHANTING_TABLE = REGISTRY.register("celestial_enchanting_table",
        () -> new CelestialEnchantingTableBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_PURPLE)
            .strength(5.0f, 1200.0f)
            .sound(SoundType.STONE)
            .lightLevel(state -> 12)
            .noOcclusion()));

    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}
