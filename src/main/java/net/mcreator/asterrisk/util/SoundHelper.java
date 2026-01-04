package net.mcreator.asterrisk.util;

import net.mcreator.asterrisk.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * サウンド再生ヘルパークラス
 */
public class SoundHelper {
    
    // === 魔法系サウンド ===
    
    /**
     * マナ充填音を再生
     */
    public static void playManaCharge(Level level, BlockPos pos) {
        level.playSound(null, pos, ModSounds.MANA_CHARGE.get(), SoundSource.BLOCKS, 0.8f, 1.0f);
    }
    
    /**
     * マナ消費音を再生
     */
    public static void playManaConsume(Level level, Player player) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(), 
            ModSounds.MANA_CONSUME.get(), SoundSource.PLAYERS, 0.5f, 1.0f);
    }
    
    /**
     * 魔法発動音を再生
     */
    public static void playSpellCast(Level level, Entity entity) {
        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
            ModSounds.SPELL_CAST.get(), SoundSource.PLAYERS, 0.8f, 0.9f + level.random.nextFloat() * 0.2f);
    }
    
    /**
     * 月光魔法音を再生
     */
    public static void playLunarMagic(Level level, BlockPos pos) {
        level.playSound(null, pos, ModSounds.LUNAR_MAGIC.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
    }
    
    /**
     * 星屑魔法音を再生
     */
    public static void playStellarMagic(Level level, BlockPos pos) {
        level.playSound(null, pos, ModSounds.STELLAR_MAGIC.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
    }
    
    // === ブロック系サウンド ===
    
    /**
     * 錬金釜作動音を再生
     */
    public static void playCauldronBubble(Level level, BlockPos pos) {
        level.playSound(null, pos, ModSounds.CAULDRON_BUBBLE.get(), SoundSource.BLOCKS, 0.6f, 0.8f + level.random.nextFloat() * 0.4f);
    }
    
    /**
     * 祭壇起動音を再生
     */
    public static void playAltarActivate(Level level, BlockPos pos) {
        level.playSound(null, pos, ModSounds.ALTAR_ACTIVATE.get(), SoundSource.BLOCKS, 1.0f, 0.9f);
    }
    
    /**
     * 儀式完了音を再生
     */
    public static void playRitualComplete(Level level, BlockPos pos) {
        level.playSound(null, pos, ModSounds.RITUAL_COMPLETE.get(), SoundSource.BLOCKS, 1.2f, 1.0f);
    }
    
    /**
     * 月相の金床使用音を再生
     */
    public static void playPhaseAnvilUse(Level level, BlockPos pos) {
        level.playSound(null, pos, ModSounds.PHASE_ANVIL_USE.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
    }
    
    /**
     * 共振器リンク音を再生
     */
    public static void playResonatorLink(Level level, BlockPos pos) {
        level.playSound(null, pos, ModSounds.RESONATOR_LINK.get(), SoundSource.BLOCKS, 0.8f, 1.0f);
    }
    
    // === 環境音 ===
    
    /**
     * 流星落下音を再生
     */
    public static void playMeteorFall(Level level, BlockPos pos) {
        level.playSound(null, pos, ModSounds.METEOR_FALL.get(), SoundSource.AMBIENT, 1.5f, 0.5f);
    }
    
    /**
     * 流星衝突音を再生
     */
    public static void playMeteorImpact(Level level, BlockPos pos) {
        level.playSound(null, pos, ModSounds.METEOR_IMPACT.get(), SoundSource.AMBIENT, 2.0f, 0.7f);
    }
    
    // === エンティティ系サウンド ===
    
    /**
     * ボス登場音を再生
     */
    public static void playBossSpawn(Level level, Entity entity) {
        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
            ModSounds.BOSS_SPAWN.get(), SoundSource.HOSTILE, 2.0f, 0.8f);
    }
    
    /**
     * ボス攻撃音を再生
     */
    public static void playBossAttack(Level level, Entity entity) {
        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
            ModSounds.BOSS_ATTACK.get(), SoundSource.HOSTILE, 1.0f, 1.0f);
    }
    
    /**
     * ボス死亡音を再生
     */
    public static void playBossDeath(Level level, Entity entity) {
        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
            ModSounds.BOSS_DEATH.get(), SoundSource.HOSTILE, 2.0f, 1.0f);
    }
    
    /**
     * 虚空のささやきを再生
     */
    public static void playVoidWhisper(Level level, Entity entity) {
        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
            ModSounds.VOID_WHISPER.get(), SoundSource.HOSTILE, 0.5f, 0.5f + level.random.nextFloat() * 0.5f);
    }
    
    // === アイテム系サウンド ===
    
    /**
     * 虹色武器ヒット音を再生
     */
    public static void playPrismaticHit(Level level, Entity target) {
        level.playSound(null, target.getX(), target.getY(), target.getZ(),
            ModSounds.PRISMATIC_HIT.get(), SoundSource.PLAYERS, 1.0f, 1.0f + level.random.nextFloat() * 0.3f);
    }
    
    /**
     * 影の大鎌スイング音を再生
     */
    public static void playShadowSwing(Level level, Entity entity) {
        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
            ModSounds.SHADOW_SWING.get(), SoundSource.PLAYERS, 0.8f, 0.7f);
    }
    
    /**
     * 虚空の短剣ヒット音を再生
     */
    public static void playVoidStrike(Level level, Entity target) {
        level.playSound(null, target.getX(), target.getY(), target.getZ(),
            ModSounds.VOID_STRIKE.get(), SoundSource.PLAYERS, 0.9f, 1.0f);
    }
}
