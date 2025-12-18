package net.mcreator.asterrisk.init;

import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.registry.ModSpawnEggs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 手動で追加するアイテム・ブロックのクリエイティブタブ登録
 * MCreatorによって上書きされないファイル
 */
@Mod.EventBusSubscriber(modid = AsterRiskMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CustomCreativeTabHandler {

    @SubscribeEvent
    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        // Aster Riskタブにカスタムアイテムを追加
        if (event.getTabKey() == AsterRiskModTabs.ASTER_RISK.getKey()) {
            // === 機能ブロック ===
            event.accept(AsterRiskModItems.LUNAR_COLLECTOR.get());
            event.accept(AsterRiskModItems.RESONATOR_TIER1.get());
            event.accept(AsterRiskModItems.RESONATOR_TIER2.get());
            event.accept(AsterRiskModItems.RESONATOR_TIER3.get());
            event.accept(AsterRiskModItems.MANA_BATTERY.get());
            
            // === 儀式システム ===
            event.accept(AsterRiskModItems.RITUAL_PEDESTAL.get());
            event.accept(AsterRiskModItems.ALTAR_CORE.get());
            
            // === Phase 6.1: 機能ブロック ===
            event.accept(AsterRiskModItems.MOONLIGHT_BEACON.get());
            event.accept(AsterRiskModItems.LUNAR_INFUSER.get());
            event.accept(AsterRiskModItems.STAR_ANVIL.get());
            
            // === 魔法道具 ===
            event.accept(AsterRiskModItems.MOONLIGHT_WAND.get());
            event.accept(AsterRiskModItems.STARGAZER_WAND.get());
            event.accept(AsterRiskModItems.LUNAR_COMPASS.get());
            event.accept(AsterRiskModItems.METEOR_WAND.get());
            event.accept(AsterRiskModItems.LUNAR_HEALING_STAFF.get());
            event.accept(AsterRiskModItems.LINKING_WAND.get());
            
            // === 武器 ===
            event.accept(AsterRiskModItems.LUNAR_BLADE.get());
            event.accept(AsterRiskModItems.METEOR_HAMMER.get());
            event.accept(AsterRiskModItems.STARDUST_DAGGER.get());
            event.accept(AsterRiskModItems.METEORITE_GREATSWORD.get());
            
            // === 月光の防具セット ===
            event.accept(AsterRiskModItems.LUNAR_HELMET.get());
            event.accept(AsterRiskModItems.LUNAR_CHESTPLATE.get());
            event.accept(AsterRiskModItems.LUNAR_LEGGINGS.get());
            event.accept(AsterRiskModItems.LUNAR_BOOTS.get());
            
            // === 星屑の防具セット ===
            event.accept(AsterRiskModItems.STELLAR_CROWN.get());
            event.accept(AsterRiskModItems.STELLAR_ROBE.get());
            event.accept(AsterRiskModItems.STELLAR_LEGGINGS.get());
            event.accept(AsterRiskModItems.STELLAR_BOOTS.get());
            
            // === 隕石の防具セット ===
            event.accept(AsterRiskModItems.METEORITE_HELMET.get());
            event.accept(AsterRiskModItems.METEORITE_CHESTPLATE.get());
            event.accept(AsterRiskModItems.METEORITE_LEGGINGS.get());
            event.accept(AsterRiskModItems.METEORITE_BOOTS.get());
            
            // === Phase 7: 消費アイテム（エフェクト付与） ===
            event.accept(AsterRiskModItems.LUNAR_ELIXIR.get());
            event.accept(AsterRiskModItems.STARDUST_CANDY.get());
            event.accept(AsterRiskModItems.CELESTIAL_CHARM.get());
            
            // === Phase 8: 友好Mobスポーンエッグ ===
            event.accept(ModSpawnEggs.MOON_RABBIT_SPAWN_EGG.get());
            event.accept(ModSpawnEggs.STAR_SPIRIT_SPAWN_EGG.get());
            event.accept(ModSpawnEggs.MOONLIGHT_FAIRY_SPAWN_EGG.get());
            
            // === Phase 8: 敵対Mobスポーンエッグ ===
            event.accept(ModSpawnEggs.ECLIPSE_PHANTOM_SPAWN_EGG.get());
            event.accept(ModSpawnEggs.VOID_WALKER_SPAWN_EGG.get());
            event.accept(ModSpawnEggs.CORRUPTED_GOLEM_SPAWN_EGG.get());
            
            // === オベリスク ===
            event.accept(AsterRiskModItems.LUNAR_OBELISK.get());
            event.accept(AsterRiskModItems.STELLAR_OBELISK.get());
            event.accept(AsterRiskModItems.SOLAR_OBELISK.get());
            event.accept(AsterRiskModItems.VOID_OBELISK.get());
            
            // === Phase 8: ボススポーンエッグ ===
            event.accept(ModSpawnEggs.ECLIPSE_MONARCH_SPAWN_EGG.get());
            event.accept(ModSpawnEggs.STAR_DEVOURER_SPAWN_EGG.get());
            
            // === Phase 10: Lunar Realm ===
            event.accept(AsterRiskModItems.LUNAR_SAPLING.get());
            
            // === Phase 11: Boss Structures ===
            event.accept(AsterRiskModItems.ECLIPSE_ALTAR.get());
            event.accept(AsterRiskModItems.STELLAR_SPIRE_CORE.get());
            
            // === Mob Drop Items ===
            event.accept(AsterRiskModItems.ECLIPSE_CORE.get());
            event.accept(AsterRiskModItems.STELLAR_HEART.get());
            event.accept(AsterRiskModItems.SHADOW_ESSENCE.get());
            event.accept(AsterRiskModItems.VOID_SHARD.get());
            event.accept(AsterRiskModItems.CORRUPTED_CORE.get());
            
            // === Endgame Equipment - Eclipse Set ===
            event.accept(AsterRiskModItems.ECLIPSE_CROWN.get());
            event.accept(AsterRiskModItems.ECLIPSE_ARMOR.get());
            event.accept(AsterRiskModItems.ECLIPSE_GREAVES.get());
            event.accept(AsterRiskModItems.ECLIPSE_BOOTS.get());
            event.accept(AsterRiskModItems.ECLIPSE_BLADE.get());
            
            // === Endgame Equipment - Stellar Set ===
            event.accept(AsterRiskModItems.STELLAR_DIADEM.get());
            event.accept(AsterRiskModItems.STELLAR_VESTMENT.get());
            event.accept(AsterRiskModItems.STELLAR_PANTS.get());
            event.accept(AsterRiskModItems.STELLAR_SABATONS.get());
            event.accept(AsterRiskModItems.STELLAR_SCEPTER.get());
        }
    }
}
