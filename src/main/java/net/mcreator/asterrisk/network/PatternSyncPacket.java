package net.mcreator.asterrisk.network;

import net.mcreator.asterrisk.pattern.FocusPattern;
import net.mcreator.asterrisk.pattern.PatternManager;
import net.mcreator.asterrisk.pattern.PedestalPattern;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * サーバーからクライアントへFocus/Pedestalパターンを同期するパケット
 * JEIカテゴリがマルチプレイ環境でパターン情報を取得できるようにするため
 */
public class PatternSyncPacket {

    private final List<FocusPattern> focusPatterns;
    private final List<PedestalPattern> pedestalPatterns;

    public PatternSyncPacket(Collection<FocusPattern> focusPatterns,
                              Collection<PedestalPattern> pedestalPatterns) {
        this.focusPatterns = new ArrayList<>(focusPatterns);
        this.pedestalPatterns = new ArrayList<>(pedestalPatterns);
    }

    private PatternSyncPacket(List<FocusPattern> focusPatterns,
                               List<PedestalPattern> pedestalPatterns,
                               boolean fromDecode) {
        this.focusPatterns = focusPatterns;
        this.pedestalPatterns = pedestalPatterns;
    }

    public static void encode(PatternSyncPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.focusPatterns.size());
        for (FocusPattern pattern : msg.focusPatterns) {
            writePattern(buf, pattern.getId(), pattern.getName(), pattern.getDescription(), pattern.getPositions());
        }

        buf.writeVarInt(msg.pedestalPatterns.size());
        for (PedestalPattern pattern : msg.pedestalPatterns) {
            writePattern(buf, pattern.getId(), pattern.getName(), pattern.getDescription(), pattern.getPositions());
        }
    }

    public static PatternSyncPacket decode(FriendlyByteBuf buf) {
        int focusCount = buf.readVarInt();
        List<FocusPattern> focusPatterns = new ArrayList<>(focusCount);
        for (int i = 0; i < focusCount; i++) {
            ResourceLocation id = buf.readResourceLocation();
            String name = buf.readUtf();
            String description = buf.readUtf();
            List<BlockPos> positions = readPositions(buf);
            focusPatterns.add(new FocusPattern(id, name, description, positions));
        }

        int pedestalCount = buf.readVarInt();
        List<PedestalPattern> pedestalPatterns = new ArrayList<>(pedestalCount);
        for (int i = 0; i < pedestalCount; i++) {
            ResourceLocation id = buf.readResourceLocation();
            String name = buf.readUtf();
            String description = buf.readUtf();
            List<BlockPos> positions = readPositions(buf);
            pedestalPatterns.add(new PedestalPattern(id, name, description, positions));
        }

        return new PatternSyncPacket(focusPatterns, pedestalPatterns, true);
    }

    private static void writePattern(FriendlyByteBuf buf, ResourceLocation id, String name,
                                      String description, List<BlockPos> positions) {
        buf.writeResourceLocation(id);
        buf.writeUtf(name);
        buf.writeUtf(description);
        buf.writeVarInt(positions.size());
        for (BlockPos pos : positions) {
            buf.writeBlockPos(pos);
        }
    }

    private static List<BlockPos> readPositions(FriendlyByteBuf buf) {
        int count = buf.readVarInt();
        List<BlockPos> positions = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            positions.add(buf.readBlockPos());
        }
        return positions;
    }

    public static void handle(PatternSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            if (context.getDirection().getReceptionSide().isClient()) {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handleClient(msg));
            }
        });
        context.setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClient(PatternSyncPacket msg) {
        PatternManager.getInstance().clearAndReloadFromSync(msg.focusPatterns, msg.pedestalPatterns);
    }
}
