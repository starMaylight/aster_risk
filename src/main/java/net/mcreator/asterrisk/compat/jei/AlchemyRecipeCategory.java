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
import net.mcreator.asterrisk.recipe.AlchemyRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * JEI用の錬金釜レシピカテゴリ
 */
public class AlchemyRecipeCategory implements IRecipeCategory<AlchemyRecipe> {

    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(AsterRiskMod.MODID, "alchemy");
    public static final RecipeType<AlchemyRecipe> RECIPE_TYPE = RecipeType.create(AsterRiskMod.MODID, "alchemy", AlchemyRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    private static final int WIDTH = 150;
    private static final int HEIGHT = 70;

    public AlchemyRecipeCategory(IGuiHelper helper) {
        this.background = helper.createBlankDrawable(WIDTH, HEIGHT);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, 
            new ItemStack(AsterRiskModBlocks.ALCHEMICAL_CAULDRON.get()));
    }

    @Override
    public RecipeType<AlchemyRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.aster_risk.jei.alchemy");
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
    public void setRecipe(IRecipeLayoutBuilder builder, AlchemyRecipe recipe, IFocusGroup focuses) {
        var ingredients = recipe.getIngredients();
        
        // 入力スロット（最大4つ、2x2配置）
        int[] xPositions = {10, 28, 10, 28};
        int[] yPositions = {10, 10, 28, 28};
        
        for (int i = 0; i < ingredients.size() && i < 4; i++) {
            builder.addSlot(RecipeIngredientRole.INPUT, xPositions[i], yPositions[i])
                .addIngredients(ingredients.get(i));
        }

        // 出力スロット（右側）
        builder.addSlot(RecipeIngredientRole.OUTPUT, WIDTH - 28, 20)
            .addItemStack(recipe.getResultItem());
    }

    @Override
    public void draw(AlchemyRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;
        
        // 矢印を描画
        guiGraphics.drawString(font, "==>", 60, 22, 0x555555, false);
        
        // マナコスト表示
        String manaText = "Mana: " + (int) recipe.getManaCost();
        guiGraphics.drawString(font, manaText, 10, HEIGHT - 15, 0x9966FF, false);
        
        // 処理時間表示（秒）
        float seconds = recipe.getProcessTime() / 20.0f;
        String timeText = String.format("%.1fs", seconds);
        guiGraphics.drawString(font, timeText, 80, HEIGHT - 15, 0x666666, false);
        
        // 触媒必要表示
        if (recipe.requiresCatalyst()) {
            guiGraphics.drawString(font, "Catalyst Required", 10, HEIGHT - 5, 0xFF6666, false);
        }
    }
}
