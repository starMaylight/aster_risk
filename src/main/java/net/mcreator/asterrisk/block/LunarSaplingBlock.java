package net.mcreator.asterrisk.block;

import net.mcreator.asterrisk.init.AsterRiskModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.PlantType;

import java.util.Optional;

/**
 * 月の木の苗木
 */
public class LunarSaplingBlock extends BushBlock implements BonemealableBlock {
    
    public static final IntegerProperty STAGE = BlockStateProperties.STAGE;
    protected static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);
    
    private static final ResourceKey<ConfiguredFeature<?, ?>> LUNAR_TREE = ResourceKey.create(
        Registries.CONFIGURED_FEATURE,
        ResourceLocation.fromNamespaceAndPath("aster_risk", "lunar_tree")
    );
    
    public LunarSaplingBlock() {
        super(BlockBehaviour.Properties.of()
            .noCollission()
            .randomTicks()
            .instabreak()
            .sound(net.minecraft.world.level.block.SoundType.GRASS));
        this.registerDefaultState(this.stateDefinition.any().setValue(STAGE, 0));
    }
    
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
    
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 1)) return;
        if (level.getMaxLocalRawBrightness(pos.above()) >= 9 && random.nextInt(7) == 0) {
            this.advanceTree(level, pos, state, random);
        }
    }
    
    public void advanceTree(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        if (state.getValue(STAGE) == 0) {
            level.setBlock(pos, state.cycle(STAGE), 4);
        } else {
            this.growTree(level, pos, state, random);
        }
    }
    
    private void growTree(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        Optional<Holder.Reference<ConfiguredFeature<?, ?>>> optional = level.registryAccess()
            .registryOrThrow(Registries.CONFIGURED_FEATURE)
            .getHolder(LUNAR_TREE);
        
        if (optional.isPresent()) {
            // 苗木を空気に置き換えてから木を生成
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 4);
            if (!optional.get().value().place(level, level.getChunkSource().getGenerator(), random, pos)) {
                // 生成失敗時は苗木を戻す
                level.setBlock(pos, state, 4);
            }
        }
    }
    
    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(AsterRiskModBlocks.LUNARGRASS.get()) 
            || state.is(AsterRiskModBlocks.LUNARDIRT.get())
            || state.is(Blocks.GRASS_BLOCK)
            || state.is(Blocks.DIRT)
            || state.is(Blocks.COARSE_DIRT)
            || state.is(Blocks.PODZOL)
            || state.is(Blocks.FARMLAND);
    }
    
    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockpos = pos.below();
        return this.mayPlaceOn(level.getBlockState(blockpos), level, blockpos);
    }
    
    @Override
    public PlantType getPlantType(BlockGetter level, BlockPos pos) {
        return PlantType.PLAINS;
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STAGE);
    }
    
    // BonemealableBlock implementation
    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }
    
    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return level.random.nextFloat() < 0.45;
    }
    
    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        this.advanceTree(level, pos, state, random);
    }
}
