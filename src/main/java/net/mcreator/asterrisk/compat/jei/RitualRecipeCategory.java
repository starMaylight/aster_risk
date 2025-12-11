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
import net.mcreator.asterrisk.recipe.RitualRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * JEI用の儀式レシピカテゴリ
 */
public class RitualRecipeCategory implements IRecipeCategory<RitualRecipe> {

    public static final ResourceLocation UID = new ResourceLocation(AsterRiskMod.MODID, "ritual");

    public static final RecipeType<RitualRecipe> RECIPE_TYPE = RecipeType.create(AsterRiskMod.MODID, "ritual", RitualRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    // レシピ表示領域のサイズ
    private static final int WIDTH = 150;
    private static final int HEIGHT = 75;

    public RitualRecipeCategory(IGuiHelper helper) {
        // シンプルな空白背景を使用
        this.background = helper.createBlankDrawable(WIDTH, HEIGHT);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(AsterRiskModBlocks.ALTAR_CORE.get()));
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

        // 入力スロット位置（左側に配置、2x4グリッド風）
        // 中央に祭壇イメージ、右に出力
        int startX = 5;
        int startY = 5;
        int slotSize = 18;
        
        // 材料の数に応じてレイアウトを調整
        int count = Math.min(ingredients.size(), 8);
        
        if (count <= 4) {
            // 4個以下: 横一列
            for (int i = 0; i < count; i++) {
                builder.addSlot(RecipeIngredientRole.INPUT, startX + i * slotSize, startY + 20)
                    .addIngredients(ingredients.get(i));
            }
        } else {
            // 5個以上: 2行に分ける
            int topRow = (count + 1) / 2;
            int bottomRow = count - topRow;
            
            // 上段
            for (int i = 0; i < topRow; i++) {
                builder.addSlot(RecipeIngredientRole.INPUT, startX + i * slotSize, startY + 10)
                    .addIngredients(ingredients.get(i));
            }
            // 下段
            for (int i = 0; i < bottomRow; i++) {
                builder.addSlot(RecipeIngredientRole.INPUT, startX + i * slotSize, startY + 10 + slotSize + 5)
                    .addIngredients(ingredients.get(topRow + i));
            }
        }

        // 出力スロット（右側）
        builder.addSlot(RecipeIngredientRole.OUTPUT, WIDTH - 25, 25)
            .addItemStack(recipe.getResultItem());
    }

    @Override
    public void draw(RitualRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;
        
        // 矢印を描画
        guiGraphics.drawString(font, ">>>", 95, 30, 0x555555, false);
        
        // マナコスト表示
        String manaText = "Mana: " + (int) recipe.getManaCost();
        guiGraphics.drawString(font, manaText, WIDTH / 2 - font.width(manaText) / 2, HEIGHT - 15, 0x9966FF, false);
    }
}
