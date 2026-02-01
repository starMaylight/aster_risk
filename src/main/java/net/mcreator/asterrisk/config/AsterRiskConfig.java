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

    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final ClientConfig CLIENT;

    static {
        ForgeConfigSpec.Builder commonBuilder = new ForgeConfigSpec.Builder();
        COMMON = new CommonConfig(commonBuilder);
        COMMON_SPEC = commonBuilder.build();

        ForgeConfigSpec.Builder clientBuilder = new ForgeConfigSpec.Builder();
        CLIENT = new ClientConfig(clientBuilder);
        CLIENT_SPEC = clientBuilder.build();
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_SPEC);
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

    /**
     * クライアント設定（各クライアントで個別に設定）
     */
    public static class ClientConfig {

        // マナHUD設定
        public final ForgeConfigSpec.BooleanValue manaHudEnabled;
        public final ForgeConfigSpec.EnumValue<ManaHudPosition> manaHudPosition;
        public final ForgeConfigSpec.IntValue manaHudCustomX;
        public final ForgeConfigSpec.IntValue manaHudCustomY;
        public final ForgeConfigSpec.DoubleValue manaHudScale;

        public ClientConfig(ForgeConfigSpec.Builder builder) {
            builder.push("mana_hud");

            manaHudEnabled = builder
                .comment("Enable the mana HUD overlay")
                .comment("マナHUDオーバーレイを有効にする")
                .define("enabled", true);

            manaHudPosition = builder
                .comment("Preset position for the mana bar")
                .comment("マナバーのプリセット位置")
                .comment("Options: TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, TOP_CENTER, CUSTOM")
                .defineEnum("position", ManaHudPosition.TOP_LEFT);

            manaHudCustomX = builder
                .comment("Custom X position (only used when position is CUSTOM)")
                .comment("カスタムX座標（positionがCUSTOMの場合のみ使用）")
                .defineInRange("customX", 10, 0, 4096);

            manaHudCustomY = builder
                .comment("Custom Y position (only used when position is CUSTOM)")
                .comment("カスタムY座標（positionがCUSTOMの場合のみ使用）")
                .defineInRange("customY", 10, 0, 2160);

            manaHudScale = builder
                .comment("Scale of the mana HUD (0.5 = half size, 2.0 = double size)")
                .comment("マナHUDのスケール（0.5 = 半分、2.0 = 2倍）")
                .defineInRange("scale", 1.0, 0.5, 2.0);

            builder.pop();
        }
    }

    /**
     * マナHUDの位置プリセット
     */
    public enum ManaHudPosition {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        TOP_CENTER,
        CUSTOM
    }
}
