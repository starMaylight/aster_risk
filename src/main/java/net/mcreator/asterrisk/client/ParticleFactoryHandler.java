package net.mcreator.asterrisk.client;

import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.particle.*;
import net.mcreator.asterrisk.registry.ModParticles;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * クライアント側でのパーティクルプロバイダー登録
 */
@Mod.EventBusSubscriber(modid = AsterRiskMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ParticleFactoryHandler {
    
    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        // 月光系
        event.registerSpriteSet(ModParticles.LUNAR_SPARKLE.get(), LunarSparkleParticle.Provider::new);
        event.registerSpriteSet(ModParticles.LUNAR_DUST.get(), LunarSparkleParticle.Provider::new);
        event.registerSpriteSet(ModParticles.LUNAR_AURA.get(), LunarSparkleParticle.Provider::new);
        
        // 星屑系
        event.registerSpriteSet(ModParticles.STARDUST_SPARKLE.get(), StardustSparkleParticle.Provider::new);
        event.registerSpriteSet(ModParticles.SHOOTING_STAR.get(), StardustSparkleParticle.Provider::new);
        event.registerSpriteSet(ModParticles.STAR_BURST.get(), StardustSparkleParticle.Provider::new);
        
        // マナ系
        event.registerSpriteSet(ModParticles.MANA_FLOW.get(), ManaFlowParticle.Provider::new);
        event.registerSpriteSet(ModParticles.MANA_ABSORB.get(), ManaFlowParticle.Provider::new);
        event.registerSpriteSet(ModParticles.MANA_RELEASE.get(), ManaFlowParticle.Provider::new);
        
        // 虚空系
        event.registerSpriteSet(ModParticles.VOID_MIST.get(), VoidMistParticle.Provider::new);
        event.registerSpriteSet(ModParticles.VOID_SPIRAL.get(), VoidMistParticle.Provider::new);
        event.registerSpriteSet(ModParticles.CORRUPTION.get(), VoidMistParticle.Provider::new);
        
        // 影系
        event.registerSpriteSet(ModParticles.SHADOW_TRAIL.get(), VoidMistParticle.Provider::new);
        event.registerSpriteSet(ModParticles.SHADOW_BURST.get(), VoidMistParticle.Provider::new);
        
        // 特殊
        event.registerSpriteSet(ModParticles.PRISMATIC_SPARKLE.get(), PrismaticSparkleParticle.Provider::new);
        event.registerSpriteSet(ModParticles.RITUAL_RUNE.get(), StardustSparkleParticle.Provider::new);
        event.registerSpriteSet(ModParticles.METEOR_TRAIL.get(), StardustSparkleParticle.Provider::new);
    }
}
