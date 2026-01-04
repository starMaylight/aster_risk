package net.mcreator.asterrisk.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 虚空の霧パーティクル
 * 暗紫色の霧が漂う
 */
@OnlyIn(Dist.CLIENT)
public class VoidMistParticle extends TextureSheetParticle {
    
    private final SpriteSet sprites;
    
    protected VoidMistParticle(ClientLevel level, double x, double y, double z,
                                double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.sprites = sprites;
        
        this.lifetime = 30 + this.random.nextInt(30);
        this.gravity = -0.005f;
        this.hasPhysics = false;
        
        this.xd = xSpeed + (this.random.nextDouble() - 0.5) * 0.02;
        this.yd = ySpeed + this.random.nextDouble() * 0.01;
        this.zd = zSpeed + (this.random.nextDouble() - 0.5) * 0.02;
        
        this.quadSize = 0.2f + this.random.nextFloat() * 0.15f;
        
        // 暗紫色
        this.rCol = 0.2f + this.random.nextFloat() * 0.1f;
        this.gCol = 0.05f + this.random.nextFloat() * 0.05f;
        this.bCol = 0.3f + this.random.nextFloat() * 0.1f;
        this.alpha = 0.5f;
        
        this.setSpriteFromAge(sprites);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // ゆっくり拡大
        this.quadSize *= 1.01f;
        
        // フェードイン・アウト
        if (this.age < 10) {
            this.alpha = this.age / 10.0f * 0.5f;
        } else if (this.age > this.lifetime - 15) {
            this.alpha = (this.lifetime - this.age) / 15.0f * 0.5f;
        }
        
        // ゆらゆら動く
        this.xd += (this.random.nextDouble() - 0.5) * 0.005;
        this.zd += (this.random.nextDouble() - 0.5) * 0.005;
        
        this.setSpriteFromAge(sprites);
    }
    
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
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
            return new VoidMistParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}
