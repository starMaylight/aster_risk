package net.mcreator.asterrisk.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

/**
 * 虹色の隕石 - 流星召喚で入手できるレア素材
 */
public class PrismaticMeteoriteItem extends Item {
    public PrismaticMeteoriteItem() {
        super(new Properties().stacksTo(64).rarity(Rarity.EPIC));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true; // キラキラエフェクト
    }
}
