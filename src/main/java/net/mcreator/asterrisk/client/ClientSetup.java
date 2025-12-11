package net.mcreator.asterrisk.client;

import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.client.renderer.LunarInfuserRenderer;
import net.mcreator.asterrisk.client.renderer.RitualPedestalRenderer;
import net.mcreator.asterrisk.client.renderer.StarAnvilRenderer;
import net.mcreator.asterrisk.registry.ModBlockEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * クライアント側のレンダラー登録
 */
@Mod.EventBusSubscriber(modid = AsterRiskMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // 儀式台座のレンダラー登録
        event.registerBlockEntityRenderer(ModBlockEntities.RITUAL_PEDESTAL.get(), RitualPedestalRenderer::new);
        
        // Lunar Infuserのレンダラー登録
        event.registerBlockEntityRenderer(ModBlockEntities.LUNAR_INFUSER.get(), LunarInfuserRenderer::new);
        
        // Star Anvilのレンダラー登録
        event.registerBlockEntityRenderer(ModBlockEntities.STAR_ANVIL.get(), StarAnvilRenderer::new);
    }
}
