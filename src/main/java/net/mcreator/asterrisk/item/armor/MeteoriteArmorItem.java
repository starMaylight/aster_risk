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
 * 隕石の防具アイテム - 攻撃特化
 */
public class MeteoriteArmorItem extends ArmorItem {
    
    private final String customName;

    public MeteoriteArmorItem(Type type, Properties properties, String customName) {
        super(MeteoriteArmorMaterial.INSTANCE, type, properties);
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
        tooltip.add(Component.literal("§c☄ Meteorite Armor Set"));
        tooltip.add(Component.literal("§7Full set bonus:"));
        tooltip.add(Component.literal("§b +15 Max Mana"));
        tooltip.add(Component.literal("§c +3 Mana on hit"));
    }

    /**
     * プレイヤーが隕石セットをフル装備しているかチェック
     */
    public static boolean hasFullSet(Player player) {
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        
        return helmet.getItem() == AsterRiskModItems.METEORITE_HELMET.get()
            && chest.getItem() == AsterRiskModItems.METEORITE_CHESTPLATE.get()
            && legs.getItem() == AsterRiskModItems.METEORITE_LEGGINGS.get()
            && boots.getItem() == AsterRiskModItems.METEORITE_BOOTS.get();
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
