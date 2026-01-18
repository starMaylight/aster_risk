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
import net.mcreator.asterrisk.recipe.FocusChamberRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * 集光チャンバーのJEIカテゴリ
 */
public class FocusChamberRecipeCategory implements IRecipeCategory<FocusChamberRecipe> {
    
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(AsterRiskMod.MODID, "focus_chamber");
    public static final RecipeType<FocusChamberRecipe> RECIPE_TYPE = 
        RecipeType.create(AsterRiskMod.MODID, "focus_chamber", FocusChamberRecipe.class);
    
    private final IDrawable background;
    private final IDrawable icon;
    
    // 3x3グリッドの位置
    private static final int[][] INPUT_SLOTS = {
        {10, 10}, {32, 10}, {54, 10},
        {10, 32}, {32, 32}, {54, 32},
        {10, 54}, {32, 54}, {54, 54}
    };
    
    public FocusChamberRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(150, 90);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, 
            new ItemStack(Items.END_CRYSTAL)); // 仮アイコン
    }
    
    @Override
    public RecipeType<FocusChamberRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }
    
    @Override
    public Component getTitle() {
        return Component.translatable("gui.aster_risk.jei.focus_chamber");
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
    public void setRecipe(IRecipeLayoutBuilder builder, FocusChamberRecipe recipe, IFocusGroup focuses) {
        NonNullList<Ingredient> ingredients = recipe.getIngredientList();
        
        // 入力スロット（最大9個）
        for (int i = 0; i < Math.min(ingredients.size(), 9); i++) {
            builder.addSlot(RecipeIngredientRole.INPUT, INPUT_SLOTS[i][0], INPUT_SLOTS[i][1])
                   .addIngredients(ingredients.get(i));
        }
        
        // 出力スロット
        builder.addSlot(RecipeIngredientRole.OUTPUT, 115, 32)
               .addItemStack(recipe.getResultItem());
    }
    
    @Override
    public void draw(FocusChamberRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;
        
        // 矢印描画
        guiGraphics.drawString(font, "→", 85, 38, 0x555555, false);
        
        // 月光コスト
        String moonlightCost = "☽ " + recipe.getMoonlightCost();
        guiGraphics.drawString(font, moonlightCost, 100, 70, 0x8888FF, false);
        
        // 3x3グリッドの枠を描画
        drawGrid(guiGraphics, 8, 8, 3, 3, 22);
    }
    
    private void drawGrid(GuiGraphics guiGraphics, int startX, int startY, int cols, int rows, int cellSize) {
        int color = 0x40AAAAFF;
        
        // 縦線
        for (int i = 0; i <= cols; i++) {
            int x = startX + i * cellSize;
            guiGraphics.fill(x, startY, x + 1, startY + rows * cellSize, color);
        }
        
        // 横線
        for (int i = 0; i <= rows; i++) {
            int y = startY + i * cellSize;
            guiGraphics.fill(startX, y, startX + cols * cellSize, y + 1, color);
        }
    }
}
