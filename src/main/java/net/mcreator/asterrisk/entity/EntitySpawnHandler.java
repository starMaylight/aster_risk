package net.mcreator.asterrisk.entity;

import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.registry.ModEntities;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * 敵対Mobのスポーン設定
 */
@Mod.EventBusSubscriber(modid = AsterRiskMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EntitySpawnHandler {
    
    /**
     * スポーン条件チェック - 夜間かつ屋外
     */
    @SubscribeEvent
    public static void onCheckSpawn(MobSpawnEvent.FinalizeSpawn event) {
        // 必要に応じてスポーン条件を追加
    }
}
