package net.mcreator.asterrisk.block;

import net.mcreator.asterrisk.block.entity.ManaBatteryBlockEntity;
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
 * マナバッテリーブロック
 * 大容量マナ貯蔵、右クリックでモード切替
 */
public class ManaBatteryBlock extends BaseEntityBlock {

    private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 14, 15);

    public ManaBatteryBlock() {
        super(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_CYAN)
            .strength(4.0f, 8.0f)
            .requiresCorrectToolForDrops()
            .lightLevel(state -> 8)
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
        return ManaBatteryBlockEntity.create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        // マナバッテリーは自動処理なし（受動的なストレージ）
        return null;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ManaBatteryBlockEntity battery) {
                if (player.isShiftKeyDown()) {
                    // Shift+右クリック: モード切替
                    battery.cycleMode();
                    player.displayClientMessage(
                        Component.literal("Mode: ").withStyle(ChatFormatting.GRAY)
                            .append(battery.getModeDisplayName()),
                        true
                    );
                } else {
                    // 右クリック: マナ量表示
                    float mana = battery.getMana();
                    float maxMana = battery.getMaxMana();
                    int percent = (int) ((mana / maxMana) * 100);
                    
                    String modeColor = switch (battery.getMode()) {
                        case INPUT_ONLY -> "§a";
                        case OUTPUT_ONLY -> "§c";
                        case BIDIRECTIONAL -> "§b";
                    };
                    
                    player.displayClientMessage(
                        Component.literal(modeColor + "Mana: " + String.format("%.0f", mana) + " / " + String.format("%.0f", maxMana) + " (" + percent + "%)"),
                        true
                    );
                }
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§b⚡ Mana Battery"));
        tooltip.add(Component.literal("§7Large mana storage block"));
        tooltip.add(Component.literal("§b  Capacity: 5000 Mana"));
        tooltip.add(Component.literal("§b  Transfer: 100 mana/s"));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§7Right-click: View mana"));
        tooltip.add(Component.literal("§7Shift+Right-click: Change mode"));
        tooltip.add(Component.literal("§7Modes: §aInput §7/ §cOutput §7/ §bBoth"));
    }
}
