package net.mcreator.asterrisk.util;

import net.mcreator.asterrisk.mana.LunarManaCapability;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

/**
 * マナ操作のヘルパーメソッド
 */
public final class ManaHelper {
    
    private ManaHelper() {} // インスタンス化防止
    
    /**
     * プレイヤーのマナを取得
     * @param player プレイヤー
     * @return マナ量（Capabilityが無い場合は0）
     */
    public static float getMana(Player player) {
        if (player == null) return 0f;
        return player.getCapability(LunarManaCapability.LUNAR_MANA)
            .map(LunarManaCapability.ILunarMana::getMana)
            .orElse(0f);
    }
    
    /**
     * プレイヤーの最大マナを取得
     * @param player プレイヤー
     * @return 最大マナ量（Capabilityが無い場合はデフォルト値）
     */
    public static float getMaxMana(Player player) {
        if (player == null) return AsterRiskConstants.DEFAULT_MAX_MANA;
        return player.getCapability(LunarManaCapability.LUNAR_MANA)
            .map(LunarManaCapability.ILunarMana::getMaxMana)
            .orElse(AsterRiskConstants.DEFAULT_MAX_MANA);
    }
    
    /**
     * プレイヤーのマナ割合を取得
     * @param player プレイヤー
     * @return マナ割合（0.0-1.0）
     */
    public static float getManaPercentage(Player player) {
        float max = getMaxMana(player);
        if (max <= 0) return 0f;
        return getMana(player) / max;
    }
    
    /**
     * プレイヤーがマナを消費できるか確認
     * @param player プレイヤー
     * @param amount 必要マナ量
     * @return 消費可能な場合true
     */
    public static boolean canConsumeMana(Player player, float amount) {
        return getMana(player) >= amount;
    }
    
    /**
     * プレイヤーのマナを消費
     * @param player プレイヤー
     * @param amount 消費量
     * @return 実際に消費した量
     */
    public static float consumeMana(Player player, float amount) {
        if (player == null || amount <= 0) return 0f;
        
        return player.getCapability(LunarManaCapability.LUNAR_MANA)
            .map(mana -> {
                float consumed = Math.min(mana.getMana(), amount);
                mana.setMana(mana.getMana() - consumed);
                return consumed;
            })
            .orElse(0f);
    }
    
    /**
     * プレイヤーのマナを消費（成功/失敗を返す）
     * @param player プレイヤー
     * @param amount 消費量
     * @return 消費に成功した場合true
     */
    public static boolean tryConsumeMana(Player player, float amount) {
        if (!canConsumeMana(player, amount)) return false;
        consumeMana(player, amount);
        return true;
    }
    
    /**
     * プレイヤーのマナを回復
     * @param player プレイヤー
     * @param amount 回復量
     * @return 実際に回復した量
     */
    public static float addMana(Player player, float amount) {
        if (player == null || amount <= 0) return 0f;
        
        return player.getCapability(LunarManaCapability.LUNAR_MANA)
            .map(mana -> {
                float space = mana.getMaxMana() - mana.getMana();
                float added = Math.min(space, amount);
                mana.setMana(mana.getMana() + added);
                return added;
            })
            .orElse(0f);
    }
    
    /**
     * プレイヤーのマナを満タンにする
     * @param player プレイヤー
     */
    public static void fillMana(Player player) {
        if (player == null) return;
        player.getCapability(LunarManaCapability.LUNAR_MANA)
            .ifPresent(mana -> mana.setMana(mana.getMaxMana()));
    }
    
    /**
     * プレイヤーのマナを空にする
     * @param player プレイヤー
     */
    public static void drainMana(Player player) {
        if (player == null) return;
        player.getCapability(LunarManaCapability.LUNAR_MANA)
            .ifPresent(mana -> mana.setMana(0f));
    }
    
    /**
     * プレイヤーの最大マナを設定
     * @param player プレイヤー
     * @param maxMana 新しい最大マナ
     */
    public static void setMaxMana(Player player, float maxMana) {
        if (player == null) return;
        player.getCapability(LunarManaCapability.LUNAR_MANA)
            .ifPresent(mana -> mana.setMaxMana(maxMana));
    }
    
    /**
     * マナCapabilityのOptionalを取得
     * @param player プレイヤー
     * @return マナCapabilityのOptional
     */
    public static Optional<LunarManaCapability.ILunarMana> getCapability(Player player) {
        if (player == null) return Optional.empty();
        return player.getCapability(LunarManaCapability.LUNAR_MANA).resolve();
    }
}
