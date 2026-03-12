package net.mcreator.asterrisk;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.FriendlyByteBuf;

import net.mcreator.asterrisk.world.structure.ModStructures;
import net.mcreator.asterrisk.world.structure.ModStructurePieces;
import net.mcreator.asterrisk.registry.ModSpawnEggs;
import net.mcreator.asterrisk.registry.ModSounds;
import net.mcreator.asterrisk.registry.ModPotions;
import net.mcreator.asterrisk.registry.ModParticles;
import net.mcreator.asterrisk.registry.ModEntities;
import net.mcreator.asterrisk.registry.ModEnchantments;
import net.mcreator.asterrisk.registry.ModBlocks;
import net.mcreator.asterrisk.registry.ModItems;
import net.mcreator.asterrisk.registry.ModEffects;
import net.mcreator.asterrisk.registry.ModFluids;
import net.mcreator.asterrisk.registry.ModTabs;
import net.mcreator.asterrisk.recipe.ModRecipes;
import net.mcreator.asterrisk.network.AsterRiskNetwork;

import java.util.function.Supplier;
import java.util.function.Function;
import java.util.function.BiConsumer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.AbstractMap;

@Mod("aster_risk")
public class AsterRiskMod {
	public static final Logger LOGGER = LogManager.getLogger(AsterRiskMod.class);
	public static final String MODID = "aster_risk";

	public AsterRiskMod(FMLJavaModLoadingContext context) {
		IEventBus bus = context.getModEventBus();

		net.mcreator.asterrisk.registry.ModBlockEntities.REGISTRY.register(bus);
		ModRecipes.RECIPE_TYPES.register(bus);
		ModRecipes.RECIPE_SERIALIZERS.register(bus);
		ModEffects.REGISTRY.register(bus);
		ModPotions.REGISTRY.register(bus);
		ModEnchantments.REGISTRY.register(bus);
		ModEntities.REGISTRY.register(bus);
		ModSpawnEggs.REGISTRY.register(bus);
		ModSounds.REGISTRY.register(bus);
		ModParticles.REGISTRY.register(bus);
		ModStructures.register(bus);
		ModStructurePieces.register(bus);
		ModBlocks.register(bus);
		ModItems.REGISTRY.register(bus);
		ModTabs.REGISTRY.register(bus);
		ModFluids.FLUIDS.register(bus);
		ModFluids.FLUID_TYPES.register(bus);
		net.mcreator.asterrisk.entity.ModEntityTypes.REGISTRY.register(bus);
		net.mcreator.asterrisk.config.AsterRiskConfig.register();

		MinecraftForge.EVENT_BUS.register(this);

		net.mcreator.asterrisk.mana.ManaSyncPacket.register();
		AsterRiskNetwork.register();
	}

	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(ResourceLocation.fromNamespaceAndPath(MODID, MODID), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
	private static int messageID = 0;

	public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
		PACKET_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer);
		messageID++;
	}

	private static final Collection<AbstractMap.SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

	public static void queueServerWork(int tick, Runnable action) {
		if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER)
			workQueue.add(new AbstractMap.SimpleEntry<>(action, tick));
	}

	@SubscribeEvent
	public void tick(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			List<AbstractMap.SimpleEntry<Runnable, Integer>> actions = new ArrayList<>();
			workQueue.forEach(work -> {
				work.setValue(work.getValue() - 1);
				if (work.getValue() == 0)
					actions.add(work);
			});
			actions.forEach(e -> e.getKey().run());
			workQueue.removeAll(actions);
		}
	}
}
