package net.mcreator.asterrisk.block;

import org.checkerframework.checker.units.qual.s;

import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;

public class MoonlightLanternBlock extends Block {
	public MoonlightLanternBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.LANTERN).strength(1f, 10f).lightLevel(s -> 15).noOcclusion().isRedstoneConductor((bs, br, bp) -> false));
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return true;
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 0;
	}

	@Override
	public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return Shapes.or(box(7, 15, 7, 9, 16, 9), box(7, 12, 7, 9, 15, 9), box(4, 11, 4, 12, 12, 12), box(4, 3, 4, 5, 11, 5), box(11, 3, 4, 12, 11, 5), box(4, 3, 11, 5, 11, 12), box(11, 3, 11, 12, 11, 12), box(4, 2, 4, 12, 3, 12),
				box(5, 0, 5, 11, 2, 11), box(5, 3, 5, 11, 11, 11));
	}
}