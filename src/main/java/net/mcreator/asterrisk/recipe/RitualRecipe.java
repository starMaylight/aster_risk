package net.mcreator.asterrisk.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * 儀式レシピ
 */
public class RitualRecipe implements Recipe<Container> {

    public static final String TYPE_ID = "ritual";

    private final ResourceLocation id;
    private final NonNullList<Ingredient> ingredients;
    private final ItemStack result;
    private final float manaCost;

    public RitualRecipe(ResourceLocation id, NonNullList<Ingredient> ingredients, ItemStack result, float manaCost) {
        this.id = id;
        this.ingredients = ingredients;
        this.result = result;
        this.manaCost = manaCost;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    public float getManaCost() {
        return manaCost;
    }

    public ItemStack getResultItem() {
        return result.copy();
    }

    /**
     * 入力アイテムリストがこのレシピにマッチするか
     */
    public boolean matches(List<ItemStack> inputs) {
        if (inputs.size() != ingredients.size()) return false;

        List<Ingredient> remaining = new ArrayList<>(ingredients);
        for (ItemStack input : inputs) {
            boolean found = false;
            for (int i = 0; i < remaining.size(); i++) {
                if (remaining.get(i).test(input)) {
                    remaining.remove(i);
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return remaining.isEmpty();
    }

    // === Recipe interface implementation ===

    @Override
    public boolean matches(Container container, Level level) {
        // Container版は使用しない（カスタムマッチングを使用）
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
        return ModRecipes.RITUAL_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.RITUAL_TYPE.get();
    }

    /**
     * レシピシリアライザー
     */
    public static class Serializer implements RecipeSerializer<RitualRecipe> {

        @Override
        public RitualRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            // ingredients配列を読み込み
            JsonArray ingredientsJson = GsonHelper.getAsJsonArray(json, "ingredients");
            NonNullList<Ingredient> ingredients = NonNullList.create();
            for (JsonElement element : ingredientsJson) {
                ingredients.add(Ingredient.fromJson(element));
            }

            // result読み込み
            JsonObject resultJson = GsonHelper.getAsJsonObject(json, "result");
            String itemId = GsonHelper.getAsString(resultJson, "item");
            int count = GsonHelper.getAsInt(resultJson, "count", 1);
            ItemStack result = new ItemStack(ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(itemId)), count);

            // manaCost読み込み
            float manaCost = GsonHelper.getAsFloat(json, "mana_cost", 100f);

            return new RitualRecipe(recipeId, ingredients, result, manaCost);
        }

        @Override
        public @Nullable RitualRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            int ingredientCount = buffer.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.create();
            for (int i = 0; i < ingredientCount; i++) {
                ingredients.add(Ingredient.fromNetwork(buffer));
            }
            ItemStack result = buffer.readItem();
            float manaCost = buffer.readFloat();

            return new RitualRecipe(recipeId, ingredients, result, manaCost);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, RitualRecipe recipe) {
            buffer.writeVarInt(recipe.ingredients.size());
            for (Ingredient ingredient : recipe.ingredients) {
                ingredient.toNetwork(buffer);
            }
            buffer.writeItem(recipe.result);
            buffer.writeFloat(recipe.manaCost);
        }
    }
}
