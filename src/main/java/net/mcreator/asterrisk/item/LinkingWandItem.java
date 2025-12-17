package net.mcreator.asterrisk.item;

import net.mcreator.asterrisk.block.entity.AltarCoreBlockEntity;
import net.mcreator.asterrisk.block.entity.ObeliskBlockEntity;
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
 * 共振器同士、またはオベリスクとAltar Coreをリンクするためのアイテム
 */
public class LinkingWandItem extends Item {

    private static final int MAX_OBELISK_RANGE = 32;

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
        
        // 共振器の処理
        if (be instanceof ResonatorBlockEntity resonator) {
            return handleResonatorLink(level, pos, player, stack, resonator, context);
        }
        
        // オベリスクの処理
        if (be instanceof ObeliskBlockEntity obelisk) {
            return handleObeliskLink(level, pos, player, stack, obelisk, context);
        }
        
        // Altar Coreの処理
        if (be instanceof AltarCoreBlockEntity altar) {
            return handleAltarLink(level, pos, player, stack, altar, context);
        }
        
        // その他のブロックをクリックしたら選択解除
        if (!level.isClientSide()) {
            CompoundTag tag = stack.getOrCreateTag();
            if (tag.contains("firstPos") || tag.contains("obeliskPos")) {
                tag.remove("firstPos");
                tag.remove("obeliskPos");
                tag.remove("linkMode");
                player.displayClientMessage(
                    Component.literal("Selection cleared")
                        .withStyle(ChatFormatting.GRAY),
                    true
                );
            }
        }
        
