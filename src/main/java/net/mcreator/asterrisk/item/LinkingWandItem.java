package net.mcreator.asterrisk.item;

import net.mcreator.asterrisk.block.entity.ResonatorBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.List;

/**
 * リンクの杖
 * 共振器同士をリンクするためのアイテム
 */
public class LinkingWandItem extends Item {

    public LinkingWandItem() {
        super(new Item.Properties()
            .stacksTo(1)
            .durability(256)
            .rarity(Rarity.UNCOMMON));
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.literal("Linking Wand");
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        
        if (player == null) return InteractionResult.PASS;
        
        BlockEntity be = level.getBlockEntity(pos);
        
        if (be instanceof ResonatorBlockEntity resonator) {
            if (!level.isClientSide()) {
                CompoundTag tag = stack.getOrCreateTag();
                
                // スニークでリンク解除モード
                if (player.isShiftKeyDown()) {
                    int linkCount = resonator.getLinkedPositions().size();
                    resonator.unlinkAll();
                    player.displayClientMessage(
                        Component.literal("Cleared " + linkCount + " links")
                            .withStyle(ChatFormatting.YELLOW),
                        true
                    );
                    level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.5f, 0.5f);
                    tag.remove("firstPos");
                    return InteractionResult.SUCCESS;
                }
                
                // 1つ目の共振器を選択
                if (!tag.contains("firstPos")) {
                    tag.put("firstPos", NbtUtils.writeBlockPos(pos));
                    player.displayClientMessage(
                        Component.literal("First resonator selected at " + pos.toShortString())
                            .withStyle(ChatFormatting.AQUA),
                        true
                    );
                    level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_HIT, SoundSource.BLOCKS, 1.0f, 1.2f);
                } else {
                    // 2つ目の共振器を選択してリンク
                    BlockPos firstPos = NbtUtils.readBlockPos(tag.getCompound("firstPos"));
                    tag.remove("firstPos");
                    
                    if (firstPos.equals(pos)) {
                        player.displayClientMessage(
                            Component.literal("Selection cleared")
                                .withStyle(ChatFormatting.GRAY),
                            true
                        );
                        return InteractionResult.SUCCESS;
                    }
                    
                    BlockEntity firstBE = level.getBlockEntity(firstPos);
                    if (firstBE instanceof ResonatorBlockEntity firstResonator) {
                        // 距離チェック
                        double distance = Math.sqrt(firstPos.distSqr(pos));
                        int maxRange = Math.min(firstResonator.getMaxRange(), resonator.getMaxRange());
                        
                        if (distance > maxRange) {
                            player.displayClientMessage(
                                Component.literal("Too far! Max range: " + maxRange + " blocks (distance: " + String.format("%.1f", distance) + ")")
                                    .withStyle(ChatFormatting.RED),
                                true
                            );
                            level.playSound(null, pos, SoundEvents.NOTE_BLOCK_BASS.get(), SoundSource.BLOCKS, 1.0f, 0.5f);
                            return InteractionResult.FAIL;
                        }
                        
                        // リンク実行
                        if (firstResonator.linkTo(pos)) {
                            player.displayClientMessage(
                                Component.literal("Linked! " + firstPos.toShortString() + " <-> " + pos.toShortString())
                                    .withStyle(ChatFormatting.GREEN),
                                true
                            );
                            level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.0f, 1.5f);
                            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(context.getHand()));
                        } else {
                            player.displayClientMessage(
                                Component.literal("Failed to link!")
                                    .withStyle(ChatFormatting.RED),
                                true
                            );
                        }
                    } else {
                        player.displayClientMessage(
                            Component.literal("First resonator no longer exists!")
                                .withStyle(ChatFormatting.RED),
                            true
                        );
                    }
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        } else {
            // 共振器以外をクリックしたら選択解除
            if (!level.isClientSide()) {
                CompoundTag tag = stack.getOrCreateTag();
                if (tag.contains("firstPos")) {
                    tag.remove("firstPos");
                    player.displayClientMessage(
                        Component.literal("Selection cleared")
                            .withStyle(ChatFormatting.GRAY),
                        true
                    );
                }
            }
        }
        
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§b☽ Linking Wand"));
        tooltip.add(Component.literal("§7Links resonators together"));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§e Right-click: §7Select/Link resonator"));
        tooltip.add(Component.literal("§e Shift+Right-click: §7Clear all links"));
        
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("firstPos")) {
            BlockPos pos = NbtUtils.readBlockPos(tag.getCompound("firstPos"));
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal("§6Selected: " + pos.toShortString()));
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains("firstPos");
    }
}
