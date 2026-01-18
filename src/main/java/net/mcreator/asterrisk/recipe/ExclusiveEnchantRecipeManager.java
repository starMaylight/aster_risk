package net.mcreator.asterrisk.recipe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mcreator.asterrisk.AsterRiskMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

/**
 * CelestialEnchantingTable専用エンチャントレシピマネージャ
 * JSONからレシピを読み込み、管理する
 * 
 * 仕様:
 * 1. レベル1で付与
 * 2. 同じ儀式で既に付与されているアイテムに付与するとレベル+1（Maxまで）
 * 3. 同じアイテムに付与するごとにコストが指数関数的に跳ね上がる（baseCost * 3^currentLevel）
 */
public class ExclusiveEnchantRecipeManager extends SimpleJsonResourceReloadListener {
    
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    
    // シングルトンインスタンス
    private static ExclusiveEnchantRecipeManager instance;
    
    private final Map<String, ExclusiveEnchantData> recipesByPattern = new HashMap<>();
    private final List<ExclusiveEnchantData> allRecipes = new ArrayList<>();
    
    public ExclusiveEnchantRecipeManager() {
        super(GSON, "recipes/exclusive_enchant");
        instance = this;
    }
    
    public static ExclusiveEnchantRecipeManager getInstance() {
        if (instance == null) {
            instance = new ExclusiveEnchantRecipeManager();
        }
        return instance;
    }
    
    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsons, ResourceManager resourceManager, ProfilerFiller profiler) {
        recipesByPattern.clear();
        allRecipes.clear();
        
        for (Map.Entry<ResourceLocation, JsonElement> entry : jsons.entrySet()) {
            ResourceLocation id = entry.getKey();
            try {
                JsonObject json = entry.getValue().getAsJsonObject();
                ExclusiveEnchantData recipe = parseRecipe(id, json);
                if (recipe != null) {
                    recipesByPattern.put(recipe.pattern, recipe);
                    allRecipes.add(recipe);
                    LOGGER.debug("Loaded exclusive enchant recipe: {} -> {}", id, recipe.pattern);
                }
            } catch (Exception e) {
                LOGGER.error("Failed to load exclusive enchant recipe: {}", id, e);
            }
        }
        
        LOGGER.info("Loaded {} exclusive enchant recipes", allRecipes.size());
    }
    
    @Nullable
    private ExclusiveEnchantData parseRecipe(ResourceLocation id, JsonObject json) {
        String type = GsonHelper.getAsString(json, "type", "");
        if (!type.equals("aster_risk:exclusive_enchant")) {
            return null;
        }
        
        String pattern = GsonHelper.getAsString(json, "pattern");
        String enchantmentId = GsonHelper.getAsString(json, "enchantment");
        int baseCost = GsonHelper.getAsInt(json, "base_cost", 500);
        int maxLevel = GsonHelper.getAsInt(json, "max_level", 5);
        String itemType = GsonHelper.getAsString(json, "item_type", "weapon");
        String description = GsonHelper.getAsString(json, "description", "");
        
        ResourceLocation enchantLoc = ResourceLocation.tryParse(enchantmentId);
        if (enchantLoc == null) {
            LOGGER.warn("Invalid enchantment ID: {} in recipe {}", enchantmentId, id);
            return null;
        }
        
        Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(enchantLoc);
        if (enchantment == null) {
            LOGGER.warn("Enchantment not found: {} in recipe {}", enchantmentId, id);
            return null;
        }
        
        Predicate<ItemStack> canApply = getItemPredicate(itemType);
        
        return new ExclusiveEnchantData(id, enchantment, pattern, baseCost, maxLevel, canApply, description);
    }
    
    /**
     * アイテムタイプから適用可能判定を取得
     */
    private Predicate<ItemStack> getItemPredicate(String itemType) {
        return switch (itemType.toLowerCase()) {
            case "weapon" -> stack -> 
                stack.getItem() instanceof SwordItem || stack.getItem() instanceof AxeItem;
            case "armor" -> stack -> 
                stack.getItem() instanceof ArmorItem;
            case "digger", "tool" -> stack -> 
                stack.getItem() instanceof DiggerItem;
            case "damageable" -> ItemStack::isDamageableItem;
            case "sword" -> stack -> 
                stack.getItem() instanceof SwordItem;
            case "axe" -> stack -> 
                stack.getItem() instanceof AxeItem;
            case "pickaxe" -> stack -> 
                stack.getItem() instanceof PickaxeItem;
            case "shovel" -> stack -> 
                stack.getItem() instanceof ShovelItem;
            case "hoe" -> stack -> 
                stack.getItem() instanceof HoeItem;
            case "bow" -> stack -> 
                stack.getItem() instanceof BowItem;
            case "crossbow" -> stack -> 
                stack.getItem() instanceof CrossbowItem;
            case "trident" -> stack -> 
                stack.getItem() instanceof TridentItem;
            default -> stack -> true;
        };
    }
    
    /**
     * パターンから専用エンチャントデータを取得
     */
    @Nullable
    public ExclusiveEnchantData getByPattern(String pattern) {
        return recipesByPattern.get(pattern);
    }
    
    /**
     * アイテムとパターンから適用可能なエンチャントを検索
     */
    @Nullable
    public ExclusiveEnchantData findApplicable(String pattern, ItemStack item) {
        ExclusiveEnchantData data = recipesByPattern.get(pattern);
        if (data == null) return null;
        if (!data.canApply.test(item)) return null;
        
        // 既にMaxレベルの場合は適用不可
        int currentLevel = EnchantmentHelper.getItemEnchantmentLevel(data.enchantment, item);
        if (currentLevel >= data.maxLevel) return null;
        
        return data;
    }
    
    /**
     * 必要マナコストを計算（指数関数的）
     * cost = baseCost * 3^currentLevel
     */
    public int calculateCost(ExclusiveEnchantData data, ItemStack item) {
        int currentLevel = EnchantmentHelper.getItemEnchantmentLevel(data.enchantment, item);
        return data.calculateCost(currentLevel);
    }
    
    /**
     * アイテムにエンチャントを適用（レベル+1）
     */
    public void applyEnchantment(ExclusiveEnchantData data, ItemStack item) {
        Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(item);
        int currentLevel = enchants.getOrDefault(data.enchantment, 0);
        int newLevel = Math.min(currentLevel + 1, data.maxLevel);
        enchants.put(data.enchantment, newLevel);
        EnchantmentHelper.setEnchantments(enchants, item);
    }
    
    /**
     * 全ての専用エンチャントデータを取得
     */
    public List<ExclusiveEnchantData> getAllRecipes() {
        return Collections.unmodifiableList(allRecipes);
    }
    
    /**
     * 専用エンチャントデータクラス
     */
    public static class ExclusiveEnchantData {
        public final ResourceLocation id;
        public final Enchantment enchantment;
        public final String pattern;
        public final int baseCost;
        public final int maxLevel;
        public final Predicate<ItemStack> canApply;
        public final String description;
        
        public ExclusiveEnchantData(ResourceLocation id, Enchantment enchantment, String pattern,
                                    int baseCost, int maxLevel, Predicate<ItemStack> canApply, String description) {
            this.id = id;
            this.enchantment = enchantment;
            this.pattern = pattern;
            this.baseCost = baseCost;
            this.maxLevel = maxLevel;
            this.canApply = canApply;
            this.description = description;
        }
        
        /**
         * コストを計算（指数関数的に増加）
         * cost = baseCost * 3^currentLevel
         */
        public int calculateCost(int currentLevel) {
            return (int)(baseCost * Math.pow(3, currentLevel));
        }
    }
}
