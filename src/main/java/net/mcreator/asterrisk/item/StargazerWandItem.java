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

/**
 * 星見の杖 - 暗視効果を付与（60秒）
 */
public class StargazerWandItem extends Item {
    
    private static final float MANA_COST = 20f;
    private static final int COOLDOWN_TICKS = 40; // 2秒
    private static final int EFFECT_DURATION = 1200; // 60秒（20tick * 60）

    public StargazerWandItem() {
        super(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.UNCOMMON)
            .durability(192)
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
                // 暗視効果を付与
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, EFFECT_DURATION, 0, false, true));
                
                // 効果音
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0f, 1.2f);
                
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
        return Component.literal("Stargazer Wand");
    }
}
