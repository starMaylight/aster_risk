package net.mcreator.asterrisk.registry;

import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.enchantment.*;
import net.mcreator.asterrisk.enchantment.celestial.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Aster Risk Modのエンチャント登録
 */
public class ModEnchantments {
    public static final DeferredRegister<Enchantment> REGISTRY = 
        DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, AsterRiskMod.MODID);

    // === 武器エンチャント ===
    
    public static final RegistryObject<Enchantment> MOONLIGHT = 
        REGISTRY.register("moonlight", MoonlightEnchantment::new);
    
    public static final RegistryObject<Enchantment> VOID_TOUCH = 
        REGISTRY.register("void_touch", VoidTouchEnchantment::new);
    
    public static final RegistryObject<Enchantment> SHADOW_DRAIN = 
        REGISTRY.register("shadow_drain", ShadowDrainEnchantment::new);
    
    public static final RegistryObject<Enchantment> PRISMATIC_STRIKE = 
        REGISTRY.register("prismatic_strike", PrismaticStrikeEnchantment::new);
    
    // === 防具エンチャント ===
    
    public static final RegistryObject<Enchantment> CELESTIAL_PROTECTION = 
        REGISTRY.register("celestial_protection", CelestialProtectionEnchantment::new);
    
    public static final RegistryObject<Enchantment> STELLAR_WARD = 
        REGISTRY.register("stellar_ward", StellarWardEnchantment::new);
    
    public static final RegistryObject<Enchantment> MANA_SPRING = 
        REGISTRY.register("mana_spring", ManaSpringEnchantment::new);
    
    public static final RegistryObject<Enchantment> LUNAR_STRIDE = 
        REGISTRY.register("lunar_stride", LunarStrideEnchantment::new);
    
    // === ツールエンチャント ===
    
    public static final RegistryObject<Enchantment> STARDUST_FORTUNE = 
        REGISTRY.register("stardust_fortune", StardustFortuneEnchantment::new);
    
    // === 汎用エンチャント ===
    
    public static final RegistryObject<Enchantment> LUNAR_GRAVITY = 
        REGISTRY.register("lunar_gravity", LunarGravityEnchantment::new);
    
    // ========================================
    // === CelestialEnchantingTable専用 ===
    // ========================================
    
    // 1. 星砕きの一撃 - 攻撃時レベル%で防御無視ダメージ*10
    public static final RegistryObject<Enchantment> STAR_BREAKER = 
        REGISTRY.register("star_breaker", StarBreakerEnchantment::new);
    
    // 2. 絶対障壁 - 被ダメ時レベル%で完全無効化
    public static final RegistryObject<Enchantment> ABSOLUTE_BARRIER = 
        REGISTRY.register("absolute_barrier", AbsoluteBarrierEnchantment::new);
    
    // 3. 反転治癒 - 被ダメ時レベル%でダメージ*2回復
    public static final RegistryObject<Enchantment> REVERSE_HEALING = 
        REGISTRY.register("reverse_healing", ReverseHealingEnchantment::new);
    
    // 4. 幸運の極み - 採掘時レベル*2%で幸運5回判定
    public static final RegistryObject<Enchantment> FORTUNES_PEAK = 
        REGISTRY.register("fortunes_peak", FortunesPeakEnchantment::new);
    
    // 5. 死の連鎖 - 撃破時に周囲へダメージ拡散
    public static final RegistryObject<Enchantment> DEATH_CHAIN = 
        REGISTRY.register("death_chain", DeathChainEnchantment::new);
    
    // 6. 天罰の鉄槌 - 攻撃時レベル*5%で金床落下
    public static final RegistryObject<Enchantment> ANVIL_FROM_HEAVEN = 
        REGISTRY.register("anvil_from_heaven", AnvilFromHeavenEnchantment::new);
    
    // 7. 呪詛の反撃 - 被ダメ時レベル%で周囲にデバフ
    public static final RegistryObject<Enchantment> CURSED_RETALIATION = 
        REGISTRY.register("cursed_retaliation", CursedRetaliationEnchantment::new);
    
    // 8. 幸運の星 - 攻撃時5%でレベル個のバフ付与
    public static final RegistryObject<Enchantment> LUCKY_STAR = 
        REGISTRY.register("lucky_star", LuckyStarEnchantment::new);
    
    // 9. マナビーム - 攻撃時レベル%でマナ消費してビーム
    public static final RegistryObject<Enchantment> MANA_BEAM = 
        REGISTRY.register("mana_beam", ManaBeamEnchantment::new);
    
    // 10. 星の昇華 - 他エンチャントのレベルを上げる
    public static final RegistryObject<Enchantment> STELLAR_ASCENSION = 
        REGISTRY.register("stellar_ascension", StellarAscensionEnchantment::new);
}
