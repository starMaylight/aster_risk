package net.mcreator.asterrisk.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

/**
 * 輝くダイヤモンド - 錬金術で作成
 */
public class RadiantDiamondItem extends Item {
    public RadiantDiamondItem() {
        super(new Properties().stacksTo(64).rarity(Rarity.RARE));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
