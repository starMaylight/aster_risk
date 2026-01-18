package net.mcreator.asterrisk.recipe;

import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.*;

/**
 * 天体エンチャントレシピマネージャ
 */
public class CelestialEnchantRecipeManager {
    
    private static final Map<String, List<CelestialEnchantRecipe>> recipesByPattern = new HashMap<>();
    private static final List<CelestialEnchantRecipe> allRecipes = new ArrayList<>();
    
    public static void registerRecipe(CelestialEnchantRecipe recipe) {
        allRecipes.add(recipe);
        recipesByPattern.computeIfAbsent(recipe.getPattern(), k -> new ArrayList<>()).add(recipe);
    }
    
    public static void clearRecipes() {
        allRecipes.clear();
        recipesByPattern.clear();
    }
    
    @Nullable
    public static CelestialEnchantRecipe findRecipe(String pattern, ItemStack item) {
        List<CelestialEnchantRecipe> candidates = recipesByPattern.get(pattern);
        if (candidates == null) return null;
        
        for (CelestialEnchantRecipe recipe : candidates) {
            if (recipe.matches(pattern, item)) {
                return recipe;
            }
        }
        return null;
    }
    
    public static List<CelestialEnchantRecipe> getAllRecipes() {
        return Collections.unmodifiableList(allRecipes);
    }
    
    public static List<CelestialEnchantRecipe> getRecipesByPattern(String pattern) {
        return recipesByPattern.getOrDefault(pattern, Collections.emptyList());
    }
}
