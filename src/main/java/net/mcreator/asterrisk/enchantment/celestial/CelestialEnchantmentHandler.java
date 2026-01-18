package net.mcreator.asterrisk.enchantment.celestial;

import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.mana.LunarManaCapability;
import net.mcreator.asterrisk.registry.ModEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.mcreator.asterrisk.entity.HeavenlyAnvilEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;
/**
 * CelestialEnchantingTable専用エンチャントのイベントハンドラー
 */
@Mod.EventBusSubscriber(modid = AsterRiskMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CelestialEnchantmentHandler {
    
    private static final Random RANDOM = new Random();
    
    // バフ効果リスト
    private static final MobEffect[] BUFF_EFFECTS = {
        MobEffects.DAMAGE_BOOST,
        MobEffects.MOVEMENT_SPEED,
        MobEffects.DIG_SPEED,
        MobEffects.REGENERATION,
        MobEffects.DAMAGE_RESISTANCE,
        MobEffects.FIRE_RESISTANCE,
        MobEffects.JUMP,
        MobEffects.ABSORPTION,
        MobEffects.LUCK
    };
    
    // デバフ効果リスト
    private static final MobEffect[] DEBUFF_EFFECTS = {
        MobEffects.MOVEMENT_SLOWDOWN,
        MobEffects.DIG_SLOWDOWN,
        MobEffects.WEAKNESS,
        MobEffects.BLINDNESS,
        MobEffects.CONFUSION,
        MobEffects.POISON,
        MobEffects.WITHER,
        MobEffects.LEVITATION,
        MobEffects.GLOWING
    };
    
    /**
     * ダメージイベント処理
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingHurt(LivingHurtEvent event) {
        Entity sourceEntity = event.getSource().getEntity();
        LivingEntity target = event.getEntity();
        float damage = event.getAmount();
        
        // === 攻撃者側の処理 ===
        if (sourceEntity instanceof Player attacker) {
            ItemStack weapon = attacker.getMainHandItem();
            
            // 1. 星砕きの一撃
            int starBreakerLevel = getEnchantLevel(weapon, ModEnchantments.STAR_BREAKER.get());
            if (starBreakerLevel > 0 && RANDOM.nextFloat() < StarBreakerEnchantment.getTriggerChance(starBreakerLevel)) {
                float weaponDamage = (float) attacker.getAttributeValue(Attributes.ATTACK_DAMAGE);
                float bonusDamage = weaponDamage * StarBreakerEnchantment.getDamageMultiplier();
                // 防御無視ダメージ
                target.hurt(attacker.damageSources().magic(), bonusDamage);
                spawnCritParticles(target);
            }
            
            // 6. 天罰の鉄槌
            int anvilLevel = getEnchantLevel(weapon, ModEnchantments.ANVIL_FROM_HEAVEN.get());
            if (anvilLevel > 0 && RANDOM.nextFloat() < AnvilFromHeavenEnchantment.getTriggerChance(anvilLevel)) {
                spawnFallingAnvil(target);
            }
            
            // 8. 幸運の星
            int luckyStarLevel = getEnchantLevel(weapon, ModEnchantments.LUCKY_STAR.get());
            if (luckyStarLevel > 0 && RANDOM.nextFloat() < LuckyStarEnchantment.getTriggerChance()) {
                applyRandomBuffs(attacker, LuckyStarEnchantment.getBuffCount(luckyStarLevel));
            }
            
            // 9. マナビーム
            int manaBeamLevel = getEnchantLevel(weapon, ModEnchantments.MANA_BEAM.get());
            if (manaBeamLevel > 0 && RANDOM.nextFloat() < ManaBeamEnchantment.getTriggerChance(manaBeamLevel)) {
                tryFireManaBeam(attacker, target, weapon);
            }
        }
        
        // === 防御者側の処理 ===
        if (target instanceof Player defender) {
            int totalBarrierLevel = 0;
            int totalReverseHealLevel = 0;
            int totalCursedLevel = 0;
            
            for (ItemStack armor : defender.getArmorSlots()) {
                totalBarrierLevel += getEnchantLevel(armor, ModEnchantments.ABSOLUTE_BARRIER.get());
                totalReverseHealLevel += getEnchantLevel(armor, ModEnchantments.REVERSE_HEALING.get());
                totalCursedLevel += getEnchantLevel(armor, ModEnchantments.CURSED_RETALIATION.get());
            }
            
            // 2. 絶対障壁
            if (totalBarrierLevel > 0 && RANDOM.nextFloat() < AbsoluteBarrierEnchantment.getTriggerChance(totalBarrierLevel)) {
                event.setCanceled(true);
                spawnShieldParticles(defender);
                defender.level().playSound(null, defender.getX(), defender.getY(), defender.getZ(),
                    SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.0f, 1.5f);
                return;
            }
            
            // 3. 反転治癒
            if (totalReverseHealLevel > 0 && RANDOM.nextFloat() < ReverseHealingEnchantment.getTriggerChance(totalReverseHealLevel)) {
                float healAmount = damage * ReverseHealingEnchantment.getHealMultiplier();
                defender.heal(healAmount);
                spawnHealParticles(defender);
            }
            
            // 7. 呪詛の反撃
            if (totalCursedLevel > 0 && RANDOM.nextFloat() < CursedRetaliationEnchantment.getTriggerChance(totalCursedLevel)) {
                applyDebuffsToNearbyEnemies(defender);
            }
        }
    }
    
    /**
     * 死亡イベント処理 - 5. 死の連鎖
     */
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        Entity killer = event.getSource().getEntity();
        LivingEntity victim = event.getEntity();
        
        if (killer instanceof Player player) {
            ItemStack weapon = player.getMainHandItem();
            int deathChainLevel = getEnchantLevel(weapon, ModEnchantments.DEATH_CHAIN.get());
            
            if (deathChainLevel > 0) {
                float victimMaxHealth = victim.getMaxHealth();
                float chainDamage = victimMaxHealth * DeathChainEnchantment.getDamageMultiplier(deathChainLevel);
                double range = DeathChainEnchantment.getRange();
                
                AABB area = new AABB(
                    victim.getX() - range, victim.getY() - range, victim.getZ() - range,
                    victim.getX() + range, victim.getY() + range, victim.getZ() + range
                );
                
                List<LivingEntity> nearbyMobs = victim.level().getEntitiesOfClass(LivingEntity.class, area, 
                    e -> e != player && e != victim && e.isAlive());
                
                for (LivingEntity mob : nearbyMobs) {
                    mob.hurt(player.damageSources().magic(), chainDamage);
                }
                
                if (!nearbyMobs.isEmpty() && victim.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.SOUL, 
                        victim.getX(), victim.getY() + 1, victim.getZ(), 
                        20, 1.0, 1.0, 1.0, 0.1);
                }
            }
        }
    }
    
    /**
     * ブロック破壊イベント - 4. 幸運の極み
     */
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;
        
        ItemStack tool = player.getMainHandItem();
        int fortunesPeakLevel = getEnchantLevel(tool, ModEnchantments.FORTUNES_PEAK.get());
        
        if (fortunesPeakLevel > 0 && RANDOM.nextFloat() < FortunesPeakEnchantment.getTriggerChance(fortunesPeakLevel)) {
            // 幸運の極みが発動 - ドロップ増加はLootModifierで処理するか、
            // ここでマーカーをつける
            // 実際の実装はLootModifierが必要
            if (player.level() instanceof ServerLevel serverLevel) {
                BlockPos pos = event.getPos();
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    10, 0.5, 0.5, 0.5, 0);
            }
        }
    }
    
    // ===== ヘルパーメソッド =====
    
    private static int getEnchantLevel(ItemStack stack, Enchantment enchant) {
        if (stack.isEmpty()) return 0;
        
        // 10. 星の昇華の効果を適用
        int baseLevel = EnchantmentHelper.getItemEnchantmentLevel(enchant, stack);
        
        if (enchant != ModEnchantments.STELLAR_ASCENSION.get()) {
            int ascensionLevel = EnchantmentHelper.getItemEnchantmentLevel(
                ModEnchantments.STELLAR_ASCENSION.get(), stack);
            if (ascensionLevel > 0 && baseLevel > 0) {
                baseLevel += StellarAscensionEnchantment.getLevelBonus(ascensionLevel);
            }
        }
        
        return baseLevel;
    }
    
    private static void spawnCritParticles(LivingEntity target) {
        if (target.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.CRIT,
                target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(),
                30, 0.5, 0.5, 0.5, 0.5);
            serverLevel.sendParticles(ParticleTypes.ENCHANT,
                target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(),
                20, 0.5, 0.5, 0.5, 1.0);
        }
    }
    
    private static void spawnShieldParticles(Player player) {
        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.END_ROD,
                player.getX(), player.getY() + 1, player.getZ(),
                30, 0.8, 0.8, 0.8, 0.05);
        }
    }
    
    private static void spawnHealParticles(Player player) {
        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.HEART,
                player.getX(), player.getY() + 1.5, player.getZ(),
                10, 0.5, 0.5, 0.5, 0);
        }
    }
    
    private static void spawnFallingAnvil(LivingEntity target) {
        Level level = target.level();
        if (level.isClientSide) return;
        
        double x = target.getX();
        double y = target.getY() + AnvilFromHeavenEnchantment.getDropHeight() + 5;
        double z = target.getZ();
        
        // カスタム金床エンティティを生成
        HeavenlyAnvilEntity anvil = new HeavenlyAnvilEntity(level, x, y, z);
        level.addFreshEntity(anvil);
        
        level.playSound(null, target.getX(), target.getY() + 5, target.getZ(),
            SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 0.5f, 1.5f);
    }
    
    private static void applyRandomBuffs(Player player, int count) {
        for (int i = 0; i < count; i++) {
            MobEffect buff = BUFF_EFFECTS[RANDOM.nextInt(BUFF_EFFECTS.length)];
            player.addEffect(new MobEffectInstance(buff, 200, 1)); // 10秒、レベル2
        }
        
        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                player.getX(), player.getY() + 1, player.getZ(),
                20, 0.5, 0.5, 0.5, 0);
        }
    }
    
    private static void applyDebuffsToNearbyEnemies(Player player) {
        double range = CursedRetaliationEnchantment.getRange();
        AABB area = player.getBoundingBox().inflate(range);
        
        List<Monster> enemies = player.level().getEntitiesOfClass(Monster.class, area);
        
        for (Monster enemy : enemies) {
            MobEffect debuff = DEBUFF_EFFECTS[RANDOM.nextInt(DEBUFF_EFFECTS.length)];
            enemy.addEffect(new MobEffectInstance(debuff, 100, 1)); // 5秒、レベル2
        }
        
        if (!enemies.isEmpty() && player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.WITCH,
                player.getX(), player.getY() + 1, player.getZ(),
                30, range / 2, 1, range / 2, 0);
        }
    }
    
    private static void tryFireManaBeam(Player player, LivingEntity target, ItemStack weapon) {
        player.getCapability(LunarManaCapability.LUNAR_MANA).ifPresent(mana -> {
            float cost = ManaBeamEnchantment.getManaCost();
            if (mana.canConsume(cost)) {
                mana.consumeMana(cost);
                
                float weaponDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
                
                // ビームの視覚効果
                if (player.level() instanceof ServerLevel serverLevel) {
                    Vec3 start = player.getEyePosition();
                    Vec3 end = target.position().add(0, target.getBbHeight() / 2, 0);
                    Vec3 direction = end.subtract(start).normalize();
                    double distance = start.distanceTo(end);
                    
                    for (double d = 0; d < distance; d += 0.5) {
                        Vec3 pos = start.add(direction.scale(d));
                        serverLevel.sendParticles(ParticleTypes.END_ROD,
                            pos.x, pos.y, pos.z, 1, 0, 0, 0, 0);
                    }
                }
                
                // 追加ダメージ
                target.hurt(player.damageSources().magic(), weaponDamage);
                
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0f, 2.0f);
            }
        });
    }
}
