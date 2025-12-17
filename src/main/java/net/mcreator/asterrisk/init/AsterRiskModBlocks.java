/*
*    MCreator note: This file will be REGENERATED on each build.
*/
package net.mcreator.asterrisk.init;

import org.checkerframework.checker.units.qual.s;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;

import net.mcreator.asterrisk.block.entity.ObeliskEnergyType;
import net.mcreator.asterrisk.block.StarryGlassBlock;
import net.mcreator.asterrisk.block.StarfallsandBlock;
import net.mcreator.asterrisk.block.StardustBlockBlock;
import net.mcreator.asterrisk.block.StarAnvilBlock;
import net.mcreator.asterrisk.block.SilveroreBlock;
import net.mcreator.asterrisk.block.SilverBricksBlock;
import net.mcreator.asterrisk.block.SilverBlockBlock;
import net.mcreator.asterrisk.block.RitualPedestalBlock;
import net.mcreator.asterrisk.block.ResonatorBlock;
import net.mcreator.asterrisk.block.PolishedMoonstoneBlock;
import net.mcreator.asterrisk.block.ObeliskBlock;
import net.mcreator.asterrisk.block.MossyMoonstoneBricksBlock;
import net.mcreator.asterrisk.block.MoonwaterBlock;
import net.mcreator.asterrisk.block.MoonstoneblockBlock;
import net.mcreator.asterrisk.block.MoonstoneTilesBlock;
import net.mcreator.asterrisk.block.MoonstoneOreBlock;
import net.mcreator.asterrisk.block.MoonstoneBricksBlock;
import net.mcreator.asterrisk.block.MoonlightLanternBlock;
import net.mcreator.asterrisk.block.MoonlightBlock;
import net.mcreator.asterrisk.block.MoonlightBeaconBlock;
import net.mcreator.asterrisk.block.MeteoriteOreBlock;
import net.mcreator.asterrisk.block.MeteoriteBlockBlock;
import net.mcreator.asterrisk.block.ManaBatteryBlock;
import net.mcreator.asterrisk.block.LunarstoneBlock;
import net.mcreator.asterrisk.block.LunarplanksBlock;
import net.mcreator.asterrisk.block.LunarlogBlock;
import net.mcreator.asterrisk.block.LunarleavesBlock;
import net.mcreator.asterrisk.block.LunargrassBlock;
import net.mcreator.asterrisk.block.LunardirtBlock;
import net.mcreator.asterrisk.block.LunarPortalBlock;
import net.mcreator.asterrisk.block.LunarPillarBlock;
import net.mcreator.asterrisk.block.LunarInfuserBlock;
import net.mcreator.asterrisk.block.LunarCollectorBlock;
import net.mcreator.asterrisk.block.EclipsestoneBlock;
import net.mcreator.asterrisk.block.CrackedMoonstoneBricksBlock;
import net.mcreator.asterrisk.block.ChiseledMoonstoneBlock;
import net.mcreator.asterrisk.block.CelestialTileBlock;
import net.mcreator.asterrisk.block.AltarCoreBlock;
import net.mcreator.asterrisk.AsterRiskMod;

