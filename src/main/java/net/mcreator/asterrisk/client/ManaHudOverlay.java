package net.mcreator.asterrisk.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.mana.LunarManaCapability;
import net.mcreator.asterrisk.mana.ManaUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 魔力ゲージのHUD表示
 */
@Mod.EventBusSubscriber(modid = AsterRiskMod.MODID, value = Dist.CLIENT)
public class ManaHudOverlay {

    private static final ResourceLocation MANA_BAR_TEXTURE = ResourceLocation.fromNamespaceAndPath(
        AsterRiskMod.MODID, "textures/gui/mana_bar.png"
    );

    // HUDの位置設定
    private static final int BAR_X = 10;
    private static final int BAR_Y = 10;
    private static final int BAR_WIDTH = 80;
    private static final int BAR_HEIGHT = 8;

    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
        // エクスペリエンスバーの後に描画
        if (event.getOverlay() == VanillaGuiOverlay.EXPERIENCE_BAR.type()) {
            Minecraft mc = Minecraft.getInstance();
            
            if (mc.player == null || mc.options.hideGui) {
                return;
            }

            mc.player.getCapability(LunarManaCapability.LUNAR_MANA).ifPresent(mana -> {
                GuiGraphics guiGraphics = event.getGuiGraphics();
                
                float currentMana = mana.getMana();
                float maxMana = mana.getMaxMana();
                float manaPercent = currentMana / maxMana;

                int screenWidth = mc.getWindow().getGuiScaledWidth();
                int screenHeight = mc.getWindow().getGuiScaledHeight();

                // 画面左上に表示
                int x = BAR_X;
                int y = BAR_Y;

                // 背景（暗い青）
                guiGraphics.fill(x - 1, y - 1, x + BAR_WIDTH + 1, y + BAR_HEIGHT + 1, 0xFF000033);
                
                // 背景（空のバー）
                guiGraphics.fill(x, y, x + BAR_WIDTH, y + BAR_HEIGHT, 0xFF1a1a3a);

                // 魔力バー（月齢に応じて色を変える）
                int moonPhase = 0;
                if (mc.level != null) {
                    moonPhase = mc.level.getMoonPhase();
                }
                int barColor = getManaBarColor(moonPhase, manaPercent);
                
                int filledWidth = (int) (BAR_WIDTH * manaPercent);
                if (filledWidth > 0) {
                    guiGraphics.fill(x, y, x + filledWidth, y + BAR_HEIGHT, barColor);
                    
                    // 光沢エフェクト（上部を明るく）
                    guiGraphics.fill(x, y, x + filledWidth, y + 2, brightenColor(barColor, 0.3f));
                }

                // 枠線
                guiGraphics.fill(x - 1, y - 1, x + BAR_WIDTH + 1, y, 0xFF4a4a6a);
                guiGraphics.fill(x - 1, y + BAR_HEIGHT, x + BAR_WIDTH + 1, y + BAR_HEIGHT + 1, 0xFF2a2a4a);
                guiGraphics.fill(x - 1, y, x, y + BAR_HEIGHT, 0xFF4a4a6a);
                guiGraphics.fill(x + BAR_WIDTH, y, x + BAR_WIDTH + 1, y + BAR_HEIGHT, 0xFF2a2a4a);

                // 魔力の数値表示
                String manaText = String.format("%.0f/%.0f", currentMana, maxMana);
                guiGraphics.drawString(mc.font, manaText, x + BAR_WIDTH + 5, y, 0xAABBFF, true);

                // 月齢アイコン表示
                String moonIcon = getMoonPhaseIcon(moonPhase);
                guiGraphics.drawString(mc.font, moonIcon, x + BAR_WIDTH + 55, y, 0xFFFFDD, true);
            });
        }
    }

    /**
     * 月齢に応じたバーの色を取得
     */
    private static int getManaBarColor(int moonPhase, float manaPercent) {
        // 満月に近いほど明るい青、新月に近いほど暗い紫
        return switch (moonPhase) {
            case 0 -> 0xFF88CCFF; // 満月：明るい青
            case 1, 7 -> 0xFF7799EE; // 更待月/十三夜月
            case 2, 6 -> 0xFF6688DD; // 下弦/上弦
            case 3, 5 -> 0xFF5566CC; // 有明月/三日月
            case 4 -> 0xFF4444AA; // 新月：暗い紫
            default -> 0xFF6688DD;
        };
    }

    /**
     * 色を明るくする
     */
    private static int brightenColor(int color, float amount) {
        int a = (color >> 24) & 0xFF;
        int r = Math.min(255, (int) (((color >> 16) & 0xFF) * (1 + amount)));
        int g = Math.min(255, (int) (((color >> 8) & 0xFF) * (1 + amount)));
        int b = Math.min(255, (int) ((color & 0xFF) * (1 + amount)));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /**
     * 月齢のアイコン文字を取得
     */
    private static String getMoonPhaseIcon(int moonPhase) {
        return switch (moonPhase) {
            case 0 -> "●"; // 満月
            case 1 -> "◐"; // 更待月
            case 2 -> "◑"; // 下弦
            case 3 -> "◔"; // 有明月（代替）
            case 4 -> "○"; // 新月
            case 5 -> "◕"; // 三日月（代替）
            case 6 -> "◐"; // 上弦
            case 7 -> "◑"; // 十三夜月
            default -> "?";
        };
    }
}
