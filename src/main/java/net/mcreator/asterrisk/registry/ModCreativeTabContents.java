package net.mcreator.asterrisk.registry;

import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.init.AsterRiskModItems;
import net.mcreator.asterrisk.init.AsterRiskModBlocks;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * カスタムアイテムをCreativeTabに追加
 * MCreatorのAsterRiskModTabs.javaは上書きされるため、ここで独自追加
 */
@Mod.EventBusSubscriber(modid = AsterRiskMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCreativeTabContents {

    @SubscribeEvent
    public static void addCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        // Aster Riskタブに追加
        if (event.getTabKey() == net.mcreator.asterrisk.init.AsterRiskModTabs.ASTER_RISK.getKey()) {
            // === 機能ブロック ===
            event.accept(AsterRiskModItems.LUNAR_COLLECTOR.get());
            event.accept(AsterRiskModItems.RESONATOR_TIER1.get());
            event.accept(AsterRiskModItems.RESONATOR_TIER2.get());
            event.accept(AsterRiskModItems.RESONATOR_TIER3.get());
            event.accept(AsterRiskModItems.MANA_BATTERY.get());
            event.accept(AsterRiskModItems.RITUAL_PEDESTAL.get());
            event.accept(AsterRiskModItems.ALTAR_CORE.get());
            event.accept(AsterRiskModItems.MOONLIGHT_BEACON.get());
            event.accept(AsterRiskModItems.LUNAR_INFUSER.get());
            event.accept(AsterRiskModItems.STAR_ANVIL.get());
            
            // === オベリスク ===
            event.accept(AsterRiskModItems.LUNAR_OBELISK.get());
            event.accept(AsterRiskModItems.STELLAR_OBELISK.get());
            event.accept(AsterRiskModItems.SOLAR_OBELISK.get());
            event.accept(AsterRiskModItems.VOID_OBELISK.get());
            
            // === 苗木 ===
            event.accept(AsterRiskModItems.LUNAR_SAPLING.get());
            
            // === ボス召喚祈壇 ===
            event.accept(AsterRiskModItems.ECLIPSE_ALTAR.get());
            event.accept(AsterRiskModItems.STELLAR_SPIRE_CORE.get());
            
            // === 魔法道具 ===
            event.accept(AsterRiskModItems.MOONLIGHT_WAND.get());
            event.accept(AsterRiskModItems.STARGAZER_WAND.get());
            event.accept(AsterRiskModItems.LUNAR_COMPASS.get());
            event.accept(AsterRiskModItems.METEOR_WAND.get());
            event.accept(AsterRiskModItems.LUNAR_HEALING_STAFF.get());
            event.accept(AsterRiskModItems.LINKING_WAND.get());
            
            // === 防具 - Lunar ===
            event.accept(AsterRiskModItems.LUNAR_HELMET.get());
            event.accept(AsterRiskModItems.LUNAR_CHESTPLATE.get());
            event.accept(AsterRiskModItems.LUNAR_LEGGINGS.get());
            event.accept(AsterRiskModItems.LUNAR_BOOTS.get());
            
            // === 防具 - Stellar ===
            event.accept(AsterRiskModItems.STELLAR_CROWN.get());
            event.accept(AsterRiskModItems.STELLAR_ROBE.get());
            event.accept(AsterRiskModItems.STELLAR_LEGGINGS.get());
            event.accept(AsterRiskModItems.STELLAR_BOOTS.get());
            
            // === 防具 - Meteorite ===
            event.accept(AsterRiskModItems.METEORITE_HELMET.get());
            event.accept(AsterRiskModItems.METEORITE_CHESTPLATE.get());
            event.accept(AsterRiskModItems.METEORITE_LEGGINGS.get());
            event.accept(AsterRiskModItems.METEORITE_BOOTS.get());
            
            // === 武器 ===
            event.accept(AsterRiskModItems.LUNAR_BLADE.get());
            event.accept(AsterRiskModItems.METEOR_HAMMER.get());
            event.accept(AsterRiskModItems.STARDUST_DAGGER.get());
            event.accept(AsterRiskModItems.METEORITE_GREATSWORD.get());
            
            // === 消費アイテム ===
            event.accept(AsterRiskModItems.LUNAR_ELIXIR.get());
            event.accept(AsterRiskModItems.STARDUST_CANDY.get());
            event.accept(AsterRiskModItems.CELESTIAL_CHARM.get());
            
            // === ボスドロップ ===
            event.accept(AsterRiskModItems.ECLIPSE_CORE.get());
            event.accept(AsterRiskModItems.STELLAR_HEART.get());
            event.accept(AsterRiskModItems.SHADOW_ESSENCE.get());
            event.accept(AsterRiskModItems.VOID_SHARD.get());
            event.accept(AsterRiskModItems.CORRUPTED_CORE.get());
            
            // === 最終装備 - Eclipse ===
            event.accept(AsterRiskModItems.ECLIPSE_CROWN.get());
            event.accept(AsterRiskModItems.ECLIPSE_ARMOR.get());
            event.accept(AsterRiskModItems.ECLIPSE_GREAVES.get());
            event.accept(AsterRiskModItems.ECLIPSE_BOOTS.get());
            event.accept(AsterRiskModItems.ECLIPSE_BLADE.get());
            
            // === 最終装備 - Stellar ===
            event.accept(AsterRiskModItems.STELLAR_DIADEM.get());
            event.accept(AsterRiskModItems.STELLAR_VESTMENT.get());
            event.accept(AsterRiskModItems.STELLAR_PANTS.get());
            event.accept(AsterRiskModItems.STELLAR_SABATONS.get());
            event.accept(AsterRiskModItems.STELLAR_SCEPTER.get());
            
            // === ガイドブック ===
            event.accept(AsterRiskModItems.ASTER_GUIDE.get());
            
            // === 新システム: 流星召喚・月相鍛冶・錬金術 ===
            // 新ブロック
            event.accept(AsterRiskModItems.ALCHEMICAL_CAULDRON.get());
            event.accept(AsterRiskModItems.PHASE_ANVIL.get());
            event.accept(AsterRiskModItems.METEOR_SUMMONING.get());
            
            // 新素材
            event.accept(AsterRiskModItems.PRISMATIC_METEORITE.get());
            event.accept(AsterRiskModItems.CELESTIAL_NUCLEUS.get());
            event.accept(AsterRiskModItems.ENHANCED_GOLD.get());
            event.accept(AsterRiskModItems.RADIANT_DIAMOND.get());
            event.accept(AsterRiskModItems.STARDUST_CATALYST.get());
            
            // 月相の刻印
            event.accept(AsterRiskModItems.PHASE_SIGIL_FULL_MOON.get());
            event.accept(AsterRiskModItems.PHASE_SIGIL_WANING_GIBBOUS.get());
            event.accept(AsterRiskModItems.PHASE_SIGIL_LAST_QUARTER.get());
            event.accept(AsterRiskModItems.PHASE_SIGIL_WANING_CRESCENT.get());
            event.accept(AsterRiskModItems.PHASE_SIGIL_NEW_MOON.get());
            event.accept(AsterRiskModItems.PHASE_SIGIL_WAXING_CRESCENT.get());
            event.accept(AsterRiskModItems.PHASE_SIGIL_FIRST_QUARTER.get());
            event.accept(AsterRiskModItems.PHASE_SIGIL_WAXING_GIBBOUS.get());
            
            // 流星召喚の核
            event.accept(AsterRiskModItems.METEOR_CORE_SMALL.get());
            event.accept(AsterRiskModItems.METEOR_CORE_STARDUST.get());
            event.accept(AsterRiskModItems.METEOR_CORE_PRISMATIC.get());
            event.accept(AsterRiskModItems.METEOR_CORE_OMINOUS.get());
            
            // === 新ツール・武器・防具 ===
            // 銀のツールセット
            event.accept(AsterRiskModItems.SILVER_SWORD.get());
            event.accept(AsterRiskModItems.SILVER_PICKAXE.get());
            event.accept(AsterRiskModItems.SILVER_AXE.get());
            event.accept(AsterRiskModItems.SILVER_SHOVEL.get());
            event.accept(AsterRiskModItems.SILVER_HOE.get());
            
            // 月光石のツールセット
            event.accept(AsterRiskModItems.MOONSTONE_SWORD.get());
            event.accept(AsterRiskModItems.MOONSTONE_PICKAXE.get());
            event.accept(AsterRiskModItems.MOONSTONE_AXE.get());
            event.accept(AsterRiskModItems.MOONSTONE_SHOVEL.get());
            event.accept(AsterRiskModItems.MOONSTONE_HOE.get());
            
            // 新武器
            event.accept(AsterRiskModItems.PRISMATIC_GREATSWORD.get());
            event.accept(AsterRiskModItems.VOID_DAGGER.get());
            event.accept(AsterRiskModItems.SHADOW_SCYTHE.get());
            
            // 銀の防具セット
            event.accept(AsterRiskModItems.SILVER_HELMET.get());
            event.accept(AsterRiskModItems.SILVER_CHESTPLATE.get());
            event.accept(AsterRiskModItems.SILVER_LEGGINGS.get());
            event.accept(AsterRiskModItems.SILVER_BOOTS.get());
            
            // 虚空の防具セット
            event.accept(AsterRiskModItems.VOID_HELMET.get());
            event.accept(AsterRiskModItems.VOID_CHESTPLATE.get());
            event.accept(AsterRiskModItems.VOID_LEGGINGS.get());
            event.accept(AsterRiskModItems.VOID_BOOTS.get());
        }
    }
}
