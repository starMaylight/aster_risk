package net.mcreator.asterrisk.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * マナの流れパーティクル
 * 青い光が流れるように移動
 */
@OnlyIn(Dist.CLIENT)
public class ManaFlowParticle extends TextureSheetParticle {
    
    private final SpriteSet sprites;
    
    protected ManaFlowParticle(ClientLevel level, double x, double y, double z,
                                double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.sprites = sprites;
        
        this.lifetime = 10 + this.random.nextInt(10);
        this.gravity = 0;
        this.hasPhysics = false;
        
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        
        this.quadSize = 0.05f + this.random.nextFloat() * 0.03f;
        
        // 青い光
        this.rCol = 0.3f + this.random.nextFloat() * 0.2f;
        this.gCol = 0.6f + this.random.nextFloat() * 0.2f;
        this.bCol = 1.0f;
        this.alpha = 0.9f;
        
        this.setSpriteFromAge(sprites);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // フェードアウト
        this.alpha = (float)(this.lifetime - this.age) / this.lifetime;
        
        // 軌跡を残すように縮小
        this.quadSize *= 0.95f;
        
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
            return new ManaFlowParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}
