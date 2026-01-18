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
import net.mcreator.asterrisk.recipe.CelestialEnchantRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

/**
 * 天体エンチャントのJEIカテゴリ
 */
public class CelestialEnchantRecipeCategory implements IRecipeCategory<CelestialEnchantRecipe> {
    
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(AsterRiskMod.MODID, "celestial_enchant");
    public static final RecipeType<CelestialEnchantRecipe> RECIPE_TYPE = 
        RecipeType.create(AsterRiskMod.MODID, "celestial_enchant", CelestialEnchantRecipe.class);
    
    private final IDrawable background;
    private final IDrawable icon;
    
    public CelestialEnchantRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(160, 100);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, 
            new ItemStack(Items.ENCHANTED_BOOK));
    }
    
    @Override
    public RecipeType<CelestialEnchantRecipe> getRecipeType() {
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
    public void setRecipe(IRecipeLayoutBuilder builder, CelestialEnchantRecipe recipe, IFocusGroup focuses) {
        // 入力アイテム
        builder.addSlot(RecipeIngredientRole.INPUT, 20, 40)
               .addIngredients(recipe.getInputItem());
        
        // 出力（同じアイテム+エンチャント表示用）
        ItemStack[] inputs = recipe.getInputItem().getItems();
        if (inputs.length > 0) {
            ItemStack output = inputs[0].copy();
            // エンチャント付きで表示
            Enchantment enchant = ForgeRegistries.ENCHANTMENTS.getValue(recipe.getEnchantment());
            if (enchant != null) {
                output.enchant(enchant, recipe.getEnchantmentLevel());
            }
            builder.addSlot(RecipeIngredientRole.OUTPUT, 120, 40)
                   .addItemStack(output);
        }
    }
    
    @Override
    public void draw(CelestialEnchantRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;
        
        // 矢印
        guiGraphics.drawString(font, "→", 70, 44, 0x555555, false);
        
        // パターン名
        String patternName = "Pattern: " + formatPatternName(recipe.getPattern());
        guiGraphics.drawString(font, patternName, 5, 5, 0xAA55AA, false);
        
        // エンチャント名
        Enchantment enchant = ForgeRegistries.ENCHANTMENTS.getValue(recipe.getEnchantment());
        String enchantName = enchant != null ? 
            enchant.getFullname(recipe.getEnchantmentLevel()).getString() : 
            recipe.getEnchantment().toString();
        guiGraphics.drawString(font, enchantName, 5, 70, 0x55AA55, false);
        
        // 月光コスト
        String moonlightCost = "☽ " + recipe.getMoonlightCost();
        guiGraphics.drawString(font, moonlightCost, 5, 85, 0x8888FF, false);
        
        // パターン図の簡易描画
        drawPatternPreview(guiGraphics, recipe.getPattern(), 100, 5);
    }
    
    private String formatPatternName(String pattern) {
        return switch (pattern) {
            case "LUNAR_CRESCENT" -> "Lunar Crescent";
            case "STAR_CROSS" -> "Star Cross";
            case "CONSTELLATION" -> "Constellation";
            case "ECLIPSE_RING" -> "Eclipse Ring";
            case "VOID_SPIRAL" -> "Void Spiral";
            case "HEAVENS_HAMMER" -> "Heaven's Hammer";
            case "CURSED_MIRROR" -> "Cursed Mirror";
            case "LUCKY_NOVA" -> "Lucky Nova";
            case "MANA_FOCUS" -> "Mana Focus";
            case "STELLAR_CORE" -> "Stellar Core";
            default -> pattern;
        };
    }
    
    private void drawPatternPreview(GuiGraphics guiGraphics, String pattern, int startX, int startY) {
        FocusPattern focusPattern = PatternManager.getInstance().getFocusPatternByName(pattern);
        if (focusPattern == null) return;
        List<BlockPos> positions = focusPattern.getPositions();
        
        int color = 0xFF8855FF;
        int centerX = startX + 25;
        int centerY = startY + 15;
        int scale = 5;
        
        // 中心点（エンチャント台）
        guiGraphics.fill(centerX - 2, centerY - 2, centerX + 2, centerY + 2, 0xFFFFFF00);
        
        // フォーカス位置
        for (BlockPos pos : positions) {
            int fx = centerX + pos.getX() * scale;
            int fz = centerY + pos.getZ() * scale;
            guiGraphics.fill(fx - 1, fz - 1, fx + 1, fz + 1, color);
        }
    }
}
