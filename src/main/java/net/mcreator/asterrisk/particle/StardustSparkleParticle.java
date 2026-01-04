package net.mcreator.asterrisk.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 星屑の輝きパーティクル
 * 金色の小さな星がきらめく
 */
@OnlyIn(Dist.CLIENT)
public class StardustSparkleParticle extends TextureSheetParticle {
    
    private final SpriteSet sprites;
    private float rotSpeed;
    
    protected StardustSparkleParticle(ClientLevel level, double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.sprites = sprites;
        
        this.lifetime = 15 + this.random.nextInt(15);
        this.gravity = 0.02f;
        this.hasPhysics = false;
        
        this.xd = xSpeed + (this.random.nextDouble() - 0.5) * 0.05;
        this.yd = ySpeed + (this.random.nextDouble() - 0.5) * 0.05;
        this.zd = zSpeed + (this.random.nextDouble() - 0.5) * 0.05;
        
        this.quadSize = 0.08f + this.random.nextFloat() * 0.04f;
        this.rotSpeed = (this.random.nextFloat() - 0.5f) * 0.2f;
        
        // 金色
        this.rCol = 1.0f;
        this.gCol = 0.85f + this.random.nextFloat() * 0.15f;
        this.bCol = 0.3f + this.random.nextFloat() * 0.2f;
        this.alpha = 1.0f;
        
        this.setSpriteFromAge(sprites);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // 回転
        this.oRoll = this.roll;
        this.roll += this.rotSpeed;
        
        // フェードアウトと点滅
        if (this.age > this.lifetime - 5) {
            this.alpha = (this.lifetime - this.age) / 5.0f;
        } else {
            // きらめき効果
            this.alpha = 0.7f + (float)Math.sin(this.age * 0.5) * 0.3f;
        }
        
        this.setSpriteFromAge(sprites);
    }
    
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
    
    @Override
    public int getLightColor(float partialTick) {
        return 0xF000F0;
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
            return new StardustSparkleParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}
