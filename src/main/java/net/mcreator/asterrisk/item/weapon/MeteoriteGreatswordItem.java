package net.mcreator.asterrisk.item.weapon;

import net.mcreator.asterrisk.registry.ModItems;
import net.mcreator.asterrisk.mana.ManaUtils;
import net.mcreator.asterrisk.util.TooltipHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 隕石の大剣 - 攻撃時に確率で爆発ダメージ追加
 */
public class MeteoriteGreatswordItem extends SwordItem {

    private static final float MANA_COST_PER_PROC = 5f;
    private static final float PROC_CHANCE = 0.25f; // 25%の確率
    private static final float EXPLOSION_DAMAGE = 8.0f;  // 爆発ダメージ増

    private static final Tier METEORITE_TIER = new Tier() {
        @Override public int getUses() { return 1400; }
        @Override public float getSpeed() { return 6.5f; }
        @Override public float getAttackDamageBonus() { return 10.0f; } // 基礎4 + 10 = 14ダメージ
        @Override public int getLevel() { return 4; }
        @Override public int getEnchantmentValue() { return 12; }
        @Override public Ingredient getRepairIngredient() { 
            return Ingredient.of(ModItems.METEORITE_FRAGMENT.get()); 
        }
    };

    public MeteoriteGreatswordItem(Properties properties) {
        super(METEORITE_TIER, 7, -3.0f, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player player && !player.level().isClientSide()) {
            // 確率で爆発効果発動
            if (Math.random() < PROC_CHANCE) {
                // マナがあれば消費して爆発
                if (ManaUtils.tryConsumeMana(player, MANA_COST_PER_PROC)) {
                    // 追加ダメージ
                    target.hurt(player.damageSources().playerAttack(player), EXPLOSION_DAMAGE);
                    
                    // 炎上効果
                    target.setSecondsOnFire(3);

                    // エフェクト
                    Level level = player.level();
                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.EXPLOSION,
                            target.getX(), target.getY() + 1, target.getZ(),
                            1, 0, 0, 0, 0);
                        for (int i = 0; i < 15; i++) {
                            serverLevel.sendParticles(ParticleTypes.FLAME,
                                target.getX(), target.getY() + 0.5, target.getZ(),
                                1, 0.5, 0.5, 0.5, 0.05);
                        }
                    }

                    // 音
                    level.playSound(null, target.getX(), target.getY(), target.getZ(),
                        SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 0.5f, 1.2f);
                }
            }
        }

        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        TooltipHelper.addBlank(tooltip);
        TooltipHelper.addHeader(tooltip, ChatFormatting.RED, "tooltip.aster_risk.meteorite_greatsword.header");
        TooltipHelper.addStat(tooltip, ChatFormatting.GRAY, "tooltip.aster_risk.meteorite_greatsword.action");
        TooltipHelper.addStat(tooltip, ChatFormatting.RED, "tooltip.aster_risk.meteorite_greatsword.proc",
            (int) (PROC_CHANCE * 100), (int) EXPLOSION_DAMAGE);
        TooltipHelper.addStat(tooltip, ChatFormatting.RED, "tooltip.aster_risk.meteorite_greatsword.fire");
        TooltipHelper.addStat(tooltip, ChatFormatting.DARK_AQUA, "tooltip.aster_risk.stat.mana_cost_proc", (int) MANA_COST_PER_PROC);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
