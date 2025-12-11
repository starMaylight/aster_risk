package net.mcreator.asterrisk.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 共振器リンク用パケット
 */
public class ResonatorLinkPacket {

    private final BlockPos pos1;
    private final BlockPos pos2;
    private final boolean link; // true=リンク, false=リンク解除

    public ResonatorLinkPacket(BlockPos pos1, BlockPos pos2, boolean link) {
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.link = link;
    }

    public static void encode(ResonatorLinkPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos1);
        buf.writeBlockPos(msg.pos2);
        buf.writeBoolean(msg.link);
    }

    public static ResonatorLinkPacket decode(FriendlyByteBuf buf) {
        return new ResonatorLinkPacket(buf.readBlockPos(), buf.readBlockPos(), buf.readBoolean());
    }

    public static void handle(ResonatorLinkPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // サーバー側でリンク処理
            // ResonatorBlockEntityで処理
        });
        ctx.get().setPacketHandled(true);
    }
}
