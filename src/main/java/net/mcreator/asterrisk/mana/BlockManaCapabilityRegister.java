package net.mcreator.asterrisk.mana;

import net.mcreator.asterrisk.AsterRiskMod;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * ブロックマナCapabilityの登録
 */
@Mod.EventBusSubscriber(modid = AsterRiskMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlockManaCapabilityRegister {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(BlockManaCapability.IBlockMana.class);
    }
}
