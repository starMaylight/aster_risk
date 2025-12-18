package net.mcreator.asterrisk.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Stellar Heart - Star Devourerのドロップ
 * 最強装備の素材
 */
public class StellarHeartItem extends Item {
    
    public StellarHeartItem() {
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
        tooltip.add(Component.literal("§6The core of a Star Devourer"));
        tooltip.add(Component.literal("§eRadiating immense stellar power"));
    }
}
