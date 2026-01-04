package net.mcreator.asterrisk.registry;

import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.enchantment.*;
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

    // === 武器エンチャント ===
    
    // 月光（武器：夜間ダメージ増加）
    public static final RegistryObject<Enchantment> MOONLIGHT = 
        REGISTRY.register("moonlight", MoonlightEnchantment::new);
    
    // 虚空の触手（武器：虚空の腐食を付与）
    public static final RegistryObject<Enchantment> VOID_TOUCH = 
        REGISTRY.register("void_touch", VoidTouchEnchantment::new);
    
    // 影吸収（武器：体力吸収）
    public static final RegistryObject<Enchantment> SHADOW_DRAIN = 
        REGISTRY.register("shadow_drain", ShadowDrainEnchantment::new);
    
    // 虹色の一撃（武器：虹色の輝きを自分に付与）
    public static final RegistryObject<Enchantment> PRISMATIC_STRIKE = 
        REGISTRY.register("prismatic_strike", PrismaticStrikeEnchantment::new);
    
    // === 防具エンチャント ===
    
    // 天体の守護（防具：ダメージ軽減）
    public static final RegistryObject<Enchantment> CELESTIAL_PROTECTION = 
        REGISTRY.register("celestial_protection", CelestialProtectionEnchantment::new);
    
    // 星の守護（防具：ダメージ時に星の加護付与）
    public static final RegistryObject<Enchantment> STELLAR_WARD = 
        REGISTRY.register("stellar_ward", StellarWardEnchantment::new);
    
    // マナの泉（チェスト：マナ回復速度上昇）
    public static final RegistryObject<Enchantment> MANA_SPRING = 
        REGISTRY.register("mana_spring", ManaSpringEnchantment::new);
    
    // 月光の歩み（ブーツ：落下ダメージ軽減、夜間速度上昇）
    public static final RegistryObject<Enchantment> LUNAR_STRIDE = 
        REGISTRY.register("lunar_stride", LunarStrideEnchantment::new);
    
    // === ツールエンチャント ===
    
    // 星屑（道具：幸運効果）
    public static final RegistryObject<Enchantment> STARDUST_FORTUNE = 
        REGISTRY.register("stardust_fortune", StardustFortuneEnchantment::new);
    
    // === 汎用エンチャント ===
    
    // 月の引力（全装備：アイテム吸引）
    public static final RegistryObject<Enchantment> LUNAR_GRAVITY = 
        REGISTRY.register("lunar_gravity", LunarGravityEnchantment::new);
}
