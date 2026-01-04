package net.mcreator.asterrisk.registry;

import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.init.AsterRiskModItems;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.alchemy.PotionUtils;

/**
 * ポーションの醸造レシピを登録
 * ※initフォルダはMCreatorに上書きされるためregistryに配置
 */
@Mod.EventBusSubscriber(modid = AsterRiskMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBrewingRecipes {
    
    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // === 月光系ポーション ===
            
            // 奇妙なポーション + 月光石 → 月光の祝福
            BrewingRecipeRegistry.addRecipe(
                Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD)),
                Ingredient.of(AsterRiskModItems.MOONSTONE.get()),
                PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.LUNAR_BLESSING.get())
            );
            
            // 月光の祝福 + グロウストーン → 強化版
            BrewingRecipeRegistry.addRecipe(
                Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.LUNAR_BLESSING.get())),
                Ingredient.of(Items.GLOWSTONE_DUST),
                PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.LUNAR_BLESSING_STRONG.get())
            );
            
            // 奇妙なポーション + 月の塵 → 月光浮遊
            BrewingRecipeRegistry.addRecipe(
                Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD)),
                Ingredient.of(AsterRiskModItems.LUNAR_DUST.get()),
                PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.LUNAR_LEVITATION.get())
            );
            
            // 月光浮遊 + レッドストーン → 延長版
            BrewingRecipeRegistry.addRecipe(
                Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.LUNAR_LEVITATION.get())),
                Ingredient.of(Items.REDSTONE),
                PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.LUNAR_LEVITATION_LONG.get())
            );
            
            // === 星屑系ポーション ===
            
            // 奇妙なポーション + 星の欠片 → 星の加護
            BrewingRecipeRegistry.addRecipe(
                Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD)),
                Ingredient.of(AsterRiskModItems.STARFLAGMENT.get()),
                PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.STELLAR_BLESSING.get())
            );
            
            // 星の加護 + グロウストーン → 強化版
            BrewingRecipeRegistry.addRecipe(
                Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.STELLAR_BLESSING.get())),
                Ingredient.of(Items.GLOWSTONE_DUST),
                PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.STELLAR_BLESSING_STRONG.get())
            );
            
            // 奇妙なポーション + 星屑 → 星屑の守り
            BrewingRecipeRegistry.addRecipe(
                Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD)),
                Ingredient.of(AsterRiskModItems.STARDUST.get()),
                PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.STARDUST_PROTECTION.get())
            );
            
            // === マナ系ポーション ===
            
            // 奇妙なポーション + 星屑触媒 → マナバースト
            BrewingRecipeRegistry.addRecipe(
                Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD)),
                Ingredient.of(AsterRiskModItems.STARDUST_CATALYST.get()),
                PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.MANA_BURST.get())
            );
            
            // マナバースト + グロウストーン → 強化版
            BrewingRecipeRegistry.addRecipe(
                Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.MANA_BURST.get())),
                Ingredient.of(Items.GLOWSTONE_DUST),
                PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.MANA_BURST_STRONG.get())
            );
            
            // === 戦闘系ポーション ===
            
            // 奇妙なポーション + 隕石片 → 隕石の力
            BrewingRecipeRegistry.addRecipe(
                Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD)),
                Ingredient.of(AsterRiskModItems.METEORITE_FRAGMENT.get()),
                PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.METEORITE_POWER.get())
            );
            
            // 隕石の力 + グロウストーン → 強化版
            BrewingRecipeRegistry.addRecipe(
                Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.METEORITE_POWER.get())),
                Ingredient.of(Items.GLOWSTONE_DUST),
                PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.METEORITE_POWER_STRONG.get())
            );
            
            // 奇妙なポーション + 銀インゴット → 銀の輝き
            BrewingRecipeRegistry.addRecipe(
                Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD)),
                Ingredient.of(AsterRiskModItems.SILVER_INGOT.get()),
                PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.SILVER_SHINE.get())
            );
            
            // === 特殊ポーション ===
            
            // 奇妙なポーション + 虹色隕石 → 虹色の輝き
            BrewingRecipeRegistry.addRecipe(
                Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD)),
                Ingredient.of(AsterRiskModItems.PRISMATIC_METEORITE.get()),
                PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.PRISMATIC_GLOW.get())
            );
            
            // 奇妙なポーション + 影のエッセンス → 影の抱擁
            BrewingRecipeRegistry.addRecipe(
                Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD)),
                Ingredient.of(AsterRiskModItems.SHADOW_ESSENCE.get()),
                PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.SHADOW_EMBRACE.get())
            );
            
            // 奇妙なポーション + 天体の核 → 天体の守護
            BrewingRecipeRegistry.addRecipe(
                Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD)),
                Ingredient.of(AsterRiskModItems.CELESTIAL_NUCLEUS.get()),
                PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotions.CELESTIAL_GUARD.get())
            );
        });
    }
}
