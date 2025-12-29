package net.mcreator.asterrisk.item.armor;

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
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§7[Silver Set]"));
        tooltip.add(Component.literal("§e  +25% damage to undead"));
        tooltip.add(Component.literal("§8[Full Set] §eSmite aura (damages nearby undead)"));
    }
}
