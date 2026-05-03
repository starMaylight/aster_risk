package net.mcreator.asterrisk.item.tool;

import net.mcreator.asterrisk.util.TooltipHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 月光石のツールセット
 * 夜間に性能がアップ
 */
public class MoonstoneToolItems {

    private static boolean isNight(Level level) {
        if (level == null) return false;
        long dayTime = level.getDayTime() % 24000;
        return dayTime >= 13000 && dayTime <= 23000;
    }

    public static class MoonstoneSword extends SwordItem {
        public MoonstoneSword(Properties properties) {
            super(ModToolTiers.MOONSTONE, 3, -2.4f, properties);
        }

        @Override
        public float getDamage() {
            return super.getDamage();
        }

        @Override
        public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
            // 夜間は追加ダメージ
            if (isNight(attacker.level())) {
                target.hurt(attacker.damageSources().magic(), 3.0f);
            }
            return super.hurtEnemy(stack, target, attacker);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level,
                                    List<Component> tooltip, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltip, flag);
            TooltipHelper.addBlank(tooltip);
            TooltipHelper.addHeader(tooltip, ChatFormatting.AQUA, "tooltip.aster_risk.moonstone_tool.weapon_header");
            TooltipHelper.addStat(tooltip, ChatFormatting.GRAY, "tooltip.aster_risk.moonstone_tool.sword_bonus");
        }
    }

    public static class MoonstonePickaxe extends PickaxeItem {
        public MoonstonePickaxe(Properties properties) {
            super(ModToolTiers.MOONSTONE, 1, -2.8f, properties);
        }

        @Override
        public float getDestroySpeed(ItemStack stack, net.minecraft.world.level.block.state.BlockState state) {
            float baseSpeed = super.getDestroySpeed(stack, state);
            // 実際のチェックはプレイヤーのレベルが必要なため、イベントで処理
            return baseSpeed;
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level,
                                    List<Component> tooltip, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltip, flag);
            TooltipHelper.addBlank(tooltip);
            TooltipHelper.addHeader(tooltip, ChatFormatting.AQUA, "tooltip.aster_risk.moonstone_tool.tool_header");
            TooltipHelper.addStat(tooltip, ChatFormatting.GRAY, "tooltip.aster_risk.moonstone_tool.pickaxe_bonus");
        }
    }

    public static class MoonstoneAxe extends AxeItem {
        public MoonstoneAxe(Properties properties) {
            super(ModToolTiers.MOONSTONE, 5.0f, -3.0f, properties);
        }

        @Override
        public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
            if (isNight(attacker.level())) {
                target.hurt(attacker.damageSources().magic(), 2.0f);
            }
            return super.hurtEnemy(stack, target, attacker);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level,
                                    List<Component> tooltip, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltip, flag);
            TooltipHelper.addBlank(tooltip);
            TooltipHelper.addHeader(tooltip, ChatFormatting.AQUA, "tooltip.aster_risk.moonstone_tool.tool_header");
            TooltipHelper.addStat(tooltip, ChatFormatting.GRAY, "tooltip.aster_risk.moonstone_tool.axe_bonus");
        }
    }

    public static class MoonstoneShovel extends ShovelItem {
        public MoonstoneShovel(Properties properties) {
            super(ModToolTiers.MOONSTONE, 1.5f, -3.0f, properties);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level,
                                    List<Component> tooltip, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltip, flag);
            TooltipHelper.addBlank(tooltip);
            TooltipHelper.addHeader(tooltip, ChatFormatting.AQUA, "tooltip.aster_risk.moonstone_tool.tool_header");
            TooltipHelper.addStat(tooltip, ChatFormatting.GRAY, "tooltip.aster_risk.moonstone_tool.shovel_bonus");
        }
    }

    public static class MoonstoneHoe extends HoeItem {
        public MoonstoneHoe(Properties properties) {
            super(ModToolTiers.MOONSTONE, -3, 0.0f, properties);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level,
                                    List<Component> tooltip, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltip, flag);
            TooltipHelper.addBlank(tooltip);
            TooltipHelper.addHeader(tooltip, ChatFormatting.AQUA, "tooltip.aster_risk.moonstone_tool.tool_header");
            TooltipHelper.addStat(tooltip, ChatFormatting.GRAY, "tooltip.aster_risk.moonstone_tool.hoe_bonus");
        }
    }
}
