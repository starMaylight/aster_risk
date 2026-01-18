package net.mcreator.asterrisk.datagen;

import net.mcreator.asterrisk.AsterRiskMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

/**
 * データ生成イベントハンドラー
 */
@Mod.EventBusSubscriber(modid = AsterRiskMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        
        // パターンデータ生成（サーバー側）
        generator.addProvider(event.includeServer(), new PatternDataProvider(output));
        
        // 天体エンチャントレシピ生成（サーバー側）
        generator.addProvider(event.includeServer(), new CelestialEnchantRecipeProvider(output));
        
        // 専用エンチャントレシピ生成（サーバー側）
        generator.addProvider(event.includeServer(), new ExclusiveEnchantRecipeProvider(output));
        
        AsterRiskMod.LOGGER.info("Registered Aster Risk data generators");
    }
}
