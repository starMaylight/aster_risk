package net.mcreator.asterrisk.datagen;

import com.google.gson.JsonObject;
import net.mcreator.asterrisk.AsterRiskMod;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 天体エンチャントレシピのデータ生成プロバイダー
 */
public class CelestialEnchantRecipeProvider implements DataProvider {
    
    private final PackOutput output;
    private final List<EnchantRecipeEntry> recipes = new ArrayList<>();
    
    public CelestialEnchantRecipeProvider(PackOutput output) {
        this.output = output;
        registerRecipes();
    }
    
    /**
     * レシピを登録
     */
    private void registerRecipes() {
        // ===== 基本パターン: LUNAR_CRESCENT =====
        addRecipe("moonlight_sword", "LUNAR_CRESCENT", "forge:tools/swords", 
            "aster_risk:moonlight", 5, 500, "Enhance sword with powerful moonlight energy");
        
        addRecipe("moonlight_axe", "LUNAR_CRESCENT", "forge:tools/axes",
            "aster_risk:moonlight", 3, 400, "Infuse axe with lunar power");
        
        // ===== STAR_CROSS パターン =====
        addRecipe("stardust_fortune_pickaxe", "STAR_CROSS", "forge:tools/pickaxes",
            "minecraft:fortune", 3, 600, "Grant pickaxe fortune through stellar alignment");
        
        addRecipe("stardust_fortune_shovel", "STAR_CROSS", "forge:tools/shovels",
            "minecraft:fortune", 3, 500, "Grant shovel fortune through stellar alignment");
        
        // ===== CONSTELLATION パターン =====
        addRecipe("celestial_protection_armor", "CONSTELLATION", "forge:armors",
            "minecraft:protection", 4, 800, "Protect with celestial blessing");
        
        addRecipe("celestial_unbreaking", "CONSTELLATION", "forge:tools",
            "minecraft:unbreaking", 3, 700, "Strengthen with celestial durability");
        
        // ===== ECLIPSE_RING パターン =====
        addRecipe("void_touch_weapon", "ECLIPSE_RING", "forge:tools/swords",
            "aster_risk:void_touch", 3, 1000, "Imbue weapon with void energy");
        
        addRecipe("eclipse_sharpness", "ECLIPSE_RING", "forge:tools/swords",
            "minecraft:sharpness", 5, 900, "Sharpen with eclipse energy");
        
        // ===== VOID_SPIRAL パターン =====
        addRecipe("lunar_stride_boots", "VOID_SPIRAL", "minecraft:diamond_boots",
            "aster_risk:lunar_stride", 2, 1200, "Grant boots the power of lunar stride");
        
        addRecipe("void_walker_boots", "VOID_SPIRAL", "forge:armors/boots",
            "minecraft:feather_falling", 4, 800, "Walk safely through the void");
        
        // ===== HEAVENS_HAMMER パターン =====
        addRecipe("heavens_hammer_axe", "HEAVENS_HAMMER", "forge:tools/axes",
            "aster_risk:heavens_hammer", 3, 1500, "Bestow the power of Heaven's Hammer");
        
        addRecipe("smite_weapon", "HEAVENS_HAMMER", "forge:tools/swords",
            "minecraft:smite", 5, 700, "Holy power against undead");
        
        // ===== CURSED_MIRROR パターン =====
        addRecipe("cursed_mirror_shield", "CURSED_MIRROR", "minecraft:shield",
            "aster_risk:cursed_mirror", 1, 2000, "Reflect damage back to attackers");
        
        addRecipe("thorns_armor", "CURSED_MIRROR", "forge:armors/chestplates",
            "minecraft:thorns", 3, 900, "Return damage to attackers");
        
        // ===== LUCKY_NOVA パターン =====
        addRecipe("lucky_nova_tool", "LUCKY_NOVA", "forge:tools",
            "aster_risk:lucky_nova", 2, 1800, "Increase luck with stellar fortune");
        
        addRecipe("looting_sword", "LUCKY_NOVA", "forge:tools/swords",
            "minecraft:looting", 3, 1000, "Increase drops with stellar luck");
        
        // ===== MANA_FOCUS パターン =====
        addRecipe("mana_efficiency", "MANA_FOCUS", "forge:tools",
            "aster_risk:mana_efficiency", 3, 600, "Reduce mana consumption");
        
        addRecipe("efficiency_tool", "MANA_FOCUS", "forge:tools",
            "minecraft:efficiency", 5, 800, "Increase tool efficiency");
        
        // ===== STELLAR_CORE パターン =====
        addRecipe("stellar_core_weapon", "STELLAR_CORE", "forge:tools/swords",
            "aster_risk:stellar_core", 1, 2500, "Channel the power of stellar cores");
        
        addRecipe("fire_aspect_weapon", "STELLAR_CORE", "forge:tools/swords",
            "minecraft:fire_aspect", 2, 1100, "Ignite with stellar heat");
        
        // ===== 追加の汎用レシピ =====
        addRecipe("mending_any", "CONSTELLATION", "forge:tools",
            "minecraft:mending", 1, 2000, "Self-repair through celestial energy");
        
        addRecipe("silk_touch_pickaxe", "STAR_CROSS", "forge:tools/pickaxes",
            "minecraft:silk_touch", 1, 1500, "Gently harvest with starlight");
        
        addRecipe("aqua_affinity_helmet", "LUNAR_CRESCENT", "forge:armors/helmets",
            "minecraft:aqua_affinity", 1, 500, "Breathe underwater like the moon reflects on water");
        
        addRecipe("respiration_helmet", "VOID_SPIRAL", "forge:armors/helmets",
            "minecraft:respiration", 3, 700, "Breathe in the void of space");
        
        addRecipe("depth_strider_boots", "LUNAR_CRESCENT", "forge:armors/boots",
            "minecraft:depth_strider", 3, 600, "Walk on water surfaces");
        
        addRecipe("knockback_weapon", "HEAVENS_HAMMER", "forge:tools/swords",
            "minecraft:knockback", 2, 400, "Push back enemies with celestial force");
        
        addRecipe("power_bow", "STELLAR_CORE", "minecraft:bow",
            "minecraft:power", 5, 1000, "Empower arrows with stellar energy");
        
        addRecipe("infinity_bow", "LUCKY_NOVA", "minecraft:bow",
            "minecraft:infinity", 1, 1800, "Infinite arrows from the stars");
        
        addRecipe("flame_bow", "STELLAR_CORE", "minecraft:bow",
            "minecraft:flame", 1, 800, "Ignite arrows with stellar flame");
        
        addRecipe("punch_bow", "HEAVENS_HAMMER", "minecraft:bow",
            "minecraft:punch", 2, 500, "Knock back targets with force");
    }
    
