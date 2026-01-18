package net.mcreator.asterrisk.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mcreator.asterrisk.block.entity.ObeliskEnergyType;
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
import java.util.*;

/**
 * 儀式レシピ - オベリスクエネルギー要求対応（複数タイプ対応）
 */
public class RitualRecipe implements Recipe<Container> {

    public static final String TYPE_ID = "ritual";

    private final ResourceLocation id;
    private final NonNullList<Ingredient> ingredients;
    private final ItemStack result;
    private final float manaCost;
    
    // オベリスクエネルギー要求（複数タイプ対応）
    private final Map<ObeliskEnergyType, Integer> requiredEnergies;
    
    // 全オベリスクリンク必須フラグ
    private final boolean requiresAllObelisks;

    public RitualRecipe(ResourceLocation id, NonNullList<Ingredient> ingredients, ItemStack result, float manaCost) {
        this(id, ingredients, result, manaCost, new EnumMap<>(ObeliskEnergyType.class), false);
    }
    
    public RitualRecipe(ResourceLocation id, NonNullList<Ingredient> ingredients, ItemStack result, float manaCost,
                        @Nullable ObeliskEnergyType requiredEnergyType, int requiredEnergyAmount) {
        this(id, ingredients, result, manaCost, createSingleEnergyMap(requiredEnergyType, requiredEnergyAmount), false);
    }
    
    public RitualRecipe(ResourceLocation id, NonNullList<Ingredient> ingredients, ItemStack result, float manaCost,
                        Map<ObeliskEnergyType, Integer> requiredEnergies, boolean requiresAllObelisks) {
        this.id = id;
        this.ingredients = ingredients;
        this.result = result;
        this.manaCost = manaCost;
        this.requiredEnergies = requiredEnergies != null ? new EnumMap<>(requiredEnergies) : new EnumMap<>(ObeliskEnergyType.class);
        this.requiresAllObelisks = requiresAllObelisks;
    }
    
    private static Map<ObeliskEnergyType, Integer> createSingleEnergyMap(@Nullable ObeliskEnergyType type, int amount) {
        Map<ObeliskEnergyType, Integer> map = new EnumMap<>(ObeliskEnergyType.class);
        if (type != null && amount > 0) {
            map.put(type, amount);
        }
        return map;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    public float getManaCost() {
        return manaCost;
    }
    
    /**
     * 後方互換性のため - 単一タイプ取得
     */
    @Nullable
    public ObeliskEnergyType getRequiredEnergyType() {
        if (requiredEnergies.isEmpty()) return null;
        return requiredEnergies.keySet().iterator().next();
    }
    
    /**
     * 後方互換性のため - 単一量取得
     */
    public int getRequiredEnergyAmount() {
        if (requiredEnergies.isEmpty()) return 0;
        return requiredEnergies.values().iterator().next();
    }
    
    /**
     * 全エネルギー要求マップを取得
     */
    public Map<ObeliskEnergyType, Integer> getRequiredEnergies() {
        return Collections.unmodifiableMap(requiredEnergies);
    }
    
    /**
     * 全オベリスクリンクが必須かどうか
     */
    public boolean requiresAllObelisks() {
        return requiresAllObelisks;
    }
    
    public boolean requiresObeliskEnergy() {
        return !requiredEnergies.isEmpty() || requiresAllObelisks;
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
            
            // 全オベリスク必須フラグ
            boolean requiresAllObelisks = GsonHelper.getAsBoolean(json, "requires_all_obelisks", false);
            
            // オベリスクエネルギー要求読み込み
            Map<ObeliskEnergyType, Integer> requiredEnergies = new EnumMap<>(ObeliskEnergyType.class);
            
            // 新形式: required_energies配列（複数エネルギー対応）
            if (json.has("required_energies")) {
                JsonArray energiesJson = GsonHelper.getAsJsonArray(json, "required_energies");
                for (JsonElement element : energiesJson) {
                    JsonObject energyJson = element.getAsJsonObject();
                    String typeStr = GsonHelper.getAsString(energyJson, "type", "");
                    ObeliskEnergyType energyType = ObeliskEnergyType.fromName(typeStr);
                    int energyAmount = GsonHelper.getAsInt(energyJson, "amount", 0);
                    if (energyType != null && energyAmount > 0) {
                        requiredEnergies.put(energyType, energyAmount);
                    }
                }
            }
            // 旧形式: required_energy（後方互換性）
            else if (json.has("required_energy")) {
                JsonObject energyJson = GsonHelper.getAsJsonObject(json, "required_energy");
                String typeStr = GsonHelper.getAsString(energyJson, "type", "");
                ObeliskEnergyType energyType = ObeliskEnergyType.fromName(typeStr);
                int energyAmount = GsonHelper.getAsInt(energyJson, "amount", 0);
                if (energyType != null && energyAmount > 0) {
                    requiredEnergies.put(energyType, energyAmount);
                }
            }

            return new RitualRecipe(recipeId, ingredients, result, manaCost, requiredEnergies, requiresAllObelisks);
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
            boolean requiresAllObelisks = buffer.readBoolean();
            
            // 複数エネルギー要求読み込み
            int energyCount = buffer.readVarInt();
            Map<ObeliskEnergyType, Integer> requiredEnergies = new EnumMap<>(ObeliskEnergyType.class);
            for (int i = 0; i < energyCount; i++) {
                String typeStr = buffer.readUtf();
                ObeliskEnergyType energyType = ObeliskEnergyType.fromName(typeStr);
                int energyAmount = buffer.readVarInt();
                if (energyType != null) {
                    requiredEnergies.put(energyType, energyAmount);
                }
            }

            return new RitualRecipe(recipeId, ingredients, result, manaCost, requiredEnergies, requiresAllObelisks);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, RitualRecipe recipe) {
            buffer.writeVarInt(recipe.ingredients.size());
            for (Ingredient ingredient : recipe.ingredients) {
                ingredient.toNetwork(buffer);
            }
            buffer.writeItem(recipe.result);
            buffer.writeFloat(recipe.manaCost);
            buffer.writeBoolean(recipe.requiresAllObelisks);
            
            // 複数エネルギー要求書き込み
            buffer.writeVarInt(recipe.requiredEnergies.size());
            for (Map.Entry<ObeliskEnergyType, Integer> entry : recipe.requiredEnergies.entrySet()) {
                buffer.writeUtf(entry.getKey().getName());
                buffer.writeVarInt(entry.getValue());
            }
        }
    }
}
