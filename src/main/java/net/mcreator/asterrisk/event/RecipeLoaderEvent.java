package net.mcreator.asterrisk.event;

import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.recipe.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * カスタムレシピのローダーイベント
 * データパックからレシピを読み込み、各マネージャに登録
 */
@Mod.EventBusSubscriber(modid = AsterRiskMod.MODID)
public class RecipeLoaderEvent {
    
    /**
     * クライアント側でレシピが更新された時（サーバー同期後）
     */
    @SubscribeEvent
    public static void onRecipesUpdated(RecipesUpdatedEvent event) {
        loadAllRecipes(event.getRecipeManager());
    }
    
    /**
     * 全てのカスタムレシピをマネージャに登録
     */
    public static void loadAllRecipes(RecipeManager recipeManager) {
        // クリア
        RitualCircleRecipeManager.clearRecipes();
        FocusChamberRecipeManager.clearRecipes();
        CelestialEnchantRecipeManager.clearRecipes();
        
        // 魔法陣レシピ
        recipeManager.getAllRecipesFor(ModRecipes.RITUAL_CIRCLE_TYPE.get()).forEach(recipe -> {
            RitualCircleRecipeManager.registerRecipe(recipe);
            AsterRiskMod.LOGGER.debug("Loaded ritual circle recipe: {}", recipe.getId());
        });
        
        // 集光チャンバーレシピ
        recipeManager.getAllRecipesFor(ModRecipes.FOCUS_CHAMBER_TYPE.get()).forEach(recipe -> {
            FocusChamberRecipeManager.registerRecipe(recipe);
            AsterRiskMod.LOGGER.debug("Loaded focus chamber recipe: {}", recipe.getId());
        });
        
        // 天体エンチャントレシピ
        recipeManager.getAllRecipesFor(ModRecipes.CELESTIAL_ENCHANT_TYPE.get()).forEach(recipe -> {
            CelestialEnchantRecipeManager.registerRecipe(recipe);
            AsterRiskMod.LOGGER.debug("Loaded celestial enchant recipe: {}", recipe.getId());
        });
        
        AsterRiskMod.LOGGER.info("Loaded {} ritual circle, {} focus chamber, {} celestial enchant recipes",
            RitualCircleRecipeManager.getAllRecipes().size(),
            FocusChamberRecipeManager.getAllRecipes().size(),
            CelestialEnchantRecipeManager.getAllRecipes().size());
    }
}
