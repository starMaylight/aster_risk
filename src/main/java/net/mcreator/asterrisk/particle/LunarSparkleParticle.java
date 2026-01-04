package net.mcreator.asterrisk.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 月光の輝きパーティクル
 * 青白い光の粒子がゆっくり上昇
 */
@OnlyIn(Dist.CLIENT)
public class LunarSparkleParticle extends TextureSheetParticle {
    
    private final SpriteSet sprites;
    
    protected LunarSparkleParticle(ClientLevel level, double x, double y, double z,
                                    double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.sprites = sprites;
        
        this.lifetime = 20 + this.random.nextInt(20);
        this.gravity = -0.01f;
        this.hasPhysics = false;
        
        this.xd = xSpeed + (this.random.nextDouble() - 0.5) * 0.1;
        this.yd = ySpeed + this.random.nextDouble() * 0.05 + 0.02;
        this.zd = zSpeed + (this.random.nextDouble() - 0.5) * 0.1;
        
        this.quadSize = 0.1f + this.random.nextFloat() * 0.05f;
        
        // 青白い色
        this.rCol = 0.7f + this.random.nextFloat() * 0.2f;
        this.gCol = 0.85f + this.random.nextFloat() * 0.15f;
        this.bCol = 1.0f;
        this.alpha = 0.8f;
        
        this.setSpriteFromAge(sprites);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // フェードアウト
        if (this.age > this.lifetime - 10) {
            this.alpha = (this.lifetime - this.age) / 10.0f;
        }
        
        // 少しずつ縮小
        this.quadSize *= 0.98f;
        
        this.setSpriteFromAge(sprites);
    }
    
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
    
    @Override
    public int getLightColor(float partialTick) {
        return 0xF000F0; // 最大輝度
    }
    
    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        
        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }
        
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new LunarSparkleParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}
