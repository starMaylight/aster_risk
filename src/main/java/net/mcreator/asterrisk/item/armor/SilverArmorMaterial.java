package net.mcreator.asterrisk.item.armor;

import net.mcreator.asterrisk.AsterRiskMod;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.mcreator.asterrisk.init.AsterRiskModItems;

import java.util.function.Supplier;

/**
 * 銀の防具素材
 * アンデッドに対して強い
 */
public class SilverArmorMaterial implements ArmorMaterial {

    private static final int[] DURABILITY = {13, 15, 16, 11};
    private static final int[] PROTECTION = {2, 5, 6, 2}; // 鉄と同等
    
    public static final SilverArmorMaterial INSTANCE = new SilverArmorMaterial();

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return DURABILITY[type.ordinal()] * 18; // 鉄より少し低い
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return PROTECTION[type.ordinal()];
    }

    @Override
    public int getEnchantmentValue() {
        return 20; // 高いエンチャント適性
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_IRON;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(AsterRiskModItems.SILVER_INGOT.get());
    }

    @Override
    public String getName() {
        return AsterRiskMod.MODID + ":silver";
    }

    @Override
    public float getToughness() {
        return 0.0f;
    }

    @Override
    public float getKnockbackResistance() {
        return 0.0f;
    }
}
