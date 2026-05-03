package net.mcreator.asterrisk.block;

import net.mcreator.asterrisk.block.entity.LunarCollectorBlockEntity;
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
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 月光収集器ブロック
 * 夜間にマナを自動収集する
 */
public class LunarCollectorBlock extends BaseEntityBlock {

    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 14, 14);

    public LunarCollectorBlock() {
        super(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_LIGHT_BLUE)
            .strength(3.0f, 6.0f)
            .lightLevel(state -> 7)
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
        return LunarCollectorBlockEntity.create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        // シンプルなラムダでTickerを返す
        return (lvl, pos, st, be) -> {
            if (be instanceof LunarCollectorBlockEntity collector) {
                LunarCollectorBlockEntity.serverTick(lvl, pos, st, collector);
            }
        };
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof LunarCollectorBlockEntity collector) {
                float mana = collector.getMana();
                float maxMana = collector.getMaxMana();
                int percent = (int) ((mana / maxMana) * 100);
                
                player.displayClientMessage(
                    Component.translatable("message.aster_risk.lunar_collector.mana",
                        String.format("%.1f", mana), String.format("%.0f", maxMana), percent)
                        .withStyle(ChatFormatting.AQUA),
                    true
                );
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        TooltipHelper.addBlank(tooltip);
        TooltipHelper.addHeader(tooltip, ChatFormatting.BLUE, "tooltip.aster_risk.lunar_collector.header");
        TooltipHelper.addDescription(tooltip, "tooltip.aster_risk.lunar_collector.line1");
        TooltipHelper.addDescription(tooltip, "tooltip.aster_risk.lunar_collector.line2");
        TooltipHelper.addStat(tooltip, ChatFormatting.AQUA, "tooltip.aster_risk.stat.max_storage",
            TooltipHelper.formatNumber(LunarCollectorBlockEntity.MAX_MANA));
    }
}
