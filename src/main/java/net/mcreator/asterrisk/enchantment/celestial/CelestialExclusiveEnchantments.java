package net.mcreator.asterrisk.enchantment.celestial;

import net.mcreator.asterrisk.registry.ModEnchantments;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

/**
 * CelestialEnchantingTable専用エンチャントのレシピ管理
 * 各エンチャントには専用のFocusパターンが必要
 */
public class CelestialExclusiveEnchantments {
    
    // エンチャントデータクラス
    public static class ExclusiveEnchantData {
        public final Enchantment enchantment;
        public final String focusPattern;
        public final int baseCost;
        public final int maxLevel;
        public final Predicate<ItemStack> canApply;
        
        public ExclusiveEnchantData(Enchantment enchantment, String focusPattern, int baseCost, int maxLevel, Predicate<ItemStack> canApply) {
            this.enchantment = enchantment;
            this.focusPattern = focusPattern;
            this.baseCost = baseCost;
            this.maxLevel = maxLevel;
            this.canApply = canApply;
        }
        
        /**
         * コストを計算（指数関数的に増加）
         * cost = baseCost * 3^currentLevel
         */
        public int calculateCost(int currentLevel) {
            return (int)(baseCost * Math.pow(3, currentLevel));
        }
    }
    
    // 専用エンチャント一覧
    private static final List<ExclusiveEnchantData> EXCLUSIVE_ENCHANTS = new ArrayList<>();
    private static final Map<String, ExclusiveEnchantData> BY_PATTERN = new HashMap<>();
    private static boolean initialized = false;
    
    /**
     * 初期化（ModEnchantments登録後に呼び出す）
     */
    public static void init() {
        if (initialized) return;
        initialized = true;
        
        // 武器判定
        Predicate<ItemStack> isWeapon = stack -> 
            stack.getItem() instanceof SwordItem || stack.getItem() instanceof AxeItem;
        
        // 防具判定
        Predicate<ItemStack> isArmor = stack -> 
            stack.getItem() instanceof ArmorItem;
        
        // ツール判定
        Predicate<ItemStack> isDigger = stack -> 
            stack.getItem() instanceof DiggerItem;
        
        // 全装備判定
        Predicate<ItemStack> isDamageable = ItemStack::isDamageableItem;
        
        // 10個の専用エンチャントを登録
        // 各エンチャントに異なるFocusパターンを割り当て
        
        // 1. 星砕きの一撃 (武器) - LUNAR_CRESCENT
        register(new ExclusiveEnchantData(
            ModEnchantments.STAR_BREAKER.get(),
            "LUNAR_CRESCENT",
            500, 5, isWeapon
        ));
        
        // 2. 絶対障壁 (防具) - STAR_CROSS
        register(new ExclusiveEnchantData(
            ModEnchantments.ABSOLUTE_BARRIER.get(),
            "STAR_CROSS",
            600, 5, isArmor
        ));
        
        // 3. 反転治癒 (防具) - CONSTELLATION
        register(new ExclusiveEnchantData(
            ModEnchantments.REVERSE_HEALING.get(),
            "CONSTELLATION",
            400, 5, isArmor
        ));
        
        // 4. 幸運の極み (ツール) - ECLIPSE_RING
        register(new ExclusiveEnchantData(
            ModEnchantments.FORTUNES_PEAK.get(),
            "ECLIPSE_RING",
            800, 5, isDigger
        ));
        
        // 5. 死の連鎖 (武器) - VOID_SPIRAL
        register(new ExclusiveEnchantData(
            ModEnchantments.DEATH_CHAIN.get(),
            "VOID_SPIRAL",
            700, 10, isWeapon
        ));
        
        // 新パターンが必要なので追加登録
        // 6. 天罰の鉄槌 (武器) - HEAVENS_HAMMER (新規)
        register(new ExclusiveEnchantData(
            ModEnchantments.ANVIL_FROM_HEAVEN.get(),
            "HEAVENS_HAMMER",
            500, 5, isWeapon
        ));
        
        // 7. 呪詛の反撃 (防具) - CURSED_MIRROR (新規)
        register(new ExclusiveEnchantData(
            ModEnchantments.CURSED_RETALIATION.get(),
            "CURSED_MIRROR",
            450, 5, isArmor
        ));
        
        // 8. 幸運の星 (武器) - LUCKY_NOVA (新規)
        register(new ExclusiveEnchantData(
            ModEnchantments.LUCKY_STAR.get(),
            "LUCKY_NOVA",
            400, 5, isWeapon
        ));
        
        // 9. マナビーム (武器) - MANA_FOCUS (新規)
        register(new ExclusiveEnchantData(
            ModEnchantments.MANA_BEAM.get(),
            "MANA_FOCUS",
            600, 10, isWeapon
        ));
        
        // 10. 星の昇華 (全装備) - STELLAR_CORE (新規)
        register(new ExclusiveEnchantData(
            ModEnchantments.STELLAR_ASCENSION.get(),
            "STELLAR_CORE",
            1000, 3, isDamageable
        ));
    }
    
