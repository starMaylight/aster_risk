package net.mcreator.asterrisk.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * 星の速度 - 移動速度アップ
 */
public class StarSpeedEffect extends MobEffect {
    
    public StarSpeedEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x00CED1); // ダークターコイズ
        // 移動速度アップ
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "a7b8c9d0-e1f2-3456-0123-567890123456",
            0.1, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }
    
    @Override
    public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
        return modifier.getAmount() * (amplifier + 1);
    }
}
