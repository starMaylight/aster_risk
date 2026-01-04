package net.mcreator.asterrisk.item;

import net.mcreator.asterrisk.init.AsterRiskModBlocks;
import net.mcreator.asterrisk.mana.ManaProcedures;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.mcreator.asterrisk.registry.ModParticles;
import net.mcreator.asterrisk.registry.ModSounds;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * 月光の杖 - 一時的な光源を設置
 */
public class MoonlightWandItem extends Item {
    
    private static final float MANA_COST = 15f;
    private static final int COOLDOWN_TICKS = 20; // 1秒

    public MoonlightWandItem() {
        super(new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.UNCOMMON)
            .durability(256)
        );
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        
        // クールダウン中は使用不可
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.pass(itemstack);
        }

        // 視線の先のブロックを取得
        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
        
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos targetPos = hitResult.getBlockPos().relative(hitResult.getDirection());
            
            // 空気ブロックの場所にのみ設置可能
            if (level.getBlockState(targetPos).isAir()) {
                // サーバー側でのみ処理
                if (!level.isClientSide()) {
                    // 魔力を消費
                    if (ManaProcedures.castSpell(player, MANA_COST)) {
                        // 光源ブロックを設置
                        BlockState moonlight = AsterRiskModBlocks.MOONLIGHT.get().defaultBlockState();
                        level.setBlock(targetPos, moonlight, 3);
                        
                        // 効果音とパーティクル
                        level.playSound(null, targetPos, ModSounds.LUNAR_MAGIC.get(), 
                            SoundSource.PLAYERS, 0.8f, 1.2f);
                        level.playSound(null, targetPos, ModSounds.MANA_CONSUME.get(), 
                            SoundSource.PLAYERS, 0.5f, 1.0f);
                        
                        if (level instanceof ServerLevel serverLevel) {
                            serverLevel.sendParticles(ModParticles.LUNAR_SPARKLE.get(),
                                targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5,
                                10, 0.3, 0.3, 0.3, 0.05);
                            serverLevel.sendParticles(ModParticles.MANA_RELEASE.get(),
                                player.getX(), player.getY() + 1, player.getZ(),
                                5, 0.2, 0.2, 0.2, 0.05);
                        }
                        
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
        }
        
        return InteractionResultHolder.pass(itemstack);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true; // エンチャントの光沢
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.literal("Moonlight Wand");
    }
}
