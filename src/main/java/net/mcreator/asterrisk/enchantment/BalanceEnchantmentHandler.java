package net.mcreator.asterrisk.enchantment;

import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.enchantment.celestial.DurabilityRetentionEnchantment;
import net.mcreator.asterrisk.enchantment.celestial.LastHeroEnchantment;
import net.mcreator.asterrisk.registry.ModEnchantments;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

/**
 * バランス調整用エンチャントの処理
 * - 最後の英雄（Last Hero）: 死亡時に経験値を消費して食いしばる
 * - 耐久保持（Durability Retention）: 耐久1/4以下でレベルを1消費して全回復
 */
@Mod.EventBusSubscriber(modid = AsterRiskMod.MODID)
public class BalanceEnchantmentHandler {

    /** 耐久保持のチェック間隔（tick） */
    private static final int DURABILITY_CHECK_INTERVAL = 20;

    // ===== 最後の英雄 =====

    /**
     * 致死ダメージそのものを打ち消す（連続攻撃でも経験値が続く限り耐え続ける）。
     * LOWESTで受けることで、他modによる軽減がすべて済んだ最終ダメージ値で判定する。
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDamage(LivingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        if (player.isCreative() || player.isSpectator()) return;

        // /kill など強制death系は食いしばれない
        if (event.getSource().is(DamageTypes.GENERIC_KILL)) return;

        // 致死でなければ何もしない（吸収シールドも考慮）
        float effectiveHealth = player.getHealth() + player.getAbsorptionAmount();
        if (event.getAmount() < effectiveHealth) return;

        if (!tryEndure(player)) return;

        // ダメージ自体を無効化することで死亡判定に到達させない
        event.setAmount(0.0F);
        event.setCanceled(true);
    }

    /**
     * 死亡イベント側の保険。
     * LivingDamageEventを経由しない死因（直接setHealth等）に対応する。
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        if (player.isCreative() || player.isSpectator()) return;
        if (event.getSource().is(DamageTypes.GENERIC_KILL)) return;

        if (!tryEndure(player)) return;
        event.setCanceled(true);
    }

    /**
     * 経験値を消費して食いしばりを試みる。
     * @return 発動できたらtrue
     */
    private static boolean tryEndure(Player player) {
        // 防具4部位のうち最も高いレベルを採用
        int enchantLevel = 0;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (!slot.isArmor()) continue;
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.isEmpty()) continue;
            enchantLevel = Math.max(enchantLevel,
                EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.LAST_HERO.get(), stack));
        }
        if (enchantLevel <= 0) return false;

        // 経験値が足りなければ発動しない
        int cost = LastHeroEnchantment.getXpLevelCost(enchantLevel);
        if (player.experienceLevel < cost) return false;

        player.giveExperienceLevels(-cost);

        // 復活処理
        player.setHealth(Math.max(1.0F, player.getMaxHealth() * 0.3F));
        player.setAbsorptionAmount(0.0F);
        player.removeAllEffects();
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 1));
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 200, 0));
        player.clearFire();
        // 連撃の次の一撃までにわずかな猶予を与える
        player.invulnerableTime = Math.max(player.invulnerableTime, 20);

        player.displayClientMessage(
            Component.translatable("message.aster_risk.last_hero.triggered", cost)
                .withStyle(ChatFormatting.GOLD),
            true);

        player.level().playSound(null, player.blockPosition(),
            SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.TOTEM_OF_UNDYING,
                player.getX(), player.getY() + 1.0D, player.getZ(), 60, 0.5, 1.0, 0.5, 0.3);
        }
        return true;
    }

    // ===== 耐久保持 =====

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) return;
        if (entity.tickCount % DURABILITY_CHECK_INTERVAL != 0) return;

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (stack.isEmpty()) continue;
            tryRestoreDurability(entity, stack);
        }
    }

    /**
     * 耐久が1/4以下ならエンチャントレベルを1つ消費して全回復する
     */
    private static void tryRestoreDurability(LivingEntity entity, ItemStack stack) {
        Enchantment enchantment = ModEnchantments.DURABILITY_RETENTION.get();
        int level = EnchantmentHelper.getItemEnchantmentLevel(enchantment, stack);
        if (level <= 0) return;
        if (!DurabilityRetentionEnchantment.shouldTrigger(stack)) return;

        // レベルを1つ消費（0になったらエンチャント自体を除去）
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
        if (level > 1) {
            enchantments.put(enchantment, level - 1);
        } else {
            enchantments.remove(enchantment);
        }
        EnchantmentHelper.setEnchantments(enchantments, stack);

        // 耐久を全回復
        stack.setDamageValue(0);

        entity.level().playSound(null, entity.blockPosition(),
            SoundEvents.ANVIL_USE, SoundSource.PLAYERS, 0.7F, 1.6F);
        if (entity.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.ENCHANT,
                entity.getX(), entity.getY() + 1.5D, entity.getZ(), 30, 0.5, 0.5, 0.5, 0.5);
        }

        if (entity instanceof Player player) {
            player.displayClientMessage(
                Component.translatable("message.aster_risk.durability_retention.triggered",
                    stack.getHoverName(), level - 1).withStyle(ChatFormatting.AQUA),
                true);
        }
    }
}
