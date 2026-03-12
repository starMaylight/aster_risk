package net.mcreator.asterrisk.fluid;

import net.minecraftforge.fluids.ForgeFlowingFluid;

import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.LiquidBlock;

import net.mcreator.asterrisk.registry.ModItems;
import net.mcreator.asterrisk.registry.ModFluids;
import net.mcreator.asterrisk.registry.ModBlocks;

public abstract class MoonwaterFluid extends ForgeFlowingFluid {
	public static final ForgeFlowingFluid.Properties PROPERTIES = new ForgeFlowingFluid.Properties(() -> ModFluids.MOONWATER_TYPE.get(), () -> ModFluids.MOONWATER.get(), () -> ModFluids.FLOWING_MOONWATER.get())
			.explosionResistance(100f).bucket(() -> ModItems.MOONWATER_BUCKET.get()).block(() -> (LiquidBlock) ModBlocks.MOONWATER.get());

	private MoonwaterFluid() {
		super(PROPERTIES);
	}

	public static class Source extends MoonwaterFluid {
		public int getAmount(FluidState state) {
			return 8;
		}

		public boolean isSource(FluidState state) {
			return true;
		}
	}

	public static class Flowing extends MoonwaterFluid {
		protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
			super.createFluidStateDefinition(builder);
			builder.add(LEVEL);
		}

		public int getAmount(FluidState state) {
			return state.getValue(LEVEL);
		}

		public boolean isSource(FluidState state) {
			return false;
		}
	}
}
