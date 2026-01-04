package net.mcreator.asterrisk.block;

import net.mcreator.asterrisk.block.entity.AlchemicalCauldronBlockEntity;
import net.mcreator.asterrisk.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 錬金釜 - 月水+星屑+素材で錬金術を行う
 */
public class AlchemicalCauldronBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    
    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 12, 14);

    public AlchemicalCauldronBlock() {
        super(BlockBehaviour.Properties.of()
            .strength(3.0f, 6.0f)
            .lightLevel(state -> state.getValue(ACTIVE) ? 8 : 0)
            .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ACTIVE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AlchemicalCauldronBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return createTickerHelper(type, ModBlockEntities.ALCHEMICAL_CAULDRON.get(), AlchemicalCauldronBlockEntity::serverTick);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof AlchemicalCauldronBlockEntity cauldron) {
            ItemStack heldItem = player.getItemInHand(hand);
            
            if (heldItem.isEmpty()) {
                // 空の手で右クリック - アイテム取り出し
                ItemStack removed = cauldron.removeItem();
                if (!removed.isEmpty()) {
                    if (!player.getInventory().add(removed)) {
                        player.drop(removed, false);
                    }
                    return InteractionResult.CONSUME;
                }
            } else {
                // アイテムを持って右クリック - アイテム投入
                if (cauldron.addItem(heldItem.copy())) {
                    if (!player.isCreative()) {
                        heldItem.shrink(1);
                    }
                    return InteractionResult.CONSUME;
                }
            }
            
            // ステータス表示
            float mana = cauldron.getMana();
            float waterLevel = cauldron.getWaterLevel();
            player.displayClientMessage(Component.literal(
                String.format("§b水: %.0f%% §d| §bマナ: %.0f/%.0f", 
                    waterLevel * 100, mana, cauldron.getMaxMana())), true);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof AlchemicalCauldronBlockEntity cauldron) {
                cauldron.dropContents();
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§b✦ Alchemical Cauldron"));
        tooltip.add(Component.literal("§7Transmute items using Moon Water"));
        tooltip.add(Component.literal("§7and Stardust catalysts"));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§7Fill with Moon Water bucket"));
        tooltip.add(Component.literal("§7Add ingredients to begin"));
    }
}
