package net.mcreator.asterrisk.item.armor;

import net.mcreator.asterrisk.util.TooltipHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 虚空の防具
 * セットボーナス: ダメージ無効化（ArmorSetBonusHandlerで処理）
 */
public class VoidArmorItem extends ArmorItem {

    public static final String SET_ID = "void";

    public VoidArmorItem(Type type, Properties properties) {
        super(VoidArmorMaterial.INSTANCE, type, properties.rarity(Rarity.RARE));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        TooltipHelper.addBlank(tooltip);
        TooltipHelper.addHeader(tooltip, ChatFormatting.DARK_PURPLE, "tooltip.aster_risk.void_armor.header");
        TooltipHelper.addStat(tooltip, ChatFormatting.LIGHT_PURPLE, "tooltip.aster_risk.void_armor.bonus_negate");
        TooltipHelper.addDescription(tooltip, "tooltip.aster_risk.void_armor.set_bonus");
    }
}
