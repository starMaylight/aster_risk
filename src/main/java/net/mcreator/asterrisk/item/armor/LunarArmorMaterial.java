package net.mcreator.asterrisk.item.armor;

import net.mcreator.asterrisk.AsterRiskMod;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.mcreator.asterrisk.init.AsterRiskModItems;

/**
 * 月光の防具素材
 */
public class LunarArmorMaterial implements ArmorMaterial {
    
    private static final String NAME = "lunar";
    private static final int[] DURABILITY = {13, 15, 16, 11}; // boots, leggings, chestplate, helmet
    private static final int[] DEFENSE = {3, 6, 8, 3}; // boots, leggings, chestplate, helmet (合計20)
    private static final int DURABILITY_MULTIPLIER = 25; // 鉄(15)とダイヤ(33)の中間
    
    public static final LunarArmorMaterial INSTANCE = new LunarArmorMaterial();

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return DURABILITY[type.getSlot().getIndex()] * DURABILITY_MULTIPLIER;
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return DEFENSE[type.getSlot().getIndex()];
    }

    @Override
    public int getEnchantmentValue() {
        return 18; // 鉄(9)とダイヤ(10)より高い、金(25)より低い
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_IRON;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(AsterRiskModItems.MOONSTONE.get());
    }

    @Override
    public String getName() {
        return AsterRiskMod.MODID + ":" + NAME;
    }

    @Override
    public float getToughness() {
        return 1.0f; // 鉄(0)とダイヤ(2)の中間
    }

    @Override
    public float getKnockbackResistance() {
        return 0.0f;
    }
}
