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
 * 集光チャンバーレシピ
 * 月光フォーカスからの光を受けて3x3x3構造内でアイテムを変換
 */
public class FocusChamberRecipe implements Recipe<Container> {
    
    public static final String TYPE_ID = "focus_chamber";
    
    private final ResourceLocation id;
    private final NonNullList<Ingredient> ingredients;
    private final ItemStack result;
    private final int moonlightCost;
    private final String description;
    
    public FocusChamberRecipe(ResourceLocation id, NonNullList<Ingredient> ingredients,
                              ItemStack result, int moonlightCost, String description) {
        this.id = id;
        this.ingredients = ingredients;
        this.result = result;
        this.moonlightCost = moonlightCost;
        this.description = description;
    }
    
    /**
     * アイテムリストがこのレシピにマッチするか（順不同）
     */
    public boolean matches(List<ItemStack> items) {
        if (items.size() != ingredients.size()) return false;
        
        List<ItemStack> remaining = new ArrayList<>(items);
        
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
    public int getMoonlightCost() { return moonlightCost; }
    public String getDescription() { return description; }
    public ItemStack getResultItem() { return result.copy(); }
    public NonNullList<Ingredient> getIngredientList() { return ingredients; }
    
    // Recipe interface
    @Override
    public boolean matches(Container container, Level level) {
        return false;
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
        return ModRecipes.FOCUS_CHAMBER_SERIALIZER.get();
    }
    
    @Override
    public RecipeType<?> getType() {
        return ModRecipes.FOCUS_CHAMBER_TYPE.get();
    }
    
    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }
    
    // Serializer
    public static class Serializer implements RecipeSerializer<FocusChamberRecipe> {
        
        @Override
        public FocusChamberRecipe fromJson(ResourceLocation id, JsonObject json) {
            int moonlightCost = GsonHelper.getAsInt(json, "moonlight_cost", 100);
            String description = GsonHelper.getAsString(json, "description", "");
            
            JsonArray ingredientsArray = GsonHelper.getAsJsonArray(json, "ingredients");
            NonNullList<Ingredient> ingredients = NonNullList.create();
            for (int i = 0; i < ingredientsArray.size(); i++) {
                ingredients.add(Ingredient.fromJson(ingredientsArray.get(i)));
            }
            
            ItemStack result = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true);
            
            return new FocusChamberRecipe(id, ingredients, result, moonlightCost, description);
        }
        
        @Nullable
        @Override
        public FocusChamberRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            int moonlightCost = buf.readVarInt();
            String description = buf.readUtf();
            
            int ingredientCount = buf.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(ingredientCount, Ingredient.EMPTY);
            for (int i = 0; i < ingredientCount; i++) {
                ingredients.set(i, Ingredient.fromNetwork(buf));
            }
            
            ItemStack result = buf.readItem();
            
            return new FocusChamberRecipe(id, ingredients, result, moonlightCost, description);
        }
        
        @Override
        public void toNetwork(FriendlyByteBuf buf, FocusChamberRecipe recipe) {
            buf.writeVarInt(recipe.moonlightCost);
            buf.writeUtf(recipe.description);
            
            buf.writeVarInt(recipe.ingredients.size());
            for (Ingredient ingredient : recipe.ingredients) {
                ingredient.toNetwork(buf);
            }
            
            buf.writeItem(recipe.result);
        }
    }
}