    private static void register(ExclusiveEnchantData data) {
        EXCLUSIVE_ENCHANTS.add(data);
        BY_PATTERN.put(data.focusPattern, data);
    }
    
    /**
     * パターンから専用エンチャントデータを取得
     */
    @Nullable
    public static ExclusiveEnchantData getByPattern(String pattern) {
        init();
        return BY_PATTERN.get(pattern);
    }
    
    /**
     * アイテムとパターンから適用可能なエンチャントを検索
     * @return 適用可能なエンチャントデータ、またはnull
     */
    @Nullable
    public static ExclusiveEnchantData findApplicable(String pattern, ItemStack item) {
        init();
        ExclusiveEnchantData data = BY_PATTERN.get(pattern);
        if (data == null) return null;
        if (!data.canApply.test(item)) return null;
        
        // 既にMaxレベルの場合は適用不可
        int currentLevel = EnchantmentHelper.getItemEnchantmentLevel(data.enchantment, item);
        if (currentLevel >= data.maxLevel) return null;
        
        return data;
    }
    
    /**
     * 必要マナコストを計算
     */
    public static int calculateCost(ExclusiveEnchantData data, ItemStack item) {
        int currentLevel = EnchantmentHelper.getItemEnchantmentLevel(data.enchantment, item);
        return data.calculateCost(currentLevel);
    }
    
    /**
     * アイテムにエンチャントを適用（レベル+1）
     */
    public static void applyEnchantment(ExclusiveEnchantData data, ItemStack item) {
        Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(item);
        int currentLevel = enchants.getOrDefault(data.enchantment, 0);
        int newLevel = Math.min(currentLevel + 1, data.maxLevel);
        enchants.put(data.enchantment, newLevel);
        EnchantmentHelper.setEnchantments(enchants, item);
    }
    
    /**
     * 全ての専用エンチャントデータを取得
     */
    public static List<ExclusiveEnchantData> getAllExclusiveEnchants() {
        init();
        return Collections.unmodifiableList(EXCLUSIVE_ENCHANTS);
    }
    
    /**
     * 専用エンチャント用の新パターンを取得
     */
    public static Map<String, List<net.minecraft.core.BlockPos>> getNewPatterns() {
        Map<String, List<net.minecraft.core.BlockPos>> patterns = new HashMap<>();
        
        // HEAVENS_HAMMER - 十字型（高め）
        patterns.put("heavens_hammer", Arrays.asList(
            new net.minecraft.core.BlockPos(0, 2, 2),
            new net.minecraft.core.BlockPos(0, 2, -2),
            new net.minecraft.core.BlockPos(2, 2, 0),
            new net.minecraft.core.BlockPos(-2, 2, 0)
        ));
        
        // CURSED_MIRROR - 四隅対角
        patterns.put("cursed_mirror", Arrays.asList(
            new net.minecraft.core.BlockPos(1, 1, 1),
            new net.minecraft.core.BlockPos(-1, 1, -1),
            new net.minecraft.core.BlockPos(1, 1, -1),
            new net.minecraft.core.BlockPos(-1, 1, 1)
        ));
        
        // LUCKY_NOVA - 星型（拡張）
        patterns.put("lucky_nova", Arrays.asList(
            new net.minecraft.core.BlockPos(0, 1, 3),
            new net.minecraft.core.BlockPos(0, 1, -3),
            new net.minecraft.core.BlockPos(3, 1, 0),
            new net.minecraft.core.BlockPos(-3, 1, 0),
            new net.minecraft.core.BlockPos(2, 1, 2),
            new net.minecraft.core.BlockPos(-2, 1, 2)
        ));
        
        // MANA_FOCUS - 集中型
        patterns.put("mana_focus", Arrays.asList(
            new net.minecraft.core.BlockPos(1, 2, 0),
            new net.minecraft.core.BlockPos(-1, 2, 0),
            new net.minecraft.core.BlockPos(0, 2, 1),
            new net.minecraft.core.BlockPos(0, 2, -1),
            new net.minecraft.core.BlockPos(0, 3, 0)
        ));
        
        // STELLAR_CORE - 立体八面体
        patterns.put("stellar_core", Arrays.asList(
            new net.minecraft.core.BlockPos(0, 3, 0),
            new net.minecraft.core.BlockPos(2, 1, 0),
            new net.minecraft.core.BlockPos(-2, 1, 0),
            new net.minecraft.core.BlockPos(0, 1, 2),
            new net.minecraft.core.BlockPos(0, 1, -2),
            new net.minecraft.core.BlockPos(1, 2, 1),
            new net.minecraft.core.BlockPos(-1, 2, -1)
        ));
        
        return patterns;
    }
}
