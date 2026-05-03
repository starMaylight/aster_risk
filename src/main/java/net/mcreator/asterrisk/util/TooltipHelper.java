package net.mcreator.asterrisk.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

/**
 * Unified tooltip formatting and localization helper.
 *
 * Format convention:
 *   §[accent] [icon] [Title]            - header line (translatable)
 *   §7  [description]                   - description line (translatable, gray)
 *   §[statColor]   [Stat]: [value]      - stat line, indented further (translatable)
 *   ""                                  - leading blank for spacing
 *
 * All static strings should resolve via Component.translatable so lang files
 * can override them. Dynamic numeric values are passed as arguments.
 */
public final class TooltipHelper {

    private TooltipHelper() {}

    public static void addBlank(List<Component> tooltip) {
        tooltip.add(Component.literal(""));
    }

    public static void addHeader(List<Component> tooltip, ChatFormatting color, String key, Object... args) {
        tooltip.add(Component.translatable(key, args).withStyle(color));
    }

    public static void addDescription(List<Component> tooltip, String key, Object... args) {
        tooltip.add(Component.translatable(key, args).withStyle(ChatFormatting.GRAY));
    }

    public static void addStat(List<Component> tooltip, ChatFormatting color, String key, Object... args) {
        MutableComponent indent = Component.literal("  ");
        tooltip.add(indent.append(Component.translatable(key, args)).withStyle(color));
    }

    public static void addInfo(List<Component> tooltip, ChatFormatting color, String key, Object... args) {
        tooltip.add(Component.translatable(key, args).withStyle(color));
    }

    public static String formatNumber(float value) {
        if (value == (int) value) {
            return String.valueOf((int) value);
        }
        return String.format("%.1f", value);
    }

    public static String formatNumber(double value) {
        if (value == (int) value) {
            return String.valueOf((int) value);
        }
        return String.format("%.1f", value);
    }

    public static String formatPercent(float ratio) {
        return ((int) (ratio * 100)) + "%";
    }
}
