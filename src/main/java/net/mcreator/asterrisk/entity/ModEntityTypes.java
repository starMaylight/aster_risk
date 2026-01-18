package net.mcreator.asterrisk.entity;

import net.mcreator.asterrisk.AsterRiskMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * カスタムエンティティタイプの登録
 */
public class ModEntityTypes {
    
    public static final DeferredRegister<EntityType<?>> REGISTRY = 
        DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, AsterRiskMod.MODID);
    
    // 天罰の鉄槌の金床
    public static final RegistryObject<EntityType<HeavenlyAnvilEntity>> HEAVENLY_ANVIL = 
        REGISTRY.register("heavenly_anvil", () -> 
            EntityType.Builder.<HeavenlyAnvilEntity>of(HeavenlyAnvilEntity::new, MobCategory.MISC)
                .sized(0.98f, 0.98f)
                .clientTrackingRange(10)
                .updateInterval(20)
                .build("heavenly_anvil")
        );
}
