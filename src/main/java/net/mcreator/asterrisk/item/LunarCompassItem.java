package net.mcreator.asterrisk.item;

import net.mcreator.asterrisk.mana.ManaProcedures;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

/**
 * 月の羅針盤 - スポーン地点への方向と距離を表示
 * （構造物検索は負荷が高すぎるため、シンプルな機能に変更）
 */
public class LunarCompassItem extends Item {
    
    private static final float MANA_COST = 25f;
    private static final int COOLDOWN_TICKS = 60; // 3秒

    public LunarCompassItem() {
        super(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.RARE)
            .durability(64)
        );
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        
        // クールダウン中は使用不可
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.pass(itemstack);
        }

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            // 魔力を消費
            if (ManaProcedures.castSpell(player, MANA_COST)) {
                
                BlockPos playerPos = player.blockPosition();
                
                // スポーン地点を取得
                BlockPos spawnPos = serverLevel.getSharedSpawnPos();
                
                // 方向と距離を計算
                double dx = spawnPos.getX() - playerPos.getX();
                double dz = spawnPos.getZ() - playerPos.getZ();
                double distance = Math.sqrt(dx * dx + dz * dz);
                String direction = getDirection(dx, dz);
                
                // 現在の座標も表示
                player.displayClientMessage(
                    Component.literal("§b[Lunar Compass]"),
                    false
                );
                player.displayClientMessage(
                    Component.literal("§7Your position: §e" + playerPos.getX() + ", " + playerPos.getY() + ", " + playerPos.getZ()),
                    false
                );
                player.displayClientMessage(
                    Component.literal("§7World spawn: §e" + direction + " §7(§e" + (int)distance + " blocks§7)"),
                    false
                );
                
                // 月齢情報も表示
                int moonPhase = serverLevel.getMoonPhase();
                String moonName = getMoonPhaseName(moonPhase);
                player.displayClientMessage(
                    Component.literal("§7Moon phase: §e" + moonName),
                    false
                );
                
                // 効果音
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS, 1.0f, 0.8f);
                
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

    private String getDirection(double dx, double dz) {
        double angle = Math.atan2(dz, dx) * 180 / Math.PI;
        angle = (angle + 360) % 360;
        
        if (angle < 22.5 || angle >= 337.5) return "East →";
        if (angle < 67.5) return "South-East ↘";
        if (angle < 112.5) return "South ↓";
        if (angle < 157.5) return "South-West ↙";
        if (angle < 202.5) return "West ←";
        if (angle < 247.5) return "North-West ↖";
        if (angle < 292.5) return "North ↑";
        return "North-East ↗";
    }

    private String getMoonPhaseName(int moonPhase) {
        return switch (moonPhase) {
            case 0 -> "Full Moon ●";
            case 1 -> "Waning Gibbous";
            case 2 -> "Last Quarter ◐";
            case 3 -> "Waning Crescent";
            case 4 -> "New Moon ○";
            case 5 -> "Waxing Crescent";
            case 6 -> "First Quarter ◑";
            case 7 -> "Waxing Gibbous";
            default -> "Unknown";
        };
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.literal("Lunar Compass");
    }
}
