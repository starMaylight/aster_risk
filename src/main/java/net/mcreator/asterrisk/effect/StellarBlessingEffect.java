package net.mcreator.asterrisk.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * 星の加護 - Stellar Blessing
 * ポジティブエフェクト
 * - 攻撃力上昇
 * - 幸運効果
 * - 星屑系アイテムや装備が付与
 */
public class StellarBlessingEffect extends MobEffect {
    
    public StellarBlessingEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFD700); // ゴールド
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, "8F3D5E1A-C7B2-4A9E-B6D4-2E1F3A5C7D9B", 2.0D, AttributeModifier.Operation.ADDITION);
        this.addAttributeModifier(Attributes.LUCK, "1A2B3C4D-5E6F-7A8B-9C0D-1E2F3A4B5C6D", 1.0D, AttributeModifier.Operation.ADDITION);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        // 属性変更のみで追加効果なし
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
