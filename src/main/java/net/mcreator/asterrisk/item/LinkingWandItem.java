package net.mcreator.asterrisk.item;

import net.mcreator.asterrisk.block.entity.AltarCoreBlockEntity;
import net.mcreator.asterrisk.block.entity.CelestialEnchantingTableBlockEntity;
import net.mcreator.asterrisk.block.entity.FocusChamberCoreBlockEntity;
import net.mcreator.asterrisk.block.entity.MoonlightFocusBlockEntity;
import net.mcreator.asterrisk.block.entity.ObeliskBlockEntity;
import net.mcreator.asterrisk.block.entity.ResonatorBlockEntity;
import net.mcreator.asterrisk.block.entity.RitualCircleBlockEntity;
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
 * 各種ブロックをリンクするためのアイテム
 * 
 * 対応:
 * - Resonator同士のリンク
 * - Obelisk → AltarCore のリンク
 * - Focus → Focus/RitualCircle/ChamberCore/CelestialEnchantTable のリンク
 */
public class LinkingWandItem extends Item {

    private static final int MAX_OBELISK_RANGE = 32;
    private static final int MAX_FOCUS_RANGE = 24;

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
        
        // ===== MoonlightFocus =====
        if (be instanceof MoonlightFocusBlockEntity focus) {
            return handleFocusInteraction(level, pos, player, stack, focus, context);
        }
        
        // ===== Focus用リンク先ブロック =====
        if (be instanceof FocusChamberCoreBlockEntity ||
            be instanceof RitualCircleBlockEntity ||
            be instanceof CelestialEnchantingTableBlockEntity) {
            return handleFocusTargetInteraction(level, pos, player, stack, be, context);
        }
        
        // ===== Resonator =====
        if (be instanceof ResonatorBlockEntity resonator) {
            return handleResonatorLink(level, pos, player, stack, resonator, context);
        }
        
        // ===== Obelisk =====
        if (be instanceof ObeliskBlockEntity obelisk) {
            return handleObeliskLink(level, pos, player, stack, obelisk, context);
        }
        
        // ===== AltarCore =====
        if (be instanceof AltarCoreBlockEntity altar) {
            return handleAltarLink(level, pos, player, stack, altar, context);
        }
        
        // その他のブロック - 選択解除
        if (!level.isClientSide()) {
            CompoundTag tag = stack.getOrCreateTag();
            if (hasAnySelection(tag)) {
                clearAllSelections(tag);
                player.displayClientMessage(
                    Component.literal("Selection cleared").withStyle(ChatFormatting.GRAY),
                    true
                );
            }
        }
        
