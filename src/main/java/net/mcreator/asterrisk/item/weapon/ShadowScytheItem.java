package net.mcreator.asterrisk.item.weapon;

import net.minecraft.core.particles.ParticleTypes;
import net.mcreator.asterrisk.registry.ModParticles;
import net.mcreator.asterrisk.registry.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 影の鎌 - 攻撃時に体力を吸収
 */
public class ShadowScytheItem extends SwordItem {

    private static final float LIFESTEAL_PERCENT = 0.20f; // 20%吸血

    public ShadowScytheItem(Properties properties) {
        super(Tiers.DIAMOND, 5, -2.6f, properties.rarity(Rarity.RARE));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        Level level = attacker.level();
        
        // 与えたダメージの20%を回復
        float damageDealt = 8.0f + 5.0f; // 基礎ダメージ + ティアボーナス（概算）
        float healAmount = damageDealt * LIFESTEAL_PERCENT;
        
        if (attacker instanceof Player player) {
            float currentHealth = player.getHealth();
            float maxHealth = player.getMaxHealth();
            
            if (currentHealth < maxHealth) {
                player.heal(healAmount);
                
                // 回復エフェクト
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.HEART,
                        player.getX(), player.getY() + 1.5, player.getZ(),
                        3, 0.3, 0.3, 0.3, 0.05);
                }
            }
        }

        // 攻撃エフェクト
        if (level instanceof ServerLevel serverLevel) {
            // 影のパーティクル
            serverLevel.sendParticles(ModParticles.SHADOW_TRAIL.get(),
                target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(),
                8, 0.3, 0.3, 0.3, 0.03);
            serverLevel.sendParticles(ModParticles.SHADOW_BURST.get(),
                target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(),
                5, 0.2, 0.2, 0.2, 0.05);
            serverLevel.sendParticles(ParticleTypes.SOUL,
                target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(),
                3, 0.2, 0.2, 0.2, 0.08);
            
            // サウンド
            serverLevel.playSound(null, target.getX(), target.getY(), target.getZ(),
                ModSounds.SHADOW_SWING.get(), SoundSource.PLAYERS, 0.7f, 0.8f);
        }

        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, 
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§8✦ Shadow Scythe"));
        tooltip.add(Component.literal("§7Lifesteal: §c+" + (int)(LIFESTEAL_PERCENT * 100) + "%"));
        tooltip.add(Component.literal("§7Heal from damage dealt"));
    }
}
