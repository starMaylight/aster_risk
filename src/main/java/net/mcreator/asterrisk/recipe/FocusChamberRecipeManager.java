package net.mcreator.asterrisk.recipe;

import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.*;

/**
 * 集光チャンバーレシピマネージャ
 */
public class FocusChamberRecipeManager {
    
    private static final List<FocusChamberRecipe> allRecipes = new ArrayList<>();
    
    public static void registerRecipe(FocusChamberRecipe recipe) {
        allRecipes.add(recipe);
    }
    
    public static void clearRecipes() {
        allRecipes.clear();
    }
    
    @Nullable
    public static FocusChamberRecipe findRecipe(List<ItemStack> items) {
        for (FocusChamberRecipe recipe : allRecipes) {
            if (recipe.matches(items)) {
                return recipe;
            }
        }
        return null;
    }
    
    public static List<FocusChamberRecipe> getAllRecipes() {
        return Collections.unmodifiableList(allRecipes);
    }
}
