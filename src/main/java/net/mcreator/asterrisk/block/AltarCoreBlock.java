package net.mcreator.asterrisk.block;

import net.mcreator.asterrisk.block.entity.AltarCoreBlockEntity;
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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 祭壇コアブロック
 * 儀式の中心、周囲の台座と連携してクラフトを実行
 */
public class AltarCoreBlock extends BaseEntityBlock {

    // 祭壇の形状（複雑な多層構造）
    private static final VoxelShape BASE = Block.box(0, 0, 0, 16, 4, 16);
    private static final VoxelShape MIDDLE = Block.box(2, 4, 2, 14, 10, 14);
    private static final VoxelShape TOP = Block.box(4, 10, 4, 12, 14, 12);
    private static final VoxelShape SHAPE = Shapes.or(BASE, MIDDLE, TOP);

    public AltarCoreBlock() {
        super(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_PURPLE)
            .strength(5.0f, 10.0f)
            .requiresCorrectToolForDrops()
            .lightLevel(state -> 10)
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
        return AltarCoreBlockEntity.create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return (lvl, pos, st, be) -> {
            if (be instanceof AltarCoreBlockEntity altar) {
                AltarCoreBlockEntity.serverTick(lvl, pos, st, altar);
            }
        };
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        // Linking Wandを持っている場合は処理をスキップ（アイテム側で処理）
        if (player.getItemInHand(hand).getItem() instanceof net.mcreator.asterrisk.item.LinkingWandItem) {
            return InteractionResult.PASS;
        }
        
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof AltarCoreBlockEntity altar)) {
            return InteractionResult.PASS;
        }

        if (player.isShiftKeyDown()) {
            // Shift+右クリック: マナ量表示
            float mana = altar.getMana();
            float maxMana = altar.getMaxMana();
            int percent = (int) ((mana / maxMana) * 100);
            int pedestalCount = altar.findPedestals().size();
            int itemCount = altar.findPedestalsWithItems().size();

            player.displayClientMessage(
                Component.literal("§d✦ Altar Core")
                    .append(Component.literal(" | Mana: " + String.format("%.0f", mana) + "/" + String.format("%.0f", maxMana)).withStyle(ChatFormatting.AQUA))
                    .append(Component.literal(" | Pedestals: " + itemCount + "/" + pedestalCount).withStyle(ChatFormatting.GRAY)),
                true
            );
        } else {
            // 右クリック: 儀式開始
            if (altar.isRitualInProgress()) {
                player.displayClientMessage(
                    Component.literal("§e Ritual in progress..."),
                    true
                );
            } else {
                altar.startRitual(player);
            }
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§5✧ Altar Core"));
        tooltip.add(Component.literal("§7Center of ritual crafting"));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§7Place Ritual Pedestals around"));
        tooltip.add(Component.literal("§7at distance of 2 blocks"));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§7Right-click: Start ritual"));
        tooltip.add(Component.literal("§7Shift+Right-click: View status"));
        tooltip.add(Component.literal("§b  Max Mana: 2000"));
    }
}
