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
import net.mcreator.asterrisk.init.AsterRiskModBlocks;
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
        String manaText = "Mana: " + (int) recipe.getManaCost();
        guiGraphics.drawString(font, manaText, 5, HEIGHT - 25, 0x9966FF, false);
        
        // オベリスクエネルギー要求表示
        if (recipe.requiresObeliskEnergy()) {
            ObeliskEnergyType type = recipe.getRequiredEnergyType();
            int amount = recipe.getRequiredEnergyAmount();
            
            String typeName = type != null ? type.getName() : "unknown";
            typeName = typeName.substring(0, 1).toUpperCase() + typeName.substring(1);
            
            int color = type != null ? type.getColor() : 0xFFFFFF;
            
            String energyText = typeName + " Energy: " + amount;
            guiGraphics.drawString(font, energyText, 5, HEIGHT - 12, color, false);
            
            // アイコン表示（オベリスクの種類を示す）
            String icon = switch (type) {
                case LUNAR -> "☽";
                case STELLAR -> "★";
                case SOLAR -> "☀";
                case VOID -> "◯";
                default -> "?";
            };
            guiGraphics.drawString(font, icon, WIDTH - 15, HEIGHT - 12, color, false);
        } else {
            // エネルギー不要の場合
            guiGraphics.drawString(font, "No special energy required", 5, HEIGHT - 12, 0x888888, false);
        }
    }
}
