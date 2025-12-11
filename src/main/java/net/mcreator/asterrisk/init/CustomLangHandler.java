package net.mcreator.asterrisk.init;

import net.mcreator.asterrisk.AsterRiskMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * カスタム言語エントリを追加するためのハンドラー
 * 
 * 注意: Minecraftの言語システムは複数のJSONファイルをマージするため、
 * 追加の言語ファイルを作成することで対応可能です。
 * 
 * assets/aster_risk/lang/en_us_custom.json は読み込まれないため、
 * 代わりに動的に追加するか、MCreatorでダミー要素を作成する必要があります。
 */
public class CustomLangHandler {
    
    // カスタムアイテム/ブロックの翻訳キー
    // これらはMCreatorでダミー要素を作成するか、
    // Mixinを使用して動的に追加する必要があります
    
    public static final String MOONLIGHT_WAND = "item.aster_risk.moonlight_wand";
    public static final String MOONLIGHT_WAND_NAME = "Moonlight Wand";
    
    public static final String MOONLIGHT_BLOCK = "block.aster_risk.moonlight";
    public static final String MOONLIGHT_BLOCK_NAME = "Moonlight";
}
