package net.mcreator.asterrisk.fluid;

import net.minecraftforge.fluids.ForgeFlowingFluid;

import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.LiquidBlock;

import net.mcreator.asterrisk.init.AsterRiskModItems;
import net.mcreator.asterrisk.init.AsterRiskModFluids;
import net.mcreator.asterrisk.init.AsterRiskModFluidTypes;
import net.mcreator.asterrisk.init.AsterRiskModBlocks;

public abstract class MoonwaterFluid extends ForgeFlowingFluid {
	public static final ForgeFlowingFluid.Properties PROPERTIES = new ForgeFlowingFluid.Properties(() -> AsterRiskModFluidTypes.MOONWATER_TYPE.get(), () -> AsterRiskModFluids.MOONWATER.get(), () -> AsterRiskModFluids.FLOWING_MOONWATER.get())
			.explosionResistance(100f).bucket(() -> AsterRiskModItems.MOONWATER_BUCKET.get()).block(() -> (LiquidBlock) AsterRiskModBlocks.MOONWATER.get());

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