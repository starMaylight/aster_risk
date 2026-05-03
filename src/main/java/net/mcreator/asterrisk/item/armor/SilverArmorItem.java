package net.mcreator.asterrisk.item.armor;

import net.mcreator.asterrisk.util.TooltipHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 銀の防具
 * セットボーナス: アンデッド特効（ArmorSetBonusHandlerで処理）
 */
public class SilverArmorItem extends ArmorItem {

    public static final String SET_ID = "silver";

    public SilverArmorItem(Type type, Properties properties) {
        super(SilverArmorMaterial.INSTANCE, type, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        TooltipHelper.addBlank(tooltip);
        TooltipHelper.addHeader(tooltip, ChatFormatting.GRAY, "tooltip.aster_risk.silver_armor.header");
        TooltipHelper.addStat(tooltip, ChatFormatting.YELLOW, "tooltip.aster_risk.silver_armor.bonus_undead");
        TooltipHelper.addDescription(tooltip, "tooltip.aster_risk.silver_armor.set_bonus");
    }
}
