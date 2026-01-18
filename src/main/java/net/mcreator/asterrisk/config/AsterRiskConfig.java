package net.mcreator.asterrisk.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

/**
 * Aster Riskのコンフィグ設定
 */
public class AsterRiskConfig {
    
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final CommonConfig COMMON;
    
    static {
        ForgeConfigSpec.Builder commonBuilder = new ForgeConfigSpec.Builder();
        COMMON = new CommonConfig(commonBuilder);
        COMMON_SPEC = commonBuilder.build();
    }
    
    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_SPEC);
    }
    
    /**
     * 共通設定
     */
    public static class CommonConfig {
        
        // デバッグ設定
        public final ForgeConfigSpec.BooleanValue enableDebugMessages;
        public final ForgeConfigSpec.BooleanValue enableRitualCircleDebug;
        public final ForgeConfigSpec.BooleanValue enableCelestialEnchantDebug;
        public final ForgeConfigSpec.BooleanValue enableFocusChamberDebug;
        public final ForgeConfigSpec.BooleanValue enableManaDebug;
        
        public CommonConfig(ForgeConfigSpec.Builder builder) {
            builder.push("debug");
            
            enableDebugMessages = builder
                .comment("Enable all debug messages (master switch)")
                .define("enableDebugMessages", false);
            
            enableRitualCircleDebug = builder
                .comment("Enable Ritual Circle debug info on Shift+Click")
                .define("enableRitualCircleDebug", false);
            
            enableCelestialEnchantDebug = builder
                .comment("Enable Celestial Enchanting Table debug info on Shift+Click")
                .define("enableCelestialEnchantDebug", false);
            
            enableFocusChamberDebug = builder
                .comment("Enable Focus Chamber Core debug info on Shift+Click")
                .define("enableFocusChamberDebug", false);
            
            enableManaDebug = builder
                .comment("Enable mana system debug messages in console")
                .define("enableManaDebug", false);
            
            builder.pop();
        }
    }
    
    // ===== Helper Methods =====
    
    /**
     * デバッグメッセージが有効かどうか
     */
    public static boolean isDebugEnabled() {
        return COMMON.enableDebugMessages.get();
    }
    
    /**
     * RitualCircleのデバッグが有効かどうか
     */
    public static boolean isRitualCircleDebugEnabled() {
        return COMMON.enableRitualCircleDebug.get();
    }
    
    /**
     * CelestialEnchantのデバッグが有効かどうか
     */
    public static boolean isCelestialEnchantDebugEnabled() {
        return COMMON.enableCelestialEnchantDebug.get();
    }
    
    /**
     * FocusChamberのデバッグが有効かどうか
     */
    public static boolean isFocusChamberDebugEnabled() {
        return COMMON.enableFocusChamberDebug.get();
    }
    
    /**
     * Manaデバッグが有効かどうか
     */
    public static boolean isManaDebugEnabled() {
        return COMMON.enableManaDebug.get() || COMMON.enableDebugMessages.get();
    }
}
