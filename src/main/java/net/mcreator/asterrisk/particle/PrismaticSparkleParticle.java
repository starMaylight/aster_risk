package net.mcreator.asterrisk.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 虹色の輝きパーティクル
 * 色が時間とともに変化する
 */
@OnlyIn(Dist.CLIENT)
public class PrismaticSparkleParticle extends TextureSheetParticle {
    
    private final SpriteSet sprites;
    private float hue;
    
    protected PrismaticSparkleParticle(ClientLevel level, double x, double y, double z,
                                        double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.sprites = sprites;
        
        this.lifetime = 25 + this.random.nextInt(15);
        this.gravity = 0;
        this.hasPhysics = false;
        
        this.xd = xSpeed + (this.random.nextDouble() - 0.5) * 0.1;
        this.yd = ySpeed + (this.random.nextDouble() - 0.5) * 0.1;
        this.zd = zSpeed + (this.random.nextDouble() - 0.5) * 0.1;
        
        this.quadSize = 0.1f + this.random.nextFloat() * 0.05f;
        this.hue = this.random.nextFloat();
        
        updateColor();
        this.alpha = 1.0f;
        
        this.setSpriteFromAge(sprites);
    }
    
    private void updateColor() {
        // HSV to RGB変換（簡易版）
        float h = this.hue * 6;
        int i = (int) h;
        float f = h - i;
        float q = 1 - f;
        float t = f;
        
        switch (i % 6) {
            case 0 -> { rCol = 1; gCol = t; bCol = 0; }
            case 1 -> { rCol = q; gCol = 1; bCol = 0; }
            case 2 -> { rCol = 0; gCol = 1; bCol = t; }
            case 3 -> { rCol = 0; gCol = q; bCol = 1; }
            case 4 -> { rCol = t; gCol = 0; bCol = 1; }
            case 5 -> { rCol = 1; gCol = 0; bCol = q; }
        }
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // 色を変化
        this.hue += 0.03f;
        if (this.hue > 1) this.hue -= 1;
        updateColor();
        
        // フェードアウト
        if (this.age > this.lifetime - 10) {
            this.alpha = (this.lifetime - this.age) / 10.0f;
        }
        
        // 少しずつ縮小
        this.quadSize *= 0.97f;
        
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
            return new PrismaticSparkleParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}
