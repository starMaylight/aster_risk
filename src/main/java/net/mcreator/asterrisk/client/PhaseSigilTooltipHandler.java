package net.mcreator.asterrisk.client;

import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.block.entity.PhaseAnvilBlockEntity;
import net.mcreator.asterrisk.item.PhaseSigilItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 月相刻印のツールチップ表示
 */
@Mod.EventBusSubscriber(modid = AsterRiskMod.MODID, value = Dist.CLIENT)
public class PhaseSigilTooltipHandler {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) return;

        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(PhaseAnvilBlockEntity.TAG_PHASE_SIGILS)) return;

        CompoundTag sigils = tag.getCompound(PhaseAnvilBlockEntity.TAG_PHASE_SIGILS);
        if (sigils.isEmpty()) return;

        // 空行を追加
        event.getToolTip().add(Component.empty());
        
        // ヘッダー
        event.getToolTip().add(Component.translatable("tooltip.aster_risk.phase_sigils")
            .withStyle(ChatFormatting.LIGHT_PURPLE));

        // 各刻印を表示
        for (PhaseSigilItem.MoonPhase phase : PhaseSigilItem.MoonPhase.values()) {
            int level = sigils.getInt(phase.getName());
            if (level > 0) {
                MutableComponent line = Component.literal("  ")
                    .append(Component.translatable("tooltip.aster_risk.phase." + phase.getName())
                        .withStyle(getPhaseColor(phase)))
                    .append(Component.literal(" " + getLevelText(level))
                        .withStyle(ChatFormatting.WHITE))
                    .append(Component.literal(" - ")
                        .withStyle(ChatFormatting.GRAY))
                    .append(getEffectText(phase, level));
                
                event.getToolTip().add(line);
            }
        }
    }

    private static ChatFormatting getPhaseColor(PhaseSigilItem.MoonPhase phase) {
        return switch (phase) {
            case FULL_MOON -> ChatFormatting.YELLOW;
            case WANING_GIBBOUS -> ChatFormatting.GOLD;
            case LAST_QUARTER -> ChatFormatting.BLUE;
            case WANING_CRESCENT -> ChatFormatting.DARK_PURPLE;
            case NEW_MOON -> ChatFormatting.DARK_GRAY;
            case WAXING_CRESCENT -> ChatFormatting.GREEN;
            case FIRST_QUARTER -> ChatFormatting.AQUA;
            case WAXING_GIBBOUS -> ChatFormatting.WHITE;
        };
    }

    private static String getLevelText(int level) {
        return switch (level) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            default -> String.valueOf(level);
        };
    }

    private static Component getEffectText(PhaseSigilItem.MoonPhase phase, int level) {
        String effectKey = "tooltip.aster_risk.phase_effect." + phase.getName();
        float multiplier = level * 1.0f;
        
        return switch (phase) {
            case FULL_MOON -> Component.literal("+" + (int)(15 * multiplier) + "% ")
                .append(Component.translatable("tooltip.aster_risk.effect.attack"))
                .withStyle(ChatFormatting.RED);
            case WANING_GIBBOUS -> Component.literal("+" + (int)(10 * multiplier) + "% ")
                .append(Component.translatable("tooltip.aster_risk.effect.magic"))
                .withStyle(ChatFormatting.LIGHT_PURPLE);
            case LAST_QUARTER -> Component.literal("+" + (int)(12 * multiplier) + "% ")
                .append(Component.translatable("tooltip.aster_risk.effect.defense"))
                .withStyle(ChatFormatting.BLUE);
            case WANING_CRESCENT -> Component.literal("+" + (int)(8 * multiplier) + "% ")
                .append(Component.translatable("tooltip.aster_risk.effect.mana_regen"))
                .withStyle(ChatFormatting.AQUA);
            case NEW_MOON -> Component.translatable("tooltip.aster_risk.effect.stealth")
                .append(Component.literal(" " + getLevelText(level)))
                .withStyle(ChatFormatting.DARK_GRAY);
            case WAXING_CRESCENT -> Component.literal("+" + (int)(10 * multiplier) + "% ")
                .append(Component.translatable("tooltip.aster_risk.effect.speed"))
                .withStyle(ChatFormatting.GREEN);
            case FIRST_QUARTER -> Component.literal("+" + (int)(15 * multiplier) + "% ")
                .append(Component.translatable("tooltip.aster_risk.effect.mining"))
                .withStyle(ChatFormatting.AQUA);
            case WAXING_GIBBOUS -> Component.literal("+" + (int)(10 * multiplier) + "% ")
                .append(Component.translatable("tooltip.aster_risk.effect.xp"))
                .withStyle(ChatFormatting.GREEN);
        };
    }
}
