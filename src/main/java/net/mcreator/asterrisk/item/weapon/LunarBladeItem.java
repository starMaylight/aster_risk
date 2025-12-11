package net.mcreator.asterrisk.item.weapon;

import net.mcreator.asterrisk.init.AsterRiskModItems;
import net.mcreator.asterrisk.mana.ManaProcedures;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 月光の剣 - 右クリックで範囲攻撃+発光効果
 */
public class LunarBladeItem extends SwordItem {

    private static final float MANA_COST = 20f;
    private static final int COOLDOWN_TICKS = 30; // 1.5秒
    private static final double RANGE = 5.0;
    private static final float DAMAGE = 6.0f;

    private static final Tier LUNAR_TIER = new Tier() {
        @Override public int getUses() { return 750; }
        @Override public float getSpeed() { return 7.0f; }
        @Override public float getAttackDamageBonus() { return 3.0f; } // 基礎4 + 3 = 7ダメージ
        @Override public int getLevel() { return 3; }
        @Override public int getEnchantmentValue() { return 18; }
        @Override public Ingredient getRepairIngredient() { 
            return Ingredient.of(AsterRiskModItems.MOONSTONE.get()); 
        }
    };

    public LunarBladeItem(Properties properties) {
        super(LUNAR_TIER, 3, -2.4f, properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.literal("Lunar Blade");
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide()) {
            // マナチェック＆消費
            if (!ManaProcedures.castSpell(player, MANA_COST)) {
                return InteractionResultHolder.fail(stack);
            }

            // 範囲内の敵にダメージ+発光効果
            AABB area = player.getBoundingBox().inflate(RANGE);
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area,
                e -> e != player && e.isAlive());

            for (LivingEntity entity : entities) {
                entity.hurt(player.damageSources().playerAttack(player), DAMAGE);
                entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 0)); // 5秒間発光
            }

            // エフェクト
            if (level instanceof ServerLevel serverLevel) {
                for (int i = 0; i < 30; i++) {
                    double offsetX = (Math.random() - 0.5) * RANGE * 2;
                    double offsetY = Math.random() * 2;
                    double offsetZ = (Math.random() - 0.5) * RANGE * 2;
                    serverLevel.sendParticles(ParticleTypes.END_ROD,
                        player.getX() + offsetX, player.getY() + offsetY, player.getZ() + offsetZ,
                        1, 0, 0.1, 0, 0.05);
                }
            }

            // 音
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0f, 0.8f);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0f, 1.2f);

            // クールダウン
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§9☽ Lunar Blade"));
        tooltip.add(Component.literal("§7Right-click: Lunar Slash"));
        tooltip.add(Component.literal("§b  Deals " + (int)DAMAGE + " damage to nearby enemies"));
        tooltip.add(Component.literal("§b  Applies Glowing effect"));
        tooltip.add(Component.literal("§3  Mana Cost: " + (int)MANA_COST));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
