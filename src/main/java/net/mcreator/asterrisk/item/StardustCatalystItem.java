package net.mcreator.asterrisk.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

/**
 * 星屑触媒 - 錬金術の効率を上げる消耗品
 */
public class StardustCatalystItem extends Item {
    public StardustCatalystItem() {
        super(new Properties().stacksTo(64).rarity(Rarity.UNCOMMON));
    }
}
