package net.mcreator.asterrisk.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * 魔法陣クラフトレシピ
 * パターン（CROSS, DIAMOND, STAR等）と台座上のアイテムで結果が決まる
 */
public class RitualCircleRecipe implements Recipe<Container> {
    
    public static final String TYPE_ID = "ritual_circle";
    
    private final ResourceLocation id;
    private final String pattern;           // CROSS, DIAMOND, STAR, HEXAGON, TRIANGLE
    private final NonNullList<Ingredient> ingredients; // 台座に置くアイテム（順不同）
    private final ItemStack result;
    private final int manaCost;
    private final String description;
    
    public RitualCircleRecipe(ResourceLocation id, String pattern, NonNullList<Ingredient> ingredients,
                              ItemStack result, int manaCost, String description) {
        this.id = id;
        this.pattern = pattern;
        this.ingredients = ingredients;
        this.result = result;
        this.manaCost = manaCost;
        this.description = description;
    }
    
    /**
     * 台座上のアイテムリストがこのレシピにマッチするか（順不同）
     */
    public boolean matches(List<ItemStack> pedestalItems) {
        if (pedestalItems.size() != ingredients.size()) return false;
        
        List<ItemStack> remaining = new ArrayList<>(pedestalItems);
        
        for (Ingredient ingredient : ingredients) {
            boolean found = false;
            for (int i = 0; i < remaining.size(); i++) {
                if (ingredient.test(remaining.get(i))) {
                    remaining.remove(i);
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return remaining.isEmpty();
    }
    
    // Getters
    public String getPattern() { return pattern; }
    public int getManaCost() { return manaCost; }
    public String getDescription() { return description; }
    public ItemStack getResultItem() { return result.copy(); }
    public NonNullList<Ingredient> getIngredientList() { return ingredients; }
    
    // Recipe interface implementation
    @Override
    public boolean matches(Container container, Level level) {
        return false; // 通常のコンテナマッチングは使用しない
    }
    
    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return result.copy();
    }
    
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }
    
    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return result.copy();
    }
    
    @Override
    public ResourceLocation getId() {
        return id;
    }
    
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.RITUAL_CIRCLE_SERIALIZER.get();
    }
    
    @Override
    public RecipeType<?> getType() {
        return ModRecipes.RITUAL_CIRCLE_TYPE.get();
    }
    
    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }
    
    // Serializer
    public static class Serializer implements RecipeSerializer<RitualCircleRecipe> {
        
        @Override
        public RitualCircleRecipe fromJson(ResourceLocation id, JsonObject json) {
            String pattern = GsonHelper.getAsString(json, "pattern");
            int manaCost = GsonHelper.getAsInt(json, "mana_cost", 500);
            String description = GsonHelper.getAsString(json, "description", "");
            
            // Ingredients
            JsonArray ingredientsArray = GsonHelper.getAsJsonArray(json, "ingredients");
            NonNullList<Ingredient> ingredients = NonNullList.create();
            for (int i = 0; i < ingredientsArray.size(); i++) {
                ingredients.add(Ingredient.fromJson(ingredientsArray.get(i)));
            }
            
            // Result
            ItemStack result = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true);
            
            return new RitualCircleRecipe(id, pattern, ingredients, result, manaCost, description);
        }
        
        @Nullable
        @Override
        public RitualCircleRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            String pattern = buf.readUtf();
            int manaCost = buf.readVarInt();
            String description = buf.readUtf();
            
            int ingredientCount = buf.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(ingredientCount, Ingredient.EMPTY);
            for (int i = 0; i < ingredientCount; i++) {
                ingredients.set(i, Ingredient.fromNetwork(buf));
            }
            
            ItemStack result = buf.readItem();
            
            return new RitualCircleRecipe(id, pattern, ingredients, result, manaCost, description);
        }
        
        @Override
        public void toNetwork(FriendlyByteBuf buf, RitualCircleRecipe recipe) {
            buf.writeUtf(recipe.pattern);
            buf.writeVarInt(recipe.manaCost);
            buf.writeUtf(recipe.description);
            
            buf.writeVarInt(recipe.ingredients.size());
            for (Ingredient ingredient : recipe.ingredients) {
                ingredient.toNetwork(buf);
            }
            
            buf.writeItem(recipe.result);
        }
    }
}
