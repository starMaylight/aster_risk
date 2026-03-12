package net.mcreator.asterrisk.registry;

import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.fluid.MoonwaterFluid;
import net.mcreator.asterrisk.fluid.types.MoonwaterFluidType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS =
        DeferredRegister.create(ForgeRegistries.FLUIDS, AsterRiskMod.MODID);
    public static final DeferredRegister<FluidType> FLUID_TYPES =
        DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, AsterRiskMod.MODID);

    public static final RegistryObject<FlowingFluid> MOONWATER =
        FLUIDS.register("moonwater", () -> new MoonwaterFluid.Source());
    public static final RegistryObject<FlowingFluid> FLOWING_MOONWATER =
        FLUIDS.register("flowing_moonwater", () -> new MoonwaterFluid.Flowing());

    public static final RegistryObject<FluidType> MOONWATER_TYPE =
        FLUID_TYPES.register("moonwater", () -> new MoonwaterFluidType());

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class FluidsClientSideHandler {
        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event) {
            ItemBlockRenderTypes.setRenderLayer(MOONWATER.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(FLOWING_MOONWATER.get(), RenderType.translucent());
        }
    }
}
