package net.mcreator.asterrisk.item.armor;

import net.mcreator.asterrisk.AsterRiskMod;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.mcreator.asterrisk.init.AsterRiskModItems;

/**
 * 隕石の防具素材 - 攻撃特化
 */
public class MeteoriteArmorMaterial implements ArmorMaterial {
    
    private static final String NAME = "meteorite";
    private static final int[] DURABILITY = {14, 16, 17, 12}; // boots, leggings, chestplate, helmet
    private static final int[] DEFENSE = {3, 6, 8, 3}; // 合計20（月光と同等）
    private static final int DURABILITY_MULTIPLIER = 30; // 高耐久
    
    public static final MeteoriteArmorMaterial INSTANCE = new MeteoriteArmorMaterial();

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
        return 12; // 普通のエンチャント適性
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_NETHERITE;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(AsterRiskModItems.METEORITE_FRAGMENT.get());
    }

    @Override
    public String getName() {
        return AsterRiskMod.MODID + ":" + NAME;
    }

    @Override
    public float getToughness() {
        return 2.0f; // 高いタフネス
    }

    @Override
    public float getKnockbackResistance() {
        return 0.1f; // 少しのノックバック耐性
    }
}
