package net.mcreator.asterrisk.network;

import net.mcreator.asterrisk.recipe.ExclusiveEnchantRecipeManager;
import net.mcreator.asterrisk.recipe.ExclusiveEnchantRecipeManager.ExclusiveEnchantData;
import net.mcreator.asterrisk.recipe.ExclusiveEnchantRecipeManager.SyncData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * サーバーからクライアントへExclusiveEnchantレシピを同期するパケット
 */
public class ExclusiveEnchantSyncPacket {

    private final List<SyncData> recipes;

    /**
     * サーバー側で送信用に作成
     */
    public ExclusiveEnchantSyncPacket(List<ExclusiveEnchantData> recipes) {
        this.recipes = new ArrayList<>();
        for (ExclusiveEnchantData data : recipes) {
            ResourceLocation enchantmentId = ForgeRegistries.ENCHANTMENTS.getKey(data.enchantment);
            if (enchantmentId != null) {
                this.recipes.add(new SyncData(
                    data.id,
                    enchantmentId,
                    data.pattern,
                    data.baseCost,
                    data.maxLevel,
                    data.itemType,
                    data.description
                ));
            }
        }
    }

    /**
     * デコード用の内部コンストラクタ
     */
    private ExclusiveEnchantSyncPacket(ArrayList<SyncData> recipes, boolean fromDecode) {
        this.recipes = recipes;
    }

    public static void encode(ExclusiveEnchantSyncPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.recipes.size());
        for (SyncData data : msg.recipes) {
            buf.writeResourceLocation(data.id());
            buf.writeResourceLocation(data.enchantmentId());
            buf.writeUtf(data.pattern());
            buf.writeVarInt(data.baseCost());
            buf.writeVarInt(data.maxLevel());
            buf.writeUtf(data.itemType());
            buf.writeUtf(data.description());
        }
    }

    public static ExclusiveEnchantSyncPacket decode(FriendlyByteBuf buf) {
        int count = buf.readVarInt();
        ArrayList<SyncData> recipes = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            recipes.add(new SyncData(
                buf.readResourceLocation(),
                buf.readResourceLocation(),
                buf.readUtf(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readUtf(),
                buf.readUtf()
            ));
        }
        return new ExclusiveEnchantSyncPacket(recipes, true);
    }

    public static void handle(ExclusiveEnchantSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            if (context.getDirection().getReceptionSide().isClient()) {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handleClient(msg));
            }
        });
        context.setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClient(ExclusiveEnchantSyncPacket msg) {
        ExclusiveEnchantRecipeManager.getInstance().clearAndReloadFromSync(msg.recipes);
    }
}
