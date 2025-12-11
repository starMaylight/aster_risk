/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.asterrisk.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;

import net.mcreator.asterrisk.AsterRiskMod;

public class AsterRiskModTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AsterRiskMod.MODID);
	public static final RegistryObject<CreativeModeTab> ASTER_RISK = REGISTRY.register("aster_risk",
			() -> CreativeModeTab.builder().title(Component.translatable("item_group.aster_risk.aster_risk")).icon(() -> new ItemStack(AsterRiskModItems.MOONSTONE.get())).displayItems((parameters, tabData) -> {
				tabData.accept(AsterRiskModItems.MOONSTONE.get());
				tabData.accept(AsterRiskModBlocks.MOONSTONE_ORE.get().asItem());
				tabData.accept(AsterRiskModItems.RAW_SILVER.get());
				tabData.accept(AsterRiskModItems.SILVER_INGOT.get());
				tabData.accept(AsterRiskModItems.STARFLAGMENT.get());
				tabData.accept(AsterRiskModItems.STARDUST.get());
				tabData.accept(AsterRiskModItems.LUNAR_DUST.get());
				tabData.accept(AsterRiskModItems.METEORITE_FRAGMENT.get());
				tabData.accept(AsterRiskModBlocks.METEORITE_ORE.get().asItem());
				tabData.accept(AsterRiskModBlocks.SILVER_ORE.get().asItem());
				tabData.accept(AsterRiskModBlocks.MOONSTONE_BLOCK.get().asItem());
				tabData.accept(AsterRiskModBlocks.SILVER_BLOCK.get().asItem());
				tabData.accept(AsterRiskModBlocks.STARDUST_BLOCK.get().asItem());
				tabData.accept(AsterRiskModBlocks.METEORITE_BLOCK.get().asItem());
				tabData.accept(AsterRiskModBlocks.MOONSTONE_BRICKS.get().asItem());
				tabData.accept(AsterRiskModBlocks.POLISHED_MOONSTONE.get().asItem());
				tabData.accept(AsterRiskModBlocks.MOONSTONE_TILES.get().asItem());
				tabData.accept(AsterRiskModBlocks.CRACKED_MOONSTONE_BRICKS.get().asItem());
				tabData.accept(AsterRiskModBlocks.MOSSY_MOONSTONE_BRICKS.get().asItem());
				tabData.accept(AsterRiskModBlocks.SILVER_BRICKS.get().asItem());
				tabData.accept(AsterRiskModBlocks.CHISELED_MOONSTONE.get().asItem());
				tabData.accept(AsterRiskModBlocks.STARRY_GLASS.get().asItem());
				tabData.accept(AsterRiskModBlocks.LUNAR_PILLAR.get().asItem());
				tabData.accept(AsterRiskModBlocks.CELESTIAL_TILE.get().asItem());
				tabData.accept(AsterRiskModBlocks.MOONLIGHT_LANTERN.get().asItem());
			}).build());
}