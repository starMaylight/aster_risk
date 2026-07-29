package net.mcreator.asterrisk.compat.jei;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Locale;

/**
 * JEI表示用のテキストヘルパー
 * パターン名などを翻訳キー経由で解決する
 */
public final class JeiTextHelper {

    private JeiTextHelper() {
    }

    /**
     * パターン名（STAR_CROSS等）を翻訳可能なコンポーネントに変換する。
     * 翻訳キー: pattern.aster_risk.&lt;lowercase&gt;
     * 未定義の場合はキーがそのまま出るため、lang側に追加すること。
     */
    public static MutableComponent patternName(String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return Component.translatable("gui.aster_risk.jei.pattern.none");
        }
        return Component.translatable("pattern.aster_risk." + pattern.toLowerCase(Locale.ROOT));
    }
}
