package net.mcreator.asterrisk.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 儀式レシピの検索ヘルパー
 */
public class RitualRecipeManager {

    /**
     * 入力アイテムリストにマッチするレシピを検索
     */
    @Nullable
    public static RitualRecipe findRecipe(Level level, List<ItemStack> inputs) {
        if (level == null) return null;

        RecipeManager recipeManager = level.getRecipeManager();
        List<RitualRecipe> allRecipes = recipeManager.getAllRecipesFor(ModRecipes.RITUAL_TYPE.get());

        for (RitualRecipe recipe : allRecipes) {
            if (recipe.matches(inputs)) {
                return recipe;
            }
        }
        return null;
    }

    /**
     * 全ての儀式レシピを取得
     */
    public static List<RitualRecipe> getAllRecipes(Level level) {
        if (level == null) return List.of();
        return level.getRecipeManager().getAllRecipesFor(ModRecipes.RITUAL_TYPE.get());
    }
}
