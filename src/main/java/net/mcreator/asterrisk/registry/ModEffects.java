package net.mcreator.asterrisk.registry;

import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.effect.*;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final DeferredRegister<MobEffect> REGISTRY =
        DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, AsterRiskMod.MODID);

    // === ポジティブエフェクト ===

    public static final RegistryObject<MobEffect> LUNAR_BLESSING =
        REGISTRY.register("lunar_blessing", LunarBlessingEffect::new);

    public static final RegistryObject<MobEffect> STARDUST_PROTECTION =
        REGISTRY.register("stardust_protection", StardustProtectionEffect::new);

    public static final RegistryObject<MobEffect> CELESTIAL_GUARD =
        REGISTRY.register("celestial_guard", CelestialGuardEffect::new);

    public static final RegistryObject<MobEffect> STELLAR_BLESSING =
        REGISTRY.register("stellar_blessing", StellarBlessingEffect::new);

    public static final RegistryObject<MobEffect> LUNAR_LEVITATION =
        REGISTRY.register("lunar_levitation", LunarLevitationEffect::new);

    public static final RegistryObject<MobEffect> METEORITE_POWER =
        REGISTRY.register("meteorite_power", MeteoritePowerEffect::new);

    public static final RegistryObject<MobEffect> MANA_BURST =
        REGISTRY.register("mana_burst", ManaBurstEffect::new);

    public static final RegistryObject<MobEffect> SILVER_SHINE =
        REGISTRY.register("silver_shine", SilverShineEffect::new);

    public static final RegistryObject<MobEffect> PRISMATIC_GLOW =
        REGISTRY.register("prismatic_glow", PrismaticGlowEffect::new);

    // === ネガティブエフェクト ===

    public static final RegistryObject<MobEffect> LUNAR_ECLIPSE_CURSE =
        REGISTRY.register("lunar_eclipse_curse", LunarEclipseCurseEffect::new);

    public static final RegistryObject<MobEffect> STARLESS_NIGHT =
        REGISTRY.register("starless_night", StarlessNightEffect::new);

    public static final RegistryObject<MobEffect> VOID_CORRUPTION =
        REGISTRY.register("void_corruption", VoidCorruptionEffect::new);

    public static final RegistryObject<MobEffect> ECLIPSE_CURSE =
        REGISTRY.register("eclipse_curse", EclipseCurseEffect::new);

    // === 中立エフェクト ===

    public static final RegistryObject<MobEffect> SHADOW_EMBRACE =
        REGISTRY.register("shadow_embrace", ShadowEmbraceEffect::new);
}
