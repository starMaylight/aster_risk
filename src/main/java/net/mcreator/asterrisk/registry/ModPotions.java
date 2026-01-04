package net.mcreator.asterrisk.registry;

import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.init.AsterRiskModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Aster Risk Modのポーション登録
 * ※initフォルダはMCreatorに上書きされるためregistryに配置
 */
public class ModPotions {
    public static final DeferredRegister<Potion> REGISTRY = 
        DeferredRegister.create(ForgeRegistries.POTIONS, AsterRiskMod.MODID);

    // === 月光系ポーション ===
    
    // 月光の祝福ポーション（3分）
    public static final RegistryObject<Potion> LUNAR_BLESSING = REGISTRY.register("lunar_blessing",
        () -> new Potion(new MobEffectInstance(AsterRiskModEffects.LUNAR_BLESSING.get(), 3600, 0)));
    
    // 強化版（5分、レベル2）
    public static final RegistryObject<Potion> LUNAR_BLESSING_STRONG = REGISTRY.register("lunar_blessing_strong",
        () -> new Potion(new MobEffectInstance(AsterRiskModEffects.LUNAR_BLESSING.get(), 6000, 1)));
    
    // 月光浮遊ポーション（1分）
    public static final RegistryObject<Potion> LUNAR_LEVITATION = REGISTRY.register("lunar_levitation",
        () -> new Potion(new MobEffectInstance(AsterRiskModEffects.LUNAR_LEVITATION.get(), 1200, 0)));
    
    // 強化版（2分）
    public static final RegistryObject<Potion> LUNAR_LEVITATION_LONG = REGISTRY.register("lunar_levitation_long",
        () -> new Potion(new MobEffectInstance(AsterRiskModEffects.LUNAR_LEVITATION.get(), 2400, 0)));
    
    // === 星屑系ポーション ===
    
    // 星の加護ポーション（3分）
    public static final RegistryObject<Potion> STELLAR_BLESSING = REGISTRY.register("stellar_blessing",
        () -> new Potion(new MobEffectInstance(AsterRiskModEffects.STELLAR_BLESSING.get(), 3600, 0)));
    
    // 強化版（3分、レベル2）
    public static final RegistryObject<Potion> STELLAR_BLESSING_STRONG = REGISTRY.register("stellar_blessing_strong",
        () -> new Potion(new MobEffectInstance(AsterRiskModEffects.STELLAR_BLESSING.get(), 3600, 1)));
    
    // 星屑の守りポーション（3分）
    public static final RegistryObject<Potion> STARDUST_PROTECTION = REGISTRY.register("stardust_protection",
        () -> new Potion(new MobEffectInstance(AsterRiskModEffects.STARDUST_PROTECTION.get(), 3600, 0)));
    
    // === マナ系ポーション ===
    
    // マナバーストポーション（30秒）
    public static final RegistryObject<Potion> MANA_BURST = REGISTRY.register("mana_burst",
        () -> new Potion(new MobEffectInstance(AsterRiskModEffects.MANA_BURST.get(), 600, 0)));
    
    // 強化版（30秒、レベル2）
    public static final RegistryObject<Potion> MANA_BURST_STRONG = REGISTRY.register("mana_burst_strong",
        () -> new Potion(new MobEffectInstance(AsterRiskModEffects.MANA_BURST.get(), 600, 1)));
    
    // === 戦闘系ポーション ===
    
    // 隕石の力ポーション（1分30秒）
    public static final RegistryObject<Potion> METEORITE_POWER = REGISTRY.register("meteorite_power",
        () -> new Potion(new MobEffectInstance(AsterRiskModEffects.METEORITE_POWER.get(), 1800, 0)));
    
    // 強化版（1分30秒、レベル2）
    public static final RegistryObject<Potion> METEORITE_POWER_STRONG = REGISTRY.register("meteorite_power_strong",
        () -> new Potion(new MobEffectInstance(AsterRiskModEffects.METEORITE_POWER.get(), 1800, 1)));
    
    // 銀の輝きポーション（3分）
    public static final RegistryObject<Potion> SILVER_SHINE = REGISTRY.register("silver_shine",
        () -> new Potion(new MobEffectInstance(AsterRiskModEffects.SILVER_SHINE.get(), 3600, 0)));
    
    // 虹色の輝きポーション（2分）
    public static final RegistryObject<Potion> PRISMATIC_GLOW = REGISTRY.register("prismatic_glow",
        () -> new Potion(new MobEffectInstance(AsterRiskModEffects.PRISMATIC_GLOW.get(), 2400, 0)));
    
    // === 特殊ポーション ===
    
    // 影の抱擁ポーション（45秒）- リスクあり
    public static final RegistryObject<Potion> SHADOW_EMBRACE = REGISTRY.register("shadow_embrace",
        () -> new Potion(new MobEffectInstance(AsterRiskModEffects.SHADOW_EMBRACE.get(), 900, 0)));
    
    // 天体の守護ポーション（3分）
    public static final RegistryObject<Potion> CELESTIAL_GUARD = REGISTRY.register("celestial_guard",
        () -> new Potion(new MobEffectInstance(AsterRiskModEffects.CELESTIAL_GUARD.get(), 3600, 0)));
}
