package net.mcreator.asterrisk.item.armor;

import net.mcreator.asterrisk.AsterRiskMod;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

/**
 * Eclipse Armor Material - 最強防具素材（日蝕セット）
 */
public class EclipseArmorMaterial implements ArmorMaterial {
    
    private static final int[] DURABILITY = {500, 720, 680, 420};
    private static final int[] DEFENSE = {5, 9, 8, 5};
    
    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return DURABILITY[type.getSlot().getIndex()];
    }
    
    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return DEFENSE[type.getSlot().getIndex()];
    }
    
    @Override
    public int getEnchantmentValue() {
        return 20;
    }
    
    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_NETHERITE;
    }
    
    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.EMPTY;
    }
    
    @Override
    public String getName() {
        return AsterRiskMod.MODID + ":eclipse";
    }
    
    @Override
    public float getToughness() {
        return 5.0F;
    }
    
    @Override
    public float getKnockbackResistance() {
        return 0.2F;
    }
}
