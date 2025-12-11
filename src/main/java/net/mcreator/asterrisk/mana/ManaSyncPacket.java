package net.mcreator.asterrisk.mana;

import net.mcreator.asterrisk.AsterRiskMod;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * サーバーからクライアントへの魔力同期パケット
 */
public class ManaSyncPacket {
    private final float mana;
    private final float maxMana;

    public ManaSyncPacket(float mana, float maxMana) {
        this.mana = mana;
        this.maxMana = maxMana;
    }

    public ManaSyncPacket(FriendlyByteBuf buf) {
        this.mana = buf.readFloat();
        this.maxMana = buf.readFloat();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeFloat(mana);
        buf.writeFloat(maxMana);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                // クライアント側で魔力を更新
                if (Minecraft.getInstance().player != null) {
                    Minecraft.getInstance().player.getCapability(LunarManaCapability.LUNAR_MANA).ifPresent(manaCap -> {
                        manaCap.setMaxMana(maxMana);
                        manaCap.setMana(mana);
                    });
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }

    /**
     * パケットを登録するためのメソッド
     */
    public static void register() {
        AsterRiskMod.addNetworkMessage(
            ManaSyncPacket.class,
            ManaSyncPacket::encode,
            ManaSyncPacket::new,
            ManaSyncPacket::handle
        );
    }
}
