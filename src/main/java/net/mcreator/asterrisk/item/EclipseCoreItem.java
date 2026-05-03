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
 * Eclipse Core - Eclipse Monarchのドロップ
 * 最強装備の素材
 */
public class EclipseCoreItem extends Item {

    public EclipseCoreItem() {
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
        TooltipHelper.addInfo(tooltip, ChatFormatting.DARK_PURPLE, "tooltip.aster_risk.eclipse_core.flavor1");
        TooltipHelper.addInfo(tooltip, ChatFormatting.DARK_GRAY, "tooltip.aster_risk.eclipse_core.flavor2");
    }
}