        return InteractionResult.PASS;
    }
    
    // ===== Focus リンク処理 =====
    
    private InteractionResult handleFocusInteraction(Level level, BlockPos pos, Player player, 
            ItemStack stack, MoonlightFocusBlockEntity focus, UseOnContext context) {
        if (!level.isClientSide()) {
            CompoundTag tag = stack.getOrCreateTag();
            
            // Shift+クリック: リンク解除
            if (player.isShiftKeyDown()) {
                focus.clearAllLinks(player);
                clearAllSelections(tag);
                level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.5f, 0.5f);
                return InteractionResult.SUCCESS;
            }
            
            // 別のFocusが選択されていればリンク（Focus同士）
            if (tag.contains("focusPos") && "focus".equals(tag.getString("linkMode"))) {
                BlockPos sourcePos = NbtUtils.readBlockPos(tag.getCompound("focusPos"));
                
                if (!sourcePos.equals(pos)) {
                    BlockEntity sourceBe = level.getBlockEntity(sourcePos);
                    if (sourceBe instanceof MoonlightFocusBlockEntity sourceFocus) {
                        double distance = Math.sqrt(sourcePos.distSqr(pos));
                        if (distance <= MAX_FOCUS_RANGE) {
                            if (sourceFocus.addLink(pos, player)) {
                                level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0f, 1.5f);
                                stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(context.getHand()));
                            }
                        } else {
                            player.displayClientMessage(
                                Component.literal("Too far! Max range: " + MAX_FOCUS_RANGE + " blocks")
                                    .withStyle(ChatFormatting.RED),
                                true
                            );
                        }
                    }
                } else {
                    // 同じFocusをクリック - 選択解除
                    player.displayClientMessage(
                        Component.literal("Selection cleared").withStyle(ChatFormatting.GRAY),
                        true
                    );
                }
                clearAllSelections(tag);
                return InteractionResult.SUCCESS;
            }
            
            // このFocusを選択
            tag.put("focusPos", NbtUtils.writeBlockPos(pos));
            tag.putString("linkMode", "focus");
            clearOtherSelections(tag, "focusPos");
            
            // メッセージ表示（showLinkInfoの後に選択メッセージを表示）
            int linkCount = focus.getLinkCount();
            float mana = focus.getStoredMana();
            player.displayClientMessage(
                Component.literal("§b[Focus] §aSelected! §7Mana: §e" + (int)mana + "/500 §7| Links: §a" + linkCount + 
                    " §7- Click target to link")
                    .withStyle(ChatFormatting.AQUA),
                true
            );
            level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_HIT, SoundSource.BLOCKS, 1.0f, 1.2f);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
    
    private InteractionResult handleFocusTargetInteraction(Level level, BlockPos pos, Player player,
            ItemStack stack, BlockEntity target, UseOnContext context) {
        if (!level.isClientSide()) {
            CompoundTag tag = stack.getOrCreateTag();
            
            // Focusが選択されていればリンク
            if (tag.contains("focusPos") && "focus".equals(tag.getString("linkMode"))) {
                BlockPos focusPos = NbtUtils.readBlockPos(tag.getCompound("focusPos"));
                BlockEntity focusBe = level.getBlockEntity(focusPos);
                
                if (focusBe instanceof MoonlightFocusBlockEntity focus) {
                    double distance = Math.sqrt(focusPos.distSqr(pos));
                    if (distance <= MAX_FOCUS_RANGE) {
                        if (focus.addLink(pos, player)) {
                            level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0f, 1.5f);
                            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(context.getHand()));
                        }
                    } else {
                        player.displayClientMessage(
                            Component.literal("Too far! Max range: " + MAX_FOCUS_RANGE + " blocks")
                                .withStyle(ChatFormatting.RED),
                            true
                        );
                    }
                }
                clearAllSelections(tag);
                return InteractionResult.SUCCESS;
            }
            
            // 情報表示
            String blockName = target.getClass().getSimpleName().replace("BlockEntity", "");
            player.displayClientMessage(
                Component.literal("Select a Focus first, then click here to link")
                    .withStyle(ChatFormatting.GRAY),
                true
            );
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
    
    // ===== Resonator リンク処理 =====
    
    private InteractionResult handleResonatorLink(Level level, BlockPos pos, Player player, ItemStack stack, 
            ResonatorBlockEntity resonator, UseOnContext context) {
        if (!level.isClientSide()) {
            CompoundTag tag = stack.getOrCreateTag();
            
            if (player.isShiftKeyDown()) {
                int linkCount = resonator.getLinkedPositions().size();
                resonator.unlinkAll();
                player.displayClientMessage(
                    Component.literal("Cleared " + linkCount + " links").withStyle(ChatFormatting.YELLOW),
                    true
                );
                level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.5f, 0.5f);
                clearAllSelections(tag);
                return InteractionResult.SUCCESS;
            }
            
            if (!tag.contains("firstPos") || !"resonator".equals(tag.getString("linkMode"))) {
                tag.put("firstPos", NbtUtils.writeBlockPos(pos));
                tag.putString("linkMode", "resonator");
                clearOtherSelections(tag, "firstPos");
                player.displayClientMessage(
                    Component.literal("First resonator selected at " + pos.toShortString())
                        .withStyle(ChatFormatting.AQUA),
                    true
                );
                level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_HIT, SoundSource.BLOCKS, 1.0f, 1.2f);
            } else {
                BlockPos firstPos = NbtUtils.readBlockPos(tag.getCompound("firstPos"));
                clearAllSelections(tag);
                
                if (firstPos.equals(pos)) {
                    player.displayClientMessage(
                        Component.literal("Selection cleared").withStyle(ChatFormatting.GRAY),
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
                            Component.literal("Linked resonators!").withStyle(ChatFormatting.GREEN),
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
    
    // ===== Obelisk リンク処理 =====
    
    private InteractionResult handleObeliskLink(Level level, BlockPos pos, Player player, ItemStack stack,
            ObeliskBlockEntity obelisk, UseOnContext context) {
        if (!level.isClientSide()) {
            CompoundTag tag = stack.getOrCreateTag();
            
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
            
            tag.put("obeliskPos", NbtUtils.writeBlockPos(pos));
            tag.putString("linkMode", "obelisk");
            clearOtherSelections(tag, "obeliskPos");
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
    
    // ===== AltarCore リンク処理 =====
    
    private InteractionResult handleAltarLink(Level level, BlockPos pos, Player player, ItemStack stack,
            AltarCoreBlockEntity altar, UseOnContext context) {
        if (!level.isClientSide()) {
            CompoundTag tag = stack.getOrCreateTag();
            
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
            
            if (tag.contains("obeliskPos") && "obelisk".equals(tag.getString("linkMode"))) {
                BlockPos obeliskPos = NbtUtils.readBlockPos(tag.getCompound("obeliskPos"));
                clearAllSelections(tag);
                
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
                        Component.literal("Obelisk no longer exists!").withStyle(ChatFormatting.RED),
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
    
    // ===== ヘルパーメソッド =====
    
    private boolean hasAnySelection(CompoundTag tag) {
        return tag.contains("firstPos") || tag.contains("obeliskPos") || tag.contains("focusPos");
    }
    
    private void clearAllSelections(CompoundTag tag) {
        tag.remove("firstPos");
        tag.remove("obeliskPos");
        tag.remove("focusPos");
        tag.remove("linkMode");
    }
    
    private void clearOtherSelections(CompoundTag tag, String keep) {
        if (!keep.equals("firstPos")) tag.remove("firstPos");
        if (!keep.equals("obeliskPos")) tag.remove("obeliskPos");
        if (!keep.equals("focusPos")) tag.remove("focusPos");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§b☽ Linking Wand"));
        tooltip.add(Component.literal("§7Links magical blocks together"));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§e■ Focus: §7Link to targets (multi-link)"));
        tooltip.add(Component.literal("§e  Targets: §7Focus, Chamber, Circle, Enchant"));
        tooltip.add(Component.literal("§e■ Resonators: §7Link to transfer mana"));
        tooltip.add(Component.literal("§e■ Obelisk → Altar: §7Link for ritual power"));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§e Right-click: §7Select/Link"));
        tooltip.add(Component.literal("§e Shift+Right-click: §7Clear links"));
        
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            String mode = tag.getString("linkMode");
            if (tag.contains("firstPos") && "resonator".equals(mode)) {
                BlockPos pos = NbtUtils.readBlockPos(tag.getCompound("firstPos"));
                tooltip.add(Component.literal(""));
                tooltip.add(Component.literal("§6Resonator selected: " + pos.toShortString()));
            } else if (tag.contains("obeliskPos") && "obelisk".equals(mode)) {
                BlockPos pos = NbtUtils.readBlockPos(tag.getCompound("obeliskPos"));
                tooltip.add(Component.literal(""));
                tooltip.add(Component.literal("§5Obelisk selected: " + pos.toShortString()));
            } else if (tag.contains("focusPos") && "focus".equals(mode)) {
                BlockPos pos = NbtUtils.readBlockPos(tag.getCompound("focusPos"));
                tooltip.add(Component.literal(""));
                tooltip.add(Component.literal("§bFocus selected: " + pos.toShortString()));
            }
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && hasAnySelection(tag);
    }
}
