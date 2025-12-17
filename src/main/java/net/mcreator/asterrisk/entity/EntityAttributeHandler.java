package net.mcreator.asterrisk.entity;

import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.registry.ModEntities;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * エンティティの属性登録ハンドラ
 */
@Mod.EventBusSubscriber(modid = AsterRiskMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityAttributeHandler {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        // 友好Mob
        event.put(ModEntities.MOON_RABBIT.get(), MoonRabbitEntity.createAttributes().build());
        event.put(ModEntities.STAR_SPIRIT.get(), StarSpiritEntity.createAttributes().build());
        event.put(ModEntities.MOONLIGHT_FAIRY.get(), MoonlightFairyEntity.createAttributes().build());
        
        // 敵対Mob
        event.put(ModEntities.ECLIPSE_PHANTOM.get(), EclipsePhantomEntity.createAttributes().build());
        event.put(ModEntities.VOID_WALKER.get(), VoidWalkerEntity.createAttributes().build());
        event.put(ModEntities.CORRUPTED_GOLEM.get(), CorruptedGolemEntity.createAttributes().build());
        
        // ボス
        event.put(ModEntities.ECLIPSE_MONARCH.get(), EclipseMonarchEntity.createAttributes().build());
        event.put(ModEntities.STAR_DEVOURER.get(), StarDevourerEntity.createAttributes().build());
    }
}
