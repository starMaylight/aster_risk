package net.mcreator.asterrisk.event;

import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.item.tool.MoonstoneToolItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 月光石ツールの夜間採掘速度ボーナス
 */
@Mod.EventBusSubscriber(modid = AsterRiskMod.MODID)
public class MoonstoneToolEventHandler {

    private static final float NIGHT_SPEED_BONUS = 1.3f; // +30%

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        Level level = player.level();
        
        if (isNight(level)) {
            ItemStack heldItem = player.getMainHandItem();
            
            // 月光石ツールかチェック
            if (heldItem.getItem() instanceof MoonstoneToolItems.MoonstonePickaxe ||
                heldItem.getItem() instanceof MoonstoneToolItems.MoonstoneShovel ||
                heldItem.getItem() instanceof MoonstoneToolItems.MoonstoneAxe) {
                
                event.setNewSpeed(event.getOriginalSpeed() * NIGHT_SPEED_BONUS);
            }
        }
    }

    private static boolean isNight(Level level) {
        long dayTime = level.getDayTime() % 24000;
        return dayTime >= 13000 && dayTime <= 23000;
    }
}
