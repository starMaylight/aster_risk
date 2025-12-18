package net.mcreator.asterrisk.block;

import net.mcreator.asterrisk.entity.EclipseMonarchEntity;
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
 * Eclipse Altar - Eclipse Monarchを召喚する祭壇
 */
public class EclipseAltarBlock extends Block {
    
    public EclipseAltarBlock() {
        super(BlockBehaviour.Properties.of()
            .strength(50.0F, 1200.0F)
            .requiresCorrectToolForDrops()
            .sound(SoundType.DEEPSLATE)
            .lightLevel(state -> 8)
            .noOcclusion());
    }
    
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);
        
        // Meteorite Fragmentで召喚
        if (heldItem.is(AsterRiskModItems.METEORITE_FRAGMENT.get())) {
            if (!level.isClientSide()) {
                ServerLevel serverLevel = (ServerLevel) level;
                
                // 構造物チェック（簡易版：周囲にEclipse Stoneがあるか）
                if (!checkStructure(level, pos)) {
                    player.displayClientMessage(
                        Component.literal("§c The altar structure is incomplete! Place Eclipse Stone around it."), true);
                    return InteractionResult.FAIL;
                }
                
                // ボス召喚
                EclipseMonarchEntity boss = ModEntities.ECLIPSE_MONARCH.get().create(serverLevel);
                if (boss != null) {
                    boss.moveTo(pos.getX() + 0.5, pos.getY() + 2, pos.getZ() + 0.5, 0, 0);
                    serverLevel.addFreshEntity(boss);
                    
                    // アイテム消費
                    if (!player.isCreative()) {
                        heldItem.shrink(1);
                    }
                    
                    // エフェクト
                    serverLevel.playSound(null, pos, SoundEvents.END_PORTAL_SPAWN, SoundSource.BLOCKS, 1.0F, 0.5F);
                    for (int i = 0; i < 50; i++) {
                        serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL,
                            pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5,
                            1, 0.5, 1, 0.5, 0.1);
                    }
                    
                    player.displayClientMessage(
                        Component.literal("§5✦ Eclipse Monarch has awakened! ✦"), false);
                    
                    return InteractionResult.SUCCESS;
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        
        return InteractionResult.PASS;
    }
    
    private boolean checkStructure(Level level, BlockPos pos) {
        // 簡易構造チェック：祭壇の周囲にEclipse Stoneがあるか
        int eclipseStoneCount = 0;
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (x == 0 && z == 0) continue;
                BlockState state = level.getBlockState(pos.offset(x, -1, z));
                if (state.is(AsterRiskModBlocks.ECLIPSESTONE.get())) {
                    eclipseStoneCount++;
                }
            }
        }
        return eclipseStoneCount >= 8;
    }
    
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(5) == 0) {
            level.addParticle(ParticleTypes.REVERSE_PORTAL,
                pos.getX() + 0.5 + random.nextGaussian() * 0.3,
                pos.getY() + 1.0,
                pos.getZ() + 0.5 + random.nextGaussian() * 0.3,
                0, 0.05, 0);
        }
    }
}
