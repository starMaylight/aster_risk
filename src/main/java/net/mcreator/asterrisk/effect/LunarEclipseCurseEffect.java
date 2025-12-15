package net.mcreator.asterrisk.effect;

import net.mcreator.asterrisk.mana.LunarManaCapability;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * 月蝕の呪い - Lunar Eclipse Curse
 * ネガティブエフェクト
 * - マナ回復を完全に停止
 * - 毎秒マナを消費
 */
public class LunarEclipseCurseEffect extends MobEffect {
    
    public LunarEclipseCurseEffect() {
        super(MobEffectCategory.HARMFUL, 0x2F4F4F); // ダークスレートグレー
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof Player player && !player.level().isClientSide()) {
            // マナを消費（レベルに応じて）
            float manaDrain = (amplifier + 1) * 2.0f;
            
            player.getCapability(LunarManaCapability.LUNAR_MANA).ifPresent(mana -> {
                mana.consumeMana(manaDrain);
            });
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // 20tick（1秒）ごとに発動
        int interval = 20;
        return duration % interval == 0;
    }
}
