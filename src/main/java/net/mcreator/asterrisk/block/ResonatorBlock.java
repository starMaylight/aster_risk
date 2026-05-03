package net.mcreator.asterrisk.block;

import net.mcreator.asterrisk.block.entity.ResonatorBlockEntity;
import net.mcreator.asterrisk.util.TooltipHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 共振器ブロック（Tier1-3共通）
 */
public class ResonatorBlock extends BaseEntityBlock {

    private static final VoxelShape SHAPE = Block.box(4, 0, 4, 12, 14, 12);
    
    private final int tier;

    public ResonatorBlock(int tier, MapColor color) {
        super(BlockBehaviour.Properties.of()
            .mapColor(color)
            .strength(2.5f, 5.0f)
            .lightLevel(state -> 5 + tier * 2)
            .noOcclusion());
        this.tier = tier;
    }

    public static ResonatorBlock createTier1() {
        return new ResonatorBlock(1, MapColor.COLOR_LIGHT_BLUE);
    }

    public static ResonatorBlock createTier2() {
        return new ResonatorBlock(2, MapColor.COLOR_PURPLE);
    }

    public static ResonatorBlock createTier3() {
        return new ResonatorBlock(3, MapColor.GOLD);
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
        return switch (tier) {
            case 1 -> ResonatorBlockEntity.createTier1(pos, state);
            case 2 -> ResonatorBlockEntity.createTier2(pos, state);
            case 3 -> ResonatorBlockEntity.createTier3(pos, state);
            default -> ResonatorBlockEntity.createTier1(pos, state);
        };
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        
        // シンプルなラムダでTickerを返す
        return (lvl, pos, st, be) -> {
            if (be instanceof ResonatorBlockEntity resonator) {
                ResonatorBlockEntity.serverTick(lvl, pos, st, resonator);
            }
        };
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ResonatorBlockEntity resonator) {
                resonator.unlinkAll();
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        TooltipHelper.addBlank(tooltip);

        String headerKey = switch (tier) {
            case 1 -> "tooltip.aster_risk.resonator.tier1";
            case 2 -> "tooltip.aster_risk.resonator.tier2";
            case 3 -> "tooltip.aster_risk.resonator.tier3";
            default -> "tooltip.aster_risk.resonator.tier1";
        };
        ChatFormatting headerColor = switch (tier) {
            case 1 -> ChatFormatting.AQUA;
            case 2 -> ChatFormatting.LIGHT_PURPLE;
            case 3 -> ChatFormatting.GOLD;
            default -> ChatFormatting.GRAY;
        };
        TooltipHelper.addHeader(tooltip, headerColor, headerKey);
        TooltipHelper.addDescription(tooltip, "tooltip.aster_risk.resonator.line1");

        int range = switch (tier) {
            case 1 -> 16;
            case 2 -> 32;
            case 3 -> 64;
            default -> 16;
        };
        float rate = switch (tier) {
            case 1 -> 10f;
            case 2 -> 25f;
            case 3 -> 50f;
            default -> 10f;
        };

        TooltipHelper.addStat(tooltip, ChatFormatting.AQUA, "tooltip.aster_risk.stat.range", range);
        TooltipHelper.addStat(tooltip, ChatFormatting.AQUA, "tooltip.aster_risk.stat.transfer_rate",
            TooltipHelper.formatNumber(rate));
        TooltipHelper.addInfo(tooltip, ChatFormatting.GRAY, "tooltip.aster_risk.resonator.use_link");
    }

    public int getTier() {
        return tier;
    }
}
