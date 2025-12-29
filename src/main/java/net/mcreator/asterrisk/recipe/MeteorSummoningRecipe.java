package net.mcreator.asterrisk.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.mcreator.asterrisk.item.MeteorSummonCoreItem;
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
 * 流星召喚レシピ - JEI表示用
 */
public class MeteorSummoningRecipe implements Recipe<Container> {

    public static final String TYPE_ID = "meteor_summoning";

    private final ResourceLocation id;
    private final MeteorSummonCoreItem.MeteorType meteorType;
    private final float manaCost;
    private final List<ItemStack> possibleDrops;
    private final String description;
    private final boolean dangerous;

    public MeteorSummoningRecipe(ResourceLocation id, MeteorSummonCoreItem.MeteorType meteorType,
                                float manaCost, List<ItemStack> possibleDrops, 
                                String description, boolean dangerous) {
        this.id = id;
        this.meteorType = meteorType;
        this.manaCost = manaCost;
        this.possibleDrops = possibleDrops;
        this.description = description;
        this.dangerous = dangerous;
    }

    public MeteorSummonCoreItem.MeteorType getMeteorType() {
        return meteorType;
    }

    public float getManaCost() {
        return manaCost;
    }

    public List<ItemStack> getPossibleDrops() {
        return possibleDrops;
    }

    public String getDescription() {
        return description;
    }

    public boolean isDangerous() {
        return dangerous;
    }

    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return possibleDrops.isEmpty() ? ItemStack.EMPTY : possibleDrops.get(0);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.METEOR_SUMMONING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.METEOR_SUMMONING_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<MeteorSummoningRecipe> {

        @Override
        public MeteorSummoningRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            String typeName = GsonHelper.getAsString(json, "meteor_type");
            MeteorSummonCoreItem.MeteorType meteorType = MeteorSummonCoreItem.MeteorType.valueOf(typeName.toUpperCase());
            
            float manaCost = GsonHelper.getAsFloat(json, "mana_cost", 500f);
            String description = GsonHelper.getAsString(json, "description", "");
            boolean dangerous = GsonHelper.getAsBoolean(json, "dangerous", false);

            List<ItemStack> drops = new ArrayList<>();
            if (json.has("drops")) {
                JsonArray dropsArray = GsonHelper.getAsJsonArray(json, "drops");
                for (var element : dropsArray) {
                    JsonObject dropObj = element.getAsJsonObject();
                    String itemId = GsonHelper.getAsString(dropObj, "item");
                    int count = GsonHelper.getAsInt(dropObj, "count", 1);
                    var item = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(itemId));
                    if (item != null) {
                        drops.add(new ItemStack(item, count));
                    }
                }
            }

            return new MeteorSummoningRecipe(recipeId, meteorType, manaCost, drops, description, dangerous);
        }

        @Override
        @Nullable
        public MeteorSummoningRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            int typeOrdinal = buffer.readVarInt();
            MeteorSummonCoreItem.MeteorType meteorType = MeteorSummonCoreItem.MeteorType.values()[typeOrdinal];
            float manaCost = buffer.readFloat();
            String description = buffer.readUtf();
            boolean dangerous = buffer.readBoolean();

            int dropCount = buffer.readVarInt();
            List<ItemStack> drops = new ArrayList<>();
            for (int i = 0; i < dropCount; i++) {
                drops.add(buffer.readItem());
            }

            return new MeteorSummoningRecipe(recipeId, meteorType, manaCost, drops, description, dangerous);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, MeteorSummoningRecipe recipe) {
            buffer.writeVarInt(recipe.meteorType.ordinal());
            buffer.writeFloat(recipe.manaCost);
            buffer.writeUtf(recipe.description);
            buffer.writeBoolean(recipe.dangerous);

            buffer.writeVarInt(recipe.possibleDrops.size());
            for (ItemStack drop : recipe.possibleDrops) {
                buffer.writeItem(drop);
            }
        }
    }
}
