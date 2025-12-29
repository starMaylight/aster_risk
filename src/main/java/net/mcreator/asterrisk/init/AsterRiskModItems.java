/*
*    MCreator note: This file will be REGENERATED on each build.
*/
package net.mcreator.asterrisk.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ArmorItem;

import net.mcreator.asterrisk.item.weapon.StellarScepterItem;
import net.mcreator.asterrisk.item.weapon.StardustDaggerItem;
import net.mcreator.asterrisk.item.weapon.MeteoriteGreatswordItem;
import net.mcreator.asterrisk.item.weapon.MeteorHammerItem;
import net.mcreator.asterrisk.item.weapon.LunarBladeItem;
import net.mcreator.asterrisk.item.weapon.EclipseBladeItem;
import net.mcreator.asterrisk.item.armor.StellarEndgameArmorItem;
import net.mcreator.asterrisk.item.armor.StellarArmorItem;
import net.mcreator.asterrisk.item.armor.MeteoriteArmorItem;
import net.mcreator.asterrisk.item.armor.LunarArmorItem;
import net.mcreator.asterrisk.item.armor.EclipseArmorItem;
import net.mcreator.asterrisk.item.VoidShardItem;
import net.mcreator.asterrisk.item.StellarHeartItem;
import net.mcreator.asterrisk.item.StargazerWandItem;
import net.mcreator.asterrisk.item.StarflagmentItem;
import net.mcreator.asterrisk.item.StardustItem;
import net.mcreator.asterrisk.item.StardustCatalystItem;
import net.mcreator.asterrisk.item.StardustCandyItem;
import net.mcreator.asterrisk.item.SilveringotItem;
import net.mcreator.asterrisk.item.ShadowEssenceItem;
import net.mcreator.asterrisk.item.RawsilverItem;
import net.mcreator.asterrisk.item.RadiantDiamondItem;
import net.mcreator.asterrisk.item.PrismaticMeteoriteItem;
import net.mcreator.asterrisk.item.PhaseSigilItem;
import net.mcreator.asterrisk.item.MoonwaterItem;
import net.mcreator.asterrisk.item.MoonstoneItem;
import net.mcreator.asterrisk.item.MoonlightWandItem;
import net.mcreator.asterrisk.item.MeteoritefragmentItem;
import net.mcreator.asterrisk.item.MeteorWandItem;
import net.mcreator.asterrisk.item.MeteorSummonCoreItem;
import net.mcreator.asterrisk.item.LunardustItem;
import net.mcreator.asterrisk.item.LunarHealingStaffItem;
import net.mcreator.asterrisk.item.LunarElixirItem;
import net.mcreator.asterrisk.item.LunarCompassItem;
import net.mcreator.asterrisk.item.LinkingWandItem;
import net.mcreator.asterrisk.item.EnhancedGoldItem;
import net.mcreator.asterrisk.item.EclipseCoreItem;
import net.mcreator.asterrisk.item.CorruptedCoreItem;
import net.mcreator.asterrisk.item.CelestialNucleusItem;
import net.mcreator.asterrisk.item.CelestialCharmItem;
import net.mcreator.asterrisk.item.AsterGuideItem;
import net.mcreator.asterrisk.AsterRiskMod;

