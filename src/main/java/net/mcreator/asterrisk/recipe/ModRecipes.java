package net.mcreator.asterrisk.recipe;

import net.mcreator.asterrisk.AsterRiskMod;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * カスタムレシピの登録
 */
public class ModRecipes {

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
        DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, AsterRiskMod.MODID);

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
        DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, AsterRiskMod.MODID);

    // === 儀式レシピ ===
    public static final RegistryObject<RecipeType<RitualRecipe>> RITUAL_TYPE =
        RECIPE_TYPES.register("ritual", () -> new RecipeType<>() {
            @Override
            public String toString() {
                return AsterRiskMod.MODID + ":ritual";
            }
        });

    public static final RegistryObject<RecipeSerializer<RitualRecipe>> RITUAL_SERIALIZER =
        RECIPE_SERIALIZERS.register("ritual", RitualRecipe.Serializer::new);

    // === Infuserレシピ ===
    public static final RegistryObject<RecipeType<InfuserRecipe>> INFUSER_TYPE =
        RECIPE_TYPES.register("infuser", () -> new RecipeType<>() {
            @Override
            public String toString() {
                return AsterRiskMod.MODID + ":infuser";
            }
        });

    public static final RegistryObject<RecipeSerializer<InfuserRecipe>> INFUSER_SERIALIZER =
        RECIPE_SERIALIZERS.register("infuser", InfuserRecipe.Serializer::new);
}
