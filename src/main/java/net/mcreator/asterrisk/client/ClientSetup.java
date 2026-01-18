package net.mcreator.asterrisk.client;

import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.models.MoonRabbitModel;
import net.mcreator.asterrisk.models.StarSpiritModel;
import net.mcreator.asterrisk.models.MoonlightFairyModel;
import net.mcreator.asterrisk.models.EclipsePhantomModel;
import net.mcreator.asterrisk.models.VoidWalkerModel;
import net.mcreator.asterrisk.models.CorruptedGolemModel;
import net.mcreator.asterrisk.models.EclipseMonarchModel;
import net.mcreator.asterrisk.models.StarDevourerModel;
import net.mcreator.asterrisk.client.renderer.LunarInfuserRenderer;
import net.mcreator.asterrisk.client.renderer.RitualPedestalRenderer;
import net.mcreator.asterrisk.client.renderer.StarAnvilRenderer;
import net.mcreator.asterrisk.client.renderer.AlchemicalCauldronRenderer;
import net.mcreator.asterrisk.client.renderer.PhaseAnvilRenderer;
import net.mcreator.asterrisk.client.renderer.MeteorSummoningRenderer;
import net.mcreator.asterrisk.client.renderer.FocusChamberCoreRenderer;
import net.mcreator.asterrisk.client.renderer.CelestialEnchantingTableRenderer;
import net.mcreator.asterrisk.client.renderer.MoonRabbitRenderer;
import net.mcreator.asterrisk.client.renderer.StarSpiritRenderer;
import net.mcreator.asterrisk.client.renderer.MoonlightFairyRenderer;
import net.mcreator.asterrisk.client.renderer.EclipsePhantomRenderer;
import net.mcreator.asterrisk.client.renderer.VoidWalkerRenderer;
import net.mcreator.asterrisk.client.renderer.CorruptedGolemRenderer;
import net.mcreator.asterrisk.client.renderer.EclipseMonarchRenderer;
import net.mcreator.asterrisk.client.renderer.StarDevourerRenderer;
import net.mcreator.asterrisk.client.renderer.HeavenlyAnvilRenderer;
import net.mcreator.asterrisk.entity.ModEntityTypes;
import net.mcreator.asterrisk.registry.ModBlockEntities;
import net.mcreator.asterrisk.registry.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AsterRiskMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // BlockEntityレンダラー
        event.registerBlockEntityRenderer(ModBlockEntities.RITUAL_PEDESTAL.get(), RitualPedestalRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.LUNAR_INFUSER.get(), LunarInfuserRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.STAR_ANVIL.get(), StarAnvilRenderer::new);
        
        // 新システムのBlockEntityレンダラー
        event.registerBlockEntityRenderer(ModBlockEntities.ALCHEMICAL_CAULDRON.get(), AlchemicalCauldronRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.PHASE_ANVIL.get(), PhaseAnvilRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.METEOR_SUMMONING.get(), MeteorSummoningRenderer::new);
        
        // 新システム: Focus Chamber と Celestial Enchanting Table
        event.registerBlockEntityRenderer(ModBlockEntities.FOCUS_CHAMBER_CORE.get(), FocusChamberCoreRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.CELESTIAL_ENCHANTING_TABLE.get(), CelestialEnchantingTableRenderer::new);
        
        // 友好Mob
        event.registerEntityRenderer(ModEntities.MOON_RABBIT.get(), MoonRabbitRenderer::new);
        event.registerEntityRenderer(ModEntities.STAR_SPIRIT.get(), StarSpiritRenderer::new);
        event.registerEntityRenderer(ModEntities.MOONLIGHT_FAIRY.get(), MoonlightFairyRenderer::new);
        
        // 敵対Mob
        event.registerEntityRenderer(ModEntities.ECLIPSE_PHANTOM.get(), EclipsePhantomRenderer::new);
        event.registerEntityRenderer(ModEntities.VOID_WALKER.get(), VoidWalkerRenderer::new);
        event.registerEntityRenderer(ModEntities.CORRUPTED_GOLEM.get(), CorruptedGolemRenderer::new);
        
        // ボス
        event.registerEntityRenderer(ModEntities.ECLIPSE_MONARCH.get(), EclipseMonarchRenderer::new);
        event.registerEntityRenderer(ModEntities.STAR_DEVOURER.get(), StarDevourerRenderer::new);
        
        // その他エンティティ
        event.registerEntityRenderer(ModEntityTypes.HEAVENLY_ANVIL.get(), HeavenlyAnvilRenderer::new);
    }
    
    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        // 友好Mob
        event.registerLayerDefinition(MoonRabbitModel.LAYER_LOCATION, MoonRabbitModel::createBodyLayer);
        event.registerLayerDefinition(StarSpiritModel.LAYER_LOCATION, StarSpiritModel::createBodyLayer);
        event.registerLayerDefinition(MoonlightFairyModel.LAYER_LOCATION, MoonlightFairyModel::createBodyLayer);
        
        // 敵対Mob
        event.registerLayerDefinition(EclipsePhantomModel.LAYER_LOCATION, EclipsePhantomModel::createBodyLayer);
        event.registerLayerDefinition(VoidWalkerModel.LAYER_LOCATION, VoidWalkerModel::createBodyLayer);
        event.registerLayerDefinition(CorruptedGolemModel.LAYER_LOCATION, CorruptedGolemModel::createBodyLayer);
        
        // ボス
        event.registerLayerDefinition(EclipseMonarchModel.LAYER_LOCATION, EclipseMonarchModel::createBodyLayer);
        event.registerLayerDefinition(StarDevourerModel.LAYER_LOCATION, StarDevourerModel::createBodyLayer);
    }
}
