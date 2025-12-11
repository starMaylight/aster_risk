package net.mcreator.asterrisk.mana;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * 魔力に関するユーティリティメソッド
 */
public class ManaUtils {

    /**
     * 魔力を消費して魔法を発動できるかチェック
     * @param player プレイヤー
     * @param cost 消費する魔力量
     * @return 発動可能かどうか
     */
    public static boolean tryConsumeMana(Player player, float cost) {
        return player.getCapability(LunarManaCapability.LUNAR_MANA).map(mana -> {
            if (mana.canConsume(cost)) {
                mana.consumeMana(cost);
                return true;
            }
            return false;
        }).orElse(false);
    }

    /**
     * 魔力を回復
     * @param player プレイヤー
     * @param amount 回復量
     */
    public static void addMana(Player player, float amount) {
        player.getCapability(LunarManaCapability.LUNAR_MANA).ifPresent(mana -> {
            mana.addMana(amount);
        });
    }

    /**
     * 魔力を全回復
     * @param player プレイヤー
     */
    public static void fullRestore(Player player) {
        player.getCapability(LunarManaCapability.LUNAR_MANA).ifPresent(mana -> {
            mana.setMana(mana.getMaxMana());
        });
    }

    /**
     * 現在の魔力を取得
     * @param player プレイヤー
     * @return 現在の魔力（取得できない場合は0）
     */
    public static float getMana(Player player) {
        return player.getCapability(LunarManaCapability.LUNAR_MANA).map(
            LunarManaCapability.ILunarMana::getMana
        ).orElse(0f);
    }

    /**
     * 最大魔力を取得
     * @param player プレイヤー
     * @return 最大魔力（取得できない場合は100）
     */
    public static float getMaxMana(Player player) {
        return player.getCapability(LunarManaCapability.LUNAR_MANA).map(
            LunarManaCapability.ILunarMana::getMaxMana
        ).orElse(100f);
    }

    /**
     * 魔力のパーセンテージを取得（0.0〜1.0）
     * @param player プレイヤー
     * @return 魔力の割合
     */
    public static float getManaPercent(Player player) {
        return player.getCapability(LunarManaCapability.LUNAR_MANA).map(mana -> 
            mana.getMana() / mana.getMaxMana()
        ).orElse(0f);
    }

    /**
     * 最大魔力を増加させる
     * @param player プレイヤー
     * @param amount 増加量
     */
    public static void increaseMaxMana(Player player, float amount) {
        player.getCapability(LunarManaCapability.LUNAR_MANA).ifPresent(mana -> {
            mana.setMaxMana(mana.getMaxMana() + amount);
        });
    }

    /**
     * 現在の月齢を取得（0-7）
     * @param level ワールド
     * @return 月齢
     */
    public static int getMoonPhase(Level level) {
        return level.getMoonPhase();
    }

    /**
     * 月齢の名前を取得
     * @param moonPhase 月齢（0-7）
     * @return 月齢の名前
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
     * 月齢の日本語名を取得
     * @param moonPhase 月齢（0-7）
     * @return 月齢の日本語名
     */
    public static String getMoonPhaseNameJP(int moonPhase) {
        return switch (moonPhase) {
            case 0 -> "満月";
            case 1 -> "更待月";
            case 2 -> "下弦の月";
            case 3 -> "有明月";
            case 4 -> "新月";
            case 5 -> "三日月";
            case 6 -> "上弦の月";
            case 7 -> "十三夜月";
            default -> "不明";
        };
    }

    /**
     * 夜間かどうかを判定
     * @param level ワールド
     * @return 夜間ならtrue
     */
    public static boolean isNight(Level level) {
        long dayTime = level.getDayTime() % 24000;
        return dayTime >= 13000 && dayTime < 23000;
    }

    /**
     * 月が見えるかどうかを判定（夜間かつ晴れ）
     * @param level ワールド
     * @return 月が見えるならtrue
     */
    public static boolean canSeeMoon(Level level) {
        return isNight(level) && !level.isRaining();
    }
}
