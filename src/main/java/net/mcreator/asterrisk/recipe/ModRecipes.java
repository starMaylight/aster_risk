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

    // === 錬金術レシピ ===
    public static final RegistryObject<RecipeType<AlchemyRecipe>> ALCHEMY_TYPE =
        RECIPE_TYPES.register("alchemy", () -> new RecipeType<>() {
            @Override
            public String toString() {
                return AsterRiskMod.MODID + ":alchemy";
            }
        });

    public static final RegistryObject<RecipeSerializer<AlchemyRecipe>> ALCHEMY_SERIALIZER =
        RECIPE_SERIALIZERS.register("alchemy", AlchemyRecipe.Serializer::new);

    // === 月相鍛冶レシピ ===
    public static final RegistryObject<RecipeType<PhaseSmithingRecipe>> PHASE_SMITHING_TYPE =
        RECIPE_TYPES.register("phase_smithing", () -> new RecipeType<>() {
            @Override
            public String toString() {
                return AsterRiskMod.MODID + ":phase_smithing";
            }
        });

    public static final RegistryObject<RecipeSerializer<PhaseSmithingRecipe>> PHASE_SMITHING_SERIALIZER =
        RECIPE_SERIALIZERS.register("phase_smithing", PhaseSmithingRecipe.Serializer::new);

    // === 流星召喚レシピ ===
    public static final RegistryObject<RecipeType<MeteorSummoningRecipe>> METEOR_SUMMONING_TYPE =
        RECIPE_TYPES.register("meteor_summoning", () -> new RecipeType<>() {
            @Override
            public String toString() {
                return AsterRiskMod.MODID + ":meteor_summoning";
            }
        });

    public static final RegistryObject<RecipeSerializer<MeteorSummoningRecipe>> METEOR_SUMMONING_SERIALIZER =
        RECIPE_SERIALIZERS.register("meteor_summoning", MeteorSummoningRecipe.Serializer::new);
}
