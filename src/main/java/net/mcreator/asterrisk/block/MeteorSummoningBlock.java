package net.mcreator.asterrisk.block;

import net.mcreator.asterrisk.block.entity.MeteorSummoningBlockEntity;
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
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 流星召喚陣 - 流星を召喚するためのメイン装置
 */
public class MeteorSummoningBlock extends BaseEntityBlock {

    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 2, 16);

    public MeteorSummoningBlock() {
        super(BlockBehaviour.Properties.of()
            .strength(4.0f, 6.0f)
            .lightLevel(state -> state.getValue(ACTIVE) ? 12 : 4)
            .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
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
        return new MeteorSummoningBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return createTickerHelper(type, ModBlockEntities.METEOR_SUMMONING.get(), MeteorSummoningBlockEntity::serverTick);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof MeteorSummoningBlockEntity summoner) {
            ItemStack heldItem = player.getItemInHand(hand);
            
            if (heldItem.isEmpty()) {
                // 空の手で右クリック - コア取り出し
                ItemStack removed = summoner.removeCore();
                if (!removed.isEmpty()) {
                    if (!player.getInventory().add(removed)) {
                        player.drop(removed, false);
                    }
                    return InteractionResult.CONSUME;
                }
            } else {
                // アイテムを持って右クリック
                if (summoner.setCore(heldItem)) {
                    if (!player.isCreative()) {
                        heldItem.shrink(1);
                    }
                    return InteractionResult.CONSUME;
                }
            }
            
            // ステータス表示
            String status = summoner.isSummoning() ? "§c召喚中..." : 
                           summoner.hasCore() ? "§a準備完了" : "§7コアを設置してください";
            float mana = summoner.getMana();
            
            player.displayClientMessage(Component.literal(
                String.format("%s §d| §bマナ: %.0f/%.0f", status, mana, summoner.getMaxMana())), true);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof MeteorSummoningBlockEntity summoner) {
                summoner.dropContents();
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§6✦ Meteor Summoning Circle"));
        tooltip.add(Component.literal("§7Summon meteors from the sky"));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§7Place Meteor Summon Core to activate"));
        tooltip.add(Component.literal("§7Requires large amounts of mana"));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§c⚠ Can only be used at night!"));
    }
}
