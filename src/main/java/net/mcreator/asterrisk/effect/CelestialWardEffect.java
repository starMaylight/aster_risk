package net.mcreator.asterrisk.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * 天体の守り - 全体的な耐性向上
 * 防御力、防具強度、最大体力を増加
 */
public class CelestialWardEffect extends MobEffect {
    
    public CelestialWardEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x9370DB); // 紫色
        // 防御力アップ
        this.addAttributeModifier(Attributes.ARMOR, "c3d4e5f6-a7b8-9012-cdef-123456789012",
            4.0, AttributeModifier.Operation.ADDITION);
        // 防具強度アップ
        this.addAttributeModifier(Attributes.ARMOR_TOUGHNESS, "d4e5f6a7-b8c9-0123-def0-234567890123",
            2.0, AttributeModifier.Operation.ADDITION);
        // 最大体力アップ
        this.addAttributeModifier(Attributes.MAX_HEALTH, "e5f6a7b8-c9d0-1234-ef01-345678901234",
            4.0, AttributeModifier.Operation.ADDITION);
        // ノックバック耐性
        this.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, "f6a7b8c9-d0e1-2345-f012-456789012345",
            0.2, AttributeModifier.Operation.ADDITION);
    }
    
    @Override
    public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
        return modifier.getAmount() * (amplifier + 1);
    }
}
