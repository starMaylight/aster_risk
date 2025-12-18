package net.mcreator.asterrisk.item.armor;

import net.mcreator.asterrisk.AsterRiskMod;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * Stellar Armor Material - 最強防具素材（星光セット）
 */
public class StellarEndgameArmorMaterial implements ArmorMaterial {
    
    private static final int[] DURABILITY = {480, 700, 660, 400};
    private static final int[] DEFENSE = {4, 8, 7, 4};
    
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
        return 25;
    }
    
    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_GOLD;
    }
    
    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.EMPTY;
    }
    
    @Override
    public String getName() {
        return AsterRiskMod.MODID + ":stellar_endgame";
    }
    
    @Override
    public float getToughness() {
        return 4.0F;
    }
    
    @Override
    public float getKnockbackResistance() {
        return 0.1F;
    }
}
