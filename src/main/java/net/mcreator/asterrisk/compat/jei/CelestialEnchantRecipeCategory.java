package net.mcreator.asterrisk.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.pattern.FocusPattern;
import net.mcreator.asterrisk.pattern.PatternManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

/**
 * 天体エンチャントのJEIカテゴリ
 * 通常の天体エンチャントと専用エンチャントを統合して表示する
 */
public class CelestialEnchantRecipeCategory implements IRecipeCategory<CelestialEnchantEntry> {

    public static final ResourceLocation UID =
        ResourceLocation.fromNamespaceAndPath(AsterRiskMod.MODID, "celestial_enchant");
    public static final RecipeType<CelestialEnchantEntry> RECIPE_TYPE =
        RecipeType.create(AsterRiskMod.MODID, "celestial_enchant", CelestialEnchantEntry.class);

    private final IDrawable background;
    private final IDrawable icon;

    public CelestialEnchantRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(160, 100);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
            new ItemStack(Items.ENCHANTED_BOOK));
    }

    @Override
    public RecipeType<CelestialEnchantEntry> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.aster_risk.jei.celestial_enchant");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CelestialEnchantEntry recipe, IFocusGroup focuses) {
        if (!recipe.getInputs().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 20, 40)
                   .addItemStacks(recipe.getInputs());
        }
        if (!recipe.getOutputs().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 120, 40)
                   .addItemStacks(recipe.getOutputs());
        }
    }

    @Override
    public void draw(CelestialEnchantEntry recipe, IRecipeSlotsView recipeSlotsView,
                     GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;

        // 矢印
        guiGraphics.drawString(font, "→", 70, 44, 0x555555, false);

        // パターン名
        guiGraphics.drawString(font,
            Component.translatable("gui.aster_risk.jei.pattern",
                JeiTextHelper.patternName(recipe.getPattern())),
            5, 5, 0xAA55AA, false);

        // 専用エンチャントのバッジ
        if (recipe.isExclusive()) {
            guiGraphics.drawString(font,
                Component.translatable("gui.aster_risk.jei.exclusive_badge"),
                5, 18, 0xFFAA00, false);
        }

        // エンチャント名（または変換後アイテム名）
        guiGraphics.drawString(font, recipe.getResultLabel(), 5, 70, 0x55AA55, false);

        // 月光コスト
        guiGraphics.drawString(font,
            Component.translatable("gui.aster_risk.jei.moonlight", recipe.getMoonlightCost()),
            5, 85, 0x8888FF, false);

        // 適用対象（専用エンチャントのみ）
        if (recipe.getItemType() != null) {
            guiGraphics.drawString(font,
                Component.translatable("gui.aster_risk.jei.item_type",
                    Component.translatable("gui.aster_risk.jei.item_type."
                        + recipe.getItemType().toLowerCase())),
                90, 85, 0x666666, false);
        }

        drawPatternPreview(guiGraphics, recipe.getPattern(), 100, 5);
    }

    /**
     * Focusパターンの簡易プレビュー（上から見た配置図）
     */
    private void drawPatternPreview(GuiGraphics guiGraphics, String pattern, int startX, int startY) {
        FocusPattern focusPattern = PatternManager.getInstance().getFocusPatternByName(pattern);
        if (focusPattern == null) return;
        List<BlockPos> positions = focusPattern.getPositions();

        int radius = 2;
        for (BlockPos pos : positions) {
            radius = Math.max(radius, Math.max(Math.abs(pos.getX()), Math.abs(pos.getZ())));
        }
        int cells = radius * 2 + 1;
        int cellSize = Math.max(5, 50 / cells);

        // 背景グリッド
        for (int x = 0; x < cells; x++) {
            for (int z = 0; z < cells; z++) {
                int px = startX + x * cellSize;
                int pz = startY + z * cellSize;
                guiGraphics.fill(px, pz, px + cellSize - 1, pz + cellSize - 1, 0x20000000);
            }
        }

        // 中央（エンチャント台）
        int cx = startX + radius * cellSize;
        int cz = startY + radius * cellSize;
        guiGraphics.fill(cx, cz, cx + cellSize - 1, cz + cellSize - 1, 0xFFAA55AA);

        // Focus位置
        for (BlockPos pos : positions) {
            int gx = radius + pos.getX();
            int gz = radius + pos.getZ();
            if (gx < 0 || gx >= cells || gz < 0 || gz >= cells) continue;
            int px = startX + gx * cellSize;
            int pz = startY + gz * cellSize;
            guiGraphics.fill(px, pz, px + cellSize - 1, pz + cellSize - 1, 0xFF55AAFF);
        }
    }
}
