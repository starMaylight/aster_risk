package net.mcreator.asterrisk.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * 天体の守り - Celestial Guard
 * ポジティブエフェクト
 * - 最大体力増加
 * - 移動速度アップ
 */
public class CelestialGuardEffect extends MobEffect {
    
    public CelestialGuardEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x9370DB); // ミディアムパープル
        
        // 最大体力増加（レベルあたり+4HP = ハート2個）
        this.addAttributeModifier(Attributes.MAX_HEALTH, 
            "a1b2c3d4-5e6f-7a8b-9c0d-1e2f3a4b5c6d", 
            4.0D, 
            AttributeModifier.Operation.ADDITION);
        
        // 移動速度アップ
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED,
            "b2c3d4e5-6f7a-8b9c-0d1e-2f3a4b5c6d7e",
            0.05D,
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
