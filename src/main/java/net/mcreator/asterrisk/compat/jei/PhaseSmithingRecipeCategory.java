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
import net.mcreator.asterrisk.init.AsterRiskModItems;
import net.mcreator.asterrisk.item.PhaseSigilItem;
import net.mcreator.asterrisk.recipe.PhaseSmithingRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * JEI用の月相鍛冶レシピカテゴリ
 */
public class PhaseSmithingRecipeCategory implements IRecipeCategory<PhaseSmithingRecipe> {

    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(AsterRiskMod.MODID, "phase_smithing");
    public static final RecipeType<PhaseSmithingRecipe> RECIPE_TYPE = RecipeType.create(AsterRiskMod.MODID, "phase_smithing", PhaseSmithingRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    private static final int WIDTH = 160;
    private static final int HEIGHT = 60;

    public PhaseSmithingRecipeCategory(IGuiHelper helper) {
        this.background = helper.createBlankDrawable(WIDTH, HEIGHT);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, 
            new ItemStack(AsterRiskModBlocks.PHASE_ANVIL.get()));
    }

    @Override
    public RecipeType<PhaseSmithingRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.aster_risk.jei.phase_smithing");
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
    public void setRecipe(IRecipeLayoutBuilder builder, PhaseSmithingRecipe recipe, IFocusGroup focuses) {
        // 刻印アイテムを表示
        ItemStack sigilStack = getSigilForPhase(recipe.getPhase());
        builder.addSlot(RecipeIngredientRole.INPUT, 10, 20)
            .addItemStack(sigilStack);

        // 装備スロット（例示用）
        builder.addSlot(RecipeIngredientRole.CATALYST, 40, 20)
            .addItemStack(new ItemStack(AsterRiskModItems.LUNAR_CHESTPLATE.get()));
    }

    private ItemStack getSigilForPhase(PhaseSigilItem.MoonPhase phase) {
        return switch (phase) {
            case FULL_MOON -> new ItemStack(AsterRiskModItems.PHASE_SIGIL_FULL_MOON.get());
            case WANING_GIBBOUS -> new ItemStack(AsterRiskModItems.PHASE_SIGIL_WANING_GIBBOUS.get());
            case LAST_QUARTER -> new ItemStack(AsterRiskModItems.PHASE_SIGIL_LAST_QUARTER.get());
            case WANING_CRESCENT -> new ItemStack(AsterRiskModItems.PHASE_SIGIL_WANING_CRESCENT.get());
            case NEW_MOON -> new ItemStack(AsterRiskModItems.PHASE_SIGIL_NEW_MOON.get());
            case WAXING_CRESCENT -> new ItemStack(AsterRiskModItems.PHASE_SIGIL_WAXING_CRESCENT.get());
            case FIRST_QUARTER -> new ItemStack(AsterRiskModItems.PHASE_SIGIL_FIRST_QUARTER.get());
            case WAXING_GIBBOUS -> new ItemStack(AsterRiskModItems.PHASE_SIGIL_WAXING_GIBBOUS.get());
        };
    }

    @Override
    public void draw(PhaseSmithingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;
        
        // 月相名
        String phaseName = recipe.getPhase().getDisplayName();
        guiGraphics.drawString(font, phaseName, 70, 10, 0xFFFFAA, false);
        
        // 効果説明
        String effect = recipe.getEffectDescription();
        guiGraphics.drawString(font, effect, 70, 22, 0xAAAAAA, false);
        
        // マナコスト
        String manaText = "Mana: " + (int) recipe.getManaCost();
        guiGraphics.drawString(font, manaText, 10, HEIGHT - 12, 0x9966FF, false);
        
        // 補足
        guiGraphics.drawString(font, "Max Level: 3", WIDTH - 60, HEIGHT - 12, 0x666666, false);
    }
}