        return InteractionResult.PASS;
    }
    
    private InteractionResult handleResonatorLink(Level level, BlockPos pos, Player player, ItemStack stack, 
            ResonatorBlockEntity resonator, UseOnContext context) {
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
                tag.remove("obeliskPos");
                tag.remove("linkMode");
                return InteractionResult.SUCCESS;
            }
            
            // 1つ目の共振器を選択
            if (!tag.contains("firstPos") || !"resonator".equals(tag.getString("linkMode"))) {
                tag.put("firstPos", NbtUtils.writeBlockPos(pos));
                tag.putString("linkMode", "resonator");
                tag.remove("obeliskPos");
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
                tag.remove("linkMode");
                
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
                    double distance = Math.sqrt(firstPos.distSqr(pos));
                    int maxRange = Math.min(firstResonator.getMaxRange(), resonator.getMaxRange());
                    
                    if (distance > maxRange) {
                        player.displayClientMessage(
                            Component.literal("Too far! Max range: " + maxRange + " blocks")
                                .withStyle(ChatFormatting.RED),
                            true
                        );
                        level.playSound(null, pos, SoundEvents.NOTE_BLOCK_BASS.get(), SoundSource.BLOCKS, 1.0f, 0.5f);
                        return InteractionResult.FAIL;
                    }
                    
                    if (firstResonator.linkTo(pos)) {
                        player.displayClientMessage(
                            Component.literal("Linked resonators!")
                                .withStyle(ChatFormatting.GREEN),
                            true
                        );
                        level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.0f, 1.5f);
                        stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(context.getHand()));
                    }
                }
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
    
    private InteractionResult handleObeliskLink(Level level, BlockPos pos, Player player, ItemStack stack,
            ObeliskBlockEntity obelisk, UseOnContext context) {
        if (!level.isClientSide()) {
            CompoundTag tag = stack.getOrCreateTag();
            
            // スニークでリンク解除
            if (player.isShiftKeyDown()) {
                int linkCount = obelisk.getLinkedAltars().size();
                for (BlockPos altarPos : obelisk.getLinkedAltars()) {
                    BlockEntity altarBe = level.getBlockEntity(altarPos);
                    if (altarBe instanceof AltarCoreBlockEntity altar) {
                        altar.removeLinkedObelisk(pos);
                    }
                }
                obelisk.clearLinkedAltars();
                player.displayClientMessage(
                    Component.literal("Cleared " + linkCount + " altar links")
                        .withStyle(ChatFormatting.YELLOW),
                    true
                );
                level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.5f, 0.5f);
                return InteractionResult.SUCCESS;
            }
            
            // オベリスク選択
            tag.put("obeliskPos", NbtUtils.writeBlockPos(pos));
            tag.putString("linkMode", "obelisk");
            tag.remove("firstPos");
            String typeName = obelisk.getEnergyType() != null ? obelisk.getEnergyType().getName() : "unknown";
            player.displayClientMessage(
                Component.literal("Obelisk (" + typeName + ") selected - now click Altar Core")
                    .withStyle(ChatFormatting.LIGHT_PURPLE),
                true
            );
            level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_HIT, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
    
    private InteractionResult handleAltarLink(Level level, BlockPos pos, Player player, ItemStack stack,
            AltarCoreBlockEntity altar, UseOnContext context) {
        if (!level.isClientSide()) {
            CompoundTag tag = stack.getOrCreateTag();
            
            // スニークでリンク解除
            if (player.isShiftKeyDown()) {
                int linkCount = altar.getLinkedObelisks().size();
                for (BlockPos obeliskPos : altar.getLinkedObelisks()) {
                    BlockEntity obeliskBe = level.getBlockEntity(obeliskPos);
                    if (obeliskBe instanceof ObeliskBlockEntity obelisk) {
                        obelisk.removeLinkedAltar(pos);
                    }
                }
                altar.clearLinkedObelisks();
                player.displayClientMessage(
                    Component.literal("Cleared " + linkCount + " obelisk links from altar")
                        .withStyle(ChatFormatting.YELLOW),
                    true
                );
                level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.5f, 0.5f);
                return InteractionResult.SUCCESS;
            }
            
            // オベリスクが選択されていればリンク実行
            if (tag.contains("obeliskPos") && "obelisk".equals(tag.getString("linkMode"))) {
                BlockPos obeliskPos = NbtUtils.readBlockPos(tag.getCompound("obeliskPos"));
                tag.remove("obeliskPos");
                tag.remove("linkMode");
                
                BlockEntity obeliskBe = level.getBlockEntity(obeliskPos);
                if (obeliskBe instanceof ObeliskBlockEntity obelisk) {
                    double distance = Math.sqrt(obeliskPos.distSqr(pos));
                    
                    if (distance > MAX_OBELISK_RANGE) {
                        player.displayClientMessage(
                            Component.literal("Too far! Max range: " + MAX_OBELISK_RANGE + " blocks")
                                .withStyle(ChatFormatting.RED),
                            true
                        );
                        level.playSound(null, pos, SoundEvents.NOTE_BLOCK_BASS.get(), SoundSource.BLOCKS, 1.0f, 0.5f);
                        return InteractionResult.FAIL;
                    }
                    
                    // 双方向リンク
                    altar.addLinkedObelisk(obeliskPos);
                    obelisk.addLinkedAltar(pos);
                    
                    String typeName = obelisk.getEnergyType() != null ? obelisk.getEnergyType().getName() : "unknown";
                    player.displayClientMessage(
                        Component.literal("Linked " + typeName + " obelisk to altar!")
                            .withStyle(ChatFormatting.GREEN),
                        true
                    );
                    level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0f, 1.5f);
                    stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(context.getHand()));
                } else {
                    player.displayClientMessage(
                        Component.literal("Obelisk no longer exists!")
                            .withStyle(ChatFormatting.RED),
                        true
                    );
                }
            } else {
                player.displayClientMessage(
                    Component.literal("Select an obelisk first, then click altar")
                        .withStyle(ChatFormatting.GRAY),
                    true
                );
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§b☽ Linking Wand"));
        tooltip.add(Component.literal("§7Links resonators or obelisks to altars"));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§e Right-click Resonator: §7Link resonators"));
        tooltip.add(Component.literal("§e Right-click Obelisk: §7Select obelisk"));
        tooltip.add(Component.literal("§e Right-click Altar: §7Link selected obelisk"));
        tooltip.add(Component.literal("§e Shift+Right-click: §7Clear links"));
        
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            if (tag.contains("firstPos") && "resonator".equals(tag.getString("linkMode"))) {
                BlockPos pos = NbtUtils.readBlockPos(tag.getCompound("firstPos"));
                tooltip.add(Component.literal(""));
                tooltip.add(Component.literal("§6Resonator selected: " + pos.toShortString()));
            } else if (tag.contains("obeliskPos") && "obelisk".equals(tag.getString("linkMode"))) {
                BlockPos pos = NbtUtils.readBlockPos(tag.getCompound("obeliskPos"));
                tooltip.add(Component.literal(""));
                tooltip.add(Component.literal("§5Obelisk selected: " + pos.toShortString()));
            }
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && (tag.contains("firstPos") || tag.contains("obeliskPos"));
    }
}
