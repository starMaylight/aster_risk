package net.mcreator.asterrisk.item.tool;

import net.mcreator.asterrisk.util.TooltipHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 銀のツールセット
 * エンチャント適性が高い
 */
public class SilverToolItems {

    private static void appendSilverTooltip(List<Component> tooltip) {
        TooltipHelper.addStat(tooltip, ChatFormatting.GRAY, "tooltip.aster_risk.silver_tool.enchantability");
    }

    public static class SilverSword extends SwordItem {
        public SilverSword(Properties properties) {
            super(ModToolTiers.SILVER, 3, -2.4f, properties);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable net.minecraft.world.level.Level level,
                                    List<Component> tooltip, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltip, flag);
            appendSilverTooltip(tooltip);
        }
    }

    public static class SilverPickaxe extends PickaxeItem {
        public SilverPickaxe(Properties properties) {
            super(ModToolTiers.SILVER, 1, -2.8f, properties);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable net.minecraft.world.level.Level level,
                                    List<Component> tooltip, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltip, flag);
            appendSilverTooltip(tooltip);
        }
    }

    public static class SilverAxe extends AxeItem {
        public SilverAxe(Properties properties) {
            super(ModToolTiers.SILVER, 6.0f, -3.1f, properties);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable net.minecraft.world.level.Level level,
                                    List<Component> tooltip, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltip, flag);
            appendSilverTooltip(tooltip);
        }
    }

    public static class SilverShovel extends ShovelItem {
        public SilverShovel(Properties properties) {
            super(ModToolTiers.SILVER, 1.5f, -3.0f, properties);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable net.minecraft.world.level.Level level,
                                    List<Component> tooltip, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltip, flag);
            appendSilverTooltip(tooltip);
        }
    }

    public static class SilverHoe extends HoeItem {
        public SilverHoe(Properties properties) {
            super(ModToolTiers.SILVER, -2, -1.0f, properties);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable net.minecraft.world.level.Level level,
                                    List<Component> tooltip, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltip, flag);
            appendSilverTooltip(tooltip);
        }
    }
}
