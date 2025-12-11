package net.mcreator.asterrisk.item.armor;

import net.mcreator.asterrisk.init.AsterRiskModItems;
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
    
    private final String customName;

    public StellarArmorItem(Type type, Properties properties, String customName) {
        super(StellarArmorMaterial.INSTANCE, type, properties);
        this.customName = customName;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.literal(customName);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§d✦ Stellar Armor Set"));
        tooltip.add(Component.literal("§7Full set bonus:"));
        tooltip.add(Component.literal("§b +50 Max Mana"));
        tooltip.add(Component.literal("§b -20% Spell Cost"));
    }

    /**
     * プレイヤーが星屑セットをフル装備しているかチェック
     */
    public static boolean hasFullSet(Player player) {
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        
        return helmet.getItem() == AsterRiskModItems.STELLAR_CROWN.get()
            && chest.getItem() == AsterRiskModItems.STELLAR_ROBE.get()
            && legs.getItem() == AsterRiskModItems.STELLAR_LEGGINGS.get()
            && boots.getItem() == AsterRiskModItems.STELLAR_BOOTS.get();
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
