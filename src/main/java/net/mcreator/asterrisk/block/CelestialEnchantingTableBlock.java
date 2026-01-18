package net.mcreator.asterrisk.block;

import net.mcreator.asterrisk.block.entity.CelestialEnchantingTableBlockEntity;
import net.mcreator.asterrisk.block.entity.MoonlightFocusBlockEntity;
import net.mcreator.asterrisk.config.AsterRiskConfig;
import net.mcreator.asterrisk.init.AsterRiskModBlocks;
import net.mcreator.asterrisk.pattern.FocusPattern;
import net.mcreator.asterrisk.pattern.PatternManager;
import net.mcreator.asterrisk.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
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

/**
 * 天体エンチャント台ブロック
 */
public class CelestialEnchantingTableBlock extends BaseEntityBlock {
    
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 12, 16);
    
    public CelestialEnchantingTableBlock(BlockBehaviour.Properties properties) {
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
        return new CelestialEnchantingTableBlockEntity(pos, state);
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (!level.isClientSide) {
            return createTickerHelper(type, ModBlockEntities.CELESTIAL_ENCHANTING_TABLE.get(), 
                CelestialEnchantingTableBlockEntity::serverTick);
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
        if (be instanceof CelestialEnchantingTableBlockEntity table) {
            ItemStack heldItem = player.getItemInHand(hand);
            ItemStack tableItem = table.getItem();
            
            if (player.isShiftKeyDown() && heldItem.isEmpty() && tableItem.isEmpty() && AsterRiskConfig.isCelestialEnchantDebugEnabled()) {
                showDebugInfo(level, pos, table, player);
                return InteractionResult.CONSUME;
            }
            
            if (player.isShiftKeyDown() && !tableItem.isEmpty() && !table.isEnchanting()) {
                table.tryStartEnchanting(player);
            } else if (!heldItem.isEmpty() && tableItem.isEmpty()) {
                ItemStack toPlace = heldItem.copy();
                toPlace.setCount(1);
                table.setItem(toPlace);
                heldItem.shrink(1);
                player.displayClientMessage(Component.literal("§d[Celestial] §7Placed: " + toPlace.getHoverName().getString()), true);
            } else if (!tableItem.isEmpty() && !table.isEnchanting()) {
                if (player.getInventory().add(tableItem)) {
                    table.removeItem();
                } else {
                    player.drop(table.removeItem(), false);
                }
                player.displayClientMessage(Component.literal("§d[Celestial] §7Removed item."), true);
            } else {
                String status;
                if (table.isEnchanting()) {
                    int percent = (int)((float)table.getEnchantProgress() / table.getEnchantTime() * 100);
                    status = "§eEnchanting " + percent + "%";
                } else if (!table.isStructureValid()) {
                    status = "§cPillars incomplete";
                } else {
                    status = "§aReady";
                }
                
                String pattern = table.getDetectedPattern();
                String patternStr = pattern != null ? "§e" + pattern : "§cNone";
                
                player.displayClientMessage(Component.literal("§d[Celestial] §7" + status + 
                    " §7| Pattern: " + patternStr + " §7| Mana: §b" + (int)table.getStoredMana()), true);
            }
        }
        
        return InteractionResult.CONSUME;
    }
    
    private void showDebugInfo(Level level, BlockPos tablePos, CelestialEnchantingTableBlockEntity table, Player player) {
        player.displayClientMessage(Component.literal("§e=== Celestial Enchant Debug ==="), false);
        player.displayClientMessage(Component.literal("§7Mana: §b" + (int)table.getStoredMana() + "/" + (int)table.getMaxMana()), false);
        
        Block lunarPillar = AsterRiskModBlocks.LUNAR_PILLAR.get();
        Block celestialTile = AsterRiskModBlocks.CELESTIAL_TILE.get();
        
        player.displayClientMessage(Component.literal("§7--- Pillar Structure ---"), false);
        for (int i = 0; i < CelestialEnchantingTableBlockEntity.PILLAR_BASES.length; i++) {
            BlockPos base = tablePos.offset(CelestialEnchantingTableBlockEntity.PILLAR_BASES[i]);
            
            boolean p1ok = level.getBlockState(base).is(lunarPillar);
            boolean p2ok = level.getBlockState(base.above(1)).is(lunarPillar);
            boolean crownok = level.getBlockState(base.above(2)).is(celestialTile);
            
            String status = (p1ok && p2ok && crownok) ? "§a✓" : "§c✗";
            player.displayClientMessage(Component.literal("  Corner" + i + ": " + status + 
                " §7P1:" + (p1ok ? "§a✓" : "§c✗") +
                " §7P2:" + (p2ok ? "§a✓" : "§c✗") +
                " §7Crown:" + (crownok ? "§a✓" : "§c✗")), false);
        }
        
        player.displayClientMessage(Component.literal("§7--- Focus Patterns ---"), false);
        for (FocusPattern pattern : PatternManager.getInstance().getAllFocusPatterns()) {
            int foundCount = 0;
            int linkedCount = 0;
            
            for (BlockPos relPos : pattern.getPositions()) {
                BlockPos focusPos = tablePos.offset(relPos);
                BlockEntity focusBe = level.getBlockEntity(focusPos);
                
                if (focusBe instanceof MoonlightFocusBlockEntity focus) {
                    foundCount++;
                    if (focus.hasLinkTo(tablePos)) {
                        linkedCount++;
                    }
                }
            }
            
            int total = pattern.getPositions().size();
            String status = (foundCount == total && linkedCount == total) ? "§a✓" : "§c✗";
            player.displayClientMessage(Component.literal("  " + pattern.getName() + ": " + status + 
                " §7(Focus: " + foundCount + "/" + total + ", Linked: " + linkedCount + "/" + total + ")"), false);
        }
    }
    
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof CelestialEnchantingTableBlockEntity table) {
                ItemStack item = table.getItem();
                if (!item.isEmpty()) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), item);
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
