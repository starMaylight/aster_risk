package net.mcreator.asterrisk.item;

import net.mcreator.asterrisk.mana.ManaProcedures;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;

/**
 * 月光の癒し - 体力を回復（ハート4個 = 8HP）+ 再生効果
 */
public class LunarHealingStaffItem extends Item {
    
    private static final float MANA_COST = 35f;
    private static final int COOLDOWN_TICKS = 100; // 5秒
    private static final float HEAL_AMOUNT = 8.0f; // ハート4個分
    private static final int REGEN_DURATION = 200; // 10秒

    public LunarHealingStaffItem() {
        super(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.RARE)
            .durability(96)
        );
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        
        // クールダウン中は使用不可
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.pass(itemstack);
        }

        // 体力が満タンの場合は使用しない
        if (player.getHealth() >= player.getMaxHealth()) {
            if (level.isClientSide()) {
                player.displayClientMessage(
                    Component.literal("§aYour health is already full!"),
                    true
                );
            }
            return InteractionResultHolder.pass(itemstack);
        }

        if (!level.isClientSide()) {
            // 魔力を消費
            if (ManaProcedures.castSpell(player, MANA_COST)) {
                // 体力を回復
                player.heal(HEAL_AMOUNT);
                
                // 再生効果を付与
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, REGEN_DURATION, 0, false, true));
                
                // パーティクルエフェクト
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(
                        ParticleTypes.HEART,
                        player.getX(), player.getY() + 1, player.getZ(),
                        8, 0.5, 0.5, 0.5, 0.1
                    );
                    serverLevel.sendParticles(
                        ParticleTypes.END_ROD,
                        player.getX(), player.getY() + 1, player.getZ(),
                        15, 0.5, 1.0, 0.5, 0.05
                    );
                }
                
                // 効果音
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.5f, 1.5f);
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.3f, 1.8f);
                
                // 耐久値を減らす（クリエイティブ以外）
                if (!player.isCreative()) {
                    itemstack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                }
                
                // クールダウンを設定
                player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
                
                return InteractionResultHolder.success(itemstack);
            }
        }
        
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.literal("Lunar Healing Staff");
    }
}
