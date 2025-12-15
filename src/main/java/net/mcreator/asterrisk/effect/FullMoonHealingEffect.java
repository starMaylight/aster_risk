package net.mcreator.asterrisk.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * 満月の癒し - 体力を徐々に回復
 */
public class FullMoonHealingEffect extends MobEffect {
    
    public FullMoonHealingEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFFACD); // レモンシフォン（淡い黄色）
    }
    
    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide()) {
            float healAmount = 0.5f * (amplifier + 1);
            if (entity.getHealth() < entity.getMaxHealth()) {
                entity.heal(healAmount);
            }
        }
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // 2秒（40tick）ごとに回復
        int interval = 40 >> amplifier; // レベルが上がると間隔が短くなる
        if (interval < 10) interval = 10; // 最低0.5秒
        return duration % interval == 0;
    }
}
