package net.mcreator.asterrisk.block.entity;

/**
 * オベリスクのエネルギータイプ
 */
public enum ObeliskEnergyType {
    LUNAR("lunar", 0x87CEEB),      // 月 - 夜間（満月時ボーナス）
    STELLAR("stellar", 0xFFD700),   // 星 - 夜間（晴天時）
    SOLAR("solar", 0xFF6B35),       // 太陽 - 昼間
    VOID("void", 0x4B0082);         // 新月/虚空 - 新月の夜

    private final String name;
    private final int color;

    ObeliskEnergyType(String name, int color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public static ObeliskEnergyType fromName(String name) {
        for (ObeliskEnergyType type : values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        return null;
    }
}
