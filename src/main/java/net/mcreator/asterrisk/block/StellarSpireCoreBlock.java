package net.mcreator.asterrisk.block;

import net.mcreator.asterrisk.entity.StarDevourerEntity;
import net.mcreator.asterrisk.init.AsterRiskModBlocks;
import net.mcreator.asterrisk.registry.ModEntities;
import net.mcreator.asterrisk.init.AsterRiskModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Stellar Spire Core - Star Devourerを召喚する尖塔の核
 */
public class StellarSpireCoreBlock extends Block {
    
    // クールダウンはワールドデータで管理する必要があるが、簡易版として削除
    
    public StellarSpireCoreBlock() {
        super(BlockBehaviour.Properties.of()
            .strength(50.0F, 1200.0F)
            .requiresCorrectToolForDrops()
            .sound(SoundType.AMETHYST)
            .lightLevel(state -> 12)
            .noOcclusion());
    }
    
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);
        
        // Star Fragmentで召喚
        if (heldItem.is(AsterRiskModItems.STARFLAGMENT.get())) {
            if (!level.isClientSide()) {
                ServerLevel serverLevel = (ServerLevel) level;
                
                // 夜間チェック
                if (serverLevel.isDay()) {
                    player.displayClientMessage(
                        Component.literal("§e The stars must be visible... Wait for nightfall."), true);
                    return InteractionResult.FAIL;
                }
                
                // 構造物チェック（簡易版：周囲にStarfall Sandがあるか）
                if (!checkStructure(level, pos)) {
                    player.displayClientMessage(
                        Component.literal("§c The spire structure is incomplete!"), true);
                    return InteractionResult.FAIL;
                }
                
                // ボス召喚
                StarDevourerEntity boss = ModEntities.STAR_DEVOURER.get().create(serverLevel);
                if (boss != null) {
                    boss.moveTo(pos.getX() + 0.5, pos.getY() + 3, pos.getZ() + 0.5, 0, 0);
                    serverLevel.addFreshEntity(boss);
                    
                    // アイテム消費
                    if (!player.isCreative()) {
                        heldItem.shrink(1);
                    }
                    
                    // エフェクト
                    serverLevel.playSound(null, pos, SoundEvents.END_PORTAL_SPAWN, SoundSource.BLOCKS, 1.0F, 1.5F);
                    for (int i = 0; i < 50; i++) {
                        serverLevel.sendParticles(ParticleTypes.END_ROD,
                            pos.getX() + 0.5, pos.getY() + 2, pos.getZ() + 0.5,
                            1, 0.5, 1.5, 0.5, 0.05);
                    }
                    
                    player.displayClientMessage(
                        Component.literal("§6✦ Star Devourer descends from the heavens! ✦"), false);
                    
                    return InteractionResult.SUCCESS;
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        
        return InteractionResult.PASS;
    }
    
    private boolean checkStructure(Level level, BlockPos pos) {
        // 簡易構造チェック：祭壇の周囲にStarfall Sandがあるか
        int sandCount = 0;
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (x == 0 && z == 0) continue;
                BlockState state = level.getBlockState(pos.offset(x, -1, z));
                if (state.is(AsterRiskModBlocks.STARFALLSAND.get())) {
                    sandCount++;
                }
            }
        }
        return sandCount >= 8;
    }
    
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(3) == 0) {
            level.addParticle(ParticleTypes.END_ROD,
                pos.getX() + 0.5 + random.nextGaussian() * 0.2,
                pos.getY() + 1.2,
                pos.getZ() + 0.5 + random.nextGaussian() * 0.2,
                0, 0.1, 0);
        }
    }
}