public class AsterRiskModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, AsterRiskMod.MODID);
	public static final RegistryObject<Item> MOONSTONE = REGISTRY.register("moonstone", () -> new MoonstoneItem());
	public static final RegistryObject<Item> MOONSTONE_ORE = block(AsterRiskModBlocks.MOONSTONE_ORE);
	public static final RegistryObject<Item> RAW_SILVER = REGISTRY.register("raw_silver", () -> new RawsilverItem());
	public static final RegistryObject<Item> SILVER_INGOT = REGISTRY.register("silver_ingot", () -> new SilveringotItem());
	public static final RegistryObject<Item> STARFLAGMENT = REGISTRY.register("starflagment", () -> new StarflagmentItem());
	public static final RegistryObject<Item> STARDUST = REGISTRY.register("stardust", () -> new StardustItem());
	public static final RegistryObject<Item> LUNAR_DUST = REGISTRY.register("lunar_dust", () -> new LunardustItem());
	public static final RegistryObject<Item> METEORITE_FRAGMENT = REGISTRY.register("meteorite_fragment", () -> new MeteoritefragmentItem());
	public static final RegistryObject<Item> METEORITE_ORE = block(AsterRiskModBlocks.METEORITE_ORE);
	public static final RegistryObject<Item> SILVER_ORE = block(AsterRiskModBlocks.SILVER_ORE);
	public static final RegistryObject<Item> MOONSTONE_BLOCK = block(AsterRiskModBlocks.MOONSTONE_BLOCK);
	public static final RegistryObject<Item> SILVER_BLOCK = block(AsterRiskModBlocks.SILVER_BLOCK);
	public static final RegistryObject<Item> STARDUST_BLOCK = block(AsterRiskModBlocks.STARDUST_BLOCK);
	public static final RegistryObject<Item> METEORITE_BLOCK = block(AsterRiskModBlocks.METEORITE_BLOCK);
	public static final RegistryObject<Item> MOONSTONE_BRICKS = block(AsterRiskModBlocks.MOONSTONE_BRICKS);
	public static final RegistryObject<Item> POLISHED_MOONSTONE = block(AsterRiskModBlocks.POLISHED_MOONSTONE);
	public static final RegistryObject<Item> MOONSTONE_TILES = block(AsterRiskModBlocks.MOONSTONE_TILES);
	public static final RegistryObject<Item> CRACKED_MOONSTONE_BRICKS = block(AsterRiskModBlocks.CRACKED_MOONSTONE_BRICKS);
	public static final RegistryObject<Item> MOSSY_MOONSTONE_BRICKS = block(AsterRiskModBlocks.MOSSY_MOONSTONE_BRICKS);
	public static final RegistryObject<Item> SILVER_BRICKS = block(AsterRiskModBlocks.SILVER_BRICKS);
	public static final RegistryObject<Item> CHISELED_MOONSTONE = block(AsterRiskModBlocks.CHISELED_MOONSTONE);
	public static final RegistryObject<Item> STARRY_GLASS = block(AsterRiskModBlocks.STARRY_GLASS);
	public static final RegistryObject<Item> LUNAR_PILLAR = block(AsterRiskModBlocks.LUNAR_PILLAR);
	public static final RegistryObject<Item> CELESTIAL_TILE = block(AsterRiskModBlocks.CELESTIAL_TILE);
	public static final RegistryObject<Item> MOONLIGHT_LANTERN = block(AsterRiskModBlocks.MOONLIGHT_LANTERN);
	public static final RegistryObject<Item> LUNARGRASS = block(AsterRiskModBlocks.LUNARGRASS);
	public static final RegistryObject<Item> LUNARDIRT = block(AsterRiskModBlocks.LUNARDIRT);
	public static final RegistryObject<Item> LUNARSTONE = block(AsterRiskModBlocks.LUNARSTONE);
	public static final RegistryObject<Item> LUNARLOG = block(AsterRiskModBlocks.LUNARLOG);
	public static final RegistryObject<Item> LUNARLEAVES = block(AsterRiskModBlocks.LUNARLEAVES);
	public static final RegistryObject<Item> LUNARPLANKS = block(AsterRiskModBlocks.LUNARPLANKS);
	public static final RegistryObject<Item> STARFALLSAND = block(AsterRiskModBlocks.STARFALLSAND);
	public static final RegistryObject<Item> ECLIPSESTONE = block(AsterRiskModBlocks.ECLIPSESTONE);
	public static final RegistryObject<Item> MOONWATER_BUCKET = REGISTRY.register("moonwater_bucket", () -> new MoonwaterItem());
	// Start of user code block custom items
	// 魔法道具
	public static final RegistryObject<Item> MOONLIGHT_WAND = REGISTRY.register("moonlight_wand", () -> new MoonlightWandItem());
	public static final RegistryObject<Item> STARGAZER_WAND = REGISTRY.register("stargazer_wand", () -> new StargazerWandItem());
	public static final RegistryObject<Item> LUNAR_COMPASS = REGISTRY.register("lunar_compass", () -> new LunarCompassItem());
	public static final RegistryObject<Item> METEOR_WAND = REGISTRY.register("meteor_wand", () -> new MeteorWandItem());
	public static final RegistryObject<Item> LUNAR_HEALING_STAFF = REGISTRY.register("lunar_healing_staff", () -> new LunarHealingStaffItem());
	public static final RegistryObject<Item> LINKING_WAND = REGISTRY.register("linking_wand", () -> new LinkingWandItem());
	// 月光の防具セット（Lunar Armor）
	public static final RegistryObject<Item> LUNAR_HELMET = REGISTRY.register("lunar_helmet", () -> new LunarArmorItem(ArmorItem.Type.HELMET, new Item.Properties(), "Lunar Helmet"));
	public static final RegistryObject<Item> LUNAR_CHESTPLATE = REGISTRY.register("lunar_chestplate", () -> new LunarArmorItem(ArmorItem.Type.CHESTPLATE, new Item.Properties(), "Lunar Chestplate"));
	public static final RegistryObject<Item> LUNAR_LEGGINGS = REGISTRY.register("lunar_leggings", () -> new LunarArmorItem(ArmorItem.Type.LEGGINGS, new Item.Properties(), "Lunar Leggings"));
	public static final RegistryObject<Item> LUNAR_BOOTS = REGISTRY.register("lunar_boots", () -> new LunarArmorItem(ArmorItem.Type.BOOTS, new Item.Properties(), "Lunar Boots"));
	// 星屑の防具セット（Stellar Armor）
	public static final RegistryObject<Item> STELLAR_CROWN = REGISTRY.register("stellar_crown", () -> new StellarArmorItem(ArmorItem.Type.HELMET, new Item.Properties(), "Stellar Crown"));
	public static final RegistryObject<Item> STELLAR_ROBE = REGISTRY.register("stellar_robe", () -> new StellarArmorItem(ArmorItem.Type.CHESTPLATE, new Item.Properties(), "Stellar Robe"));
	public static final RegistryObject<Item> STELLAR_LEGGINGS = REGISTRY.register("stellar_leggings", () -> new StellarArmorItem(ArmorItem.Type.LEGGINGS, new Item.Properties(), "Stellar Leggings"));
	public static final RegistryObject<Item> STELLAR_BOOTS = REGISTRY.register("stellar_boots", () -> new StellarArmorItem(ArmorItem.Type.BOOTS, new Item.Properties(), "Stellar Boots"));
	// 隕石の防具セット（Meteorite Armor）
	public static final RegistryObject<Item> METEORITE_HELMET = REGISTRY.register("meteorite_helmet", () -> new MeteoriteArmorItem(ArmorItem.Type.HELMET, new Item.Properties(), "Meteorite Helmet"));
	public static final RegistryObject<Item> METEORITE_CHESTPLATE = REGISTRY.register("meteorite_chestplate", () -> new MeteoriteArmorItem(ArmorItem.Type.CHESTPLATE, new Item.Properties(), "Meteorite Chestplate"));
	public static final RegistryObject<Item> METEORITE_LEGGINGS = REGISTRY.register("meteorite_leggings", () -> new MeteoriteArmorItem(ArmorItem.Type.LEGGINGS, new Item.Properties(), "Meteorite Leggings"));
	public static final RegistryObject<Item> METEORITE_BOOTS = REGISTRY.register("meteorite_boots", () -> new MeteoriteArmorItem(ArmorItem.Type.BOOTS, new Item.Properties(), "Meteorite Boots"));
	// 武器
	public static final RegistryObject<Item> LUNAR_BLADE = REGISTRY.register("lunar_blade", () -> new LunarBladeItem(new Item.Properties()));
	public static final RegistryObject<Item> METEOR_HAMMER = REGISTRY.register("meteor_hammer", () -> new MeteorHammerItem(new Item.Properties()));
	public static final RegistryObject<Item> STARDUST_DAGGER = REGISTRY.register("stardust_dagger", () -> new StardustDaggerItem(new Item.Properties()));
	public static final RegistryObject<Item> METEORITE_GREATSWORD = REGISTRY.register("meteorite_greatsword", () -> new MeteoriteGreatswordItem(new Item.Properties()));
	// 機能ブロックアイテム
	public static final RegistryObject<Item> LUNAR_COLLECTOR = block(AsterRiskModBlocks.LUNAR_COLLECTOR);
	public static final RegistryObject<Item> RESONATOR_TIER1 = block(AsterRiskModBlocks.RESONATOR_TIER1);
	public static final RegistryObject<Item> RESONATOR_TIER2 = block(AsterRiskModBlocks.RESONATOR_TIER2);
	public static final RegistryObject<Item> RESONATOR_TIER3 = block(AsterRiskModBlocks.RESONATOR_TIER3);
	public static final RegistryObject<Item> MANA_BATTERY = block(AsterRiskModBlocks.MANA_BATTERY);
	// 儀式システム
	public static final RegistryObject<Item> RITUAL_PEDESTAL = block(AsterRiskModBlocks.RITUAL_PEDESTAL);
	public static final RegistryObject<Item> ALTAR_CORE = block(AsterRiskModBlocks.ALTAR_CORE);
	// Phase 6.1: 機能ブロック
	public static final RegistryObject<Item> MOONLIGHT_BEACON = block(AsterRiskModBlocks.MOONLIGHT_BEACON);
	public static final RegistryObject<Item> LUNAR_INFUSER = block(AsterRiskModBlocks.LUNAR_INFUSER);
	public static final RegistryObject<Item> STAR_ANVIL = block(AsterRiskModBlocks.STAR_ANVIL);
	// Phase 7: 消費アイテム（エフェクト付与）
	public static final RegistryObject<Item> LUNAR_ELIXIR = REGISTRY.register("lunar_elixir", LunarElixirItem::new);
	public static final RegistryObject<Item> STARDUST_CANDY = REGISTRY.register("stardust_candy", StardustCandyItem::new);
	public static final RegistryObject<Item> CELESTIAL_CHARM = REGISTRY.register("celestial_charm", CelestialCharmItem::new);
	// オベリスク
	public static final RegistryObject<Item> LUNAR_OBELISK = block(AsterRiskModBlocks.LUNAR_OBELISK);
	public static final RegistryObject<Item> STELLAR_OBELISK = block(AsterRiskModBlocks.STELLAR_OBELISK);
	public static final RegistryObject<Item> SOLAR_OBELISK = block(AsterRiskModBlocks.SOLAR_OBELISK);
	public static final RegistryObject<Item> VOID_OBELISK = block(AsterRiskModBlocks.VOID_OBELISK);
	// 苗木
	public static final RegistryObject<Item> LUNAR_SAPLING = block(AsterRiskModBlocks.LUNAR_SAPLING);
	// ボス召喚祈壇
	public static final RegistryObject<Item> ECLIPSE_ALTAR = block(AsterRiskModBlocks.ECLIPSE_ALTAR);
	public static final RegistryObject<Item> STELLAR_SPIRE_CORE = block(AsterRiskModBlocks.STELLAR_SPIRE_CORE);
	// ボスドロップアイテム
	public static final RegistryObject<Item> ECLIPSE_CORE = REGISTRY.register("eclipse_core", EclipseCoreItem::new);
	public static final RegistryObject<Item> STELLAR_HEART = REGISTRY.register("stellar_heart", StellarHeartItem::new);
	// 敵対Mobドロップアイテム
	public static final RegistryObject<Item> SHADOW_ESSENCE = REGISTRY.register("shadow_essence", ShadowEssenceItem::new);
	public static final RegistryObject<Item> VOID_SHARD = REGISTRY.register("void_shard", VoidShardItem::new);
	public static final RegistryObject<Item> CORRUPTED_CORE = REGISTRY.register("corrupted_core", CorruptedCoreItem::new);
	// 最終装備 - Eclipse Set
	public static final RegistryObject<Item> ECLIPSE_CROWN = REGISTRY.register("eclipse_crown", () -> new EclipseArmorItem(ArmorItem.Type.HELMET, new Item.Properties()));
	public static final RegistryObject<Item> ECLIPSE_ARMOR = REGISTRY.register("eclipse_armor", () -> new EclipseArmorItem(ArmorItem.Type.CHESTPLATE, new Item.Properties()));
	public static final RegistryObject<Item> ECLIPSE_GREAVES = REGISTRY.register("eclipse_greaves", () -> new EclipseArmorItem(ArmorItem.Type.LEGGINGS, new Item.Properties()));
	public static final RegistryObject<Item> ECLIPSE_BOOTS = REGISTRY.register("eclipse_boots", () -> new EclipseArmorItem(ArmorItem.Type.BOOTS, new Item.Properties()));
	public static final RegistryObject<Item> ECLIPSE_BLADE = REGISTRY.register("eclipse_blade", () -> new EclipseBladeItem(new Item.Properties()));
	// 最終装備 - Stellar Set
	public static final RegistryObject<Item> STELLAR_DIADEM = REGISTRY.register("stellar_diadem", () -> new StellarEndgameArmorItem(ArmorItem.Type.HELMET, new Item.Properties()));
	public static final RegistryObject<Item> STELLAR_VESTMENT = REGISTRY.register("stellar_vestment", () -> new StellarEndgameArmorItem(ArmorItem.Type.CHESTPLATE, new Item.Properties()));
	public static final RegistryObject<Item> STELLAR_PANTS = REGISTRY.register("stellar_pants", () -> new StellarEndgameArmorItem(ArmorItem.Type.LEGGINGS, new Item.Properties()));
	public static final RegistryObject<Item> STELLAR_SABATONS = REGISTRY.register("stellar_sabatons", () -> new StellarEndgameArmorItem(ArmorItem.Type.BOOTS, new Item.Properties()));
	public static final RegistryObject<Item> STELLAR_SCEPTER = REGISTRY.register("stellar_scepter", () -> new StellarScepterItem(new Item.Properties()));
	public static final RegistryObject<Item> ASTER_GUIDE = REGISTRY.register("aster_guide", AsterGuideItem::new);
	// === 新システム: 流星召喚・月相鍛冶・錬金術 ===
	// 新素材
	public static final RegistryObject<Item> PRISMATIC_METEORITE = REGISTRY.register("prismatic_meteorite", PrismaticMeteoriteItem::new);
	public static final RegistryObject<Item> CELESTIAL_NUCLEUS = REGISTRY.register("celestial_nucleus", CelestialNucleusItem::new);
	public static final RegistryObject<Item> ENHANCED_GOLD = REGISTRY.register("enhanced_gold", EnhancedGoldItem::new);
	public static final RegistryObject<Item> RADIANT_DIAMOND = REGISTRY.register("radiant_diamond", RadiantDiamondItem::new);
	public static final RegistryObject<Item> STARDUST_CATALYST = REGISTRY.register("stardust_catalyst", StardustCatalystItem::new);
	// 月相の刻印（8種）
	public static final RegistryObject<Item> PHASE_SIGIL_FULL_MOON = REGISTRY.register("phase_sigil_full_moon", () -> new PhaseSigilItem(PhaseSigilItem.MoonPhase.FULL_MOON));
	public static final RegistryObject<Item> PHASE_SIGIL_WANING_GIBBOUS = REGISTRY.register("phase_sigil_waning_gibbous", () -> new PhaseSigilItem(PhaseSigilItem.MoonPhase.WANING_GIBBOUS));
	public static final RegistryObject<Item> PHASE_SIGIL_LAST_QUARTER = REGISTRY.register("phase_sigil_last_quarter", () -> new PhaseSigilItem(PhaseSigilItem.MoonPhase.LAST_QUARTER));
	public static final RegistryObject<Item> PHASE_SIGIL_WANING_CRESCENT = REGISTRY.register("phase_sigil_waning_crescent", () -> new PhaseSigilItem(PhaseSigilItem.MoonPhase.WANING_CRESCENT));
	public static final RegistryObject<Item> PHASE_SIGIL_NEW_MOON = REGISTRY.register("phase_sigil_new_moon", () -> new PhaseSigilItem(PhaseSigilItem.MoonPhase.NEW_MOON));
	public static final RegistryObject<Item> PHASE_SIGIL_WAXING_CRESCENT = REGISTRY.register("phase_sigil_waxing_crescent", () -> new PhaseSigilItem(PhaseSigilItem.MoonPhase.WAXING_CRESCENT));
	public static final RegistryObject<Item> PHASE_SIGIL_FIRST_QUARTER = REGISTRY.register("phase_sigil_first_quarter", () -> new PhaseSigilItem(PhaseSigilItem.MoonPhase.FIRST_QUARTER));
	public static final RegistryObject<Item> PHASE_SIGIL_WAXING_GIBBOUS = REGISTRY.register("phase_sigil_waxing_gibbous", () -> new PhaseSigilItem(PhaseSigilItem.MoonPhase.WAXING_GIBBOUS));
	// 流星召喚の核（4種）
	public static final RegistryObject<Item> METEOR_CORE_SMALL = REGISTRY.register("meteor_core_small", () -> new MeteorSummonCoreItem(MeteorSummonCoreItem.MeteorType.SMALL));
	public static final RegistryObject<Item> METEOR_CORE_STARDUST = REGISTRY.register("meteor_core_stardust", () -> new MeteorSummonCoreItem(MeteorSummonCoreItem.MeteorType.STARDUST));
	public static final RegistryObject<Item> METEOR_CORE_PRISMATIC = REGISTRY.register("meteor_core_prismatic", () -> new MeteorSummonCoreItem(MeteorSummonCoreItem.MeteorType.PRISMATIC));
	public static final RegistryObject<Item> METEOR_CORE_OMINOUS = REGISTRY.register("meteor_core_ominous", () -> new MeteorSummonCoreItem(MeteorSummonCoreItem.MeteorType.OMINOUS));
	// 新ブロックアイテム
	public static final RegistryObject<Item> ALCHEMICAL_CAULDRON = block(AsterRiskModBlocks.ALCHEMICAL_CAULDRON);
	public static final RegistryObject<Item> PHASE_ANVIL = block(AsterRiskModBlocks.PHASE_ANVIL);
	public static final RegistryObject<Item> METEOR_SUMMONING = block(AsterRiskModBlocks.METEOR_SUMMONING);
	// === 新ツール・武器・防具 ===
	// 銀のツールセット
	public static final RegistryObject<Item> SILVER_SWORD = REGISTRY.register("silver_sword", () -> new net.mcreator.asterrisk.item.tool.SilverToolItems.SilverSword(new Item.Properties()));
	public static final RegistryObject<Item> SILVER_PICKAXE = REGISTRY.register("silver_pickaxe", () -> new net.mcreator.asterrisk.item.tool.SilverToolItems.SilverPickaxe(new Item.Properties()));
	public static final RegistryObject<Item> SILVER_AXE = REGISTRY.register("silver_axe", () -> new net.mcreator.asterrisk.item.tool.SilverToolItems.SilverAxe(new Item.Properties()));
	public static final RegistryObject<Item> SILVER_SHOVEL = REGISTRY.register("silver_shovel", () -> new net.mcreator.asterrisk.item.tool.SilverToolItems.SilverShovel(new Item.Properties()));
	public static final RegistryObject<Item> SILVER_HOE = REGISTRY.register("silver_hoe", () -> new net.mcreator.asterrisk.item.tool.SilverToolItems.SilverHoe(new Item.Properties()));
	// 月光石のツールセット
	public static final RegistryObject<Item> MOONSTONE_SWORD = REGISTRY.register("moonstone_sword", () -> new net.mcreator.asterrisk.item.tool.MoonstoneToolItems.MoonstoneSword(new Item.Properties()));
	public static final RegistryObject<Item> MOONSTONE_PICKAXE = REGISTRY.register("moonstone_pickaxe", () -> new net.mcreator.asterrisk.item.tool.MoonstoneToolItems.MoonstonePickaxe(new Item.Properties()));
	public static final RegistryObject<Item> MOONSTONE_AXE = REGISTRY.register("moonstone_axe", () -> new net.mcreator.asterrisk.item.tool.MoonstoneToolItems.MoonstoneAxe(new Item.Properties()));
	public static final RegistryObject<Item> MOONSTONE_SHOVEL = REGISTRY.register("moonstone_shovel", () -> new net.mcreator.asterrisk.item.tool.MoonstoneToolItems.MoonstoneShovel(new Item.Properties()));
	public static final RegistryObject<Item> MOONSTONE_HOE = REGISTRY.register("moonstone_hoe", () -> new net.mcreator.asterrisk.item.tool.MoonstoneToolItems.MoonstoneHoe(new Item.Properties()));
	// 新武器
	public static final RegistryObject<Item> PRISMATIC_GREATSWORD = REGISTRY.register("prismatic_greatsword", () -> new net.mcreator.asterrisk.item.weapon.PrismaticGreatswordItem(new Item.Properties()));
	public static final RegistryObject<Item> VOID_DAGGER = REGISTRY.register("void_dagger", () -> new net.mcreator.asterrisk.item.weapon.VoidDaggerItem(new Item.Properties()));
	public static final RegistryObject<Item> SHADOW_SCYTHE = REGISTRY.register("shadow_scythe", () -> new net.mcreator.asterrisk.item.weapon.ShadowScytheItem(new Item.Properties()));
	// 銀の防具セット
	public static final RegistryObject<Item> SILVER_HELMET = REGISTRY.register("silver_helmet", () -> new net.mcreator.asterrisk.item.armor.SilverArmorItem(ArmorItem.Type.HELMET, new Item.Properties()));
	public static final RegistryObject<Item> SILVER_CHESTPLATE = REGISTRY.register("silver_chestplate", () -> new net.mcreator.asterrisk.item.armor.SilverArmorItem(ArmorItem.Type.CHESTPLATE, new Item.Properties()));
	public static final RegistryObject<Item> SILVER_LEGGINGS = REGISTRY.register("silver_leggings", () -> new net.mcreator.asterrisk.item.armor.SilverArmorItem(ArmorItem.Type.LEGGINGS, new Item.Properties()));
	public static final RegistryObject<Item> SILVER_BOOTS = REGISTRY.register("silver_boots", () -> new net.mcreator.asterrisk.item.armor.SilverArmorItem(ArmorItem.Type.BOOTS, new Item.Properties()));
	// 虚空の防具セット
	public static final RegistryObject<Item> VOID_HELMET = REGISTRY.register("void_helmet", () -> new net.mcreator.asterrisk.item.armor.VoidArmorItem(ArmorItem.Type.HELMET, new Item.Properties()));
	public static final RegistryObject<Item> VOID_CHESTPLATE = REGISTRY.register("void_chestplate", () -> new net.mcreator.asterrisk.item.armor.VoidArmorItem(ArmorItem.Type.CHESTPLATE, new Item.Properties()));
	public static final RegistryObject<Item> VOID_LEGGINGS = REGISTRY.register("void_leggings", () -> new net.mcreator.asterrisk.item.armor.VoidArmorItem(ArmorItem.Type.LEGGINGS, new Item.Properties()));
	public static final RegistryObject<Item> VOID_BOOTS = REGISTRY.register("void_boots", () -> new net.mcreator.asterrisk.item.armor.VoidArmorItem(ArmorItem.Type.BOOTS, new Item.Properties()));

	// End of user code block custom items
	private static RegistryObject<Item> block(RegistryObject<Block> block) {
		return block(block, new Item.Properties());
	}

	private static RegistryObject<Item> block(RegistryObject<Block> block, Item.Properties properties) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), properties));
	}
}