package net.mcreator.asterrisk.world;

import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.registry.ModEntities;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Mod Mobのスポーン条件設定
 */
@Mod.EventBusSubscriber(modid = AsterRiskMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModSpawnPlacements {
    
    @SubscribeEvent
    public static void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {
        // Eclipse Phantom - 夜間に空中スポーン
        event.register(
            ModEntities.ECLIPSE_PHANTOM.get(),
            SpawnPlacements.Type.ON_GROUND,
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            (entityType, level, spawnType, pos, random) -> {
                // 夜間のみスポーン（時間チェック）
                long dayTime = level.getLevel().getDayTime() % 24000;
                boolean isNight = dayTime >= 13000 && dayTime < 23000;
                return isNight && 
                       Monster.checkMonsterSpawnRules(entityType, level, spawnType, pos, random);
            },
            SpawnPlacementRegisterEvent.Operation.AND
        );
        
        // Void Walker - 暗い場所でスポーン
        event.register(
            ModEntities.VOID_WALKER.get(),
            SpawnPlacements.Type.ON_GROUND,
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            (entityType, level, spawnType, pos, random) -> {
                return level.getMaxLocalRawBrightness(pos) <= 7 &&
                       Monster.checkMonsterSpawnRules(entityType, level, spawnType, pos, random);
            },
            SpawnPlacementRegisterEvent.Operation.AND
        );
        
        // Corrupted Golem - 地下でスポーン
        event.register(
            ModEntities.CORRUPTED_GOLEM.get(),
            SpawnPlacements.Type.ON_GROUND,
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            (entityType, level, spawnType, pos, random) -> {
                return pos.getY() < 50 &&
                       Monster.checkMonsterSpawnRules(entityType, level, spawnType, pos, random);
            },
            SpawnPlacementRegisterEvent.Operation.AND
        );
    }
}
