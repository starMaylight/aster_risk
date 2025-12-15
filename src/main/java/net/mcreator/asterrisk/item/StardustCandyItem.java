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
 * 星屑のキャンディ - Stardust Candy
 * 食べると星屑の加護効果を付与
 */
public class StardustCandyItem extends Item {
    
    public StardustCandyItem() {
        super(new Item.Properties().stacksTo(16));
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 24; // キャンディなので短め
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.EAT;
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

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.aster_risk.stardust_candy"));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
