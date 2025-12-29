package net.mcreator.asterrisk.item.armor;

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
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§5[Void Set]"));
        tooltip.add(Component.literal("§d  10% chance to negate damage"));
        tooltip.add(Component.literal("§8[Full Set] §dPhase shift (brief invulnerability on hit)"));
    }
}
