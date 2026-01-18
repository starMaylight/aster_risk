package net.mcreator.asterrisk.compat.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.block.entity.CelestialEnchantingTableBlockEntity;
import net.mcreator.asterrisk.init.AsterRiskModBlocks;
import net.mcreator.asterrisk.pattern.FocusPattern;
import net.mcreator.asterrisk.pattern.PatternManager;
import net.mcreator.asterrisk.pattern.PedestalPattern;
import net.mcreator.asterrisk.registry.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * JEI用マルチブロック構造ガイドカテゴリ
 * RitualCircle, FocusChamberCore, CelestialEnchantingTableの構造を表示
 * 
 * パターンはPatternManagerから動的に取得
 */
public class MultiblockStructureGuideCategory implements IRecipeCategory<MultiblockStructureGuideCategory.StructureGuide> {
    
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(AsterRiskMod.MODID, "multiblock_structure_guide");
    public static final RecipeType<StructureGuide> RECIPE_TYPE = RecipeType.create(AsterRiskMod.MODID, "multiblock_structure_guide", StructureGuide.class);
    
    private final IDrawable background;
    private final IDrawable icon;
    private final Component title;
    
    public MultiblockStructureGuideCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(176, 165);
        
        IDrawable tempIcon;
        try {
            tempIcon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.RITUAL_CIRCLE.get()));
        } catch (Exception e) {
            tempIcon = guiHelper.createBlankDrawable(16, 16);
        }
        this.icon = tempIcon;
        this.title = Component.translatable("gui.aster_risk.jei.multiblock_guide");
    }
    
    @Override
    public RecipeType<StructureGuide> getRecipeType() {
        return RECIPE_TYPE;
    }
    
    @Override
    public Component getTitle() {
        return title;
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
    public void setRecipe(IRecipeLayoutBuilder builder, StructureGuide recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.CATALYST, 5, 5)
            .addItemStack(recipe.getCenterBlock());
        
        // 必要な素材を表示
        int slotX = 5;
        int slotY = 145;
        for (ItemStack material : recipe.getMaterials()) {
            builder.addSlot(RecipeIngredientRole.INPUT, slotX, slotY)
                .addItemStack(material);
            slotX += 18;
        }
    }
    
    @Override
    public void draw(StructureGuide recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;
        
        // タイトル
        guiGraphics.drawString(font, recipe.getName(), 26, 8, 0x5555FF, false);
        
        // 構造タイプに応じた描画
        switch (recipe.getType()) {
            case RITUAL_CIRCLE -> drawRitualCircleGuide(guiGraphics, font, recipe);
            case FOCUS_CHAMBER -> drawFocusChamberGuide(guiGraphics, font);
            case CELESTIAL_ENCHANT_PILLAR -> drawCelestialPillarGuide(guiGraphics, font);
            case CELESTIAL_ENCHANT_FOCUS -> drawCelestialFocusGuide(guiGraphics, font, recipe);
        }
    }
    
    // ===== Ritual Circle =====
    private void drawRitualCircleGuide(GuiGraphics guiGraphics, Font font, StructureGuide recipe) {
        guiGraphics.drawString(font, "Pattern: " + recipe.getPatternName(), 5, 25, 0x333333, false);
        guiGraphics.drawString(font, "Pedestals: " + recipe.getPositions().size(), 100, 25, 0x666666, false);
        
        // 5x5グリッド（上から見た図）
        int gridStartX = 53;
        int gridStartY = 38;
        int cellSize = 14;
        
        // グリッド背景
        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                int px = gridStartX + x * cellSize;
                int pz = gridStartY + z * cellSize;
                guiGraphics.fill(px, pz, px + cellSize - 1, pz + cellSize - 1, 0x30000000);
            }
        }
        
        // 中央: 魔法陣
        int centerX = gridStartX + 2 * cellSize;
        int centerZ = gridStartY + 2 * cellSize;
        guiGraphics.fill(centerX, centerZ, centerX + cellSize - 1, centerZ + cellSize - 1, 0xFF8800FF);
        
        // 台座位置
        for (BlockPos pos : recipe.getPositions()) {
            int gx = 2 + pos.getX();
            int gz = 2 + pos.getZ();
            if (gx >= 0 && gx < 5 && gz >= 0 && gz < 5) {
                int px = gridStartX + gx * cellSize;
                int pz = gridStartY + gz * cellSize;
                guiGraphics.fill(px, pz, px + cellSize - 1, pz + cellSize - 1, 0xFF00CCCC);
            }
        }
        
        // 方角
        guiGraphics.drawString(font, "N", gridStartX + 2 * cellSize + 3, gridStartY - 10, 0x888888, false);
        
        // 凡例
        guiGraphics.drawString(font, "§5■ §8Ritual Circle (center)", 5, 115, 0x333333, false);
        guiGraphics.drawString(font, "§3■ §8Pedestal", 5, 127, 0x333333, false);
        guiGraphics.drawString(font, "§8All on same Y level", 90, 127, 0x666666, false);
    }
    
    // ===== Focus Chamber Core =====
    private void drawFocusChamberGuide(GuiGraphics guiGraphics, Font font) {
        guiGraphics.drawString(font, "3x3x3 Structure (Core at center)", 5, 25, 0x333333, false);
        
        // 3層を横に並べて表示
        int cellSize = 13;
        int layerGap = 55;
        int layerStartY = 50;
        
        // Layer Y=0 (Bottom)
        guiGraphics.drawString(font, "Y=0 (Bottom)", 5, layerStartY - 12, 0x555555, false);
        drawChamberLayer(guiGraphics, 5, layerStartY, cellSize, new String[]{
            "CCC",
            "C C",
            "CCC"
        }, false);
        
        // Layer Y=1 (Core)
        guiGraphics.drawString(font, "Y=1 (Core)", 5 + layerGap, layerStartY - 12, 0x555555, false);
        drawChamberLayer(guiGraphics, 5 + layerGap, layerStartY, cellSize, new String[]{
            "C C",
            " X ",
            "C C"
        }, true);
        
        // Layer Y=2 (Top) - Same as Y=0
        guiGraphics.drawString(font, "Y=2 (Top)", 5 + layerGap * 2, layerStartY - 12, 0x555555, false);
        drawChamberLayer(guiGraphics, 5 + layerGap * 2, layerStartY, cellSize, new String[]{
            "CCC",
            "C C",
            "CCC"
        }, false);
        
        guiGraphics.drawString(font, "§a= Same as Y=0", 5 + layerGap * 2, layerStartY + cellSize * 3 + 4, 0x666666, false);
        
        // 凡例
        int legendY = 105;
        guiGraphics.drawString(font, "§6■ §8Celestial Tile (frame)", 5, legendY, 0x333333, false);
        guiGraphics.drawString(font, "§b■ §8Focus Chamber Core", 5, legendY + 12, 0x333333, false);
        guiGraphics.drawString(font, "§f□ §8Air (laser entry)", 100, legendY, 0x333333, false);
        guiGraphics.drawString(font, "§8Beams enter through air gaps", 5, legendY + 26, 0x666666, false);
    }
    
    private void drawChamberLayer(GuiGraphics guiGraphics, int startX, int startY, int cellSize, String[] layer, boolean hasCore) {
        for (int z = 0; z < layer.length; z++) {
            for (int x = 0; x < layer[z].length(); x++) {
                int px = startX + x * cellSize;
                int pz = startY + z * cellSize;
                char c = layer[z].charAt(x);
                
                int color = switch (c) {
                    case 'C' -> 0xFFFFAA00;
                    case 'X' -> 0xFF00AAFF;
                    case ' ' -> 0x40FFFFFF;
                    default -> 0x30000000;
                };
                
                guiGraphics.fill(px, pz, px + cellSize - 1, pz + cellSize - 1, color);
                
                if (c == ' ') {
                    guiGraphics.fill(px, pz, px + cellSize - 1, pz + 1, 0x80888888);
                    guiGraphics.fill(px, pz + cellSize - 2, px + cellSize - 1, pz + cellSize - 1, 0x80888888);
                    guiGraphics.fill(px, pz, px + 1, pz + cellSize - 1, 0x80888888);
                    guiGraphics.fill(px + cellSize - 2, pz, px + cellSize - 1, pz + cellSize - 1, 0x80888888);
                }
            }
        }
    }
    
    // ===== Celestial Enchant - Pillar Structure =====
    private void drawCelestialPillarGuide(GuiGraphics guiGraphics, Font font) {
        guiGraphics.drawString(font, "4 Corner Pillars (3 blocks tall each)", 5, 25, 0x333333, false);
        
        guiGraphics.drawString(font, "Top View:", 5, 38, 0x555555, false);
        int gridStartX = 8;
        int gridStartY = 50;
        int cellSize = 11;
        
        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                int px = gridStartX + x * cellSize;
                int pz = gridStartY + z * cellSize;
                guiGraphics.fill(px, pz, px + cellSize - 1, pz + cellSize - 1, 0x30000000);
            }
        }
        
        int centerX = gridStartX + 2 * cellSize;
        int centerZ = gridStartY + 2 * cellSize;
        guiGraphics.fill(centerX, centerZ, centerX + cellSize - 1, centerZ + cellSize - 1, 0xFFDD00DD);
        
        int[][] corners = {{0,0}, {0,4}, {4,0}, {4,4}};
        for (int[] corner : corners) {
            int px = gridStartX + corner[0] * cellSize;
            int pz = gridStartY + corner[1] * cellSize;
            guiGraphics.fill(px, pz, px + cellSize - 1, pz + cellSize - 1, 0xFF00AA00);
        }
        
        guiGraphics.drawString(font, "Side (per corner):", 75, 38, 0x555555, false);
        int sideX = 85;
        int sideY = 52;
        int sideCell = 12;
        
        guiGraphics.fill(sideX, sideY, sideX + sideCell, sideY + sideCell, 0xFFFFAA00);
        guiGraphics.drawString(font, "§6T §7Y+2", sideX + sideCell + 4, sideY + 2, 0x333333, false);
        
        guiGraphics.fill(sideX, sideY + sideCell + 2, sideX + sideCell, sideY + sideCell * 2 + 2, 0xFF88FF88);
        guiGraphics.drawString(font, "§aP §7Y+1", sideX + sideCell + 4, sideY + sideCell + 4, 0x333333, false);
        
        guiGraphics.fill(sideX, sideY + sideCell * 2 + 4, sideX + sideCell, sideY + sideCell * 3 + 4, 0xFF88FF88);
        guiGraphics.drawString(font, "§aP §7Y+0", sideX + sideCell + 4, sideY + sideCell * 2 + 6, 0x333333, false);
        
        int legendY = 115;
        guiGraphics.drawString(font, "§d■ §8Enchant Table (Y=0)", 5, legendY, 0x333333, false);
        guiGraphics.drawString(font, "§2■ §8Pillar corners", 100, legendY, 0x333333, false);
        guiGraphics.drawString(font, "§6T§8=Celestial Tile §aP§8=Lunar Pillar", 5, legendY + 12, 0x333333, false);
    }
    
    // ===== Celestial Enchant - Focus Pattern =====
    private void drawCelestialFocusGuide(GuiGraphics guiGraphics, Font font, StructureGuide recipe) {
        guiGraphics.drawString(font, "Pattern: " + recipe.getPatternName(), 5, 25, 0x333333, false);
        guiGraphics.drawString(font, "Focus: " + recipe.getPositions().size(), 120, 25, 0x666666, false);
        
        int gridStartX = 30;
        int gridStartY = 40;
        int cellSize = 11;
        int gridWidth = 7;
        
        for (int x = 0; x < gridWidth; x++) {
            for (int z = 0; z < gridWidth; z++) {
                int px = gridStartX + x * cellSize;
                int pz = gridStartY + z * cellSize;
                guiGraphics.fill(px, pz, px + cellSize - 1, pz + cellSize - 1, 0x20000000);
            }
        }
        
        // ピラー位置（避けるべき位置）
        int centerGx = 3;
        int centerGz = 3;
        int[][] pillarOffsets = {{-2,-2}, {-2,2}, {2,-2}, {2,2}};
        for (int[] offset : pillarOffsets) {
            int gx = centerGx + offset[0];
            int gz = centerGz + offset[1];
            if (gx >= 0 && gx < gridWidth && gz >= 0 && gz < gridWidth) {
                int px = gridStartX + gx * cellSize;
                int pz = gridStartY + gz * cellSize;
                guiGraphics.fill(px, pz, px + cellSize - 1, pz + cellSize - 1, 0x40FF0000);
            }
        }
        
        // 中央: エンチャントテーブル
        int centerPx = gridStartX + centerGx * cellSize;
        int centerPz = gridStartY + centerGz * cellSize;
        guiGraphics.fill(centerPx, centerPz, centerPx + cellSize - 1, centerPz + cellSize - 1, 0xFFDD00DD);
        
        // Focus位置
        for (BlockPos pos : recipe.getPositions()) {
            int gx = centerGx + pos.getX();
            int gz = centerGz + pos.getZ();
            if (gx >= 0 && gx < gridWidth && gz >= 0 && gz < gridWidth) {
                int px = gridStartX + gx * cellSize;
                int pz = gridStartY + gz * cellSize;
                
                int color = switch (pos.getY()) {
                    case 1 -> 0xFF00FFFF;
                    case 2 -> 0xFF00FF88;
                    case 3 -> 0xFFFFFF00;
                    default -> 0xFFFFFFFF;
                };
                
                guiGraphics.fill(px, pz, px + cellSize - 1, pz + cellSize - 1, color);
                guiGraphics.drawString(font, String.valueOf(pos.getY()), px + 2, pz + 1, 0x000000, false);
            }
        }
        
        int legendY = 122;
        guiGraphics.drawString(font, "§d■ §8Table §c□ §8Pillar (avoid)", 5, legendY, 0x333333, false);
        guiGraphics.drawString(font, "§b1§8=Y+1 §a2§8=Y+2 §e3§8=Y+3", 5, legendY + 12, 0x333333, false);
        guiGraphics.drawString(font, "§8Link all Focus to the Table", 90, legendY + 12, 0x666666, false);
    }
    
    // ===== Static Methods =====
    
    /**
     * 全てのガイドレシピを取得（PatternManagerから動的に）
     */
    public static List<StructureGuide> getAllGuides() {
        List<StructureGuide> guides = new ArrayList<>();
        
        PatternManager pm = PatternManager.getInstance();
        
        // Ritual Circle パターン（PatternManagerから）
        ItemStack ritualCircle = new ItemStack(ModBlocks.RITUAL_CIRCLE.get());
        ItemStack pedestal = new ItemStack(AsterRiskModBlocks.RITUAL_PEDESTAL.get());
        for (PedestalPattern pattern : pm.getAllPedestalPatterns()) {
            List<ItemStack> materials = new ArrayList<>();
            materials.add(ritualCircle.copy());
            ItemStack pedestals = pedestal.copy();
            pedestals.setCount(pattern.getPositions().size());
            materials.add(pedestals);
            
            guides.add(new StructureGuide(
                StructureType.RITUAL_CIRCLE,
                "Ritual: " + pattern.getName(),
                pattern.getName(),
                ritualCircle.copy(),
                pattern.getPositions(),
                materials
            ));
        }
        
        // Focus Chamber Core
        List<ItemStack> chamberMaterials = new ArrayList<>();
        chamberMaterials.add(new ItemStack(ModBlocks.FOCUS_CHAMBER_CORE.get()));
        ItemStack celestialTiles = new ItemStack(AsterRiskModBlocks.CELESTIAL_TILE.get());
        celestialTiles.setCount(22);
        chamberMaterials.add(celestialTiles);
        
        guides.add(new StructureGuide(
            StructureType.FOCUS_CHAMBER,
            "Focus Chamber Core",
            "CHAMBER",
            new ItemStack(ModBlocks.FOCUS_CHAMBER_CORE.get()),
            List.of(),
            chamberMaterials
        ));
        
        // Celestial Enchanting Table - Pillar Structure
        List<ItemStack> pillarMaterials = new ArrayList<>();
        pillarMaterials.add(new ItemStack(ModBlocks.CELESTIAL_ENCHANTING_TABLE.get()));
        ItemStack lunarPillars = new ItemStack(AsterRiskModBlocks.LUNAR_PILLAR.get());
        lunarPillars.setCount(8);
        pillarMaterials.add(lunarPillars);
        ItemStack celestialCrowns = new ItemStack(AsterRiskModBlocks.CELESTIAL_TILE.get());
        celestialCrowns.setCount(4);
        pillarMaterials.add(celestialCrowns);
        
        guides.add(new StructureGuide(
            StructureType.CELESTIAL_ENCHANT_PILLAR,
            "Enchant: Pillars",
            "PILLAR",
            new ItemStack(ModBlocks.CELESTIAL_ENCHANTING_TABLE.get()),
            List.of(),
            pillarMaterials
        ));
        
        // Celestial Enchanting Table - Focus Patterns（PatternManagerから）
        ItemStack focus = new ItemStack(ModBlocks.MOONLIGHT_FOCUS.get());
        for (FocusPattern pattern : pm.getAllFocusPatterns()) {
            List<ItemStack> focusMaterials = new ArrayList<>();
            ItemStack focuses = focus.copy();
            focuses.setCount(pattern.getPositions().size());
            focusMaterials.add(focuses);
            
            guides.add(new StructureGuide(
                StructureType.CELESTIAL_ENCHANT_FOCUS,
                "Focus: " + pattern.getName(),
                pattern.getName(),
                new ItemStack(ModBlocks.CELESTIAL_ENCHANTING_TABLE.get()),
                pattern.getPositions(),
                focusMaterials
            ));
        }
        
        return guides;
    }
    
    // ===== Inner Classes =====
    
    public enum StructureType {
        RITUAL_CIRCLE,
        FOCUS_CHAMBER,
        CELESTIAL_ENCHANT_PILLAR,
        CELESTIAL_ENCHANT_FOCUS
    }
    
    public static class StructureGuide {
        private final StructureType type;
        private final String name;
        private final String patternName;
        private final ItemStack centerBlock;
        private final List<BlockPos> positions;
        private final List<ItemStack> materials;
        
        public StructureGuide(StructureType type, String name, String patternName, 
                             ItemStack centerBlock, List<BlockPos> positions, List<ItemStack> materials) {
            this.type = type;
            this.name = name;
            this.patternName = patternName;
            this.centerBlock = centerBlock;
            this.positions = positions;
            this.materials = materials;
        }
        
        public StructureType getType() { return type; }
        public String getName() { return name; }
        public String getPatternName() { return patternName; }
        public ItemStack getCenterBlock() { return centerBlock; }
        public List<BlockPos> getPositions() { return positions; }
        public List<ItemStack> getMaterials() { return materials; }
    }
}
