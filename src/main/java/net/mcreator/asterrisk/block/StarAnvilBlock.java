package net.mcreator.asterrisk.block;

import net.mcreator.asterrisk.block.entity.StarAnvilBlockEntity;
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
        tooltip.add(Component.translatable("tooltip.aster_risk.star_anvil.line1").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.aster_risk.star_anvil.line2").withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("Right-click: Place/Take item").withStyle(ChatFormatting.YELLOW));
        tooltip.add(Component.literal("Shift+Click (empty): Repair (200 mana)").withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.literal("Shift+Click again: Enhance (500 mana)").withStyle(ChatFormatting.LIGHT_PURPLE));
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
                player.displayClientMessage(Component.literal("§7No item on the anvil."), true);
                return InteractionResult.CONSUME;
            }
            
            // まず修理を試みる（耐久値が減っている場合）
            if (anvilItem.isDamaged()) {
                if (anvil.getMana() < StarAnvilBlockEntity.REPAIR_MANA_COST) {
                    player.displayClientMessage(Component.literal("§cNot enough mana for repair! Need: 200"), true);
                    return InteractionResult.CONSUME;
                }
                
                if (anvil.repairItem()) {
                    player.displayClientMessage(Component.literal("§a✦ Item repaired!"), true);
                    return InteractionResult.CONSUME;
                }
            }
            
            // 修理不要または修理済みなら強化
            if (anvil.getMana() < StarAnvilBlockEntity.ENHANCE_MANA_COST) {
                player.displayClientMessage(Component.literal("§cNot enough mana for enhance! Need: 500"), true);
                return InteractionResult.CONSUME;
            }
            
            if (anvil.enhanceItem()) {
                player.displayClientMessage(Component.literal("§d✦ Item enhanced!"), true);
            } else {
                player.displayClientMessage(Component.literal("§cCannot enhance this item further!"), true);
            }
            
            return InteractionResult.CONSUME;
            
        } else if (!heldItem.isEmpty() && anvilItem.isEmpty()) {
            // アイテムを置く
            ItemStack toPlace = heldItem.copy();
            toPlace.setCount(1);
            anvil.setItem(toPlace);
            heldItem.shrink(1);
            player.displayClientMessage(Component.literal("§7Placed " + toPlace.getHoverName().getString()), true);
            return InteractionResult.CONSUME;
            
        } else if (heldItem.isEmpty() && !anvilItem.isEmpty() && !player.isShiftKeyDown()) {
            // 素手で右クリック: アイテムを取り出す
            ItemStack removed = anvil.removeItem();
            player.setItemInHand(hand, removed);
            player.displayClientMessage(Component.literal("§7Took " + removed.getHoverName().getString()), true);
            return InteractionResult.CONSUME;
            
        } else if (heldItem.isEmpty() && anvilItem.isEmpty()) {
            // 空の状態でステータス表示
            player.displayClientMessage(
                Component.literal("§5Star Anvil §7| Mana: §b" + (int)anvil.getMana() + "/" + (int)anvil.getMaxMana() +
                    " §7| Place an item to repair/enhance"),
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
