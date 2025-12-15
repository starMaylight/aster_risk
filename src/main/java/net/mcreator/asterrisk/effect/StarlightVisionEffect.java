package net.mcreator.asterrisk.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * 星明かりの視界 - 暗視効果（夜間視力向上）
 */
public class StarlightVisionEffect extends MobEffect {
    
    public StarlightVisionEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xE6E6FA); // ラベンダー
    }
    
    // 暗視効果はバニラのNight Visionと同様にレンダリング側で処理される
    // この効果はクライアント側でチェックされ、明るさが調整される
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false; // tickごとの処理は不要
    }
}
