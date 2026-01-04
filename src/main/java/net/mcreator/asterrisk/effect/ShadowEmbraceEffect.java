package net.mcreator.asterrisk.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * 影の抱擁 - Shadow Embrace
 * 両面効果（中立）
 * - 移動速度大幅上昇
 * - 攻撃速度上昇
 * - 体力が徐々に減少
 * - 影の大鎌で付与
 */
public class ShadowEmbraceEffect extends MobEffect {
    
    public ShadowEmbraceEffect() {
        super(MobEffectCategory.NEUTRAL, 0x1A1A2E); // 暗いネイビー
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "7B2E4A6C-8D0F-1A3B-5C7D-9E1F2A3B4C5D", 0.15D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.ATTACK_SPEED, "3D5F7A9B-1C2E-4A6B-8C0D-2E4F6A8B0C1D", 0.3D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide()) {
            // 体力を徐々に減少（最低1まで）
            if (entity.getHealth() > 1.0f) {
                entity.hurt(entity.damageSources().magic(), 0.5f * (amplifier + 1));
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // 2秒ごとにダメージ
        return duration % 40 == 0;
    }
    
    @Override
    public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
        return modifier.getAmount() * (amplifier + 1);
    }
}
