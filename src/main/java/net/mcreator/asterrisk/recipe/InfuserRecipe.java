package net.mcreator.asterrisk.recipe;

import com.google.gson.JsonObject;
import net.mcreator.asterrisk.AsterRiskMod;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

/**
 * Lunar Infuser用のレシピ
 * JSONで定義可能な変換レシピ
 */
public class InfuserRecipe implements Recipe<Container> {

    private final ResourceLocation id;
    private final Ingredient input;
    private final ItemStack output;
    private final float manaCost;
    private final int processTime; // tick単位

    public InfuserRecipe(ResourceLocation id, Ingredient input, ItemStack output, float manaCost, int processTime) {
        this.id = id;
        this.input = input;
        this.output = output;
        this.manaCost = manaCost;
        this.processTime = processTime;
    }

    public Ingredient getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output.copy();
    }

    public float getManaCost() {
        return manaCost;
    }

    public int getProcessTime() {
        return processTime;
    }

    /**
     * 入力アイテムがこのレシピにマッチするかチェック
     */
    public boolean matches(ItemStack stack) {
        return input.test(stack);
    }

    @Override
    public boolean matches(Container container, Level level) {
        return input.test(container.getItem(0));
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return output.copy();
    }

    public ItemStack getResultItem() {
        return output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.INFUSER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.INFUSER_TYPE.get();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(input);
        return list;
    }

    /**
     * シリアライザ
     */
    public static class Serializer implements RecipeSerializer<InfuserRecipe> {

        @Override
        public InfuserRecipe fromJson(ResourceLocation id, JsonObject json) {
            Ingredient input = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "input"));
            
            JsonObject resultJson = GsonHelper.getAsJsonObject(json, "result");
            ItemStack output = ShapedRecipe.itemStackFromJson(resultJson);
            
            float manaCost = GsonHelper.getAsFloat(json, "mana_cost", 100f);
            int processTime = GsonHelper.getAsInt(json, "process_time", 100);
            
            return new InfuserRecipe(id, input, output, manaCost, processTime);
        }

        @Override
        @Nullable
        public InfuserRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            Ingredient input = Ingredient.fromNetwork(buf);
            ItemStack output = buf.readItem();
            float manaCost = buf.readFloat();
            int processTime = buf.readInt();
            return new InfuserRecipe(id, input, output, manaCost, processTime);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, InfuserRecipe recipe) {
            recipe.input.toNetwork(buf);
            buf.writeItem(recipe.output);
            buf.writeFloat(recipe.manaCost);
            buf.writeInt(recipe.processTime);
        }
    }
}
