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

import net.mcreator.asterrisk.registry.ModSpawnEggs;
import net.mcreator.asterrisk.registry.ModEntities;
import net.mcreator.asterrisk.registry.ModEnchantments;
import net.mcreator.asterrisk.recipe.ModRecipes;
import net.mcreator.asterrisk.network.AsterRiskNetwork;
import net.mcreator.asterrisk.init.AsterRiskModTabs;
import net.mcreator.asterrisk.init.AsterRiskModItems;
import net.mcreator.asterrisk.init.AsterRiskModFluids;
import net.mcreator.asterrisk.init.AsterRiskModFluidTypes;
import net.mcreator.asterrisk.init.AsterRiskModEffects;
import net.mcreator.asterrisk.init.AsterRiskModBlocks;

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
		// Start of user code block mod constructor
		// BlockEntity登録（この行はuser code block内に配置）
		net.mcreator.asterrisk.registry.ModBlockEntities.REGISTRY.register(context.getModEventBus());
		// カスタムレシピ登録
		ModRecipes.RECIPE_TYPES.register(context.getModEventBus());
		ModRecipes.RECIPE_SERIALIZERS.register(context.getModEventBus());
		// エフェクト登録（initフォルダのものを使用）
		AsterRiskModEffects.REGISTRY.register(context.getModEventBus());
		// エンチャント登録（registryパッケージを使用）
		ModEnchantments.REGISTRY.register(context.getModEventBus());
		// エンティティ登録（registryパッケージを使用）
		ModEntities.REGISTRY.register(context.getModEventBus());
		// スポーンエッグ登録
		ModSpawnEggs.REGISTRY.register(context.getModEventBus());
		// End of user code block mod constructor
		MinecraftForge.EVENT_BUS.register(this);
		IEventBus bus = context.getModEventBus();
		AsterRiskModBlocks.REGISTRY.register(bus);
		AsterRiskModItems.REGISTRY.register(bus);
		AsterRiskModTabs.REGISTRY.register(bus);
		AsterRiskModFluids.REGISTRY.register(bus);
		AsterRiskModFluidTypes.REGISTRY.register(bus);
		// Start of user code block mod init
		// プレイヤーマナ同期パケットを登録
		net.mcreator.asterrisk.mana.ManaSyncPacket.register();
		// ネットワーク登録
		AsterRiskNetwork.register();
		// End of user code block mod init
	}

	// Start of user code block mod methods
	// End of user code block mod methods
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