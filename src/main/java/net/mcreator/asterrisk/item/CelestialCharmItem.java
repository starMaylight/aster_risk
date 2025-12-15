package net.mcreator.asterrisk.item;

import net.mcreator.asterrisk.init.AsterRiskModEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 天体のお守り - Celestial Charm
 * 右クリックで天体の守り効果を付与
 * 消費されない（クールダウンあり）
 */
public class CelestialCharmItem extends Item {
    
    private static final int COOLDOWN_TICKS = 20 * 60 * 10; // 10分
    private static final int EFFECT_DURATION = 20 * 60 * 3; // 3分
    
    public CelestialCharmItem() {
        super(new Item.Properties().stacksTo(1).durability(100));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (!level.isClientSide()) {
            if (!player.getCooldowns().isOnCooldown(this)) {
                // 天体の守り効果を付与
                player.addEffect(new MobEffectInstance(
                    AsterRiskModEffects.CELESTIAL_GUARD.get(), 
                    EFFECT_DURATION,
                    0
                ));
                
                // 耐久値を消費
                stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                
                // クールダウン設定
                player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
                
                return InteractionResultHolder.success(stack);
            }
        }
        
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.aster_risk.celestial_charm"));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
