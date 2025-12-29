package net.mcreator.asterrisk.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

/**
 * 強化金インゴット - 錬金術で作成
 */
public class EnhancedGoldItem extends Item {
    public EnhancedGoldItem() {
        super(new Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