    private void addRecipe(String name, String pattern, String inputTag, 
                          String enchantment, int level, int moonlightCost, String description) {
        recipes.add(new EnchantRecipeEntry(name, pattern, inputTag, null, enchantment, level, moonlightCost, description));
    }
    
    private void addRecipeItem(String name, String pattern, String inputItem,
                               String enchantment, int level, int moonlightCost, String description) {
        recipes.add(new EnchantRecipeEntry(name, pattern, null, inputItem, enchantment, level, moonlightCost, description));
    }
    
    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        
        Path basePath = output.getOutputFolder().resolve("data/" + AsterRiskMod.MODID + "/recipes/celestial_enchant");
        
        for (EnchantRecipeEntry entry : recipes) {
            Path path = basePath.resolve(entry.name + ".json");
            futures.add(DataProvider.saveStable(cache, entry.toJson(), path));
        }
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
    
    @Override
    public String getName() {
        return "Aster Risk Celestial Enchant Recipes";
    }
    
    /**
     * レシピエントリ
     */
    private static class EnchantRecipeEntry {
        final String name;
        final String pattern;
        final String inputTag;
        final String inputItem;
        final String enchantment;
        final int level;
        final int moonlightCost;
        final String description;
        
        EnchantRecipeEntry(String name, String pattern, String inputTag, String inputItem,
                          String enchantment, int level, int moonlightCost, String description) {
            this.name = name;
            this.pattern = pattern;
            this.inputTag = inputTag;
            this.inputItem = inputItem;
            this.enchantment = enchantment;
            this.level = level;
            this.moonlightCost = moonlightCost;
            this.description = description;
        }
        
        JsonObject toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("type", "aster_risk:celestial_enchant");
            json.addProperty("pattern", pattern);
            
            // input
            JsonObject input = new JsonObject();
            if (inputTag != null) {
                input.addProperty("tag", inputTag);
            } else if (inputItem != null) {
                input.addProperty("item", inputItem);
            }
            json.add("input", input);
            
            json.addProperty("enchantment", enchantment);
            json.addProperty("level", level);
            json.addProperty("moonlight_cost", moonlightCost);
            json.addProperty("description", description);
            
            return json;
        }
    }
}
