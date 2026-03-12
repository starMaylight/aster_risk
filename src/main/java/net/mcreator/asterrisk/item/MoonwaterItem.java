package net.mcreator.asterrisk.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BucketItem;

import net.mcreator.asterrisk.registry.ModFluids;

public class MoonwaterItem extends BucketItem {
	public MoonwaterItem() {
		super(ModFluids.MOONWATER, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).rarity(Rarity.COMMON));
	}
}