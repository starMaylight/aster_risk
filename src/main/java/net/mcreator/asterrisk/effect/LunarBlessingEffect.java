package net.mcreator.asterrisk.effect;

import net.mcreator.asterrisk.mana.LunarManaCapability;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * 月光の祝福 - Lunar Blessing
 * ポジティブエフェクト
 * - マナ回復速度が大幅に上昇
 * - 夜間に自然回復速度アップ
 */
public class LunarBlessingEffect extends MobEffect {
    
    public LunarBlessingEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x87CEEB); // スカイブルー
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof Player player && !player.level().isClientSide()) {
            // マナを追加回復（レベルに応じて）
            float manaBonus = (amplifier + 1) * 0.5f;
            
            player.getCapability(LunarManaCapability.LUNAR_MANA).ifPresent(mana -> {
                mana.addMana(manaBonus);
            });
            
            // 夜間は体力も少し回復
            long dayTime = player.level().getDayTime() % 24000;
            boolean isNight = dayTime >= 13000 && dayTime < 23000;
            
            if (isNight && player.getHealth() < player.getMaxHealth()) {
                player.heal(0.25f * (amplifier + 1));
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // 20tick（1秒）ごとに発動
        int interval = 20;
        return duration % interval == 0;
    }
}
