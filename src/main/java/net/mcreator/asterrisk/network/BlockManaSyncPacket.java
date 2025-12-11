package net.mcreator.asterrisk.network;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.mcreator.asterrisk.block.entity.LunarCollectorBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * ブロックマナ同期用パケット
 */
public class BlockManaSyncPacket {

    private final BlockPos pos;
    private final float mana;
    private final float maxMana;

    public BlockManaSyncPacket(BlockPos pos, float mana, float maxMana) {
        this.pos = pos;
        this.mana = mana;
        this.maxMana = maxMana;
    }

    public static void encode(BlockManaSyncPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeFloat(msg.mana);
        buf.writeFloat(msg.maxMana);
    }

    public static BlockManaSyncPacket decode(FriendlyByteBuf buf) {
        return new BlockManaSyncPacket(buf.readBlockPos(), buf.readFloat(), buf.readFloat());
    }

    public static void handle(BlockManaSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handleClient(msg));
        });
        ctx.get().setPacketHandled(true);
    }

    private static void handleClient(BlockManaSyncPacket msg) {
        Level level = Minecraft.getInstance().level;
        if (level != null) {
            BlockEntity be = level.getBlockEntity(msg.pos);
            if (be instanceof LunarCollectorBlockEntity collector) {
                collector.setClientMana(msg.mana, msg.maxMana);
            }
        }
    }
}
