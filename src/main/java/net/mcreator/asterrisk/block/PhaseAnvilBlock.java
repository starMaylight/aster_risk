package net.mcreator.asterrisk.block;

import net.mcreator.asterrisk.block.entity.PhaseAnvilBlockEntity;
import net.mcreator.asterrisk.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 月相の金床 - 月相の刻印を装備に適用する
 */
public class PhaseAnvilBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    
    // X軸方向に長い形状（北/南向き）
    private static final VoxelShape SHAPE_NS = Shapes.or(
        Block.box(2, 0, 2, 14, 4, 14),   // ベース
        Block.box(3, 4, 4, 13, 5, 12),   // 中間
        Block.box(0, 5, 3, 16, 13, 13)   // 上部
    );
    
    // Z軸方向に長い形状（東/西向き）
    private static final VoxelShape SHAPE_EW = Shapes.or(
        Block.box(2, 0, 2, 14, 4, 14),   // ベース
        Block.box(4, 4, 3, 12, 5, 13),   // 中間
        Block.box(3, 5, 0, 13, 13, 16)   // 上部
    );

    public PhaseAnvilBlock() {
        super(BlockBehaviour.Properties.of()
            .strength(5.0f, 1200.0f)
            .requiresCorrectToolForDrops()
            .lightLevel(state -> 6)
            .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        return (facing == Direction.NORTH || facing == Direction.SOUTH) ? SHAPE_NS : SHAPE_EW;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PhaseAnvilBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return createTickerHelper(type, ModBlockEntities.PHASE_ANVIL.get(), PhaseAnvilBlockEntity::serverTick);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof PhaseAnvilBlockEntity anvil) {
            ItemStack heldItem = player.getItemInHand(hand);
            
            if (player.isShiftKeyDown() && heldItem.isEmpty()) {
                // Shift+空手で刻印スロットから取り出し
                ItemStack sigil = anvil.removeSigil();
                if (!sigil.isEmpty()) {
                    if (!player.getInventory().add(sigil)) {
                        player.drop(sigil, false);
                    }
                    return InteractionResult.CONSUME;
                }
            } else if (heldItem.isEmpty()) {
                // 空の手で右クリック - 装備取り出し
                ItemStack equipment = anvil.removeEquipment();
                if (!equipment.isEmpty()) {
                    if (!player.getInventory().add(equipment)) {
                        player.drop(equipment, false);
                    }
                    return InteractionResult.CONSUME;
                }
            } else {
                // アイテムを持って右クリック
                if (anvil.addItem(heldItem)) {
                    if (!player.isCreative()) {
                        heldItem.shrink(1);
                    }
                    return InteractionResult.CONSUME;
                }
            }
            
            // 現在の月相と進行状況を表示
            int moonPhase = level.getMoonPhase();
            String phaseName = getMoonPhaseName(moonPhase);
            float mana = anvil.getMana();
            
            player.displayClientMessage(Component.literal(
                String.format("§b月相: %s §d| §bマナ: %.0f/%.0f", phaseName, mana, anvil.getMaxMana())), true);
        }

        return InteractionResult.SUCCESS;
    }

    private String getMoonPhaseName(int phase) {
        return switch (phase) {
            case 0 -> "満月";
            case 1 -> "更待月";
            case 2 -> "下弦";
            case 3 -> "有明月";
            case 4 -> "新月";
            case 5 -> "三日月";
            case 6 -> "上弦";
            case 7 -> "十三夜";
            default -> "不明";
        };
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof PhaseAnvilBlockEntity anvil) {
                anvil.dropContents();
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§d☽ Phase Anvil"));
        tooltip.add(Component.literal("§7Apply Moon Phase Sigils to equipment"));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§7Place equipment and sigil"));
        tooltip.add(Component.literal("§7Supply mana to imbue"));
    }
}
