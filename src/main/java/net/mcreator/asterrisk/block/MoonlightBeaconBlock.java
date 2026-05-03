package net.mcreator.asterrisk.block;

import net.mcreator.asterrisk.block.entity.MoonlightBeaconBlockEntity;
import net.mcreator.asterrisk.registry.ModBlockEntities;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 月光ビーコン
 * マナを消費して範囲内のプレイヤーにバフ効果を付与
 */
public class MoonlightBeaconBlock extends BaseEntityBlock {

    // カスタム形状：ビーコン風
    private static final VoxelShape SHAPE = Shapes.or(
        // ベース
        Block.box(2, 0, 2, 14, 3, 14),
        // 中央の柱
        Block.box(4, 3, 4, 12, 10, 12),
        // 上部のクリスタル
        Block.box(5, 10, 5, 11, 14, 11)
    );

    public MoonlightBeaconBlock() {
        super(BlockBehaviour.Properties.of()
            .strength(3.0f, 6.0f)
            .lightLevel(state -> 10)
            .noOcclusion());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        TooltipHelper.addBlank(tooltip);
        TooltipHelper.addHeader(tooltip, ChatFormatting.LIGHT_PURPLE, "tooltip.aster_risk.moonlight_beacon.header");
        TooltipHelper.addDescription(tooltip, "tooltip.aster_risk.moonlight_beacon.line1");
        TooltipHelper.addDescription(tooltip, "tooltip.aster_risk.moonlight_beacon.line2");
        TooltipHelper.addStat(tooltip, ChatFormatting.AQUA, "tooltip.aster_risk.stat.range",
            MoonlightBeaconBlockEntity.EFFECT_RADIUS);
        TooltipHelper.addStat(tooltip, ChatFormatting.AQUA, "tooltip.aster_risk.stat.capacity",
            TooltipHelper.formatNumber(MoonlightBeaconBlockEntity.MAX_MANA));
        TooltipHelper.addInfo(tooltip, ChatFormatting.GREEN, "tooltip.aster_risk.moonlight_beacon.effects");
        TooltipHelper.addInfo(tooltip, ChatFormatting.YELLOW, "tooltip.aster_risk.moonlight_beacon.full_moon");
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
        return MoonlightBeaconBlockEntity.create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return createTickerHelper(type, ModBlockEntities.MOONLIGHT_BEACON.get(),
            MoonlightBeaconBlockEntity::serverTick);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                  InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof MoonlightBeaconBlockEntity beacon) {
            Component status = beacon.isActive()
                ? Component.translatable("message.aster_risk.moonlight_beacon.active")
                : Component.translatable("message.aster_risk.moonlight_beacon.inactive");
            player.displayClientMessage(
                Component.translatable("message.aster_risk.moonlight_beacon.status",
                    status, (int)beacon.getMana(), (int)beacon.getMaxMana(),
                    MoonlightBeaconBlockEntity.EFFECT_RADIUS),
                true
            );
        }

        return InteractionResult.CONSUME;
    }
}
