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
 * 太陽の欠片 - Sun Fragment
 * 陽の化身のドロップ。太陽の剣の素材となる。
 */
public class SunFragmentItem extends Item {

    public SunFragmentItem() {
        super(new Item.Properties()
            .stacksTo(16)
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
        TooltipHelper.addInfo(tooltip, ChatFormatting.GOLD, "tooltip.aster_risk.sun_fragment.flavor1");
        TooltipHelper.addInfo(tooltip, ChatFormatting.YELLOW, "tooltip.aster_risk.sun_fragment.flavor2");
    }
}
