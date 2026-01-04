package net.mcreator.asterrisk.block;

import net.mcreator.asterrisk.block.entity.MoonlightBeaconBlockEntity;
import net.mcreator.asterrisk.registry.ModBlockEntities;
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
        tooltip.add(Component.translatable("tooltip.aster_risk.moonlight_beacon.line1").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.aster_risk.moonlight_beacon.line2").withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("Range: 16 blocks").withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.literal("Effects: Night Vision, Regeneration").withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.literal("Full Moon: +Resistance").withStyle(ChatFormatting.YELLOW));
        tooltip.add(Component.literal("Mana: 0.5/tick").withStyle(ChatFormatting.BLUE));
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
            String status = beacon.isActive() ? "§aActive" : "§cInactive";
            player.displayClientMessage(
                Component.literal("Moonlight Beacon - " + status + 
                    " §7| Mana: §b" + (int)beacon.getMana() + "/" + (int)beacon.getMaxMana() +
                    " §7| Range: §e" + MoonlightBeaconBlockEntity.EFFECT_RADIUS + " blocks"),
                true
            );
        }

        return InteractionResult.CONSUME;
    }
}
