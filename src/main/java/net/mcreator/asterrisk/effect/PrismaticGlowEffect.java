package net.mcreator.asterrisk.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * 虹色の輝き - Prismatic Glow
 * ポジティブエフェクト（レア）
 * - 全ステータス微上昇
 * - 虹色武器で敵を倒すと付与
 */
public class PrismaticGlowEffect extends MobEffect {
    
    public PrismaticGlowEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF69B4); // ホットピンク（虹色の代表）
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, "1F3A5C7D-9E0B-2C4D-6A8B-0C1D2E3F4A5B", 1.0D, AttributeModifier.Operation.ADDITION);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "2A4B6C8D-0E1F-3A5B-7C9D-1E2F3A4B5C6D", 0.05D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.ARMOR, "3B5D7F9A-1C2E-4A6B-8C0D-2E4F6A8B0C1D", 1.0D, AttributeModifier.Operation.ADDITION);
        this.addAttributeModifier(Attributes.LUCK, "4C6E8A0B-2D3F-5A7B-9C1D-3E5F7A9B1C2D", 1.0D, AttributeModifier.Operation.ADDITION);
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
