package net.mcreator.asterrisk.util;

import net.mcreator.asterrisk.registry.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

/**
 * パーティクル生成ヘルパークラス
 */
public class ParticleHelper {
    
    private static final Random random = new Random();
    
    // === 月光系エフェクト ===
    
    /**
     * 月光の輝きを発生（ブロック周囲）
     */
    public static void spawnLunarSparkles(Level level, BlockPos pos, int count) {
        if (level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < count; i++) {
                double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5);
                double y = pos.getY() + 0.5 + (random.nextDouble() - 0.5);
                double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5);
                serverLevel.sendParticles(ModParticles.LUNAR_SPARKLE.get(), x, y, z, 1, 0, 0.05, 0, 0.01);
            }
        }
    }
    
    /**
     * 月光オーラ（エンティティ周囲）
     */
    public static void spawnLunarAura(Level level, Entity entity, int count) {
        if (level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < count; i++) {
                double angle = random.nextDouble() * Math.PI * 2;
                double radius = 0.5 + random.nextDouble() * 0.3;
                double x = entity.getX() + Math.cos(angle) * radius;
                double y = entity.getY() + random.nextDouble() * entity.getBbHeight();
                double z = entity.getZ() + Math.sin(angle) * radius;
                serverLevel.sendParticles(ModParticles.LUNAR_AURA.get(), x, y, z, 1, 0, 0.02, 0, 0.01);
            }
        }
    }
    
    // === 星屑系エフェクト ===
    
    /**
     * 星屑の輝きを発生
     */
    public static void spawnStardustSparkles(Level level, Vec3 pos, int count) {
        if (level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < count; i++) {
                double x = pos.x + (random.nextDouble() - 0.5) * 0.5;
                double y = pos.y + (random.nextDouble() - 0.5) * 0.5;
                double z = pos.z + (random.nextDouble() - 0.5) * 0.5;
                serverLevel.sendParticles(ModParticles.STARDUST_SPARKLE.get(), x, y, z, 1, 0, 0, 0, 0.02);
            }
        }
    }
    
    /**
     * 星の爆発エフェクト（アイテム獲得時など）
     */
    public static void spawnStarBurst(Level level, Vec3 pos, int count) {
        if (level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < count; i++) {
                double angle = random.nextDouble() * Math.PI * 2;
                double speed = 0.1 + random.nextDouble() * 0.1;
                double vx = Math.cos(angle) * speed;
                double vz = Math.sin(angle) * speed;
                double vy = random.nextDouble() * 0.1;
                serverLevel.sendParticles(ModParticles.STAR_BURST.get(), pos.x, pos.y, pos.z, 1, vx, vy, vz, 0.05);
            }
        }
    }
    
    // === マナ系エフェクト ===
    
    /**
     * マナの流れ（2点間）
     */
    public static void spawnManaFlow(Level level, Vec3 from, Vec3 to, int count) {
        if (level instanceof ServerLevel serverLevel) {
            Vec3 direction = to.subtract(from).normalize();
            double distance = from.distanceTo(to);
            
            for (int i = 0; i < count; i++) {
                double t = random.nextDouble();
                double x = from.x + direction.x * distance * t;
                double y = from.y + direction.y * distance * t;
                double z = from.z + direction.z * distance * t;
                serverLevel.sendParticles(ModParticles.MANA_FLOW.get(), x, y, z, 1, 
                    direction.x * 0.05, direction.y * 0.05, direction.z * 0.05, 0.02);
            }
        }
    }
    
    /**
     * マナ吸収エフェクト（収束）
     */
    public static void spawnManaAbsorb(Level level, Vec3 center, int count) {
        if (level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < count; i++) {
                double angle = random.nextDouble() * Math.PI * 2;
                double radius = 1.0 + random.nextDouble() * 0.5;
                double x = center.x + Math.cos(angle) * radius;
                double y = center.y + (random.nextDouble() - 0.5);
                double z = center.z + Math.sin(angle) * radius;
                double vx = (center.x - x) * 0.1;
                double vy = (center.y - y) * 0.1;
                double vz = (center.z - z) * 0.1;
                serverLevel.sendParticles(ModParticles.MANA_ABSORB.get(), x, y, z, 1, vx, vy, vz, 0.05);
            }
        }
    }
    
    // === 虚空系エフェクト ===
    
    /**
     * 虚空の霧
     */
    public static void spawnVoidMist(Level level, BlockPos pos, int count) {
        if (level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < count; i++) {
                double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 2;
                double y = pos.getY() + random.nextDouble();
                double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 2;
                serverLevel.sendParticles(ModParticles.VOID_MIST.get(), x, y, z, 1, 0, 0.01, 0, 0.005);
            }
        }
    }
    
    /**
     * 虚空の渦（回転）
     */
    public static void spawnVoidSpiral(Level level, Vec3 center, int count, float rotation) {
        if (level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < count; i++) {
                double angle = rotation + (i / (double)count) * Math.PI * 2;
                double radius = 0.5 + (i / (double)count) * 0.5;
                double x = center.x + Math.cos(angle) * radius;
                double y = center.y + (i / (double)count) * 0.5;
                double z = center.z + Math.sin(angle) * radius;
                serverLevel.sendParticles(ModParticles.VOID_SPIRAL.get(), x, y, z, 1, 0, 0.02, 0, 0.01);
            }
        }
    }
    
    // === 虹色エフェクト ===
    
    /**
     * 虹色の輝き
     */
    public static void spawnPrismaticSparkles(Level level, Entity entity, int count) {
        if (level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < count; i++) {
                double x = entity.getX() + (random.nextDouble() - 0.5) * entity.getBbWidth();
                double y = entity.getY() + random.nextDouble() * entity.getBbHeight();
                double z = entity.getZ() + (random.nextDouble() - 0.5) * entity.getBbWidth();
                serverLevel.sendParticles(ModParticles.PRISMATIC_SPARKLE.get(), x, y, z, 1, 0, 0.05, 0, 0.02);
            }
        }
    }
    
    // === 儀式エフェクト ===
    
    /**
     * 儀式のルーン（祭壇周囲）
     */
    public static void spawnRitualRunes(Level level, BlockPos center, float radius, int count, float rotation) {
        if (level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < count; i++) {
                double angle = rotation + (i / (double)count) * Math.PI * 2;
                double x = center.getX() + 0.5 + Math.cos(angle) * radius;
                double y = center.getY() + 0.5;
                double z = center.getZ() + 0.5 + Math.sin(angle) * radius;
                serverLevel.sendParticles(ModParticles.RITUAL_RUNE.get(), x, y, z, 1, 0, 0.02, 0, 0.01);
            }
        }
    }
    
    // === 流星エフェクト ===
    
    /**
     * 流星の軌跡
     */
    public static void spawnMeteorTrail(Level level, Vec3 pos, Vec3 velocity, int count) {
        if (level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < count; i++) {
                double x = pos.x + (random.nextDouble() - 0.5) * 0.5;
                double y = pos.y + (random.nextDouble() - 0.5) * 0.5;
                double z = pos.z + (random.nextDouble() - 0.5) * 0.5;
                serverLevel.sendParticles(ModParticles.METEOR_TRAIL.get(), x, y, z, 1, 
                    -velocity.x * 0.1, -velocity.y * 0.1, -velocity.z * 0.1, 0.05);
            }
            // 炎も追加
            serverLevel.sendParticles(ParticleTypes.FLAME, pos.x, pos.y, pos.z, 3, 0.2, 0.2, 0.2, 0.02);
        }
    }
    
    // === 影系エフェクト ===
    
    /**
     * 影の軌跡
     */
    public static void spawnShadowTrail(Level level, Entity entity) {
        if (level instanceof ServerLevel serverLevel) {
            double x = entity.getX();
            double y = entity.getY() + entity.getBbHeight() / 2;
            double z = entity.getZ();
            serverLevel.sendParticles(ModParticles.SHADOW_TRAIL.get(), x, y, z, 3, 0.1, 0.2, 0.1, 0.01);
        }
    }
    
    /**
     * 影の爆発
     */
    public static void spawnShadowBurst(Level level, Vec3 pos, int count) {
        if (level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < count; i++) {
                double vx = (random.nextDouble() - 0.5) * 0.3;
                double vy = random.nextDouble() * 0.2;
                double vz = (random.nextDouble() - 0.5) * 0.3;
                serverLevel.sendParticles(ModParticles.SHADOW_BURST.get(), pos.x, pos.y, pos.z, 1, vx, vy, vz, 0.1);
            }
        }
    }
}
