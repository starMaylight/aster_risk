package net.mcreator.asterrisk.registry;

import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.particle.*;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Aster Risk Modのカスタムパーティクル登録
 */
public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> REGISTRY = 
        DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, AsterRiskMod.MODID);

    // === 月光系パーティクル ===
    
    // 月光の輝き（青白い光の粒子）
    public static final RegistryObject<SimpleParticleType> LUNAR_SPARKLE = 
        REGISTRY.register("lunar_sparkle", () -> new SimpleParticleType(false));
    
    // 月の塵（ゆっくり落ちる青い粒子）
    public static final RegistryObject<SimpleParticleType> LUNAR_DUST = 
        REGISTRY.register("lunar_dust", () -> new SimpleParticleType(false));
    
    // 月光オーラ（プレイヤー周囲の淡い光）
    public static final RegistryObject<SimpleParticleType> LUNAR_AURA = 
        REGISTRY.register("lunar_aura", () -> new SimpleParticleType(false));
    
    // === 星屑系パーティクル ===
    
    // 星屑の輝き（金色の小さな星）
    public static final RegistryObject<SimpleParticleType> STARDUST_SPARKLE = 
        REGISTRY.register("stardust_sparkle", () -> new SimpleParticleType(false));
    
    // 流れ星（尾を引く光）
    public static final RegistryObject<SimpleParticleType> SHOOTING_STAR = 
        REGISTRY.register("shooting_star", () -> new SimpleParticleType(false));
    
    // 星の爆発（星型に広がる粒子）
    public static final RegistryObject<SimpleParticleType> STAR_BURST = 
        REGISTRY.register("star_burst", () -> new SimpleParticleType(false));
    
    // === マナ系パーティクル ===
    
    // マナの流れ（機械間を流れる青い光）
    public static final RegistryObject<SimpleParticleType> MANA_FLOW = 
        REGISTRY.register("mana_flow", () -> new SimpleParticleType(false));
    
    // マナ吸収（収束する光）
    public static final RegistryObject<SimpleParticleType> MANA_ABSORB = 
        REGISTRY.register("mana_absorb", () -> new SimpleParticleType(false));
    
    // マナ放出（拡散する光）
    public static final RegistryObject<SimpleParticleType> MANA_RELEASE = 
        REGISTRY.register("mana_release", () -> new SimpleParticleType(false));
    
    // === 虚空系パーティクル ===
    
    // 虚空の霧（暗紫色の霧）
    public static final RegistryObject<SimpleParticleType> VOID_MIST = 
        REGISTRY.register("void_mist", () -> new SimpleParticleType(false));
    
    // 虚空の渦（回転する暗い粒子）
    public static final RegistryObject<SimpleParticleType> VOID_SPIRAL = 
        REGISTRY.register("void_spiral", () -> new SimpleParticleType(false));
    
    // 腐食エフェクト（紫の煙）
    public static final RegistryObject<SimpleParticleType> CORRUPTION = 
        REGISTRY.register("corruption", () -> new SimpleParticleType(false));
    
    // === 影系パーティクル ===
    
    // 影の軌跡（黒い煙の軌跡）
    public static final RegistryObject<SimpleParticleType> SHADOW_TRAIL = 
        REGISTRY.register("shadow_trail", () -> new SimpleParticleType(false));
    
    // 影の爆発（黒い霧の爆発）
    public static final RegistryObject<SimpleParticleType> SHADOW_BURST = 
        REGISTRY.register("shadow_burst", () -> new SimpleParticleType(false));
    
    // === 特殊パーティクル ===
    
    // 虹色の輝き（色が変化する粒子）
    public static final RegistryObject<SimpleParticleType> PRISMATIC_SPARKLE = 
        REGISTRY.register("prismatic_sparkle", () -> new SimpleParticleType(false));
    
    // 儀式エフェクト（祭壇周囲のルーン）
    public static final RegistryObject<SimpleParticleType> RITUAL_RUNE = 
        REGISTRY.register("ritual_rune", () -> new SimpleParticleType(false));
    
    // 隕石の軌跡（炎と煙の軌跡）
    public static final RegistryObject<SimpleParticleType> METEOR_TRAIL = 
        REGISTRY.register("meteor_trail", () -> new SimpleParticleType(false));
}
