package net.mcreator.asterrisk.item;

import net.mcreator.asterrisk.mana.ManaProcedures;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * 流星の杖 - 小さな流星を発射（攻撃）
 */
public class MeteorWandItem extends Item {
    
    private static final float MANA_COST = 30f;
    private static final int COOLDOWN_TICKS = 30; // 1.5秒
    private static final float PROJECTILE_SPEED = 1.5f;

    public MeteorWandItem() {
        super(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.RARE)
            .durability(128)
        );
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        
        // クールダウン中は使用不可
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.pass(itemstack);
        }

        if (!level.isClientSide()) {
            // 魔力を消費
            if (ManaProcedures.castSpell(player, MANA_COST)) {
                // プレイヤーの視線方向を取得
                Vec3 look = player.getLookAngle();
                
                // 小さな火球（流星）を発射
                SmallFireball fireball = new SmallFireball(
                    level,
                    player,
                    look.x * PROJECTILE_SPEED,
                    look.y * PROJECTILE_SPEED,
                    look.z * PROJECTILE_SPEED
                );
                
                // 発射位置を調整（プレイヤーの目の高さから）
                fireball.setPos(
                    player.getX() + look.x,
                    player.getEyeY() - 0.1,
                    player.getZ() + look.z
                );
                
                level.addFreshEntity(fireball);
                
                // 効果音
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0f, 0.8f);
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 0.5f, 1.2f);
                
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
        return Component.literal("Meteor Wand");
    }
}
