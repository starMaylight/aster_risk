package net.mcreator.asterrisk.block;

import net.mcreator.asterrisk.block.entity.LunarInfuserBlockEntity;
import net.mcreator.asterrisk.registry.ModBlockEntities;
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
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 月光注入機
 * アイテムをマナで変換する
 */
public class LunarInfuserBlock extends BaseEntityBlock {

    // カスタム形状：台座風
    private static final VoxelShape SHAPE = Shapes.or(
        // ベース
        Block.box(1, 0, 1, 15, 4, 15),
        // 中央の台
        Block.box(3, 4, 3, 13, 8, 13),
        // 上部の皿
        Block.box(2, 8, 2, 14, 10, 14)
    );

    public LunarInfuserBlock() {
        super(BlockBehaviour.Properties.of()
            .strength(3.0f, 6.0f)
            .requiresCorrectToolForDrops()
            .lightLevel(state -> 7)
            .noOcclusion());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.aster_risk.lunar_infuser.line1").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.aster_risk.lunar_infuser.line2").withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("Right-click: Place item").withStyle(ChatFormatting.YELLOW));
        tooltip.add(Component.literal("Shift+Right-click: Check status").withStyle(ChatFormatting.YELLOW));
        tooltip.add(Component.literal("Transmutes items using mana").withStyle(ChatFormatting.AQUA));
        super.appendHoverText(stack, level, tooltip, flag);
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
        return LunarInfuserBlockEntity.create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return createTickerHelper(type, ModBlockEntities.LUNAR_INFUSER.get(),
            LunarInfuserBlockEntity::serverTick);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                  InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof LunarInfuserBlockEntity infuser)) {
            return InteractionResult.PASS;
        }

        ItemStack heldItem = player.getItemInHand(hand);
        ItemStack infuserItem = infuser.getItem();

        if (player.isShiftKeyDown()) {
            // Shift+右クリック: ステータス表示
            String status = infuser.isInfusing() ? "§eInfusing..." : "§7Idle";
            int progress = (int)((float)infuser.getInfusionProgress() / infuser.getCurrentProcessTime() * 100);
            player.displayClientMessage(
                Component.literal("Lunar Infuser - " + status +
                    " §7| Mana: §b" + (int)infuser.getMana() + "/" + (int)infuser.getMaxMana() +
                    (infuser.isInfusing() ? " §7| Progress: §a" + progress + "%" : "")),
                true
            );
        } else if (!heldItem.isEmpty() && infuserItem.isEmpty()) {
            // アイテムを置く
            ItemStack toPlace = heldItem.copy();
            toPlace.setCount(1);
            infuser.setItem(toPlace);
            heldItem.shrink(1);
            player.displayClientMessage(Component.literal("§7Placed " + toPlace.getHoverName().getString()), true);
        } else if (!infuserItem.isEmpty() && heldItem.isEmpty()) {
            // アイテムを取り出す
            ItemStack removed = infuser.removeItem();
            player.setItemInHand(hand, removed);
            player.displayClientMessage(Component.literal("§7Removed " + removed.getHoverName().getString()), true);
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof LunarInfuserBlockEntity infuser) {
                infuser.dropContents();
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
