package net.mcreator.asterrisk.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * 星屑の加護 - Stardust Protection
 * ポジティブエフェクト
 * - 防御力アップ
 * - 魔法ダメージ軽減
 */
public class StardustProtectionEffect extends MobEffect {
    
    public StardustProtectionEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFD700); // ゴールド
        
        // 防御力増加
        this.addAttributeModifier(Attributes.ARMOR, 
            "d8c8b5f4-7c4a-4b5e-9f3e-1a2b3c4d5e6f", 
            2.0D, 
            AttributeModifier.Operation.ADDITION);
        
        // ノックバック耐性
        this.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE,
            "e9d9c6f5-8d5b-5c6f-0a4f-2b3c4d5e6f70",
            0.1D,
            AttributeModifier.Operation.ADDITION);
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
