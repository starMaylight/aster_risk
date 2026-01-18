package net.mcreator.asterrisk.recipe;

import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.*;

/**
 * 魔法陣レシピマネージャ
 * ゲーム内でレシピを検索・管理する
 */
public class RitualCircleRecipeManager {
    
    private static final Map<String, List<RitualCircleRecipe>> recipesByPattern = new HashMap<>();
    private static final List<RitualCircleRecipe> allRecipes = new ArrayList<>();
    
    /**
     * レシピを登録（データパック読み込み時に呼ばれる）
     */
    public static void registerRecipe(RitualCircleRecipe recipe) {
        allRecipes.add(recipe);
        recipesByPattern.computeIfAbsent(recipe.getPattern(), k -> new ArrayList<>()).add(recipe);
    }
    
    /**
     * レシピをクリア（リロード時）
     */
    public static void clearRecipes() {
        allRecipes.clear();
        recipesByPattern.clear();
    }
    
    /**
     * パターンとアイテムリストからマッチするレシピを検索
     */
    @Nullable
    public static RitualCircleRecipe findRecipe(String pattern, List<ItemStack> pedestalItems) {
        List<RitualCircleRecipe> candidates = recipesByPattern.get(pattern);
        if (candidates == null) return null;
        
        for (RitualCircleRecipe recipe : candidates) {
            if (recipe.matches(pedestalItems)) {
                return recipe;
            }
        }
        return null;
    }
    
    /**
     * 全レシピ取得（JEI用）
     */
    public static List<RitualCircleRecipe> getAllRecipes() {
        return Collections.unmodifiableList(allRecipes);
    }
    
    /**
     * 特定パターンのレシピ取得
     */
    public static List<RitualCircleRecipe> getRecipesByPattern(String pattern) {
        return recipesByPattern.getOrDefault(pattern, Collections.emptyList());
    }
}
