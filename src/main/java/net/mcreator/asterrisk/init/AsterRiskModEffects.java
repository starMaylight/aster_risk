package net.mcreator.asterrisk.init;

import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.effect.*;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Aster Risk Modのエフェクト登録
 */
public class AsterRiskModEffects {
    public static final DeferredRegister<MobEffect> REGISTRY = 
        DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, AsterRiskMod.MODID);

    // === ポジティブエフェクト ===
    
    // 月光の祝福 - マナ回復速度上昇、夜間に体力回復
    public static final RegistryObject<MobEffect> LUNAR_BLESSING = 
        REGISTRY.register("lunar_blessing", LunarBlessingEffect::new);
    
    // 星屑の守り - ダメージ軽減
    public static final RegistryObject<MobEffect> STARDUST_PROTECTION = 
        REGISTRY.register("stardust_protection", StardustProtectionEffect::new);
    
    // 天体の守護 - 全体的な防御強化
    public static final RegistryObject<MobEffect> CELESTIAL_GUARD = 
        REGISTRY.register("celestial_guard", CelestialGuardEffect::new);
    
    // 星の加護 - 攻撃力・幸運上昇
    public static final RegistryObject<MobEffect> STELLAR_BLESSING = 
        REGISTRY.register("stellar_blessing", StellarBlessingEffect::new);
    
    // 月光浮遊 - 落下ダメージ無効、緩やかに浮遊
    public static final RegistryObject<MobEffect> LUNAR_LEVITATION = 
        REGISTRY.register("lunar_levitation", LunarLevitationEffect::new);
    
    // 隕石の力 - 攻撃力大幅上昇、ノックバック耐性
    public static final RegistryObject<MobEffect> METEORITE_POWER = 
        REGISTRY.register("meteorite_power", MeteoritePowerEffect::new);
    
    // マナバースト - マナ急速回復
    public static final RegistryObject<MobEffect> MANA_BURST = 
        REGISTRY.register("mana_burst", ManaBurstEffect::new);
    
    // 銀の輝き - アンデッド特効、攻撃力上昇
    public static final RegistryObject<MobEffect> SILVER_SHINE = 
        REGISTRY.register("silver_shine", SilverShineEffect::new);
    
    // 虹色の輝き - 全ステータス微上昇
    public static final RegistryObject<MobEffect> PRISMATIC_GLOW = 
        REGISTRY.register("prismatic_glow", PrismaticGlowEffect::new);
    
    // === ネガティブエフェクト ===
    
    // 月蝕の呪い - マナ減少
    public static final RegistryObject<MobEffect> LUNAR_ECLIPSE_CURSE = 
        REGISTRY.register("lunar_eclipse_curse", LunarEclipseCurseEffect::new);
    
    // 星なき夜 - 暗視無効化、視界低下
    public static final RegistryObject<MobEffect> STARLESS_NIGHT = 
        REGISTRY.register("starless_night", StarlessNightEffect::new);
    
    // 虚空の腐食 - 最大体力・防御力低下
    public static final RegistryObject<MobEffect> VOID_CORRUPTION = 
        REGISTRY.register("void_corruption", VoidCorruptionEffect::new);
    
    // 月蝕の呪い（ボス版） - 強力なマナ減少
    public static final RegistryObject<MobEffect> ECLIPSE_CURSE = 
        REGISTRY.register("eclipse_curse", EclipseCurseEffect::new);
    
    // === 中立エフェクト ===
    
    // 影の抱擁 - 速度上昇だが体力減少
    public static final RegistryObject<MobEffect> SHADOW_EMBRACE = 
        REGISTRY.register("shadow_embrace", ShadowEmbraceEffect::new);
}