public class AsterRiskModBlocks {
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, AsterRiskMod.MODID);
	public static final RegistryObject<Block> MOONSTONE_ORE = REGISTRY.register("moonstone_ore", () -> new MoonstoneOreBlock());
	public static final RegistryObject<Block> METEORITE_ORE = REGISTRY.register("meteorite_ore", () -> new MeteoriteOreBlock());
	public static final RegistryObject<Block> SILVER_ORE = REGISTRY.register("silver_ore", () -> new SilveroreBlock());
	public static final RegistryObject<Block> MOONSTONE_BLOCK = REGISTRY.register("moonstone_block", () -> new MoonstoneblockBlock());
	public static final RegistryObject<Block> SILVER_BLOCK = REGISTRY.register("silver_block", () -> new SilverBlockBlock());
	public static final RegistryObject<Block> STARDUST_BLOCK = REGISTRY.register("stardust_block", () -> new StardustBlockBlock());
	public static final RegistryObject<Block> METEORITE_BLOCK = REGISTRY.register("meteorite_block", () -> new MeteoriteBlockBlock());
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
	public static final RegistryObject<Block> LUNARGRASS = REGISTRY.register("lunargrass", () -> new LunargrassBlock());
	public static final RegistryObject<Block> LUNARDIRT = REGISTRY.register("lunardirt", () -> new LunardirtBlock());
	public static final RegistryObject<Block> LUNARSTONE = REGISTRY.register("lunarstone", () -> new LunarstoneBlock());
	public static final RegistryObject<Block> LUNARLOG = REGISTRY.register("lunarlog", () -> new LunarlogBlock());
	public static final RegistryObject<Block> LUNARLEAVES = REGISTRY.register("lunarleaves", () -> new LunarleavesBlock());
	public static final RegistryObject<Block> LUNARPLANKS = REGISTRY.register("lunarplanks", () -> new LunarplanksBlock());
	public static final RegistryObject<Block> STARFALLSAND = REGISTRY.register("starfallsand", () -> new StarfallsandBlock());
	public static final RegistryObject<Block> ECLIPSESTONE = REGISTRY.register("eclipsestone", () -> new EclipsestoneBlock());
	public static final RegistryObject<Block> MOONWATER = REGISTRY.register("moonwater", () -> new MoonwaterBlock());
	// Start of user code block custom blocks
	public static final RegistryObject<Block> MOONLIGHT = REGISTRY.register("moonlight", () -> new MoonlightBlock());
	// 機能ブロック
	public static final RegistryObject<Block> LUNAR_COLLECTOR = REGISTRY.register("lunar_collector", () -> new LunarCollectorBlock());
	public static final RegistryObject<Block> RESONATOR_TIER1 = REGISTRY.register("resonator_tier1", () -> ResonatorBlock.createTier1());
	public static final RegistryObject<Block> RESONATOR_TIER2 = REGISTRY.register("resonator_tier2", () -> ResonatorBlock.createTier2());
	public static final RegistryObject<Block> RESONATOR_TIER3 = REGISTRY.register("resonator_tier3", () -> ResonatorBlock.createTier3());
	public static final RegistryObject<Block> MANA_BATTERY = REGISTRY.register("mana_battery", () -> new ManaBatteryBlock());
	// 儀式システム
	public static final RegistryObject<Block> RITUAL_PEDESTAL = REGISTRY.register("ritual_pedestal", () -> new RitualPedestalBlock());
	public static final RegistryObject<Block> ALTAR_CORE = REGISTRY.register("altar_core", () -> new AltarCoreBlock());
	// Phase 6.1: 機能ブロック
	public static final RegistryObject<Block> MOONLIGHT_BEACON = REGISTRY.register("moonlight_beacon", () -> new MoonlightBeaconBlock());
	public static final RegistryObject<Block> LUNAR_INFUSER = REGISTRY.register("lunar_infuser", () -> new LunarInfuserBlock());
	public static final RegistryObject<Block> STAR_ANVIL = REGISTRY.register("star_anvil", () -> new StarAnvilBlock());
	// オベリスク
	public static final RegistryObject<Block> LUNAR_OBELISK = REGISTRY.register("lunar_obelisk",
			() -> new ObeliskBlock(Block.Properties.of().strength(3.0F, 6.0F).requiresCorrectToolForDrops().lightLevel(s -> 8).noOcclusion(), ObeliskEnergyType.LUNAR));
	public static final RegistryObject<Block> STELLAR_OBELISK = REGISTRY.register("stellar_obelisk",
			() -> new ObeliskBlock(Block.Properties.of().strength(3.0F, 6.0F).requiresCorrectToolForDrops().lightLevel(s -> 10).noOcclusion(), ObeliskEnergyType.STELLAR));
	public static final RegistryObject<Block> SOLAR_OBELISK = REGISTRY.register("solar_obelisk",
			() -> new ObeliskBlock(Block.Properties.of().strength(3.0F, 6.0F).requiresCorrectToolForDrops().lightLevel(s -> 12).noOcclusion(), ObeliskEnergyType.SOLAR));
	public static final RegistryObject<Block> VOID_OBELISK = REGISTRY.register("void_obelisk", () -> new ObeliskBlock(Block.Properties.of().strength(3.0F, 6.0F).requiresCorrectToolForDrops().lightLevel(s -> 4).noOcclusion(), ObeliskEnergyType.VOID));
	// ポータル
	public static final RegistryObject<Block> LUNAR_PORTAL = REGISTRY.register("lunar_portal", () -> new LunarPortalBlock());
	// End of user code block custom blocks
}