package net.mcreator.asterrisk.network;

import net.mcreator.asterrisk.AsterRiskMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * 共振器リンク用のネットワークハンドラ
 */
public class AsterRiskNetwork {

    private static final String PROTOCOL_VERSION = "1";
    
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        ResourceLocation.fromNamespaceAndPath(AsterRiskMod.MODID, "main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        CHANNEL.registerMessage(packetId++, ResonatorLinkPacket.class,
            ResonatorLinkPacket::encode,
            ResonatorLinkPacket::decode,
            ResonatorLinkPacket::handle);
        
        CHANNEL.registerMessage(packetId++, BlockManaSyncPacket.class,
            BlockManaSyncPacket::encode,
            BlockManaSyncPacket::decode,
            BlockManaSyncPacket::handle);
    }

    public static void sendToServer(Object msg) {
        CHANNEL.sendToServer(msg);
    }

    public static void sendToPlayer(Object msg, ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }

    public static void sendToAllTracking(Object msg, ServerPlayer player) {
        CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), msg);
    }
}
