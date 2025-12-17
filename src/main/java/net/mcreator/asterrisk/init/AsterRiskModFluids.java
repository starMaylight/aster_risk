/*
 * MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.asterrisk.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;

import net.mcreator.asterrisk.fluid.MoonwaterFluid;
import net.mcreator.asterrisk.AsterRiskMod;

public class AsterRiskModFluids {
	public static final DeferredRegister<Fluid> REGISTRY = DeferredRegister.create(ForgeRegistries.FLUIDS, AsterRiskMod.MODID);
	public static final RegistryObject<FlowingFluid> MOONWATER = REGISTRY.register("moonwater", () -> new MoonwaterFluid.Source());
	public static final RegistryObject<FlowingFluid> FLOWING_MOONWATER = REGISTRY.register("flowing_moonwater", () -> new MoonwaterFluid.Flowing());

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class FluidsClientSideHandler {
		@SubscribeEvent
		public static void clientSetup(FMLClientSetupEvent event) {
			ItemBlockRenderTypes.setRenderLayer(MOONWATER.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(FLOWING_MOONWATER.get(), RenderType.translucent());
		}
	}
}