package net.mcreator.asterrisk.item.weapon;

import net.mcreator.asterrisk.registry.ModItems;
import net.mcreator.asterrisk.mana.ManaProcedures;
import net.mcreator.asterrisk.util.TooltipHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 流星のハンマー - 右クリックで地面に衝撃波
 */
public class MeteorHammerItem extends AxeItem {

    private static final float MANA_COST = 30f;
    private static final int COOLDOWN_TICKS = 50; // 2.5秒
    private static final double RANGE = 4.0;
    private static final float DAMAGE = 14.0f;  // 衝撃波ダメージ増
    private static final double KNOCKBACK_STRENGTH = 1.5;

    private static final Tier METEOR_TIER = new Tier() {
        @Override public int getUses() { return 1200; }
        @Override public float getSpeed() { return 6.5f; }
        @Override public float getAttackDamageBonus() { return 8.0f; } // 基礎4 + 8 = 12ダメージ
        @Override public int getLevel() { return 3; }
        @Override public int getEnchantmentValue() { return 14; }
        @Override public Ingredient getRepairIngredient() { 
            return Ingredient.of(ModItems.METEORITE_FRAGMENT.get()); 
        }
    };

    public MeteorHammerItem(Properties properties) {
        super(METEOR_TIER, 5.0f, -3.2f, properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide()) {
            // マナチェック＆消費
            if (!ManaProcedures.castSpell(player, MANA_COST)) {
                return InteractionResultHolder.fail(stack);
            }

            // 範囲内の敵にダメージ+ノックバック
            AABB area = player.getBoundingBox().inflate(RANGE);
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area,
                e -> e != player && e.isAlive());

            for (LivingEntity entity : entities) {
                // ダメージ
                entity.hurt(player.damageSources().playerAttack(player), DAMAGE);
                
                // ノックバック（プレイヤーから離れる方向）
                Vec3 knockbackDir = entity.position().subtract(player.position()).normalize();
                entity.setDeltaMovement(
                    knockbackDir.x * KNOCKBACK_STRENGTH,
                    0.4,
                    knockbackDir.z * KNOCKBACK_STRENGTH
                );
                entity.hurtMarked = true;
            }

            // 地面エフェクト
            if (level instanceof ServerLevel serverLevel) {
                // 衝撃波パーティクル
                for (int i = 0; i < 50; i++) {
                    double angle = Math.random() * Math.PI * 2;
                    double dist = Math.random() * RANGE;
                    double offsetX = Math.cos(angle) * dist;
                    double offsetZ = Math.sin(angle) * dist;
                    serverLevel.sendParticles(ParticleTypes.FLAME,
                        player.getX() + offsetX, player.getY() + 0.1, player.getZ() + offsetZ,
                        1, 0, 0.05, 0, 0.02);
                }
                // 煙
                for (int i = 0; i < 20; i++) {
                    double offsetX = (Math.random() - 0.5) * RANGE;
                    double offsetZ = (Math.random() - 0.5) * RANGE;
                    serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                        player.getX() + offsetX, player.getY() + 0.5, player.getZ() + offsetZ,
                        1, 0, 0.1, 0, 0.01);
                }
            }

            // 音
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 0.8f, 0.7f);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 0.5f, 0.5f);

            // クールダウン
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
            stack.hurtAndBreak(2, player, p -> p.broadcastBreakEvent(hand));
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        TooltipHelper.addBlank(tooltip);
        TooltipHelper.addHeader(tooltip, ChatFormatting.RED, "tooltip.aster_risk.meteor_hammer.header");
        TooltipHelper.addStat(tooltip, ChatFormatting.GRAY, "tooltip.aster_risk.meteor_hammer.action");
        TooltipHelper.addStat(tooltip, ChatFormatting.RED, "tooltip.aster_risk.meteor_hammer.aoe_damage", (int) DAMAGE);
        TooltipHelper.addStat(tooltip, ChatFormatting.RED, "tooltip.aster_risk.meteor_hammer.knockback");
        TooltipHelper.addStat(tooltip, ChatFormatting.DARK_AQUA, "tooltip.aster_risk.stat.mana_cost", (int) MANA_COST);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
