package net.mcreator.asterrisk.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * 隕石の力 - Meteorite Power
 * ポジティブエフェクト
 * - 大幅な攻撃力上昇
 * - ノックバック耐性
 * - 隕石武器や流星召喚後に付与
 */
public class MeteoritePowerEffect extends MobEffect {
    
    public MeteoritePowerEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF4500); // オレンジレッド
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, "5A9E3C1B-7D4F-2E8A-B6C0-3F1E5D7A9B2C", 3.0D, AttributeModifier.Operation.ADDITION);
        this.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, "2B4D6F8A-1C3E-5A7B-9D0F-2E4C6A8B0D1F", 0.5D, AttributeModifier.Operation.ADDITION);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        // 属性変更のみ
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false;
    }
    
    @Override
    public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
        return modifier.getAmount() * (amplifier + 1);
    }
}
