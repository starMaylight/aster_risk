package net.mcreator.asterrisk.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * ワールド状態に関するヘルパーメソッド
 */
public final class WorldHelper {
    
    private WorldHelper() {} // インスタンス化防止
    
    /**
     * 現在が夜かどうかを判定
     * @param level ワールド
     * @return 夜間の場合true
     */
    public static boolean isNight(Level level) {
        if (level == null) return false;
        long dayTime = level.getDayTime() % AsterRiskConstants.DAY_LENGTH;
        return dayTime >= AsterRiskConstants.NIGHT_START && dayTime < AsterRiskConstants.NIGHT_END;
    }
    
    /**
     * 現在が昼かどうかを判定
     * @param level ワールド
     * @return 昼間の場合true
     */
    public static boolean isDay(Level level) {
        return !isNight(level);
    }
    
    /**
     * 月齢に応じたマナ倍率を取得
     * @param level ワールド
     * @return マナ倍率（満月で最大、新月で最小）
     */
    public static float getMoonPhaseMultiplier(Level level) {
        if (level == null) return 1.0f;
        int moonPhase = level.getMoonPhase();
        return MOON_PHASE_MULTIPLIERS[moonPhase];
    }
    
    /** 月相ごとのマナ倍率 */
    private static final float[] MOON_PHASE_MULTIPLIERS = {
        2.0f,   // 0: 満月
        1.75f,  // 1: 更待月
        1.5f,   // 2: 下弦
        1.0f,   // 3: 有明月
        0.25f,  // 4: 新月
        1.0f,   // 5: 三日月
        1.5f,   // 6: 上弦
        1.75f   // 7: 十三夜
    };
    
    /**
     * 月相名を取得
     * @param moonPhase 月相インデックス（0-7）
     * @return 月相名
     */
    public static String getMoonPhaseName(int moonPhase) {
        return switch (moonPhase) {
            case 0 -> "Full Moon";
            case 1 -> "Waning Gibbous";
            case 2 -> "Last Quarter";
            case 3 -> "Waning Crescent";
            case 4 -> "New Moon";
            case 5 -> "Waxing Crescent";
            case 6 -> "First Quarter";
            case 7 -> "Waxing Gibbous";
            default -> "Unknown";
        };
    }
    
    /**
     * 月相の日本語名を取得
     * @param moonPhase 月相インデックス（0-7）
     * @return 月相名（日本語）
     */
    public static String getMoonPhaseNameJp(int moonPhase) {
        return switch (moonPhase) {
            case 0 -> "満月";
            case 1 -> "更待月";
            case 2 -> "下弦";
            case 3 -> "有明月";
            case 4 -> "新月";
            case 5 -> "三日月";
            case 6 -> "上弦";
            case 7 -> "十三夜";
            default -> "不明";
        };
    }
    
    /**
     * 空が見えるかどうかを判定
     * @param level ワールド
     * @param pos 判定位置
     * @return 空が見える場合true
     */
    public static boolean canSeeSky(Level level, BlockPos pos) {
        if (level == null) return false;
        return level.canSeeSky(pos);
    }
    
    /**
     * 空が見える+夜間のマナボーナス計算
     * @param level ワールド
     * @param pos 判定位置
     * @return 総合倍率
     */
    public static float calculateManaBonus(Level level, BlockPos pos) {
        float multiplier = 1.0f;
        
        // 夜間ボーナス
        if (isNight(level)) {
            multiplier *= AsterRiskConstants.NIGHT_MANA_BONUS;
            
            // 月相ボーナス
            multiplier *= getMoonPhaseMultiplier(level);
        }
        
        // 空が見えるボーナス
        if (canSeeSky(level, pos)) {
            multiplier *= AsterRiskConstants.SKY_VISIBLE_MANA_BONUS;
        }
        
        return multiplier;
    }
    
    /**
     * 現在の時刻を0-1の範囲で取得（0=日の出、0.5=日没）
     * @param level ワールド
     * @return 正規化された時刻
     */
    public static float getNormalizedDayTime(Level level) {
        if (level == null) return 0f;
        long dayTime = level.getDayTime() % AsterRiskConstants.DAY_LENGTH;
        return dayTime / (float) AsterRiskConstants.DAY_LENGTH;
    }
    
    /**
     * 満月かどうかを判定
     * @param level ワールド
     * @return 満月の場合true
     */
    public static boolean isFullMoon(Level level) {
        if (level == null) return false;
        return level.getMoonPhase() == AsterRiskConstants.MOON_PHASE_FULL;
    }
    
    /**
     * 新月かどうかを判定
     * @param level ワールド
     * @return 新月の場合true
     */
    public static boolean isNewMoon(Level level) {
        if (level == null) return false;
        return level.getMoonPhase() == AsterRiskConstants.MOON_PHASE_NEW;
    }
}
