package net.mcreator.asterrisk.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.registry.ModBlocks;
import net.mcreator.asterrisk.compat.jei.MultiblockStructureGuideCategory;
import net.mcreator.asterrisk.recipe.*;
import net.mcreator.asterrisk.registry.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

/**
 * JEI連携プラグイン
 * 
 * 注意: MCreatorの開発環境（legacyforge）では動作しません。
 * エクスポートしたmodをMinecraft + Forge + JEI環境で使用すると動作します。
 */
@JeiPlugin
public class AsterRiskJeiPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(AsterRiskMod.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        try {
            var guiHelper = registration.getJeiHelpers().getGuiHelper();
            
            // 既存カテゴリ
            registration.addRecipeCategories(new RitualRecipeCategory(guiHelper));
            registration.addRecipeCategories(new InfuserRecipeCategory(guiHelper));
            registration.addRecipeCategories(new AlchemyRecipeCategory(guiHelper));
            registration.addRecipeCategories(new PhaseSmithingRecipeCategory(guiHelper));
            registration.addRecipeCategories(new MeteorSummoningRecipeCategory(guiHelper));
            
            // 新システムカテゴリ
            registration.addRecipeCategories(new RitualCircleRecipeCategory(guiHelper));
            registration.addRecipeCategories(new FocusChamberRecipeCategory(guiHelper));
            registration.addRecipeCategories(new CelestialEnchantRecipeCategory(guiHelper));
            
            // マルチブロック構造ガイドカテゴリ（全構造を統合）
            registration.addRecipeCategories(new MultiblockStructureGuideCategory(guiHelper));
            
        } catch (Exception e) {
            AsterRiskMod.LOGGER.warn("Failed to register JEI categories: " + e.getMessage());
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        try {
            if (Minecraft.getInstance().level == null) return;
            
            RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

            // 既存レシピ
            List<RitualRecipe> ritualRecipes = recipeManager.getAllRecipesFor(ModRecipes.RITUAL_TYPE.get());
            registration.addRecipes(RitualRecipeCategory.RECIPE_TYPE, ritualRecipes);
            
            List<InfuserRecipe> infuserRecipes = recipeManager.getAllRecipesFor(ModRecipes.INFUSER_TYPE.get());
            registration.addRecipes(InfuserRecipeCategory.RECIPE_TYPE, infuserRecipes);
            
            List<AlchemyRecipe> alchemyRecipes = recipeManager.getAllRecipesFor(ModRecipes.ALCHEMY_TYPE.get());
            registration.addRecipes(AlchemyRecipeCategory.RECIPE_TYPE, alchemyRecipes);
            
            List<PhaseSmithingRecipe> phaseSmithingRecipes = recipeManager.getAllRecipesFor(ModRecipes.PHASE_SMITHING_TYPE.get());
            registration.addRecipes(PhaseSmithingRecipeCategory.RECIPE_TYPE, phaseSmithingRecipes);
            
            List<MeteorSummoningRecipe> meteorSummoningRecipes = recipeManager.getAllRecipesFor(ModRecipes.METEOR_SUMMONING_TYPE.get());
            registration.addRecipes(MeteorSummoningRecipeCategory.RECIPE_TYPE, meteorSummoningRecipes);
            
            // 新システムレシピ
            List<RitualCircleRecipe> ritualCircleRecipes = recipeManager.getAllRecipesFor(ModRecipes.RITUAL_CIRCLE_TYPE.get());
            registration.addRecipes(RitualCircleRecipeCategory.RECIPE_TYPE, ritualCircleRecipes);
            
            List<FocusChamberRecipe> focusChamberRecipes = recipeManager.getAllRecipesFor(ModRecipes.FOCUS_CHAMBER_TYPE.get());
            registration.addRecipes(FocusChamberRecipeCategory.RECIPE_TYPE, focusChamberRecipes);
            
            List<CelestialEnchantRecipe> celestialEnchantRecipes = recipeManager.getAllRecipesFor(ModRecipes.CELESTIAL_ENCHANT_TYPE.get());
            registration.addRecipes(CelestialEnchantRecipeCategory.RECIPE_TYPE, celestialEnchantRecipes);
            
            // マルチブロック構造ガイド（静的データ）
            registration.addRecipes(MultiblockStructureGuideCategory.RECIPE_TYPE, MultiblockStructureGuideCategory.getAllGuides());
            
        } catch (Exception e) {
            AsterRiskMod.LOGGER.warn("Failed to register JEI recipes: " + e.getMessage());
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        try {
            // === 既存システム ===
            registration.addRecipeCatalyst(
                new ItemStack(ModBlocks.ALTAR_CORE.get()),
                RitualRecipeCategory.RECIPE_TYPE
            );
            registration.addRecipeCatalyst(
                new ItemStack(ModBlocks.RITUAL_PEDESTAL.get()),
                RitualRecipeCategory.RECIPE_TYPE
            );
            registration.addRecipeCatalyst(
                new ItemStack(ModBlocks.LUNAR_INFUSER.get()),
                InfuserRecipeCategory.RECIPE_TYPE
            );
            registration.addRecipeCatalyst(
                new ItemStack(ModBlocks.ALCHEMICAL_CAULDRON.get()),
                AlchemyRecipeCategory.RECIPE_TYPE
            );
            registration.addRecipeCatalyst(
                new ItemStack(ModBlocks.PHASE_ANVIL.get()),
                PhaseSmithingRecipeCategory.RECIPE_TYPE
            );
            registration.addRecipeCatalyst(
                new ItemStack(ModBlocks.METEOR_SUMMONING.get()),
                MeteorSummoningRecipeCategory.RECIPE_TYPE
            );
            
            // === 新システム ===
            // 魔法陣クラフト
            registration.addRecipeCatalyst(
                new ItemStack(ModBlocks.RITUAL_CIRCLE.get()),
                RitualCircleRecipeCategory.RECIPE_TYPE
            );
            
            // 月光集光
            registration.addRecipeCatalyst(
                new ItemStack(ModBlocks.MOONLIGHT_FOCUS.get()),
                FocusChamberRecipeCategory.RECIPE_TYPE
            );
            registration.addRecipeCatalyst(
                new ItemStack(ModBlocks.FOCUS_CHAMBER_CORE.get()),
                FocusChamberRecipeCategory.RECIPE_TYPE
            );
            
            // 天体エンチャント
            registration.addRecipeCatalyst(
                new ItemStack(ModBlocks.CELESTIAL_ENCHANTING_TABLE.get()),
                CelestialEnchantRecipeCategory.RECIPE_TYPE
            );
            
            // マルチブロック構造ガイド
            registration.addRecipeCatalyst(
                new ItemStack(ModBlocks.RITUAL_CIRCLE.get()),
                MultiblockStructureGuideCategory.RECIPE_TYPE
            );
            registration.addRecipeCatalyst(
                new ItemStack(ModBlocks.RITUAL_PEDESTAL.get()),
                MultiblockStructureGuideCategory.RECIPE_TYPE
            );
            registration.addRecipeCatalyst(
                new ItemStack(ModBlocks.FOCUS_CHAMBER_CORE.get()),
                MultiblockStructureGuideCategory.RECIPE_TYPE
            );
            registration.addRecipeCatalyst(
                new ItemStack(ModBlocks.CELESTIAL_ENCHANTING_TABLE.get()),
                MultiblockStructureGuideCategory.RECIPE_TYPE
            );
            registration.addRecipeCatalyst(
                new ItemStack(ModBlocks.MOONLIGHT_FOCUS.get()),
                MultiblockStructureGuideCategory.RECIPE_TYPE
            );
            
        } catch (Exception e) {
            AsterRiskMod.LOGGER.warn("Failed to register JEI catalysts: " + e.getMessage());
        }
    }
}
