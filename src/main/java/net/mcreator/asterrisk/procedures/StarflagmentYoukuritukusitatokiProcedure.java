package net.mcreator.asterrisk.procedures;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.mcreator.asterrisk.mana.ManaProcedures;

public class StarflagmentYoukuritukusitatokiProcedure {
	public static void execute(Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		if (entity instanceof Player player) {
			ManaProcedures.fullRestoreMana(player);
			if (!player.isCreative()) {
				itemstack.shrink(1);
			}
		}
	}
}
