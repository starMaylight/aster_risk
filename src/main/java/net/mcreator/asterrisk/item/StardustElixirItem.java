package net.mcreator.asterrisk.item;

import net.mcreator.asterrisk.init.AsterRiskModEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.Level;

/**
 * 星屑の薬 - Stardust Elixir
 * 飲むと星屑の加護効果を付与
 */
public class StardustElixirItem extends Item {
    
    public StardustElixirItem() {
        super(new Item.Properties().stacksTo(16));
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide()) {
            // 星屑の加護を5分間付与
            entity.addEffect(new MobEffectInstance(
                AsterRiskModEffects.STARDUST_PROTECTION.get(), 
                20 * 60 * 5,  // 5分
                0
            ));
        }
        
        if (entity instanceof Player player && !player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        
        return stack;
    }
}
