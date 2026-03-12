package net.mcreator.asterrisk.registry;

import net.mcreator.asterrisk.AsterRiskMod;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = AsterRiskMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> REGISTRY =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AsterRiskMod.MODID);

    public static final RegistryObject<CreativeModeTab> ASTER_RISK = REGISTRY.register("aster_risk",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("item_group.aster_risk.aster_risk"))
            .icon(() -> new ItemStack(ModItems.MOONSTONE.get()))
            .build());

    @SubscribeEvent
    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == ModTabs.ASTER_RISK.getKey()) {
            // === 基本素材（MCreator登録分） ===
            event.accept(ModItems.MOONSTONE.get());
            event.accept(ModBlocks.MOONSTONE_ORE.get().asItem());
            event.accept(ModItems.RAW_SILVER.get());
            event.accept(ModItems.SILVER_INGOT.get());
            event.accept(ModItems.STARFLAGMENT.get());
            event.accept(ModItems.STARDUST.get());
            event.accept(ModItems.LUNAR_DUST.get());
            event.accept(ModItems.METEORITE_FRAGMENT.get());
            event.accept(ModBlocks.METEORITE_ORE.get().asItem());
            event.accept(ModBlocks.SILVER_ORE.get().asItem());
            event.accept(ModBlocks.MOONSTONE_BLOCK.get().asItem());
            event.accept(ModBlocks.SILVER_BLOCK.get().asItem());
            event.accept(ModBlocks.STARDUST_BLOCK.get().asItem());
            event.accept(ModBlocks.METEORITE_BLOCK.get().asItem());
            event.accept(ModBlocks.MOONSTONE_BRICKS.get().asItem());
            event.accept(ModBlocks.POLISHED_MOONSTONE.get().asItem());
            event.accept(ModBlocks.MOONSTONE_TILES.get().asItem());
            event.accept(ModBlocks.CRACKED_MOONSTONE_BRICKS.get().asItem());
            event.accept(ModBlocks.MOSSY_MOONSTONE_BRICKS.get().asItem());
            event.accept(ModBlocks.SILVER_BRICKS.get().asItem());
            event.accept(ModBlocks.CHISELED_MOONSTONE.get().asItem());
            event.accept(ModBlocks.STARRY_GLASS.get().asItem());
            event.accept(ModBlocks.LUNAR_PILLAR.get().asItem());
            event.accept(ModBlocks.CELESTIAL_TILE.get().asItem());
            event.accept(ModBlocks.MOONLIGHT_LANTERN.get().asItem());
            event.accept(ModBlocks.LUNARGRASS.get().asItem());
            event.accept(ModBlocks.LUNARDIRT.get().asItem());
            event.accept(ModBlocks.LUNARSTONE.get().asItem());
            event.accept(ModBlocks.LUNARLOG.get().asItem());
            event.accept(ModBlocks.LUNARLEAVES.get().asItem());
            event.accept(ModBlocks.LUNARPLANKS.get().asItem());
            event.accept(ModBlocks.STARFALLSAND.get().asItem());
            event.accept(ModBlocks.ECLIPSESTONE.get().asItem());
            event.accept(ModItems.MOONWATER_BUCKET.get());

            // === 機能ブロック ===
            event.accept(ModItems.LUNAR_COLLECTOR.get());
            event.accept(ModItems.RESONATOR_TIER1.get());
            event.accept(ModItems.RESONATOR_TIER2.get());
            event.accept(ModItems.RESONATOR_TIER3.get());
            event.accept(ModItems.MANA_BATTERY.get());

            // === 儀式システム ===
            event.accept(ModItems.RITUAL_PEDESTAL.get());
            event.accept(ModItems.ALTAR_CORE.get());

            // === Phase 6.1: 機能ブロック ===
            event.accept(ModItems.MOONLIGHT_BEACON.get());
            event.accept(ModItems.LUNAR_INFUSER.get());
            event.accept(ModItems.STAR_ANVIL.get());

            // === 魔法道具 ===
            event.accept(ModItems.MOONLIGHT_WAND.get());
            event.accept(ModItems.STARGAZER_WAND.get());
            event.accept(ModItems.LUNAR_COMPASS.get());
            event.accept(ModItems.METEOR_WAND.get());
            event.accept(ModItems.LUNAR_HEALING_STAFF.get());
            event.accept(ModItems.LINKING_WAND.get());

            // === 武器 ===
            event.accept(ModItems.LUNAR_BLADE.get());
            event.accept(ModItems.METEOR_HAMMER.get());
            event.accept(ModItems.STARDUST_DAGGER.get());
            event.accept(ModItems.METEORITE_GREATSWORD.get());

            // === 月光の防具セット ===
            event.accept(ModItems.LUNAR_HELMET.get());
            event.accept(ModItems.LUNAR_CHESTPLATE.get());
            event.accept(ModItems.LUNAR_LEGGINGS.get());
            event.accept(ModItems.LUNAR_BOOTS.get());

            // === 星屑の防具セット ===
            event.accept(ModItems.STELLAR_CROWN.get());
            event.accept(ModItems.STELLAR_ROBE.get());
            event.accept(ModItems.STELLAR_LEGGINGS.get());
            event.accept(ModItems.STELLAR_BOOTS.get());

            // === 隕石の防具セット ===
            event.accept(ModItems.METEORITE_HELMET.get());
            event.accept(ModItems.METEORITE_CHESTPLATE.get());
            event.accept(ModItems.METEORITE_LEGGINGS.get());
            event.accept(ModItems.METEORITE_BOOTS.get());

            // === Phase 7: 消費アイテム（エフェクト付与） ===
            event.accept(ModItems.LUNAR_ELIXIR.get());
            event.accept(ModItems.STARDUST_CANDY.get());
            event.accept(ModItems.CELESTIAL_CHARM.get());

            // === Phase 8: 友好Mobスポーンエッグ ===
            event.accept(ModSpawnEggs.MOON_RABBIT_SPAWN_EGG.get());
            event.accept(ModSpawnEggs.STAR_SPIRIT_SPAWN_EGG.get());
            event.accept(ModSpawnEggs.MOONLIGHT_FAIRY_SPAWN_EGG.get());

            // === Phase 8: 敵対Mobスポーンエッグ ===
            event.accept(ModSpawnEggs.ECLIPSE_PHANTOM_SPAWN_EGG.get());
            event.accept(ModSpawnEggs.VOID_WALKER_SPAWN_EGG.get());
            event.accept(ModSpawnEggs.CORRUPTED_GOLEM_SPAWN_EGG.get());

            // === オベリスク ===
            event.accept(ModItems.LUNAR_OBELISK.get());
            event.accept(ModItems.STELLAR_OBELISK.get());
            event.accept(ModItems.SOLAR_OBELISK.get());
            event.accept(ModItems.VOID_OBELISK.get());

            // === Phase 8: ボススポーンエッグ ===
            event.accept(ModSpawnEggs.ECLIPSE_MONARCH_SPAWN_EGG.get());
            event.accept(ModSpawnEggs.STAR_DEVOURER_SPAWN_EGG.get());

            // === Phase 10: Lunar Realm ===
            event.accept(ModItems.LUNAR_SAPLING.get());

            // === Phase 11: Boss Structures ===
            event.accept(ModItems.ECLIPSE_ALTAR.get());
            event.accept(ModItems.STELLAR_SPIRE_CORE.get());

            // === Mob Drop Items ===
            event.accept(ModItems.ECLIPSE_CORE.get());
            event.accept(ModItems.STELLAR_HEART.get());
            event.accept(ModItems.SHADOW_ESSENCE.get());
            event.accept(ModItems.VOID_SHARD.get());
            event.accept(ModItems.CORRUPTED_CORE.get());

            // === Endgame Equipment - Eclipse Set ===
            event.accept(ModItems.ECLIPSE_CROWN.get());
            event.accept(ModItems.ECLIPSE_ARMOR.get());
            event.accept(ModItems.ECLIPSE_GREAVES.get());
            event.accept(ModItems.ECLIPSE_BOOTS.get());
            event.accept(ModItems.ECLIPSE_BLADE.get());

            // === Endgame Equipment - Stellar Set ===
            event.accept(ModItems.STELLAR_DIADEM.get());
            event.accept(ModItems.STELLAR_VESTMENT.get());
            event.accept(ModItems.STELLAR_PANTS.get());
            event.accept(ModItems.STELLAR_SABATONS.get());
            event.accept(ModItems.STELLAR_SCEPTER.get());

            event.accept(ModItems.ASTER_GUIDE.get());

            // === 新クラフトシステムブロック ===
            event.accept(ModItems.RITUAL_CIRCLE.get());
            event.accept(ModItems.MOONLIGHT_FOCUS.get());
            event.accept(ModItems.FOCUS_CHAMBER_CORE.get());
            event.accept(ModItems.CELESTIAL_ENCHANTING_TABLE.get());

            // === 新システム: 流星召喚・月相鍛冶・錬金術 ===
            event.accept(ModItems.ALCHEMICAL_CAULDRON.get());
            event.accept(ModItems.PHASE_ANVIL.get());
            event.accept(ModItems.METEOR_SUMMONING.get());

            // === 新素材 ===
            event.accept(ModItems.PRISMATIC_METEORITE.get());
            event.accept(ModItems.CELESTIAL_NUCLEUS.get());
            event.accept(ModItems.ENHANCED_GOLD.get());
            event.accept(ModItems.RADIANT_DIAMOND.get());
            event.accept(ModItems.STARDUST_CATALYST.get());

            // === 月相の刻印（8種） ===
            event.accept(ModItems.PHASE_SIGIL_FULL_MOON.get());
            event.accept(ModItems.PHASE_SIGIL_WANING_GIBBOUS.get());
            event.accept(ModItems.PHASE_SIGIL_LAST_QUARTER.get());
            event.accept(ModItems.PHASE_SIGIL_WANING_CRESCENT.get());
            event.accept(ModItems.PHASE_SIGIL_NEW_MOON.get());
            event.accept(ModItems.PHASE_SIGIL_WAXING_CRESCENT.get());
            event.accept(ModItems.PHASE_SIGIL_FIRST_QUARTER.get());
            event.accept(ModItems.PHASE_SIGIL_WAXING_GIBBOUS.get());

            // === 流星召喚の核（4種） ===
            event.accept(ModItems.METEOR_CORE_SMALL.get());
            event.accept(ModItems.METEOR_CORE_STARDUST.get());
            event.accept(ModItems.METEOR_CORE_PRISMATIC.get());
            event.accept(ModItems.METEOR_CORE_OMINOUS.get());

            // === 銀のツールセット ===
            event.accept(ModItems.SILVER_SWORD.get());
            event.accept(ModItems.SILVER_PICKAXE.get());
            event.accept(ModItems.SILVER_AXE.get());
            event.accept(ModItems.SILVER_SHOVEL.get());
            event.accept(ModItems.SILVER_HOE.get());

            // === 月光石のツールセット ===
            event.accept(ModItems.MOONSTONE_SWORD.get());
            event.accept(ModItems.MOONSTONE_PICKAXE.get());
            event.accept(ModItems.MOONSTONE_AXE.get());
            event.accept(ModItems.MOONSTONE_SHOVEL.get());
            event.accept(ModItems.MOONSTONE_HOE.get());

            // === 新武器 ===
            event.accept(ModItems.PRISMATIC_GREATSWORD.get());
            event.accept(ModItems.VOID_DAGGER.get());
            event.accept(ModItems.SHADOW_SCYTHE.get());

            // === 銀の防具セット ===
            event.accept(ModItems.SILVER_HELMET.get());
            event.accept(ModItems.SILVER_CHESTPLATE.get());
            event.accept(ModItems.SILVER_LEGGINGS.get());
            event.accept(ModItems.SILVER_BOOTS.get());

            // === 虚空の防具セット ===
            event.accept(ModItems.VOID_HELMET.get());
            event.accept(ModItems.VOID_CHESTPLATE.get());
            event.accept(ModItems.VOID_LEGGINGS.get());
            event.accept(ModItems.VOID_BOOTS.get());
        }
    }
}
