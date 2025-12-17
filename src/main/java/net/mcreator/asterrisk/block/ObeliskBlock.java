package net.mcreator.asterrisk.block;

import net.mcreator.asterrisk.block.entity.AltarCoreBlockEntity;
import net.mcreator.asterrisk.block.entity.ObeliskBlockEntity;
import net.mcreator.asterrisk.block.entity.ObeliskEnergyType;
import net.mcreator.asterrisk.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

/**
 * オベリスクブロック - 特定の時間帯にエネルギーを生成
 */
public class ObeliskBlock extends Block implements EntityBlock {
    
    private final ObeliskEnergyType energyType;
    
    // オベリスクの形状（細長い柱）
    private static final VoxelShape SHAPE = Shapes.or(
        Block.box(4, 0, 4, 12, 2, 12),    // 土台
        Block.box(5, 2, 5, 11, 14, 11),   // 柱
        Block.box(4, 14, 4, 12, 16, 12)   // 頂上
    );
    
    public ObeliskBlock(Properties properties, ObeliskEnergyType energyType) {
        super(properties);
        this.energyType = energyType;
    }
    
    public ObeliskEnergyType getEnergyType() {
        return energyType;
    }
    
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        ObeliskBlockEntity entity = new ObeliskBlockEntity(pos, state);
        entity.setEnergyType(energyType);
        return entity;
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (!level.isClientSide() && type == ModBlockEntities.OBELISK.get()) {
            return (lvl, pos, st, be) -> ObeliskBlockEntity.serverTick(lvl, pos, st, (ObeliskBlockEntity) be);
        }
        return null;
    }
    
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        // Linking Wandを持っている場合は処理をスキップ（アイテム側で処理）
        if (player.getItemInHand(hand).getItem() instanceof net.mcreator.asterrisk.item.LinkingWandItem) {
            return InteractionResult.PASS;
        }
        
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ObeliskBlockEntity obelisk) {
                int energy = obelisk.getStoredEnergy();
                int maxEnergy = obelisk.getMaxEnergy();
                String typeName = obelisk.getEnergyType() != null ? obelisk.getEnergyType().getName() : "unknown";
                int linkedCount = obelisk.getLinkedAltars().size();
                
                player.displayClientMessage(
                    Component.literal(String.format("§b%s Energy: §f%d/%d §7| Linked Altars: §f%d", 
                        typeName.substring(0, 1).toUpperCase() + typeName.substring(1),
                        energy, maxEnergy, linkedCount)), 
                    true
                );
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        String description = switch (energyType) {
            case LUNAR -> "§7Generates energy during §bnight§7 (bonus at full moon)";
            case STELLAR -> "§7Generates energy during §eclear nights";
            case SOLAR -> "§7Generates energy during §6daytime";
            case VOID -> "§7Generates energy only during §5new moon§7 nights";
        };
        
        tooltip.add(Component.literal(description));
        tooltip.add(Component.literal("§7Use Linking Wand to connect to Altar Core"));
    }
    
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ObeliskBlockEntity obelisk) {
                // リンク解除
                for (BlockPos altarPos : obelisk.getLinkedAltars()) {
                    BlockEntity altarBe = level.getBlockEntity(altarPos);
                    if (altarBe instanceof AltarCoreBlockEntity altar) {
                        altar.removeLinkedObelisk(pos);
                    }
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
