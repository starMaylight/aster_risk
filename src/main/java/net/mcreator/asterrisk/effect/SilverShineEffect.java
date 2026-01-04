package net.mcreator.asterrisk.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * 銀の輝き - Silver Shine
 * ポジティブエフェクト
 * - アンデッドに対するダメージ増加
 * - わずかな光を発する
 * - 銀装備で付与
 */
public class SilverShineEffect extends MobEffect {
    
    public SilverShineEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xC0C0C0); // シルバー
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, "4E6A8C0D-2F1B-3A5C-7D9E-1F3B5D7A9C0E", 1.5D, AttributeModifier.Operation.ADDITION);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        // 属性変更のみ（アンデッド特効は武器側で実装）
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
