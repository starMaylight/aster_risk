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
import net.mcreator.asterrisk.item.MeteorSummonCoreItem;
import net.mcreator.asterrisk.recipe.MeteorSummoningRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * JEI用の流星召喚レシピカテゴリ
 */
public class MeteorSummoningRecipeCategory implements IRecipeCategory<MeteorSummoningRecipe> {

    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(AsterRiskMod.MODID, "meteor_summoning");
    public static final RecipeType<MeteorSummoningRecipe> RECIPE_TYPE = RecipeType.create(AsterRiskMod.MODID, "meteor_summoning", MeteorSummoningRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    private static final int WIDTH = 160;
    private static final int HEIGHT = 80;

    public MeteorSummoningRecipeCategory(IGuiHelper helper) {
        this.background = helper.createBlankDrawable(WIDTH, HEIGHT);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, 
            new ItemStack(AsterRiskModBlocks.METEOR_SUMMONING.get()));
    }

    @Override
    public RecipeType<MeteorSummoningRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.aster_risk.jei.meteor_summoning");
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
    public void setRecipe(IRecipeLayoutBuilder builder, MeteorSummoningRecipe recipe, IFocusGroup focuses) {
        // 流星の核を表示
        ItemStack coreStack = getCoreForType(recipe.getMeteorType());
        builder.addSlot(RecipeIngredientRole.INPUT, 10, 25)
            .addItemStack(coreStack);

        // ドロップアイテム表示
        var drops = recipe.getPossibleDrops();
        int xStart = 70;
        for (int i = 0; i < drops.size() && i < 4; i++) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, xStart + (i * 20), 25)
                .addItemStack(drops.get(i));
        }
    }

    private ItemStack getCoreForType(MeteorSummonCoreItem.MeteorType type) {
        return switch (type) {
            case SMALL -> new ItemStack(AsterRiskModItems.METEOR_CORE_SMALL.get());
            case STARDUST -> new ItemStack(AsterRiskModItems.METEOR_CORE_STARDUST.get());
            case PRISMATIC -> new ItemStack(AsterRiskModItems.METEOR_CORE_PRISMATIC.get());
            case OMINOUS -> new ItemStack(AsterRiskModItems.METEOR_CORE_OMINOUS.get());
        };
    }

    @Override
    public void draw(MeteorSummoningRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;
        
        // 流星タイプ名
        String typeName = recipe.getMeteorType().getDisplayName();
        int typeColor = recipe.isDangerous() ? 0xFF6666 : 0xFFFFAA;
        guiGraphics.drawString(font, typeName, 10, 5, typeColor, false);
        
        // 矢印
        guiGraphics.drawString(font, "=>", 45, 28, 0x555555, false);
        
        // マナコスト
        String manaText = "Mana: " + (int) recipe.getManaCost();
        guiGraphics.drawString(font, manaText, 10, HEIGHT - 20, 0x9966FF, false);
        
        // 説明
        if (!recipe.getDescription().isEmpty()) {
            guiGraphics.drawString(font, recipe.getDescription(), 10, HEIGHT - 10, 0x888888, false);
        }
        
        // 危険警告
        if (recipe.isDangerous()) {
            guiGraphics.drawString(font, "WARNING: Spawns enemies!", 10, 55, 0xFF4444, false);
        }
        
        // 夜間のみ
        guiGraphics.drawString(font, "Night only", WIDTH - 50, 5, 0x6666AA, false);
    }
}
