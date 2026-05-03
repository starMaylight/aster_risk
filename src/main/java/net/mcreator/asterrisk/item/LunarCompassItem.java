package net.mcreator.asterrisk.item;

import net.mcreator.asterrisk.block.LunarPortalBlock;
import net.mcreator.asterrisk.registry.ModBlocks;
import net.mcreator.asterrisk.mana.ManaProcedures;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 月の羅針盤 - スポーン地点への方向と距離を表示
 * 右クリックでポータル点火機能追加
 */
public class LunarCompassItem extends Item {
    
    private static final float MANA_COST = 25f;
    private static final float PORTAL_MANA_COST = 100f;
    private static final int COOLDOWN_TICKS = 60; // 3秒

    public LunarCompassItem() {
        super(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.RARE)
            .durability(64)
        );
    }
    
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        
        if (player == null) return InteractionResult.PASS;
        
        BlockState state = level.getBlockState(pos);
        
        // Moonstone Bricksをクリックした場合、ポータル点火を試みる
        if (state.is(ModBlocks.MOONSTONE_BRICKS.get())) {
            if (!level.isClientSide()) {
                // マナコスト確認
                if (!ManaProcedures.castSpell(player, PORTAL_MANA_COST)) {
                    player.displayClientMessage(
                        Component.translatable("message.aster_risk.compass.no_mana_portal", (int) PORTAL_MANA_COST)
                            .withStyle(ChatFormatting.RED),
                        true
                    );
                    return InteractionResult.FAIL;
                }
                
                // ポータルフレームを探す
                boolean success = tryIgnitePortal(level, pos, player);
                
                if (success) {
                    player.displayClientMessage(
                        Component.translatable("message.aster_risk.compass.portal_opened")
                            .withStyle(ChatFormatting.LIGHT_PURPLE),
                        true
                    );
                    level.playSound(null, pos, SoundEvents.END_PORTAL_SPAWN, SoundSource.BLOCKS, 1.0f, 1.0f);
                    
                    // 耐久値を減らす
                    if (!player.isCreative()) {
                        context.getItemInHand().hurtAndBreak(5, player, p -> p.broadcastBreakEvent(context.getHand()));
                    }
                    
                    return InteractionResult.SUCCESS;
                } else {
                    // マナを返還（失敗時）
                    player.getCapability(net.mcreator.asterrisk.mana.LunarManaCapability.LUNAR_MANA).ifPresent(mana -> mana.addMana(PORTAL_MANA_COST));
                    player.displayClientMessage(
                        Component.translatable("message.aster_risk.compass.invalid_frame")
                            .withStyle(ChatFormatting.RED),
                        true
                    );
                    return InteractionResult.FAIL;
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        
        return InteractionResult.PASS;
    }
    
    private boolean tryIgnitePortal(Level level, BlockPos clickedPos, Player player) {
        // クリックした位置の全方向を探索してポータル内部を見つける
        // 水平方向だけでなく、上下も探索
        for (Direction dir : Direction.values()) {
            BlockPos insidePos = clickedPos.relative(dir);
            BlockState insideState = level.getBlockState(insidePos);
            
            if (insideState.isAir()) {
                // X軸方向のポータルを試す（東西に広がるフレーム）
                LunarPortalBlock.Size sizeX = new LunarPortalBlock.Size(level, insidePos, Direction.Axis.X);
                if (sizeX.isValid() && !sizeX.isComplete()) {
                    sizeX.createPortalBlocks();
                    return true;
                }
                
                // Z軸方向のポータルを試す（南北に広がるフレーム）
                LunarPortalBlock.Size sizeZ = new LunarPortalBlock.Size(level, insidePos, Direction.Axis.Z);
                if (sizeZ.isValid() && !sizeZ.isComplete()) {
                    sizeZ.createPortalBlocks();
                    return true;
                }
            }
        }
        
        return false;
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

                // 現在の座標も表示
                player.displayClientMessage(
                    Component.translatable("message.aster_risk.compass.header").withStyle(ChatFormatting.AQUA),
                    false
                );
                player.displayClientMessage(
                    Component.translatable("message.aster_risk.compass.position",
                        playerPos.getX(), playerPos.getY(), playerPos.getZ()).withStyle(ChatFormatting.GRAY),
                    false
                );
                player.displayClientMessage(
                    Component.translatable("message.aster_risk.compass.spawn",
                        Component.translatable(getDirectionKey(dx, dz)), (int) distance).withStyle(ChatFormatting.GRAY),
                    false
                );

                // 月齢情報も表示
                int moonPhase = serverLevel.getMoonPhase();
                player.displayClientMessage(
                    Component.translatable("message.aster_risk.compass.moon_phase",
                        Component.translatable(getMoonPhaseKey(moonPhase))).withStyle(ChatFormatting.GRAY),
                    false
                );

                // 現在のディメンション
                Component dimComponent = level.dimension() == LunarPortalBlock.LUNAR_REALM
                    ? Component.translatable("dimension.aster_risk.lunar_realm")
                    : Component.literal(level.dimension().location().toString());
                player.displayClientMessage(
                    Component.translatable("message.aster_risk.compass.dimension", dimComponent).withStyle(ChatFormatting.GRAY),
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

    private String getDirectionKey(double dx, double dz) {
        double angle = Math.atan2(dz, dx) * 180 / Math.PI;
        angle = (angle + 360) % 360;

        if (angle < 22.5 || angle >= 337.5) return "direction.aster_risk.east";
        if (angle < 67.5) return "direction.aster_risk.south_east";
        if (angle < 112.5) return "direction.aster_risk.south";
        if (angle < 157.5) return "direction.aster_risk.south_west";
        if (angle < 202.5) return "direction.aster_risk.west";
        if (angle < 247.5) return "direction.aster_risk.north_west";
        if (angle < 292.5) return "direction.aster_risk.north";
        return "direction.aster_risk.north_east";
    }

    private String getMoonPhaseKey(int moonPhase) {
        return switch (moonPhase) {
            case 0 -> "moon_phase.aster_risk.full";
            case 1 -> "moon_phase.aster_risk.waning_gibbous";
            case 2 -> "moon_phase.aster_risk.last_quarter";
            case 3 -> "moon_phase.aster_risk.waning_crescent";
            case 4 -> "moon_phase.aster_risk.new";
            case 5 -> "moon_phase.aster_risk.waxing_crescent";
            case 6 -> "moon_phase.aster_risk.first_quarter";
            case 7 -> "moon_phase.aster_risk.waxing_gibbous";
            default -> "moon_phase.aster_risk.unknown";
        };
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
