package net.mcreator.asterrisk.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * 星なき闇 - Starless Night
 * ネガティブエフェクト
 * - 視界が暗くなる（暗視の逆）
 * - 攻撃力低下
 * - 移動速度低下
 */
public class StarlessNightEffect extends MobEffect {
    
    public StarlessNightEffect() {
        super(MobEffectCategory.HARMFUL, 0x191970); // ミッドナイトブルー
        
        // 攻撃力低下
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, 
            "c3d4e5f6-7a8b-9c0d-1e2f-3a4b5c6d7e8f", 
            -2.0D, 
            AttributeModifier.Operation.ADDITION);
        
        // 移動速度低下
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED,
            "d4e5f6a7-8b9c-0d1e-2f3a-4b5c6d7e8f90",
            -0.15D,
            AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        // 属性ベースなのでtickでの処理は不要
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false;
    }
}
