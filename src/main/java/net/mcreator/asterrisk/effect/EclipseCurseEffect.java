package net.mcreator.asterrisk.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.mcreator.asterrisk.mana.LunarManaCapability;

/**
 * 月蝕の呪い - マナ回復が停止し、徐々に減少
 * ネガティブエフェクト
 */
public class EclipseCurseEffect extends MobEffect {
    
    public EclipseCurseEffect() {
        super(MobEffectCategory.HARMFUL, 0x4B0082); // インディゴ（暗い紫）
    }
    
    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof Player player && !player.level().isClientSide()) {
            // マナを徐々に減少させる
            float manaDrain = 0.25f * (amplifier + 1); // レベル1: 0.25/秒, レベル2: 0.5/秒
            
            player.getCapability(LunarManaCapability.LUNAR_MANA).ifPresent(mana -> {
                if (mana.getMana() > 0) {
                    mana.consumeMana(manaDrain);
                }
            });
        }
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // 毎秒（20tick）ごとに効果発動
        return duration % 20 == 0;
    }
}
