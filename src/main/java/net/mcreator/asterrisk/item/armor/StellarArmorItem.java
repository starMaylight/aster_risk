package net.mcreator.asterrisk.item.armor;

import net.mcreator.asterrisk.registry.ModItems;
import net.mcreator.asterrisk.util.TooltipHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 星屑の防具アイテム - 魔法特化
 */
public class StellarArmorItem extends ArmorItem {

    public StellarArmorItem(Type type, Properties properties) {
        super(StellarArmorMaterial.INSTANCE, type, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        TooltipHelper.addBlank(tooltip);
        TooltipHelper.addHeader(tooltip, ChatFormatting.LIGHT_PURPLE, "tooltip.aster_risk.stellar_armor.header");
        TooltipHelper.addDescription(tooltip, "tooltip.aster_risk.set_bonus");
        TooltipHelper.addStat(tooltip, ChatFormatting.AQUA, "tooltip.aster_risk.stellar_armor.bonus_mana");
        TooltipHelper.addStat(tooltip, ChatFormatting.AQUA, "tooltip.aster_risk.stellar_armor.bonus_cost");
    }

    /**
     * プレイヤーが星屑セットをフル装備しているかチェック
     */
    public static boolean hasFullSet(Player player) {
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        
        return helmet.getItem() == ModItems.STELLAR_CROWN.get()
            && chest.getItem() == ModItems.STELLAR_ROBE.get()
            && legs.getItem() == ModItems.STELLAR_LEGGINGS.get()
            && boots.getItem() == ModItems.STELLAR_BOOTS.get();
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
