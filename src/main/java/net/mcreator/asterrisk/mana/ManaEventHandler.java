package net.mcreator.asterrisk.mana;

import net.mcreator.asterrisk.AsterRiskMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mcreator.asterrisk.enchantment.EnchantmentEventHandler;

/**
 * 魔力システムのイベントハンドラー
 */
@Mod.EventBusSubscriber(modid = AsterRiskMod.MODID)
public class ManaEventHandler {

    private static final ResourceLocation MANA_CAP = ResourceLocation.fromNamespaceAndPath(AsterRiskMod.MODID, "lunar_mana");

    /**
     * Capabilityの登録
     */
    @Mod.EventBusSubscriber(modid = AsterRiskMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {
        @SubscribeEvent
        public static void registerCapabilities(RegisterCapabilitiesEvent event) {
            event.register(LunarManaCapability.ILunarMana.class);
        }
    }

    /**
     * プレイヤーにCapabilityをアタッチ
     */
    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(LunarManaCapability.LUNAR_MANA).isPresent()) {
                event.addCapability(MANA_CAP, new LunarManaCapability.Provider());
            }
        }
    }

    /**
     * プレイヤーの毎tick処理（魔力回復）
     */
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            event.player.getCapability(LunarManaCapability.LUNAR_MANA).ifPresent(mana -> {
                mana.tick(event.player);
                
                // サーバー側で定期的にクライアントに同期（1秒ごと）
                if (!event.player.level().isClientSide() && event.player instanceof ServerPlayer serverPlayer) {
                    if (event.player.tickCount % 20 == 0) {
                        syncManaToClient(serverPlayer, mana);
                    }
                }
            });
            
            // 月の引力エンチャントによるアイテム吸引（毎tick）
            if (!event.player.level().isClientSide()) {
                EnchantmentEventHandler.handleLunarAttraction(event.player);
            }
        }
    }

    /**
     * プレイヤーが死亡時にCapabilityを保持
     */
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            event.getOriginal().reviveCaps();
            event.getOriginal().getCapability(LunarManaCapability.LUNAR_MANA).ifPresent(oldMana -> {
                event.getEntity().getCapability(LunarManaCapability.LUNAR_MANA).ifPresent(newMana -> {
                    // 死亡時は魔力を半分に
                    newMana.setMaxMana(oldMana.getMaxMana());
                    newMana.setMana(oldMana.getMaxMana() * 0.5f);
                });
            });
            event.getOriginal().invalidateCaps();
        }
    }

    /**
     * プレイヤーログイン時に魔力を同期
     */
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            serverPlayer.getCapability(LunarManaCapability.LUNAR_MANA).ifPresent(mana -> {
                syncManaToClient(serverPlayer, mana);
            });
        }
    }

    /**
     * ディメンション移動時に魔力を同期
     */
    @SubscribeEvent
    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            serverPlayer.getCapability(LunarManaCapability.LUNAR_MANA).ifPresent(mana -> {
                syncManaToClient(serverPlayer, mana);
            });
        }
    }

    /**
     * リスポーン時に魔力を同期
     */
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            serverPlayer.getCapability(LunarManaCapability.LUNAR_MANA).ifPresent(mana -> {
                syncManaToClient(serverPlayer, mana);
            });
        }
    }

    /**
     * クライアントに魔力を同期
     */
    private static void syncManaToClient(ServerPlayer player, LunarManaCapability.ILunarMana mana) {
        AsterRiskMod.PACKET_HANDLER.sendTo(
            new ManaSyncPacket(mana.getMana(), mana.getMaxMana()),
            player.connection.connection,
            net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT
        );
    }
}
