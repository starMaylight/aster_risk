package net.mcreator.asterrisk.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.mcreator.asterrisk.mana.LunarManaCapability;

/**
 * マナバースト - Mana Burst
 * ポジティブエフェクト
 * - マナ回復速度が大幅上昇
 * - 魔法ダメージ増加
 * - マナ系ポーションや特定のイベントで付与
 */
public class ManaBurstEffect extends MobEffect {
    
    public ManaBurstEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x00CED1); // ダークターコイズ
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof Player player && !player.level().isClientSide()) {
            // マナを急速回復
            float manaBonus = (amplifier + 1) * 2.0f;
            
            player.getCapability(LunarManaCapability.LUNAR_MANA).ifPresent(mana -> {
                mana.addMana(manaBonus);
            });
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // 10tick（0.5秒）ごとに発動
        return duration % 10 == 0;
    }
}
