package net.mcreator.asterrisk.registry;

import net.mcreator.asterrisk.AsterRiskMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * スポーンエッグの登録
 */
public class ModSpawnEggs {
    public static final DeferredRegister<Item> REGISTRY = 
        DeferredRegister.create(ForgeRegistries.ITEMS, AsterRiskMod.MODID);

    // ===== 友好Mob =====
    
    // 月うさぎのスポーンエッグ
    public static final RegistryObject<Item> MOON_RABBIT_SPAWN_EGG = REGISTRY.register("moon_rabbit_spawn_egg",
        () -> new ForgeSpawnEggItem(ModEntities.MOON_RABBIT, 
            0xC0C0E0, // 背景色（淡い青紫）
            0x87CEEB, // 斑点色（スカイブルー）
            new Item.Properties()));

    // 星の精霊のスポーンエッグ
    public static final RegistryObject<Item> STAR_SPIRIT_SPAWN_EGG = REGISTRY.register("star_spirit_spawn_egg",
        () -> new ForgeSpawnEggItem(ModEntities.STAR_SPIRIT, 
            0xFFD700, // 背景色（ゴールド）
            0xFFFFE0, // 斑点色（ライトイエロー）
            new Item.Properties()));

    // 月光の妖精のスポーンエッグ
    public static final RegistryObject<Item> MOONLIGHT_FAIRY_SPAWN_EGG = REGISTRY.register("moonlight_fairy_spawn_egg",
        () -> new ForgeSpawnEggItem(ModEntities.MOONLIGHT_FAIRY, 
            0x9370DB, // 背景色（ミディアムパープル）
            0xE6E6FA, // 斑点色（ラベンダー）
            new Item.Properties()));

    // ===== 敵対Mob =====
    
    // 日食の亡霊のスポーンエッグ
    public static final RegistryObject<Item> ECLIPSE_PHANTOM_SPAWN_EGG = REGISTRY.register("eclipse_phantom_spawn_egg",
        () -> new ForgeSpawnEggItem(ModEntities.ECLIPSE_PHANTOM, 
            0x1C1C2A, // 背景色（暗い紫黒）
            0x4A4A6A, // 斑点色（灰紫）
            new Item.Properties()));

    // 虚空の歩行者のスポーンエッグ
    public static final RegistryObject<Item> VOID_WALKER_SPAWN_EGG = REGISTRY.register("void_walker_spawn_egg",
        () -> new ForgeSpawnEggItem(ModEntities.VOID_WALKER, 
            0x0D0D15, // 背景色（ほぼ黒）
            0x6B238E, // 斑点色（ダークパープル）
            new Item.Properties()));

    // 堕落した月光石ゴーレムのスポーンエッグ
    public static final RegistryObject<Item> CORRUPTED_GOLEM_SPAWN_EGG = REGISTRY.register("corrupted_golem_spawn_egg",
        () -> new ForgeSpawnEggItem(ModEntities.CORRUPTED_GOLEM, 
            0x4A5568, // 背景色（暗い灰色）
            0x8B0000, // 斑点色（ダークレッド）
            new Item.Properties()));

    // ===== ボス =====
    
    // 月蝕の王のスポーンエッグ
    public static final RegistryObject<Item> ECLIPSE_MONARCH_SPAWN_EGG = REGISTRY.register("eclipse_monarch_spawn_egg",
        () -> new ForgeSpawnEggItem(ModEntities.ECLIPSE_MONARCH, 
            0x2D1B4E, // 背景色（深い紫）
            0xC9A227, // 斑点色（金色）
            new Item.Properties()));

    // 星喰らいのスポーンエッグ
    public static final RegistryObject<Item> STAR_DEVOURER_SPAWN_EGG = REGISTRY.register("star_devourer_spawn_egg",
        () -> new ForgeSpawnEggItem(ModEntities.STAR_DEVOURER, 
            0x0A0A1A, // 背景色（漆黒）
            0x4169E1, // 斑点色（ロイヤルブルー）
            new Item.Properties()));
}
