package net.mcreator.asterrisk.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * 虚空の腐食 - Void Corruption
 * ネガティブエフェクト
 * - 最大体力が減少
 * - 防御力が低下
 * - Void Walkerやボスが付与
 */
public class VoidCorruptionEffect extends MobEffect {
    
    public VoidCorruptionEffect() {
        super(MobEffectCategory.HARMFUL, 0x2D0A4E); // 暗い紫
        this.addAttributeModifier(Attributes.MAX_HEALTH, "7E9CC78F-DE22-4A46-B5F5-3C3C1B3A7E4D", -2.0D, AttributeModifier.Operation.ADDITION);
        this.addAttributeModifier(Attributes.ARMOR, "9D4ECA6F-B8C1-4D3A-9E5F-1A2B3C4D5E6F", -2.0D, AttributeModifier.Operation.ADDITION);
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
        // レベルに応じて効果を強化
        return modifier.getAmount() * (amplifier + 1);
    }
}
