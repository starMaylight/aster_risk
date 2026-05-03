package net.mcreator.asterrisk.event;

import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.network.AsterRiskNetwork;
import net.mcreator.asterrisk.network.ExclusiveEnchantSyncPacket;
import net.mcreator.asterrisk.network.PatternSyncPacket;
import net.mcreator.asterrisk.pattern.PatternManager;
import net.mcreator.asterrisk.recipe.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

/**
 * サーバー側でのレシピ読み込み
 */
@Mod.EventBusSubscriber(modid = AsterRiskMod.MODID)
public class ServerRecipeLoaderEvent {
    
    /**
     * データパック同期時（サーバー起動時・リロード時）
     */
    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        if (event.getPlayer() == null) {
            // サーバー全体の同期
            RecipeManager recipeManager = ServerLifecycleHooks.getCurrentServer().getRecipeManager();
            loadServerRecipes(recipeManager);

            // 全プレイヤーにExclusiveEnchantレシピを同期
            ServerLifecycleHooks.getCurrentServer().execute(() -> {
                ExclusiveEnchantSyncPacket packet = new ExclusiveEnchantSyncPacket(
                    ExclusiveEnchantRecipeManager.getInstance().getAllRecipes()
                );
                AsterRiskNetwork.sendToAll(packet);
                AsterRiskNetwork.sendToAll(buildPatternPacket());
                AsterRiskMod.LOGGER.debug("Sent ExclusiveEnchant + pattern data to all players");
            });
        } else {
            // 特定プレイヤーへの同期
            ServerPlayer player = event.getPlayer();
            ExclusiveEnchantSyncPacket packet = new ExclusiveEnchantSyncPacket(
                ExclusiveEnchantRecipeManager.getInstance().getAllRecipes()
            );
            AsterRiskNetwork.sendToPlayer(packet, player);
            AsterRiskNetwork.sendToPlayer(buildPatternPacket(), player);
            AsterRiskMod.LOGGER.debug("Sent ExclusiveEnchant + pattern data to player: {}", player.getName().getString());
        }
    }
    
    /**
     * プレイヤーログイン時
     */
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            RecipeManager recipeManager = ServerLifecycleHooks.getCurrentServer().getRecipeManager();
            loadServerRecipes(recipeManager);

            // ログインしたプレイヤーにExclusiveEnchantレシピとパターンを同期
            ExclusiveEnchantSyncPacket packet = new ExclusiveEnchantSyncPacket(
                ExclusiveEnchantRecipeManager.getInstance().getAllRecipes()
            );
            AsterRiskNetwork.sendToPlayer(packet, player);
            AsterRiskNetwork.sendToPlayer(buildPatternPacket(), player);
            AsterRiskMod.LOGGER.debug("Sent ExclusiveEnchant + pattern data to logged in player: {}", player.getName().getString());
        }
    }

    private static PatternSyncPacket buildPatternPacket() {
        PatternManager manager = PatternManager.getInstance();
        return new PatternSyncPacket(manager.getAllFocusPatterns(), manager.getAllPedestalPatterns());
    }
    
    private static void loadServerRecipes(RecipeManager recipeManager) {
        // 既に読み込み済みならスキップ
        if (!RitualCircleRecipeManager.getAllRecipes().isEmpty()) {
            return;
        }
        
        RecipeLoaderEvent.loadAllRecipes(recipeManager);
    }
    
    /**
     * リソースリロードリスナーの登録
     * ExclusiveEnchantRecipeManagerはSimpleJsonResourceReloadListenerを継承しているため
     * ここで登録すると自動的にJSONを読み込む
     */
    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(ExclusiveEnchantRecipeManager.getInstance());
        AsterRiskMod.LOGGER.info("Registered ExclusiveEnchantRecipeManager as reload listener");
    }
}
