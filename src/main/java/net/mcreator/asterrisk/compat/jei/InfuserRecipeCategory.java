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
import net.mcreator.asterrisk.init.AsterRiskModBlocks;
import net.mcreator.asterrisk.recipe.InfuserRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * JEI用のLunar Infuserレシピカテゴリ
 */
public class InfuserRecipeCategory implements IRecipeCategory<InfuserRecipe> {

    public static final ResourceLocation UID = new ResourceLocation(AsterRiskMod.MODID, "infuser");

    public static final RecipeType<InfuserRecipe> RECIPE_TYPE = RecipeType.create(AsterRiskMod.MODID, "infuser", InfuserRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    // レシピ表示領域のサイズ
    private static final int WIDTH = 120;
    private static final int HEIGHT = 50;

    public InfuserRecipeCategory(IGuiHelper helper) {
        // シンプルな空白背景を使用
        this.background = helper.createBlankDrawable(WIDTH, HEIGHT);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(AsterRiskModBlocks.LUNAR_INFUSER.get()));
    }

    @Override
    public RecipeType<InfuserRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.aster_risk.jei.infuser");
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
    public void setRecipe(IRecipeLayoutBuilder builder, InfuserRecipe recipe, IFocusGroup focuses) {
        // 入力スロット（左側）
        builder.addSlot(RecipeIngredientRole.INPUT, 10, 17)
            .addIngredients(recipe.getInput());

        // 出力スロット（右側）
        builder.addSlot(RecipeIngredientRole.OUTPUT, WIDTH - 28, 17)
            .addItemStack(recipe.getOutput());
    }

    @Override
    public void draw(InfuserRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;
        
        // 矢印を描画
        guiGraphics.drawString(font, "==>", WIDTH / 2 - 10, 20, 0x555555, false);
        
        // マナコスト表示
        String manaText = "Mana: " + (int) recipe.getManaCost();
        guiGraphics.drawString(font, manaText, 10, HEIGHT - 12, 0x9966FF, false);
        
        // 処理時間表示（秒）
        float seconds = recipe.getProcessTime() / 20.0f;
        String timeText = String.format("%.1fs", seconds);
        guiGraphics.drawString(font, timeText, WIDTH - 30, HEIGHT - 12, 0x666666, false);
    }
}
