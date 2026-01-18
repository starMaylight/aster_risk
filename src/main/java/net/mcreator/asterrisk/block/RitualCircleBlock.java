package net.mcreator.asterrisk.block;

import net.mcreator.asterrisk.block.entity.RitualCircleBlockEntity;
import net.mcreator.asterrisk.block.entity.RitualPedestalBlockEntity;
import net.mcreator.asterrisk.config.AsterRiskConfig;
import net.mcreator.asterrisk.pattern.PatternManager;
import net.mcreator.asterrisk.pattern.PedestalPattern;
import net.mcreator.asterrisk.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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

/**
 * 魔法陣ブロック
 * 周囲の台座と連携して儀式クラフトを行う
 */
public class RitualCircleBlock extends BaseEntityBlock {
    
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 2, 16);
    
    public RitualCircleBlock(BlockBehaviour.Properties properties) {
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
        return new RitualCircleBlockEntity(pos, state);
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (!level.isClientSide) {
            return createTickerHelper(type, ModBlockEntities.RITUAL_CIRCLE.get(), RitualCircleBlockEntity::serverTick);
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
        if (be instanceof RitualCircleBlockEntity circle) {
            // Shift+クリック: デバッグ情報表示（Config有効時のみ）
            if (player.isShiftKeyDown() && AsterRiskConfig.isRitualCircleDebugEnabled()) {
                showDebugInfo(level, pos, circle, player);
                return InteractionResult.CONSUME;
            }
            
            if (circle.isActive()) {
                int progress = (int)((float)circle.getRitualProgress() / circle.getRitualTime() * 100);
                player.displayClientMessage(
                    Component.literal("§d[Ritual] §7Progress: §a" + progress + "% §7| Mana: §b" + (int)circle.getMana()),
                    true
                );
            } else {
                if (circle.tryStartRitual()) {
                    player.displayClientMessage(Component.literal("§d[Ritual] §aStarting ritual..."), true);
                } else {
                    if (circle.getMana() < 100) {
                        player.displayClientMessage(
                            Component.literal("§d[Ritual] §cNot enough mana! (" + (int)circle.getMana() + "/" + (int)circle.getMaxMana() + ")"),
                            true
                        );
                    } else {
                        player.displayClientMessage(
                            Component.literal("§d[Ritual] §7No valid recipe. Shift+click for debug info."),
                            true
                        );
                    }
                }
            }
        }
        
        return InteractionResult.CONSUME;
    }
    
    /**
     * デバッグ情報を表示（PatternManagerから動的に取得）
     */
    private void showDebugInfo(Level level, BlockPos circlePos, RitualCircleBlockEntity circle, Player player) {
        player.displayClientMessage(Component.literal("§e=== Ritual Circle Debug ==="), false);
        player.displayClientMessage(Component.literal("§7Mana: §b" + (int)circle.getMana() + "/" + (int)circle.getMaxMana()), false);
        
        // PatternManagerから全パターンを取得
        for (PedestalPattern pattern : PatternManager.getInstance().getAllPedestalPatterns()) {
            String patternName = pattern.getName();
            
            int foundCount = 0;
            int itemCount = 0;
            StringBuilder details = new StringBuilder();
            
            for (BlockPos relPos : pattern.getPositions()) {
                BlockPos pedestalPos = circlePos.offset(relPos);
                BlockEntity be = level.getBlockEntity(pedestalPos);
                
                if (be instanceof RitualPedestalBlockEntity pedestal) {
                    foundCount++;
                    if (pedestal.hasItem()) {
                        itemCount++;
                        String itemName = pedestal.getItem().getHoverName().getString();
                        details.append(itemName.substring(0, Math.min(8, itemName.length()))).append(",");
                    }
                }
            }
            
            int totalPositions = pattern.getPositions().size();
            String status = foundCount == totalPositions ? "§a✓" : "§c✗";
            player.displayClientMessage(
                Component.literal("§7" + patternName + ": " + status + " §7(" + foundCount + "/" + totalPositions + " pedestals, " + itemCount + " items)"),
                false
            );
            
            if (details.length() > 0) {
                player.displayClientMessage(Component.literal("  §8Items: " + details), false);
            }
        }
    }
}
