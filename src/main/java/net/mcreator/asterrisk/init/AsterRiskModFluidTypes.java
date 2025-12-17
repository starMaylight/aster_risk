/*
 * MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.asterrisk.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fluids.FluidType;

import net.mcreator.asterrisk.fluid.types.MoonwaterFluidType;
import net.mcreator.asterrisk.AsterRiskMod;

public class AsterRiskModFluidTypes {
	public static final DeferredRegister<FluidType> REGISTRY = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, AsterRiskMod.MODID);
	public static final RegistryObject<FluidType> MOONWATER_TYPE = REGISTRY.register("moonwater", () -> new MoonwaterFluidType());
}