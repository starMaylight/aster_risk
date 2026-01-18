package net.mcreator.asterrisk.pattern;

import net.mcreator.asterrisk.AsterRiskMod;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * PatternManagerをリソースリロードリスナーとして登録
 */
@Mod.EventBusSubscriber(modid = AsterRiskMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PatternEvents {
    
    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(PatternManager.getInstance());
        AsterRiskMod.LOGGER.info("Registered PatternManager as reload listener");
    }
}
