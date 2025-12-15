package net.mcreator.asterrisk.init;

import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.effect.LunarBlessingEffect;
import net.mcreator.asterrisk.effect.StardustProtectionEffect;
import net.mcreator.asterrisk.effect.CelestialGuardEffect;
import net.mcreator.asterrisk.effect.LunarEclipseCurseEffect;
import net.mcreator.asterrisk.effect.StarlessNightEffect;
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

    // ポジティブエフェクト
    public static final RegistryObject<MobEffect> LUNAR_BLESSING = 
        REGISTRY.register("lunar_blessing", LunarBlessingEffect::new);
    
    public static final RegistryObject<MobEffect> STARDUST_PROTECTION = 
        REGISTRY.register("stardust_protection", StardustProtectionEffect::new);
    
    public static final RegistryObject<MobEffect> CELESTIAL_GUARD = 
        REGISTRY.register("celestial_guard", CelestialGuardEffect::new);

    // ネガティブエフェクト
    public static final RegistryObject<MobEffect> LUNAR_ECLIPSE_CURSE = 
        REGISTRY.register("lunar_eclipse_curse", LunarEclipseCurseEffect::new);
    
    public static final RegistryObject<MobEffect> STARLESS_NIGHT = 
        REGISTRY.register("starless_night", StarlessNightEffect::new);
}
