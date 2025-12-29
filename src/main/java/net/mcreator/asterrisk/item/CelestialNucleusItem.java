package net.mcreator.asterrisk.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

/**
 * 天体の核 - 錬金術で作成する最上位素材
 */
public class CelestialNucleusItem extends Item {
    public CelestialNucleusItem() {
        super(new Properties().stacksTo(16).rarity(Rarity.EPIC));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
