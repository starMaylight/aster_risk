package net.mcreator.asterrisk.event;

import net.mcreator.asterrisk.AsterRiskMod;
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
        }
    }
    
    /**
     * プレイヤーログイン時
     */
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            RecipeManager recipeManager = ServerLifecycleHooks.getCurrentServer().getRecipeManager();
            loadServerRecipes(recipeManager);
        }
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
