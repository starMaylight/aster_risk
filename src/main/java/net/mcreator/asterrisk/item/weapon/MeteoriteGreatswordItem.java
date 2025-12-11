package net.mcreator.asterrisk.item.weapon;

import net.mcreator.asterrisk.init.AsterRiskModItems;
import net.mcreator.asterrisk.mana.ManaUtils;
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
    private static final float EXPLOSION_DAMAGE = 4.0f;

    private static final Tier METEORITE_TIER = new Tier() {
        @Override public int getUses() { return 1200; }
        @Override public float getSpeed() { return 6.0f; }
        @Override public float getAttackDamageBonus() { return 7.0f; } // 基礎4 + 7 = 11ダメージ
        @Override public int getLevel() { return 4; }
        @Override public int getEnchantmentValue() { return 10; }
        @Override public Ingredient getRepairIngredient() { 
            return Ingredient.of(AsterRiskModItems.METEORITE_FRAGMENT.get()); 
        }
    };

    public MeteoriteGreatswordItem(Properties properties) {
        super(METEORITE_TIER, 7, -3.0f, properties); // 遅いが高火力
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.literal("Meteorite Greatsword");
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
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§c☄ Meteorite Greatsword"));
        tooltip.add(Component.literal("§7On Hit: Meteor Strike"));
        tooltip.add(Component.literal("§c  " + (int)(PROC_CHANCE * 100) + "% chance for +" + (int)EXPLOSION_DAMAGE + " explosion damage"));
        tooltip.add(Component.literal("§c  Sets target on fire"));
        tooltip.add(Component.literal("§3  Mana Cost: " + (int)MANA_COST_PER_PROC + " (on proc)"));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
