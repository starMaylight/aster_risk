package net.mcreator.asterrisk.item;

import net.mcreator.asterrisk.util.TooltipHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 不安定な核 - Unstable Core
 * 太陽召喚チェーンの第一段階。儀式陣のSUNパターンで作成
 */
public class UnstableCoreItem extends Item {

    public UnstableCoreItem() {
        super(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.EPIC)
            .fireResistant());
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        TooltipHelper.addInfo(tooltip, ChatFormatting.GOLD, "tooltip.aster_risk.unstable_core.flavor1");
        TooltipHelper.addInfo(tooltip, ChatFormatting.RED, "tooltip.aster_risk.unstable_core.flavor2");
    }
}
