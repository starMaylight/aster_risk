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
import net.mcreator.asterrisk.pattern.PatternManager;
import net.mcreator.asterrisk.pattern.PedestalPattern;
import net.mcreator.asterrisk.recipe.RitualCircleRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

/**
 * 魔法陣クラフトのJEIカテゴリ
 */
public class RitualCircleRecipeCategory implements IRecipeCategory<RitualCircleRecipe> {
    
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(AsterRiskMod.MODID, "ritual_circle");
    public static final RecipeType<RitualCircleRecipe> RECIPE_TYPE = RecipeType.create(AsterRiskMod.MODID, "ritual_circle", RitualCircleRecipe.class);
    
    private final IDrawable background;
    private final IDrawable icon;
    
    // パターンアイコンの位置マップ（8スロット対応）
    private static final int[][] SLOT_POSITIONS = {
        // STAR pattern (8 slots)
        {72, 0},   // 北
        {108, 18}, // 北東
        {126, 54}, // 東
        {108, 90}, // 南東
        {72, 108}, // 南
        {36, 90},  // 南西
        {18, 54},  // 西
        {36, 18},  // 北西
    };
    
    public RitualCircleRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(162, 130);
        // アイコンは魔法陣ブロック（実際のブロック登録後に変更）
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, 
            new ItemStack(Items.ENCHANTING_TABLE)); // 仮
    }
    
    @Override
    public RecipeType<RitualCircleRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }
    
    @Override
    public Component getTitle() {
        return Component.translatable("gui.aster_risk.jei.ritual_circle");
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
    public void setRecipe(IRecipeLayoutBuilder builder, RitualCircleRecipe recipe, IFocusGroup focuses) {
        NonNullList<Ingredient> ingredients = recipe.getIngredientList();
        String pattern = recipe.getPattern();
        PedestalPattern pedestalPattern = PatternManager.getInstance().getPedestalPatternByName(pattern);
        
        if (pedestalPattern == null) return;
        List<net.minecraft.core.BlockPos> positions = pedestalPattern.getPositions();
        
        // パターンに応じたスロット配置
        int slotCount = Math.min(ingredients.size(), SLOT_POSITIONS.length);
        for (int i = 0; i < slotCount; i++) {
            int[] pos = getSlotPosition(pattern, i, positions.size());
            builder.addSlot(RecipeIngredientRole.INPUT, pos[0], pos[1])
                   .addIngredients(ingredients.get(i));
        }
        
        // 出力スロット（中央）
        builder.addSlot(RecipeIngredientRole.OUTPUT, 72, 54)
               .addItemStack(recipe.getResultItem());
    }
    
    /**
     * パターンに応じたスロット位置を返す
     */
    private int[] getSlotPosition(String pattern, int index, int totalSlots) {
        // パターンごとの配置
        switch (pattern) {
            case "CROSS":
                return switch (index) {
                    case 0 -> new int[]{126, 54}; // 東
                    case 1 -> new int[]{18, 54};  // 西
                    case 2 -> new int[]{72, 108}; // 南
                    case 3 -> new int[]{72, 0};   // 北
                    default -> SLOT_POSITIONS[index % SLOT_POSITIONS.length];
                };
            case "DIAMOND":
                return switch (index) {
                    case 0 -> new int[]{108, 90}; // 南東
                    case 1 -> new int[]{36, 90};  // 南西
                    case 2 -> new int[]{108, 18}; // 北東
                    case 3 -> new int[]{36, 18};  // 北西
                    default -> SLOT_POSITIONS[index % SLOT_POSITIONS.length];
                };
            case "TRIANGLE":
                return switch (index) {
                    case 0 -> new int[]{72, 0};   // 北
                    case 1 -> new int[]{108, 90}; // 南東
                    case 2 -> new int[]{36, 90};  // 南西
                    default -> SLOT_POSITIONS[index % SLOT_POSITIONS.length];
                };
            case "HEXAGON":
                return switch (index) {
                    case 0 -> new int[]{126, 54}; // 東
                    case 1 -> new int[]{18, 54};  // 西
                    case 2 -> new int[]{90, 90};  // 南東
                    case 3 -> new int[]{54, 90};  // 南西
                    case 4 -> new int[]{90, 18};  // 北東
                    case 5 -> new int[]{54, 18};  // 北西
                    default -> SLOT_POSITIONS[index % SLOT_POSITIONS.length];
                };
            case "STAR":
            default:
                return SLOT_POSITIONS[index % SLOT_POSITIONS.length];
        }
    }
    
    @Override
    public void draw(RitualCircleRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;
        
        // パターン名
        String patternName = "Pattern: " + recipe.getPattern();
        guiGraphics.drawString(font, patternName, 0, 120, 0x555555, false);
        
        // マナコスト
        String manaCost = "Mana: " + recipe.getManaCost();
        guiGraphics.drawString(font, manaCost, 100, 120, 0x5555FF, false);
        
        // 魔法陣の円を描画
        drawRitualCircle(guiGraphics, 80, 62);
    }
    
    private void drawRitualCircle(GuiGraphics guiGraphics, int centerX, int centerY) {
        // 簡易的な魔法陣表示（円）
        int radius = 45;
        int segments = 32;
        int color = 0xFF8855FF;
        
        for (int i = 0; i < segments; i++) {
            double angle1 = (i * 2 * Math.PI) / segments;
            double angle2 = ((i + 1) * 2 * Math.PI) / segments;
            
            int x1 = centerX + (int)(Math.cos(angle1) * radius);
            int y1 = centerY + (int)(Math.sin(angle1) * radius);
            int x2 = centerX + (int)(Math.cos(angle2) * radius);
            int y2 = centerY + (int)(Math.sin(angle2) * radius);
            
            guiGraphics.fill(x1, y1, x1 + 1, y1 + 1, color);
        }
    }
}
