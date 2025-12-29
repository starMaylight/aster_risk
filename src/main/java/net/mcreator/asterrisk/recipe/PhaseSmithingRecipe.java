package net.mcreator.asterrisk.recipe;

import com.google.gson.JsonObject;
import net.mcreator.asterrisk.item.PhaseSigilItem;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

/**
 * 月相鍛冶レシピ - JEI表示用（実際の処理はPhaseAnvilBlockEntityで行う）
 */
public class PhaseSmithingRecipe implements Recipe<Container> {

    public static final String TYPE_ID = "phase_smithing";

    private final ResourceLocation id;
    private final PhaseSigilItem.MoonPhase phase;
    private final String effectDescription;
    private final float manaCost;

    public PhaseSmithingRecipe(ResourceLocation id, PhaseSigilItem.MoonPhase phase, 
                              String effectDescription, float manaCost) {
        this.id = id;
        this.phase = phase;
        this.effectDescription = effectDescription;
        this.manaCost = manaCost;
    }

    public PhaseSigilItem.MoonPhase getPhase() {
        return phase;
    }

    public String getEffectDescription() {
        return effectDescription;
    }

    public float getManaCost() {
        return manaCost;
    }

    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.PHASE_SMITHING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.PHASE_SMITHING_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<PhaseSmithingRecipe> {

        @Override
        public PhaseSmithingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            String phaseName = GsonHelper.getAsString(json, "phase");
            PhaseSigilItem.MoonPhase phase = PhaseSigilItem.MoonPhase.valueOf(phaseName.toUpperCase());
            String effectDescription = GsonHelper.getAsString(json, "effect", "");
            float manaCost = GsonHelper.getAsFloat(json, "mana_cost", 200f);

            return new PhaseSmithingRecipe(recipeId, phase, effectDescription, manaCost);
        }

        @Override
        @Nullable
        public PhaseSmithingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            int phaseIndex = buffer.readVarInt();
            PhaseSigilItem.MoonPhase phase = PhaseSigilItem.MoonPhase.fromIndex(phaseIndex);
            String effectDescription = buffer.readUtf();
            float manaCost = buffer.readFloat();

            return new PhaseSmithingRecipe(recipeId, phase, effectDescription, manaCost);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, PhaseSmithingRecipe recipe) {
            buffer.writeVarInt(recipe.phase.getIndex());
            buffer.writeUtf(recipe.effectDescription);
            buffer.writeFloat(recipe.manaCost);
        }
    }
}
