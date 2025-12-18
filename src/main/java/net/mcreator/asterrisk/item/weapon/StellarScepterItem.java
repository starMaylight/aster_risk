package net.mcreator.asterrisk.item.weapon;

import net.mcreator.asterrisk.mana.LunarManaCapability;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Stellar Scepter - 最終魔法武器（星光の杖）
 * 強力な魔法攻撃
 */
public class StellarScepterItem extends Item {
    
    private static final int MANA_COST = 50;
    private static final float DAMAGE = 25.0F;
    private static final double RANGE = 12.0D;
    
    public StellarScepterItem(Properties properties) {
        super(properties.stacksTo(1).rarity(Rarity.EPIC).fireResistant().durability(500));
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        // マナチェック
        AtomicBoolean hasEnoughMana = new AtomicBoolean(false);
        LunarManaCapability.get(player).ifPresent(mana -> {
            hasEnoughMana.set(mana.canConsume(MANA_COST));
        });
        
        if (!hasEnoughMana.get()) {
            if (!level.isClientSide()) {
                player.displayClientMessage(Component.literal("§c Not enough mana! (Need " + MANA_COST + ")"), true);
            }
            return InteractionResultHolder.fail(stack);
        }
        
        if (!level.isClientSide()) {
            ServerLevel serverLevel = (ServerLevel) level;
            
            // マナ消費
            LunarManaCapability.get(player).ifPresent(mana -> {
                mana.consumeMana(MANA_COST);
            });
            
            // プレイヤーの視線方向に星のビームを発射
            Vec3 look = player.getLookAngle();
            Vec3 start = player.getEyePosition();
            
            // ビームパーティクル
            for (double d = 0; d < RANGE; d += 0.5) {
                double x = start.x + look.x * d;
                double y = start.y + look.y * d;
                double z = start.z + look.z * d;
                
                serverLevel.sendParticles(ParticleTypes.END_ROD, x, y, z, 2, 0.1, 0.1, 0.1, 0.02);
                serverLevel.sendParticles(ParticleTypes.FIREWORK, x, y, z, 1, 0.05, 0.05, 0.05, 0.01);
            }
            
            // 範囲内の敵にダメージ
            Vec3 end = start.add(look.scale(RANGE));
            AABB beamBox = new AABB(start, end).inflate(1.0);
            
            List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, beamBox,
                e -> e != player && e.isAlive());
            
            for (LivingEntity target : targets) {
                // ビームの線上にいるか確認
                Vec3 toTarget = target.position().add(0, target.getBbHeight() / 2, 0).subtract(start);
                double projection = toTarget.dot(look);
                
                if (projection > 0 && projection < RANGE) {
                    Vec3 closestPoint = start.add(look.scale(projection));
                    double distance = closestPoint.distanceTo(target.position().add(0, target.getBbHeight() / 2, 0));
                    
                    if (distance < 2.0) {
                        target.hurt(player.damageSources().magic(), DAMAGE);
                        
                        // ヒットエフェクト
                        serverLevel.sendParticles(ParticleTypes.FLASH,
                            target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(),
                            1, 0, 0, 0, 0);
                    }
                }
            }
            
            // サウンド
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.END_PORTAL_SPAWN, SoundSource.PLAYERS, 0.5F, 2.0F);
            
            // クールダウン
            player.getCooldowns().addCooldown(this, 40);
            
            // 耐久値消費
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
        }
        
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§6§l[Stellar Scepter]"));
        tooltip.add(Component.literal("§e Right-click: Fire star beam"));
        tooltip.add(Component.literal("§7 Damage: " + (int)DAMAGE));
        tooltip.add(Component.literal("§9 Mana Cost: " + MANA_COST));
        tooltip.add(Component.literal("§8Channeled starlight incarnate"));
    }
    
    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
