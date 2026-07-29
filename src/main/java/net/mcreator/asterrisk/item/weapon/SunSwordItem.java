package net.mcreator.asterrisk.item.weapon;

import net.mcreator.asterrisk.item.tool.ModToolTiers;
import net.mcreator.asterrisk.util.SolarCombatHelper;
import net.mcreator.asterrisk.util.TooltipHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 太陽の剣 - Sun Sword
 *
 * 陽の化身の力を封じ込めた最終武器。
 * - 攻撃力500（貫通ダメージとして直接体力を削る）
 * - 命中時に相手の装備を灼く（耐久10%減／耐久を持たない装備は強制ドロップ）
 * - 命中時に炎上
 */
public class SunSwordItem extends SwordItem {

    /** 貫通ダメージ量 */
    public static final float PIERCING_DAMAGE = 500.0F;

    public SunSwordItem(Properties properties) {
        // 表示上の攻撃力を500に合わせる（基礎1 + ティア9 + 修飾490）
        super(ModToolTiers.SOLAR, 490, -3.0F, properties.rarity(Rarity.EPIC).fireResistant());
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);

        if (!target.level().isClientSide()) {
            // 装備劣化（陽の化身と同じロジック）
            SolarCombatHelper.degradeEquipment(target, attacker);

            // 貫通ダメージ（星霊の笏と同じロジック）
            SolarCombatHelper.dealPiercingDamage(target,
                attacker.damageSources().magic(), PIERCING_DAMAGE);

            target.setSecondsOnFire(8);

            target.level().playSound(null, target.blockPosition(),
                SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0F, 0.6F);
            if (target.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.FLAME,
                    target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(),
                    30, 0.4, 0.5, 0.4, 0.1);
            }
        }

        return result;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        TooltipHelper.addBlank(tooltip);
        TooltipHelper.addHeader(tooltip, ChatFormatting.GOLD, "tooltip.aster_risk.sun_sword.header");
        TooltipHelper.addStat(tooltip, ChatFormatting.LIGHT_PURPLE, "tooltip.aster_risk.sun_sword.piercing",
            (int) PIERCING_DAMAGE);
        TooltipHelper.addStat(tooltip, ChatFormatting.RED, "tooltip.aster_risk.sun_sword.degrade");
        TooltipHelper.addDescription(tooltip, "tooltip.aster_risk.sun_sword.flavor");
    }
}
