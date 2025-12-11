package net.mcreator.asterrisk.block;

import net.mcreator.asterrisk.block.entity.RitualPedestalBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 儀式台座ブロック
 * アイテムを配置して儀式に使用
 */
public class RitualPedestalBlock extends BaseEntityBlock {

    // 台座の形状（下部の台 + 上部の皿）
    private static final VoxelShape BASE = Block.box(3, 0, 3, 13, 2, 13);
    private static final VoxelShape PILLAR = Block.box(5, 2, 5, 11, 8, 11);
    private static final VoxelShape TOP = Block.box(2, 8, 2, 14, 10, 14);
    private static final VoxelShape SHAPE = Shapes.or(BASE, PILLAR, TOP);

    public RitualPedestalBlock() {
        super(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_LIGHT_BLUE)
            .strength(3.0f, 6.0f)
            .requiresCorrectToolForDrops()
            .lightLevel(state -> 5)
            .noOcclusion());
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
        return RitualPedestalBlockEntity.create(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof RitualPedestalBlockEntity pedestal)) {
            return InteractionResult.PASS;
        }

        ItemStack heldItem = player.getItemInHand(hand);

        if (pedestal.hasItem()) {
            // 台座にアイテムがある場合：取り出す
            ItemStack pedestalItem = pedestal.removeItem();
            if (!player.getInventory().add(pedestalItem)) {
                // インベントリに入らない場合はドロップ
                player.drop(pedestalItem, false);
            }
            player.displayClientMessage(
                Component.literal("Retrieved: ").withStyle(ChatFormatting.GRAY)
                    .append(pedestalItem.getHoverName()),
                true
            );
            return InteractionResult.CONSUME;
        } else if (!heldItem.isEmpty()) {
            // 台座が空でアイテムを持っている場合：配置
            ItemStack toPlace = heldItem.copy();
            toPlace.setCount(1);
            pedestal.setItem(toPlace);
            heldItem.shrink(1);
            player.displayClientMessage(
                Component.literal("Placed: ").withStyle(ChatFormatting.AQUA)
                    .append(toPlace.getHoverName()),
                true
            );
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof RitualPedestalBlockEntity pedestal) {
                pedestal.dropContents();
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§9✦ Ritual Pedestal"));
        tooltip.add(Component.literal("§7Place items for rituals"));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§7Right-click: Place/Retrieve item"));
        tooltip.add(Component.literal("§7Use with Altar Core"));
    }
}
