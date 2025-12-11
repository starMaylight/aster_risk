package net.mcreator.asterrisk.item.weapon;

import net.mcreator.asterrisk.init.AsterRiskModItems;
import net.mcreator.asterrisk.mana.ManaProcedures;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 星屑のダガー - 右クリックで短距離テレポート
 */
public class StardustDaggerItem extends SwordItem {

    private static final float MANA_COST = 15f;
    private static final int COOLDOWN_TICKS = 20; // 1秒
    private static final double TELEPORT_DISTANCE = 8.0;

    private static final Tier STARDUST_TIER = new Tier() {
        @Override public int getUses() { return 500; }
        @Override public float getSpeed() { return 8.0f; }
        @Override public float getAttackDamageBonus() { return 1.0f; } // 基礎4 + 1 = 5ダメージ
        @Override public int getLevel() { return 2; }
        @Override public int getEnchantmentValue() { return 22; }
        @Override public Ingredient getRepairIngredient() { 
            return Ingredient.of(AsterRiskModItems.STARFLAGMENT.get()); 
        }
    };

    public StardustDaggerItem(Properties properties) {
        super(STARDUST_TIER, 1, -1.8f, properties); // 速い攻撃速度
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.literal("Stardust Dagger");
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide()) {
            // マナチェック＆消費
            if (!ManaProcedures.castSpell(player, MANA_COST)) {
                return InteractionResultHolder.fail(stack);
            }

            // 視線方向にレイキャスト
            Vec3 eyePos = player.getEyePosition();
            Vec3 lookVec = player.getLookAngle();
            Vec3 targetPos = eyePos.add(lookVec.scale(TELEPORT_DISTANCE));

            // ブロックに当たるかチェック
            BlockHitResult hitResult = level.clip(new ClipContext(
                eyePos, targetPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));

            Vec3 teleportPos;
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                // ブロックの手前にテレポート
                teleportPos = hitResult.getLocation().subtract(lookVec.scale(0.5));
            } else {
                teleportPos = targetPos;
            }

            // 出発地点のパーティクル
            if (level instanceof ServerLevel serverLevel) {
                for (int i = 0; i < 20; i++) {
                    serverLevel.sendParticles(ParticleTypes.PORTAL,
                        player.getX(), player.getY() + 1, player.getZ(),
                        1, 0.3, 0.5, 0.3, 0.1);
                }
            }

            // テレポート
            player.teleportTo(teleportPos.x, teleportPos.y, teleportPos.z);
            player.fallDistance = 0;

            // 到着地点のパーティクル
            if (level instanceof ServerLevel serverLevel) {
                for (int i = 0; i < 30; i++) {
                    serverLevel.sendParticles(ParticleTypes.END_ROD,
                        player.getX(), player.getY() + 1, player.getZ(),
                        1, 0.3, 0.5, 0.3, 0.05);
                }
            }

            // 音
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.8f, 1.2f);

            // クールダウン
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§d✦ Stardust Dagger"));
        tooltip.add(Component.literal("§7Right-click: Blink"));
        tooltip.add(Component.literal("§d  Teleport " + (int)TELEPORT_DISTANCE + " blocks forward"));
        tooltip.add(Component.literal("§3  Mana Cost: " + (int)MANA_COST));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
