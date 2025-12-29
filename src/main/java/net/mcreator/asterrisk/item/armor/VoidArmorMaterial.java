package net.mcreator.asterrisk.item.armor;

import net.mcreator.asterrisk.AsterRiskMod;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.mcreator.asterrisk.init.AsterRiskModItems;

/**
 * 虚空の防具素材
 * 回避・無効化に特化
 */
public class VoidArmorMaterial implements ArmorMaterial {

    private static final int[] DURABILITY = {13, 15, 16, 11};
    private static final int[] PROTECTION = {3, 6, 8, 3}; // ダイヤより少し低い
    
    public static final VoidArmorMaterial INSTANCE = new VoidArmorMaterial();

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return DURABILITY[type.ordinal()] * 30; // 高耐久
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return PROTECTION[type.ordinal()];
    }

    @Override
    public int getEnchantmentValue() {
        return 15;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_NETHERITE;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(AsterRiskModItems.VOID_SHARD.get());
    }

    @Override
    public String getName() {
        return AsterRiskMod.MODID + ":void";
    }

    @Override
    public float getToughness() {
        return 2.0f;
    }

    @Override
    public float getKnockbackResistance() {
        return 0.1f;
    }
}
