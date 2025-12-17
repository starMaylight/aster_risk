package net.mcreator.asterrisk.registry;

import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.entity.MoonRabbitEntity;
import net.mcreator.asterrisk.entity.StarSpiritEntity;
import net.mcreator.asterrisk.entity.MoonlightFairyEntity;
import net.mcreator.asterrisk.entity.EclipsePhantomEntity;
import net.mcreator.asterrisk.entity.VoidWalkerEntity;
import net.mcreator.asterrisk.entity.CorruptedGolemEntity;
import net.mcreator.asterrisk.entity.EclipseMonarchEntity;
import net.mcreator.asterrisk.entity.StarDevourerEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Aster Risk Modのエンティティ登録
 */
public class ModEntities {
    public static final DeferredRegister<EntityType<?>> REGISTRY = 
        DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, AsterRiskMod.MODID);

    // ===== 友好Mob =====
    
    // 月うさぎ - Moon Rabbit
    public static final RegistryObject<EntityType<MoonRabbitEntity>> MOON_RABBIT = 
        REGISTRY.register("moon_rabbit", () -> EntityType.Builder
            .of(MoonRabbitEntity::new, MobCategory.CREATURE)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(8)
            .build(ResourceLocation.fromNamespaceAndPath(AsterRiskMod.MODID, "moon_rabbit").toString()));

    // 星の精霊 - Star Spirit
    public static final RegistryObject<EntityType<StarSpiritEntity>> STAR_SPIRIT = 
        REGISTRY.register("star_spirit", () -> EntityType.Builder
            .of(StarSpiritEntity::new, MobCategory.CREATURE)
            .sized(0.6F, 1.0F)
            .clientTrackingRange(10)
            .build(ResourceLocation.fromNamespaceAndPath(AsterRiskMod.MODID, "star_spirit").toString()));

    // 月光の妖精 - Moonlight Fairy
    public static final RegistryObject<EntityType<MoonlightFairyEntity>> MOONLIGHT_FAIRY = 
        REGISTRY.register("moonlight_fairy", () -> EntityType.Builder
            .of(MoonlightFairyEntity::new, MobCategory.CREATURE)
            .sized(0.4F, 0.6F)
            .clientTrackingRange(8)
            .build(ResourceLocation.fromNamespaceAndPath(AsterRiskMod.MODID, "moonlight_fairy").toString()));

    // ===== 敵対Mob =====
    
    // 日食の亡霊 - Eclipse Phantom（浮遊する幽霊）
    public static final RegistryObject<EntityType<EclipsePhantomEntity>> ECLIPSE_PHANTOM = 
        REGISTRY.register("eclipse_phantom", () -> EntityType.Builder
            .of(EclipsePhantomEntity::new, MobCategory.MONSTER)
            .sized(0.8F, 1.8F)
            .clientTrackingRange(10)
            .build(ResourceLocation.fromNamespaceAndPath(AsterRiskMod.MODID, "eclipse_phantom").toString()));

    // 虚空の歩行者 - Void Walker（テレポートする敵）
    public static final RegistryObject<EntityType<VoidWalkerEntity>> VOID_WALKER = 
        REGISTRY.register("void_walker", () -> EntityType.Builder
            .of(VoidWalkerEntity::new, MobCategory.MONSTER)
            .sized(0.6F, 2.2F)
            .clientTrackingRange(10)
            .build(ResourceLocation.fromNamespaceAndPath(AsterRiskMod.MODID, "void_walker").toString()));

    // 堕落した月光石ゴーレム - Corrupted Moonstone Golem
    public static final RegistryObject<EntityType<CorruptedGolemEntity>> CORRUPTED_GOLEM = 
        REGISTRY.register("corrupted_golem", () -> EntityType.Builder
            .of(CorruptedGolemEntity::new, MobCategory.MONSTER)
            .sized(1.4F, 2.7F)
            .clientTrackingRange(10)
            .build(ResourceLocation.fromNamespaceAndPath(AsterRiskMod.MODID, "corrupted_golem").toString()));

    // ===== ボス =====
    
    // 月蝕の王 - Eclipse Monarch
    public static final RegistryObject<EntityType<EclipseMonarchEntity>> ECLIPSE_MONARCH = 
        REGISTRY.register("eclipse_monarch", () -> EntityType.Builder
            .of(EclipseMonarchEntity::new, MobCategory.MONSTER)
            .sized(1.2F, 3.0F)
            .clientTrackingRange(16)
            .fireImmune()
            .build(ResourceLocation.fromNamespaceAndPath(AsterRiskMod.MODID, "eclipse_monarch").toString()));

    // 星喰らい - Star Devourer
    public static final RegistryObject<EntityType<StarDevourerEntity>> STAR_DEVOURER = 
        REGISTRY.register("star_devourer", () -> EntityType.Builder
            .of(StarDevourerEntity::new, MobCategory.MONSTER)
            .sized(2.0F, 4.0F)
            .clientTrackingRange(20)
            .fireImmune()
            .build(ResourceLocation.fromNamespaceAndPath(AsterRiskMod.MODID, "star_devourer").toString()));
}
