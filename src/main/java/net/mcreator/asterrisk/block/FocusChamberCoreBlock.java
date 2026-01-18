package net.mcreator.asterrisk.block;

import net.mcreator.asterrisk.block.entity.FocusChamberCoreBlockEntity;
import net.mcreator.asterrisk.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 集光チャンバーコアブロック
 * 3x3x3マルチブロック構造の中心
 */
public class FocusChamberCoreBlock extends BaseEntityBlock {
    
    private static final VoxelShape SHAPE = Block.box(1, 1, 1, 15, 15, 15);
    
    public FocusChamberCoreBlock(BlockBehaviour.Properties properties) {
        super(properties);
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
        return new FocusChamberCoreBlockEntity(pos, state);
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (!level.isClientSide) {
            return createTickerHelper(type, ModBlockEntities.FOCUS_CHAMBER_CORE.get(), FocusChamberCoreBlockEntity::serverTick);
        }
        return null;
    }
    
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                  InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof FocusChamberCoreBlockEntity chamber) {
            ItemStack heldItem = player.getItemInHand(hand);
            
            if (player.isShiftKeyDown()) {
                // Shift+右クリックでアイテム取り出し
                ItemStack removed = chamber.removeLastItem();
                if (!removed.isEmpty()) {
                    if (!player.getInventory().add(removed)) {
                        player.drop(removed, false);
                    }
                    player.displayClientMessage(
                        Component.literal("§b[Chamber] §7Removed: " + removed.getHoverName().getString()),
                        true
                    );
                }
            } else if (!heldItem.isEmpty()) {
                // アイテム投入
                ItemStack toAdd = heldItem.copy();
                toAdd.setCount(1);
                if (chamber.addItem(toAdd)) {
                    heldItem.shrink(1);
                    player.displayClientMessage(
                        Component.literal("§b[Chamber] §7Added: " + toAdd.getHoverName().getString()),
                        true
                    );
                }
            } else {
                // 情報表示
                String structureStatus = chamber.isStructureValid() ? "§aValid" : "§cInvalid";
                String processStatus = chamber.isProcessing() ? 
                    "§eProcessing " + (int)((float)chamber.getProcessProgress() / chamber.getProcessTime() * 100) + "%" : 
                    "§7Idle";
                
                List<ItemStack> items = chamber.getStoredItems();
                String itemsStr = items.isEmpty() ? "Empty" : items.size() + " items";
                
                player.displayClientMessage(
                    Component.literal("§b[Chamber] §7Structure: " + structureStatus + 
                        " §7| " + processStatus +
                        " §7| Moonlight: §e" + (int)chamber.getStoredMoonlight() +
                        " §7| Items: §f" + itemsStr),
                    true
                );
            }
        }
        
        return InteractionResult.CONSUME;
    }
    
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof FocusChamberCoreBlockEntity chamber) {
                chamber.dropContents();
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
