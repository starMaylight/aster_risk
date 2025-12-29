package net.mcreator.asterrisk.item.weapon;

import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 虚空の短剣 - 背後攻撃でクリティカルダメージ
 */
public class VoidDaggerItem extends SwordItem {

    public VoidDaggerItem(Properties properties) {
        super(Tiers.NETHERITE, 2, -2.0f, properties.rarity(Rarity.RARE)); // 攻撃速度が速い
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        Level level = attacker.level();
        
        // 背後攻撃判定
        if (isBackstab(target, attacker)) {
            // 追加ダメージ（2倍）
            target.hurt(attacker.damageSources().mobAttack(attacker), 8.0f);
            
            // エフェクト
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.CRIT,
                    target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(),
                    20, 0.3, 0.3, 0.3, 0.1);
                serverLevel.sendParticles(ParticleTypes.PORTAL,
                    target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(),
                    15, 0.3, 0.3, 0.3, 0.5);
            }

            // アクションバーに表示
            if (attacker instanceof Player player) {
                player.displayClientMessage(Component.literal("§5⚔ Backstab! §7(2x damage)"), true);
            }
        } else {
            // 通常攻撃でもポータルエフェクト
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.PORTAL,
                    target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(),
                    5, 0.2, 0.2, 0.2, 0.3);
            }
        }

        return super.hurtEnemy(stack, target, attacker);
    }

    /**
     * 背後攻撃かどうかを判定
     */
    private boolean isBackstab(LivingEntity target, LivingEntity attacker) {
        // ターゲットの向いている方向
        Vec3 targetLook = target.getLookAngle().normalize();
        // 攻撃者からターゲットへの方向
        Vec3 toTarget = new Vec3(
            target.getX() - attacker.getX(),
            0,
            target.getZ() - attacker.getZ()
        ).normalize();

        // 内積が正なら背後から（同じ方向を向いている）
        double dot = targetLook.x * toTarget.x + targetLook.z * toTarget.z;
        return dot > 0.5; // 約60度以内
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, 
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§5✦ Void Dagger"));
        tooltip.add(Component.literal("§7Fast attack speed"));
        tooltip.add(Component.literal("§7Backstab: §c2x damage"));
    }
}
