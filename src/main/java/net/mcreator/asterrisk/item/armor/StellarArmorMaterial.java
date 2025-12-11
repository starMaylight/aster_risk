package net.mcreator.asterrisk.item.armor;

import net.mcreator.asterrisk.AsterRiskMod;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.mcreator.asterrisk.init.AsterRiskModItems;

/**
 * 星屑の防具素材 - 魔法特化
 */
public class StellarArmorMaterial implements ArmorMaterial {
    
    private static final String NAME = "stellar";
    private static final int[] DURABILITY = {11, 13, 14, 9}; // boots, leggings, chestplate, helmet
    private static final int[] DEFENSE = {2, 5, 6, 2}; // 合計15（軽装）
    private static final int DURABILITY_MULTIPLIER = 20; // 軽め
    
    public static final StellarArmorMaterial INSTANCE = new StellarArmorMaterial();

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
        return 25; // 高いエンチャント適性（金と同等）
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_GOLD;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(AsterRiskModItems.STARFLAGMENT.get());
    }

    @Override
    public String getName() {
        return AsterRiskMod.MODID + ":" + NAME;
    }

    @Override
    public float getToughness() {
        return 0.0f; // 軽装なのでタフネスなし
    }

    @Override
    public float getKnockbackResistance() {
        return 0.0f;
    }
}
