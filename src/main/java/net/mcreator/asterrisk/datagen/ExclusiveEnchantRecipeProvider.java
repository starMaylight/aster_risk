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
 * CelestialEnchantingTable専用エンチャントレシピのデータ生成プロバイダー
 * 
 * 仕様:
 * 1. レベル1で付与
 * 2. 同じ儀式で既に付与されているアイテムに付与しようとするとレベルが+1（Maxまで）
 * 3. 同じアイテムに付与するごとにコストが指数関数的に跳ね上がる
 */
public class ExclusiveEnchantRecipeProvider implements DataProvider {
    
    private final PackOutput output;
    private final List<ExclusiveEnchantEntry> recipes = new ArrayList<>();
    
    public ExclusiveEnchantRecipeProvider(PackOutput output) {
        this.output = output;
        registerRecipes();
    }
    
    /**
     * 専用エンチャントレシピを登録
     */
    private void registerRecipes() {
        // ===== 11個の専用エンチャント =====
        
        // 1. 星砕きの一撃 (Star Breaker) - 武器
        // 攻撃時レベル%で防御無視ダメージ*10
        addRecipe("star_breaker", 
            "STAR_CROSS",          // Focusパターン
            "aster_risk:star_breaker",
            500,                   // 基本コスト
            5,                     // 最大レベル
            "weapon",              // アイテムタイプ
            "Strike with star-shattering force");
        
        // 2. 絶対障壁 (Absolute Barrier) - 防具
        // 被ダメ時レベル%で完全無効化
        addRecipe("absolute_barrier",
            "CONSTELLATION",
            "aster_risk:absolute_barrier",
            600,
            5,
            "armor",
            "Create an absolute barrier against damage");
        
        // 3. 反転治癒 (Reverse Healing) - 防具
        // 被ダメ時レベル%でダメージ*2回復
        addRecipe("reverse_healing",
            "LUNAR_CRESCENT",
            "aster_risk:reverse_healing",
            400,
            5,
            "armor",
            "Convert damage into healing");
        
        // 4. 幸運の極み (Fortune's Peak) - ツール
        // 採掘時レベル*2%で幸運5回判定
        addRecipe("fortunes_peak",
            "LUCKY_NOVA",
            "aster_risk:fortunes_peak",
            800,
            5,
            "digger",
            "Reach the peak of fortune");
        
        // 5. 死の連鎖 (Death Chain) - 武器
        // 撃破時に周囲へダメージ拡散
        addRecipe("death_chain",
            "VOID_SPIRAL",
            "aster_risk:death_chain",
            700,
            10,
            "weapon",
            "Chain death to nearby enemies");
        
        // 6. 天罰の鉄槌 (Anvil from Heaven) - 武器
        // 攻撃時レベル*5%で金床落下
        addRecipe("anvil_from_heaven",
            "HEAVENS_HAMMER",
            "aster_risk:anvil_from_heaven",
            500,
            5,
            "weapon",
            "Call down heavenly anvils upon foes");
        
        // 7. 呪詛の反撃 (Cursed Retaliation) - 防具
        // 被ダメ時レベル%で周囲にデバフ
        addRecipe("cursed_retaliation",
            "CURSED_MIRROR",
            "aster_risk:cursed_retaliation",
            450,
            5,
            "armor",
            "Curse those who dare attack");
        
        // 8. 幸運の星 (Lucky Star) - 武器
        // 攻撃時5%でレベル個のバフ付与
        addRecipe("lucky_star",
            "ECLIPSE_RING",
            "aster_risk:lucky_star",
            400,
            5,
            "weapon",
            "Be blessed by lucky stars");
        
        // 9. マナビーム (Mana Beam) - 武器
        // 攻撃時レベル%でマナ消費してビーム
        addRecipe("mana_beam",
            "MANA_FOCUS",
            "aster_risk:mana_beam",
            600,
            10,
            "weapon",
            "Channel mana into a devastating beam");
        
        // 10. 星の昇華 (Stellar Ascension) - 全装備
        // 他エンチャントのレベルを上げる
        addRecipe("stellar_ascension",
            "STELLAR_CORE",
            "aster_risk:stellar_ascension",
            1000,
            3,
            "damageable",
            "Ascend to stellar heights");
        
        // 11. 天罰の金床 (Heavenly Anvil) - 武器（天罰の鉄槌の強化版？または別名）
        // ※既にanvil_from_heavenとして登録済み。パターン違いで追加する場合：
        // 追加: 同じ効果を別パターンでも付与可能にする場合
        // addRecipe("heavenly_anvil_alt",
        //     "STELLAR_CORE",
        //     "aster_risk:anvil_from_heaven",
        //     800,
        //     5,
        //     "weapon",
        //     "Summon the heavenly anvil");
    }
    
    /**
     * 専用エンチャントレシピを追加
     * @param name レシピ名
     * @param pattern Focusパターン名
     * @param enchantment エンチャントID
     * @param baseCost 基本マナコスト（レベルごとに3倍）
     * @param maxLevel 最大レベル
     * @param itemType アイテムタイプ（weapon/armor/digger/damageable）
     * @param description 説明
     */
    private void addRecipe(String name, String pattern, String enchantment,
                          int baseCost, int maxLevel, String itemType, String description) {
        recipes.add(new ExclusiveEnchantEntry(name, pattern, enchantment, baseCost, maxLevel, itemType, description));
    }
    
    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        
        Path basePath = output.getOutputFolder().resolve("data/" + AsterRiskMod.MODID + "/recipes/exclusive_enchant");
        
        for (ExclusiveEnchantEntry entry : recipes) {
            Path path = basePath.resolve(entry.name + ".json");
            futures.add(DataProvider.saveStable(cache, entry.toJson(), path));
        }
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
    
    @Override
    public String getName() {
        return "Aster Risk Exclusive Enchant Recipes";
    }
    
    /**
     * 専用エンチャントエントリ
     */
    private static class ExclusiveEnchantEntry {
        final String name;
        final String pattern;
        final String enchantment;
        final int baseCost;
        final int maxLevel;
        final String itemType;
        final String description;
        
        ExclusiveEnchantEntry(String name, String pattern, String enchantment,
                             int baseCost, int maxLevel, String itemType, String description) {
            this.name = name;
            this.pattern = pattern;
            this.enchantment = enchantment;
            this.baseCost = baseCost;
            this.maxLevel = maxLevel;
            this.itemType = itemType;
            this.description = description;
        }
        
        JsonObject toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("type", "aster_risk:exclusive_enchant");
            json.addProperty("pattern", pattern);
            json.addProperty("enchantment", enchantment);
            json.addProperty("base_cost", baseCost);
            json.addProperty("max_level", maxLevel);
            json.addProperty("item_type", itemType);
            json.addProperty("description", description);
            
            return json;
        }
    }
}
