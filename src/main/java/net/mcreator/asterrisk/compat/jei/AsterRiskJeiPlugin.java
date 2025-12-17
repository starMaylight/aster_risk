package net.mcreator.asterrisk.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.init.AsterRiskModBlocks;
import net.mcreator.asterrisk.recipe.InfuserRecipe;
import net.mcreator.asterrisk.recipe.ModRecipes;
import net.mcreator.asterrisk.recipe.RitualRecipe;
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
            
            // 儀式レシピカテゴリ
            registration.addRecipeCategories(new RitualRecipeCategory(guiHelper));
            
            // Infuserレシピカテゴリ
            registration.addRecipeCategories(new InfuserRecipeCategory(guiHelper));
            
        } catch (Exception e) {
            AsterRiskMod.LOGGER.warn("Failed to register JEI categories: " + e.getMessage());
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        try {
            if (Minecraft.getInstance().level == null) return;
            
            RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

            // 儀式レシピを登録
            List<RitualRecipe> ritualRecipes = recipeManager.getAllRecipesFor(ModRecipes.RITUAL_TYPE.get());
            registration.addRecipes(RitualRecipeCategory.RECIPE_TYPE, ritualRecipes);
            
            // Infuserレシピを登録
            List<InfuserRecipe> infuserRecipes = recipeManager.getAllRecipesFor(ModRecipes.INFUSER_TYPE.get());
            registration.addRecipes(InfuserRecipeCategory.RECIPE_TYPE, infuserRecipes);
            
        } catch (Exception e) {
            AsterRiskMod.LOGGER.warn("Failed to register JEI recipes: " + e.getMessage());
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        try {
            // === 儀式システム ===
            // Altar Coreをカタリストとして登録
            registration.addRecipeCatalyst(
                new ItemStack(AsterRiskModBlocks.ALTAR_CORE.get()),
                RitualRecipeCategory.RECIPE_TYPE
            );

            // Ritual Pedestalも追加
            registration.addRecipeCatalyst(
                new ItemStack(AsterRiskModBlocks.RITUAL_PEDESTAL.get()),
                RitualRecipeCategory.RECIPE_TYPE
            );
            
            // === Lunar Infuser ===
            registration.addRecipeCatalyst(
                new ItemStack(AsterRiskModBlocks.LUNAR_INFUSER.get()),
                InfuserRecipeCategory.RECIPE_TYPE
            );
            
        } catch (Exception e) {
            AsterRiskMod.LOGGER.warn("Failed to register JEI catalysts: " + e.getMessage());
        }
    }
}
