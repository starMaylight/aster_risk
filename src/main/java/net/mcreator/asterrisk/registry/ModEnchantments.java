package net.mcreator.asterrisk.registry;

import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.enchantment.MoonlightEnchantment;
import net.mcreator.asterrisk.enchantment.StardustFortuneEnchantment;
import net.mcreator.asterrisk.enchantment.CelestialProtectionEnchantment;
import net.mcreator.asterrisk.enchantment.LunarGravityEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Aster Risk Modのエンチャント登録
 * ※initフォルダはMCreatorに上書きされるためregistryに配置
 */
public class ModEnchantments {
    public static final DeferredRegister<Enchantment> REGISTRY = 
        DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, AsterRiskMod.MODID);

    // 月光（武器：夜間ダメージ増加）
    public static final RegistryObject<Enchantment> MOONLIGHT = 
        REGISTRY.register("moonlight", MoonlightEnchantment::new);
    
    // 星屑（道具：幸運効果）
    public static final RegistryObject<Enchantment> STARDUST_FORTUNE = 
        REGISTRY.register("stardust_fortune", StardustFortuneEnchantment::new);
    
    // 天体の守護（防具：ダメージ軽減）
    public static final RegistryObject<Enchantment> CELESTIAL_PROTECTION = 
        REGISTRY.register("celestial_protection", CelestialProtectionEnchantment::new);
    
    // 月の引力（全装備：アイテム吸引）
    public static final RegistryObject<Enchantment> LUNAR_GRAVITY = 
        REGISTRY.register("lunar_gravity", LunarGravityEnchantment::new);
}
