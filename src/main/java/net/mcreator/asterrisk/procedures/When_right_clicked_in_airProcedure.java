package net.mcreator.asterrisk.procedures;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.mcreator.asterrisk.mana.ManaProcedures;

public class When_right_clicked_in_airProcedure {
	public static void execute(Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		if (entity instanceof Player player) {
			ManaProcedures.restoreMana(player, 25f);
			if (!player.isCreative()) {
				itemstack.shrink(1);
			}
		}
	}
}
