package net.mcreator.asterrisk.recipe;

import com.google.gson.JsonObject;
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
 * 天体エンチャントレシピ
 * フォーカスパターンと対象アイテムで特殊エンチャントを付与
 */
public class CelestialEnchantRecipe implements Recipe<Container> {
    
    public static final String TYPE_ID = "celestial_enchant";
    
    private final ResourceLocation id;
    private final String pattern;           // LUNAR_CRESCENT, STAR_CROSS, etc.
    private final Ingredient inputItem;     // 対象アイテム（武器/防具など）
    private final ResourceLocation enchantment; // 付与するエンチャントID
    private final int enchantmentLevel;
    private final int moonlightCost;
    private final String description;
    
    public CelestialEnchantRecipe(ResourceLocation id, String pattern, Ingredient inputItem,
                                   ResourceLocation enchantment, int enchantmentLevel,
                                   int moonlightCost, String description) {
        this.id = id;
        this.pattern = pattern;
        this.inputItem = inputItem;
        this.enchantment = enchantment;
        this.enchantmentLevel = enchantmentLevel;
        this.moonlightCost = moonlightCost;
        this.description = description;
    }
    
    /**
     * アイテムがこのレシピにマッチするか
     */
    public boolean matches(String patternName, ItemStack item) {
        return pattern.equals(patternName) && inputItem.test(item);
    }
    
    // Getters
    public String getPattern() { return pattern; }
    public Ingredient getInputItem() { return inputItem; }
    public ResourceLocation getEnchantment() { return enchantment; }
    public int getEnchantmentLevel() { return enchantmentLevel; }
    public int getMoonlightCost() { return moonlightCost; }
    public String getDescription() { return description; }
    
    // Recipe interface
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
        return ItemStack.EMPTY;
    }
    
    @Override
    public ResourceLocation getId() {
        return id;
    }
    
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.CELESTIAL_ENCHANT_SERIALIZER.get();
    }
    
    @Override
    public RecipeType<?> getType() {
        return ModRecipes.CELESTIAL_ENCHANT_TYPE.get();
    }
    
    // Serializer
    public static class Serializer implements RecipeSerializer<CelestialEnchantRecipe> {
        
        @Override
        public CelestialEnchantRecipe fromJson(ResourceLocation id, JsonObject json) {
            String pattern = GsonHelper.getAsString(json, "pattern");
            Ingredient inputItem = Ingredient.fromJson(json.get("input"));
            ResourceLocation enchantment = ResourceLocation.tryParse(GsonHelper.getAsString(json, "enchantment"));
            int enchantmentLevel = GsonHelper.getAsInt(json, "level", 1);
            int moonlightCost = GsonHelper.getAsInt(json, "moonlight_cost", 500);
            String description = GsonHelper.getAsString(json, "description", "");
            
            return new CelestialEnchantRecipe(id, pattern, inputItem, enchantment, enchantmentLevel, moonlightCost, description);
        }
        
        @Nullable
        @Override
        public CelestialEnchantRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            String pattern = buf.readUtf();
            Ingredient inputItem = Ingredient.fromNetwork(buf);
            ResourceLocation enchantment = buf.readResourceLocation();
            int enchantmentLevel = buf.readVarInt();
            int moonlightCost = buf.readVarInt();
            String description = buf.readUtf();
            
            return new CelestialEnchantRecipe(id, pattern, inputItem, enchantment, enchantmentLevel, moonlightCost, description);
        }
        
        @Override
        public void toNetwork(FriendlyByteBuf buf, CelestialEnchantRecipe recipe) {
            buf.writeUtf(recipe.pattern);
            recipe.inputItem.toNetwork(buf);
            buf.writeResourceLocation(recipe.enchantment);
            buf.writeVarInt(recipe.enchantmentLevel);
            buf.writeVarInt(recipe.moonlightCost);
            buf.writeUtf(recipe.description);
        }
    }
}
