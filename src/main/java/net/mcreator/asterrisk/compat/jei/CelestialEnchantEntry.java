package net.mcreator.asterrisk.compat.jei;

import net.mcreator.asterrisk.recipe.CelestialEnchantRecipe;
import net.mcreator.asterrisk.recipe.ExclusiveEnchantRecipeManager.ExclusiveEnchantData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * 天体エンチャント台で扱うレシピの統一表示エントリ。
 *
 * 通常の天体エンチャント（CelestialEnchantRecipe）と
 * 専用エンチャント（ExclusiveEnchantData）を同じJEIカテゴリで扱うためのラッパー。
 */
public class CelestialEnchantEntry {

    /** 専用エンチャントの入力候補を集める際の上限 */
    private static final int MAX_INPUT_VARIANTS = 64;

    private final String pattern;
    private final List<ItemStack> inputs;
    private final List<ItemStack> outputs;
    private final Component resultLabel;
    private final int moonlightCost;
    private final boolean exclusive;
    @Nullable
    private final String itemType;

    private CelestialEnchantEntry(String pattern, List<ItemStack> inputs, List<ItemStack> outputs,
                                  Component resultLabel, int moonlightCost,
                                  boolean exclusive, @Nullable String itemType) {
        this.pattern = pattern;
        this.inputs = inputs;
        this.outputs = outputs;
        this.resultLabel = resultLabel;
        this.moonlightCost = moonlightCost;
        this.exclusive = exclusive;
        this.itemType = itemType;
    }

    /**
     * 通常の天体エンチャントレシピから生成
     */
    public static CelestialEnchantEntry of(CelestialEnchantRecipe recipe) {
        List<ItemStack> inputs = List.of(recipe.getInputItem().getItems());
        List<ItemStack> outputs = new ArrayList<>();
        Component label;

        if (recipe.isTransformation()) {
            outputs.add(recipe.getResult());
            label = recipe.getResult().getHoverName();
        } else {
            Enchantment enchant = recipe.getEnchantment() != null
                ? ForgeRegistries.ENCHANTMENTS.getValue(recipe.getEnchantment()) : null;
            for (ItemStack input : inputs) {
                ItemStack out = input.copy();
                if (enchant != null) {
                    out.enchant(enchant, recipe.getEnchantmentLevel());
                }
                outputs.add(out);
            }
            label = enchant != null
                ? enchant.getFullname(recipe.getEnchantmentLevel())
                : Component.empty();
        }

        return new CelestialEnchantEntry(recipe.getPattern(), inputs, outputs, label,
            recipe.getMoonlightCost(), false, null);
    }

    /**
     * 専用エンチャントデータから生成
     */
    public static CelestialEnchantEntry of(ExclusiveEnchantData data) {
        List<ItemStack> inputs = new ArrayList<>();
        List<ItemStack> outputs = new ArrayList<>();

        for (var item : ForgeRegistries.ITEMS) {
            ItemStack stack = new ItemStack(item);
            if (!data.canApply.test(stack)) continue;

            inputs.add(stack);
            ItemStack out = stack.copy();
            out.enchant(data.enchantment, data.maxLevel);
            outputs.add(out);

            if (inputs.size() >= MAX_INPUT_VARIANTS) break;
        }

        return new CelestialEnchantEntry(data.pattern, inputs, outputs,
            data.enchantment.getFullname(data.maxLevel),
            data.calculateCost(0), true, data.itemType);
    }

    public String getPattern() {
        return pattern;
    }

    public List<ItemStack> getInputs() {
        return inputs;
    }

    public List<ItemStack> getOutputs() {
        return outputs;
    }

    /** エンチャント名または変換後アイテム名 */
    public Component getResultLabel() {
        return resultLabel;
    }

    public int getMoonlightCost() {
        return moonlightCost;
    }

    /** 専用エンチャント（Celestial Enchanting Tableでのみ入手可能）か */
    public boolean isExclusive() {
        return exclusive;
    }

    @Nullable
    public String getItemType() {
        return itemType;
    }
}
