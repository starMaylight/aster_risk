package net.mcreator.asterrisk.block;

import net.mcreator.asterrisk.init.AsterRiskModBlocks;
import net.mcreator.asterrisk.world.LunarRealmTeleporter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

/**
 * 月の領域ポータルブロック
 * AXIS=X: ポータルはX軸方向に薄い（東西を向いて通る）
 * AXIS=Z: ポータルはZ軸方向に薄い（南北を向いて通る）
 */
public class LunarPortalBlock extends Block {
    
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    
    // AXIS=X: 東西に広がる壁（南北方向に通る）- Z方向に薄い
    protected static final VoxelShape X_AXIS_AABB = Block.box(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
    // AXIS=Z: 南北に広がる壁（東西方向に通る）- X方向に薄い
    protected static final VoxelShape Z_AXIS_AABB = Block.box(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);
    
    public static final ResourceKey<Level> LUNAR_REALM = ResourceKey.create(
        net.minecraft.core.registries.Registries.DIMENSION,
        ResourceLocation.fromNamespaceAndPath("aster_risk", "lunar_realm")
    );
    
    public LunarPortalBlock() {
        super(BlockBehaviour.Properties.of()
            .noCollission()
            .strength(-1.0F)
            .noLootTable()
            .lightLevel(state -> 12)
            .pushReaction(PushReaction.BLOCK));
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.X));
    }
    
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(AXIS) == Direction.Axis.Z ? Z_AXIS_AABB : X_AXIS_AABB;
    }
    
    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        Direction.Axis axis = facing.getAxis();
        Direction.Axis portalAxis = state.getValue(AXIS);
        boolean flag = portalAxis != axis && axis.isHorizontal();
        
        if (!flag && !facingState.is(this) && !facingState.is(getFrameBlock())) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }
    
    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!entity.isPassenger() && !entity.isVehicle() && entity.canChangeDimensions()) {
            if (entity.isOnPortalCooldown()) {
                entity.setPortalCooldown();
            } else {
                if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
                    ServerLevel destination;
                    if (level.dimension() == LUNAR_REALM) {
                        destination = serverLevel.getServer().getLevel(Level.OVERWORLD);
                    } else {
                        destination = serverLevel.getServer().getLevel(LUNAR_REALM);
                    }
                    
                    if (destination != null) {
                        entity.setPortalCooldown();
                        entity.changeDimension(destination, new LunarRealmTeleporter(destination));
                    }
                }
            }
        }
    }
    
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        for (int i = 0; i < 4; ++i) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + random.nextDouble();
            double z = pos.getZ() + random.nextDouble();
            double dx = (random.nextDouble() - 0.5) * 0.5;
            double dy = (random.nextDouble() - 0.5) * 0.5;
            double dz = (random.nextDouble() - 0.5) * 0.5;
            
            level.addParticle(ParticleTypes.END_ROD, x, y, z, dx, dy, dz);
        }
        
        if (random.nextInt(100) == 0) {
            level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 
                SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS, 0.5F, 
                random.nextFloat() * 0.4F + 0.8F, false);
        }
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }
    
    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return switch (rot) {
            case COUNTERCLOCKWISE_90, CLOCKWISE_90 -> switch (state.getValue(AXIS)) {
                case Z -> state.setValue(AXIS, Direction.Axis.X);
                case X -> state.setValue(AXIS, Direction.Axis.Z);
                default -> state;
            };
            default -> state;
        };
    }
    
    public static Block getFrameBlock() {
        return AsterRiskModBlocks.MOONSTONE_BRICKS.get();
    }
    
    /**
     * シンプルなポータルサイズ検出クラス
     * portalAxis: ポータルが広がる方向（フレームの壁が伸びる方向）
     * - X軸: フレームはEAST-WEST方向に広がる → ポータルもX軸
     * - Z軸: フレームはNORTH-SOUTH方向に広がる → ポータルもZ軸
     */
    public static class Size {
        private final LevelAccessor level;
        private final Direction.Axis portalAxis;
        @Nullable
        private BlockPos bottomLeft;
        private int width;
        private int height;
        
        public Size(LevelAccessor level, BlockPos startPos, Direction.Axis portalAxis) {
            this.level = level;
            this.portalAxis = portalAxis;
            this.width = 0;
            this.height = 0;
            this.bottomLeft = null;
            
            BlockPos corner = findBottomLeft(startPos);
            if (corner != null) {
                this.bottomLeft = corner;
                this.width = measureWidth(corner);
                if (this.width >= 2) {
                    this.height = measureHeight(corner, this.width);
                }
            }
        }
        
        // ポータルが広がる方向（右方向）
        private Direction getRightDir() {
            return (portalAxis == Direction.Axis.X) ? Direction.EAST : Direction.SOUTH;
        }
        
        // ポータルが広がる方向の反対（左方向）
        private Direction getLeftDir() {
            return getRightDir().getOpposite();
        }
        
        private BlockPos findBottomLeft(BlockPos pos) {
            // 下に移動
            while (isEmpty(level.getBlockState(pos.below()))) {
                pos = pos.below();
                if (pos.getY() < level.getMinBuildHeight()) return null;
            }
            if (!isFrame(level.getBlockState(pos.below()))) {
                return null;
            }
            
            // 左に移動
            Direction leftDir = getLeftDir();
            while (isEmpty(level.getBlockState(pos.relative(leftDir)))) {
                pos = pos.relative(leftDir);
            }
            if (!isFrame(level.getBlockState(pos.relative(leftDir)))) {
                return null;
            }
            
            return pos;
        }
        
        private int measureWidth(BlockPos bottomLeft) {
            Direction rightDir = getRightDir();
            int w = 0;
            BlockPos pos = bottomLeft;
            
            while (w < 21) {
                BlockState state = level.getBlockState(pos);
                if (!isEmpty(state)) {
                    break;
                }
                if (!isFrame(level.getBlockState(pos.below()))) {
                    return 0;
                }
                w++;
                pos = pos.relative(rightDir);
            }
            
            if (!isFrame(level.getBlockState(pos))) {
                return 0;
            }
            
            return w;
        }
        
        private int measureHeight(BlockPos bottomLeft, int width) {
            Direction rightDir = getRightDir();
            Direction leftDir = getLeftDir();
            int h = 0;
            
            while (h < 21) {
                boolean rowValid = true;
                for (int x = 0; x < width; x++) {
                    BlockPos checkPos = bottomLeft.above(h).relative(rightDir, x);
                    if (!isEmpty(level.getBlockState(checkPos))) {
                        rowValid = false;
                        break;
                    }
                }
                
                if (!rowValid) {
                    break;
                }
                
                BlockPos leftWall = bottomLeft.above(h).relative(leftDir);
                BlockPos rightWall = bottomLeft.above(h).relative(rightDir, width);
                if (!isFrame(level.getBlockState(leftWall)) || !isFrame(level.getBlockState(rightWall))) {
                    return 0;
                }
                
                h++;
            }
            
            for (int x = 0; x < width; x++) {
                BlockPos topPos = bottomLeft.above(h).relative(rightDir, x);
                if (!isFrame(level.getBlockState(topPos))) {
                    return 0;
                }
            }
            
            return h;
        }
        
        private static boolean isEmpty(BlockState state) {
            return state.isAir() || state.is(AsterRiskModBlocks.LUNAR_PORTAL.get());
        }
        
        private static boolean isFrame(BlockState state) {
            return state.is(getFrameBlock());
        }
        
        public boolean isValid() {
            return this.bottomLeft != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
        }
        
        public boolean isComplete() {
            if (!isValid()) return false;
            Direction rightDir = getRightDir();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    BlockPos pos = bottomLeft.above(y).relative(rightDir, x);
                    if (!level.getBlockState(pos).is(AsterRiskModBlocks.LUNAR_PORTAL.get())) {
                        return false;
                    }
                }
            }
            return true;
        }
        
        public void createPortalBlocks() {
            if (!isValid()) return;
            Direction rightDir = getRightDir();
            BlockState portalState = AsterRiskModBlocks.LUNAR_PORTAL.get().defaultBlockState().setValue(AXIS, this.portalAxis);
            
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    BlockPos pos = bottomLeft.above(y).relative(rightDir, x);
                    level.setBlock(pos, portalState, 18);
                }
            }
        }
        
        public int getWidth() { return width; }
        public int getHeight() { return height; }
        public BlockPos getBottomLeft() { return bottomLeft; }
    }
}
