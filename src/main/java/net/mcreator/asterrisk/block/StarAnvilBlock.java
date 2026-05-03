package net.mcreator.asterrisk.block;

import net.mcreator.asterrisk.block.entity.StarAnvilBlockEntity;
import net.mcreator.asterrisk.util.TooltipHelper;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 星の金床
 * 
 * 操作方法:
 * - 右クリック（アイテム持ち）: アイテムを置く
 * - 右クリック（素手）: アイテムを取り出す
 * - Shift+右クリック（素手）: 修理（200マナ）
 * - Shift+Shift+右クリック（素手、連続）: 強化（500マナ）
 */
public class StarAnvilBlock extends BaseEntityBlock {

    private static final VoxelShape SHAPE = Shapes.or(
        Block.box(2, 0, 2, 14, 4, 14),
        Block.box(4, 4, 4, 12, 8, 12),
        Block.box(0, 8, 3, 16, 12, 13)
    );

    public StarAnvilBlock() {
        super(BlockBehaviour.Properties.of()
            .strength(5.0f, 1200.0f)
            .lightLevel(state -> 5)
            .noOcclusion());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        TooltipHelper.addBlank(tooltip);
        TooltipHelper.addHeader(tooltip, ChatFormatting.GOLD, "tooltip.aster_risk.star_anvil.header");
        TooltipHelper.addDescription(tooltip, "tooltip.aster_risk.star_anvil.line1");
        TooltipHelper.addDescription(tooltip, "tooltip.aster_risk.star_anvil.line2");
        TooltipHelper.addInfo(tooltip, ChatFormatting.YELLOW, "tooltip.aster_risk.star_anvil.use_place");
        TooltipHelper.addInfo(tooltip, ChatFormatting.GREEN, "tooltip.aster_risk.star_anvil.use_repair",
            TooltipHelper.formatNumber(StarAnvilBlockEntity.REPAIR_MANA_COST));
        TooltipHelper.addInfo(tooltip, ChatFormatting.LIGHT_PURPLE, "tooltip.aster_risk.star_anvil.use_enhance",
            TooltipHelper.formatNumber(StarAnvilBlockEntity.ENHANCE_MANA_COST));
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
        return StarAnvilBlockEntity.create(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                  InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof StarAnvilBlockEntity anvil)) {
            return InteractionResult.PASS;
        }

        ItemStack heldItem = player.getItemInHand(hand);
        ItemStack anvilItem = anvil.getItem();

        if (player.isShiftKeyDown() && heldItem.isEmpty()) {
            // Shift+素手: 修理または強化
            if (anvilItem.isEmpty()) {
                player.displayClientMessage(Component.translatable("message.aster_risk.star_anvil.no_item"), true);
                return InteractionResult.CONSUME;
            }

            // まず修理を試みる（耐久値が減っている場合）
            if (anvilItem.isDamaged()) {
                if (anvil.getMana() < StarAnvilBlockEntity.REPAIR_MANA_COST) {
                    player.displayClientMessage(Component.translatable("message.aster_risk.star_anvil.not_enough_mana_repair",
                        (int)StarAnvilBlockEntity.REPAIR_MANA_COST), true);
                    return InteractionResult.CONSUME;
                }

                if (anvil.repairItem()) {
                    player.displayClientMessage(Component.translatable("message.aster_risk.star_anvil.repaired"), true);
                    return InteractionResult.CONSUME;
                }
            }

            // 修理不要または修理済みなら強化
            if (anvil.getMana() < StarAnvilBlockEntity.ENHANCE_MANA_COST) {
                player.displayClientMessage(Component.translatable("message.aster_risk.star_anvil.not_enough_mana_enhance",
                    (int)StarAnvilBlockEntity.ENHANCE_MANA_COST), true);
                return InteractionResult.CONSUME;
            }

            if (anvil.enhanceItem()) {
                player.displayClientMessage(Component.translatable("message.aster_risk.star_anvil.enhanced"), true);
            } else {
                player.displayClientMessage(Component.translatable("message.aster_risk.star_anvil.cannot_enhance"), true);
            }
            
            return InteractionResult.CONSUME;
            
        } else if (!heldItem.isEmpty() && anvilItem.isEmpty()) {
            // アイテムを置く
            ItemStack toPlace = heldItem.copy();
            toPlace.setCount(1);
            anvil.setItem(toPlace);
            heldItem.shrink(1);
            player.displayClientMessage(Component.translatable("message.aster_risk.star_anvil.placed", toPlace.getHoverName()), true);
            return InteractionResult.CONSUME;

        } else if (heldItem.isEmpty() && !anvilItem.isEmpty() && !player.isShiftKeyDown()) {
            // 素手で右クリック: アイテムを取り出す
            ItemStack removed = anvil.removeItem();
            player.setItemInHand(hand, removed);
            player.displayClientMessage(Component.translatable("message.aster_risk.star_anvil.took", removed.getHoverName()), true);
            return InteractionResult.CONSUME;

        } else if (heldItem.isEmpty() && anvilItem.isEmpty()) {
            // 空の状態でステータス表示
            player.displayClientMessage(
                Component.translatable("message.aster_risk.star_anvil.status",
                    (int)anvil.getMana(), (int)anvil.getMaxMana()),
                true
            );
            return InteractionResult.CONSUME;
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof StarAnvilBlockEntity anvil) {
                anvil.dropContents();
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
