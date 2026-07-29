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
import net.mcreator.asterrisk.block.entity.ObeliskEnergyType;

import java.util.Map;
import net.mcreator.asterrisk.registry.ModBlocks;
import net.mcreator.asterrisk.recipe.RitualRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * JEI用の儀式レシピカテゴリ - オベリスクエネルギー表示対応
 */
public class RitualRecipeCategory implements IRecipeCategory<RitualRecipe> {

    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(AsterRiskMod.MODID, "ritual");

    public static final RecipeType<RitualRecipe> RECIPE_TYPE = RecipeType.create(AsterRiskMod.MODID, "ritual", RitualRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    // レシピ表示領域のサイズ
    private static final int WIDTH = 160;
    private static final int HEIGHT = 85;

    public RitualRecipeCategory(IGuiHelper helper) {
        this.background = helper.createBlankDrawable(WIDTH, HEIGHT);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.ALTAR_CORE.get()));
    }

    @Override
    public RecipeType<RitualRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.aster_risk.jei.ritual");
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
    public void setRecipe(IRecipeLayoutBuilder builder, RitualRecipe recipe, IFocusGroup focuses) {
        var ingredients = recipe.getIngredients();

        int startX = 5;
        int startY = 5;
        int slotSize = 18;
        
        int count = Math.min(ingredients.size(), 8);
        
        if (count <= 4) {
            for (int i = 0; i < count; i++) {
                builder.addSlot(RecipeIngredientRole.INPUT, startX + i * slotSize, startY + 15)
                    .addIngredients(ingredients.get(i));
            }
        } else {
            int topRow = (count + 1) / 2;
            int bottomRow = count - topRow;
            
            for (int i = 0; i < topRow; i++) {
                builder.addSlot(RecipeIngredientRole.INPUT, startX + i * slotSize, startY + 5)
                    .addIngredients(ingredients.get(i));
            }
            for (int i = 0; i < bottomRow; i++) {
                builder.addSlot(RecipeIngredientRole.INPUT, startX + i * slotSize, startY + 5 + slotSize + 5)
                    .addIngredients(ingredients.get(topRow + i));
            }
        }

        // 出力スロット
        builder.addSlot(RecipeIngredientRole.OUTPUT, WIDTH - 25, 20)
            .addItemStack(recipe.getResultItem());
    }

    @Override
    public void draw(RitualRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;
        
        // 矢印を描画
        guiGraphics.drawString(font, ">>>", 100, 25, 0x555555, false);

        // マナコスト表示
        guiGraphics.drawString(font,
            Component.translatable("gui.aster_risk.jei.mana", (int) recipe.getManaCost()),
            5, HEIGHT - 25, 0x9966FF, false);

        drawEnergyRequirements(guiGraphics, font, recipe);
    }

    /**
     * オベリスクエネルギー要求の描画。
     * 複数種のエネルギーを要求するレシピにも対応し、横に並べて表示する。
     */
    private void drawEnergyRequirements(GuiGraphics guiGraphics, Font font, RitualRecipe recipe) {
        int lineY = HEIGHT - 12;

        // 全オベリスク必須のレシピ
        if (recipe.requiresAllObelisks()) {
            guiGraphics.drawString(font,
                Component.translatable("gui.aster_risk.jei.all_obelisks"),
                5, lineY, 0xFFAA00, false);
            return;
        }

        Map<ObeliskEnergyType, Integer> energies = recipe.getRequiredEnergies();
        if (energies.isEmpty()) {
            guiGraphics.drawString(font,
                Component.translatable("gui.aster_risk.jei.no_energy"),
                5, lineY, 0x888888, false);
            return;
        }

        // 複数エネルギーを横に並べる（アイコン + 数量）
        int x = 5;
        for (Map.Entry<ObeliskEnergyType, Integer> entry : energies.entrySet()) {
            ObeliskEnergyType type = entry.getKey();
            String text = energyIcon(type) + entry.getValue();
            guiGraphics.drawString(font, text, x, lineY, type.getColor(), false);
            x += font.width(text) + 6;
        }
    }

    private static String energyIcon(ObeliskEnergyType type) {
        if (type == null) return "?";
        return switch (type) {
            case LUNAR -> "☽";
            case STELLAR -> "★";
            case SOLAR -> "☀";
            case VOID -> "◯";
        };
    }
}
