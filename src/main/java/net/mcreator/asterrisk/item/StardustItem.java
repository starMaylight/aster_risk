package net.mcreator.asterrisk.item;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;

import net.mcreator.asterrisk.procedures.StardustYoukuritukusitatokiProcedure;

public class StardustItem extends Item {
	public StardustItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		ItemStack itemstack = entity.getItemInHand(hand);
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		StardustYoukuritukusitatokiProcedure.execute(entity, itemstack);
		return ar;
	}
}
