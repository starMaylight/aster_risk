package net.mcreator.asterrisk.mana;

import net.mcreator.asterrisk.item.armor.ArmorSetBonusHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

/**
 * MCreatorのプロシージャから呼び出すための魔力操作メソッド
 * 
 * 使用例（MCreatorプロシージャ内のExecute Codeブロック）:
 * 
 * import net.mcreator.asterrisk.mana.ManaProcedures;
 * ManaProcedures.restoreMana((Player) entity, 25f);
 */
public class ManaProcedures {

    /**
     * 魔力を回復（Lunar Dustなどのアイテム使用時）
     * @param player プレイヤー
     * @param amount 回復量
     */
    public static void restoreMana(Player player, float amount) {
        if (player == null) return;
        
        ManaUtils.addMana(player, amount);
        
        // 回復メッセージを表示
        player.displayClientMessage(
            Component.literal("+" + (int)amount + " Lunar Mana")
                .withStyle(ChatFormatting.AQUA),
            true // アクションバーに表示
        );
    }

    /**
     * 魔力を全回復（Star Fragmentなどの強力なアイテム使用時）
     * @param player プレイヤー
     */
    public static void fullRestoreMana(Player player) {
        if (player == null) return;
        
        ManaUtils.fullRestore(player);
        
        player.displayClientMessage(
            Component.literal("Lunar Mana Fully Restored!")
                .withStyle(ChatFormatting.GOLD),
            true
        );
    }

    /**
     * 魔法を発動（魔力を消費）
     * 星屑セット装備時は20%コスト軽減
     * @param player プレイヤー
     * @param cost 消費魔力
     * @return 発動成功かどうか
     */
    public static boolean castSpell(Player player, float cost) {
        if (player == null) return false;
        
        // 星屑セットのコスト軽減を適用
        float actualCost = cost * ArmorSetBonusHandler.getSpellCostMultiplier(player);
        
        if (ManaUtils.tryConsumeMana(player, actualCost)) {
            return true;
        } else {
            // 魔力不足メッセージ
            player.displayClientMessage(
                Component.literal("Not enough Lunar Mana!")
                    .withStyle(ChatFormatting.RED),
                true
            );
            return false;
        }
    }

    /**
     * 魔法を発動（月齢ボーナス付き）
     * 満月時はコスト半減、新月時はコスト1.5倍
     * 星屑セット装備時はさらに20%コスト軽減
     * @param player プレイヤー
     * @param baseCost 基本消費魔力
     * @return 発動成功かどうか
     */
    public static boolean castSpellWithMoonBonus(Player player, float baseCost) {
        if (player == null) return false;
        
        int moonPhase = player.level().getMoonPhase();
        float actualCost = baseCost * getMoonPhaseCostMultiplier(moonPhase);
        
        return castSpell(player, actualCost);
    }

    /**
     * 月齢によるコスト倍率を取得
     */
    private static float getMoonPhaseCostMultiplier(int moonPhase) {
        return switch (moonPhase) {
            case 0 -> 0.5f;   // 満月：半減
            case 1, 7 -> 0.7f;
            case 2, 6 -> 0.85f;
            case 3, 5 -> 1.0f;
            case 4 -> 1.5f;   // 新月：1.5倍
            default -> 1.0f;
        };
    }

    /**
     * 最大魔力を増加（装備効果など）
     * @param player プレイヤー
     * @param amount 増加量
     */
    public static void increaseMaxMana(Player player, float amount) {
        if (player == null) return;
        
        ManaUtils.increaseMaxMana(player, amount);
    }

    /**
     * 現在の魔力を取得
     * @param player プレイヤー
     * @return 現在の魔力
     */
    public static float getCurrentMana(Player player) {
        if (player == null) return 0f;
        return ManaUtils.getMana(player);
    }

    /**
     * 最大魔力を取得
     * @param player プレイヤー
     * @return 最大魔力
     */
    public static float getMaxMana(Player player) {
        if (player == null) return 100f;
        return ManaUtils.getMaxMana(player);
    }

    /**
     * 魔力が足りているか確認
     * @param player プレイヤー
     * @param cost 必要な魔力
     * @return 足りていればtrue
     */
    public static boolean hasEnoughMana(Player player, float cost) {
        if (player == null) return false;
        return ManaUtils.getMana(player) >= cost;
    }

    /**
     * 現在の月齢を取得
     * @param player プレイヤー
     * @return 月齢（0-7）
     */
    public static int getMoonPhase(Player player) {
        if (player == null) return 0;
        return player.level().getMoonPhase();
    }

    /**
     * 夜間かどうか
     * @param player プレイヤー
     * @return 夜間ならtrue
     */
    public static boolean isNight(Player player) {
        if (player == null) return false;
        return ManaUtils.isNight(player.level());
    }

    /**
     * 月が見えるか（夜間かつ晴れ）
     * @param player プレイヤー
     * @return 月が見えるならtrue
     */
    public static boolean canSeeMoon(Player player) {
        if (player == null) return false;
        return ManaUtils.canSeeMoon(player.level());
    }
}
