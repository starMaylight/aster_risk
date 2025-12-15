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
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 月光の霊薬 - Lunar Elixir
 * 飲むと月光の祝福効果を付与
 */
public class LunarElixirItem extends Item {
    
    public LunarElixirItem() {
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
            // 月光の祝福を3分間付与
            entity.addEffect(new MobEffectInstance(
                AsterRiskModEffects.LUNAR_BLESSING.get(), 
                20 * 60 * 3,  // 3分
                0
            ));
        }
        
        if (entity instanceof Player player && !player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.aster_risk.lunar_elixir"));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
