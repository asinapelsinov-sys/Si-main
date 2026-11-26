package com.example.simine.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

public class BlizzardSnowParticle extends TextureSheetParticle {

    protected BlizzardSnowParticle(ClientLevel level, double x, double y, double z,
                                   double xd, double yd, double zd, SpriteSet sprites) {
        super(level, x, y, z);
        this.xd = xd + (this.random.nextDouble() - 0.5) * 0.10;
        this.yd = yd + (this.random.nextDouble() - 0.5) * 0.06;
        this.zd = zd + (this.random.nextDouble() - 0.5) * 0.20;
        this.gravity = 0.0F;
        this.friction = 0.99F;
        this.hasPhysics = true;
        this.lifetime = 35 + this.random.nextInt(25);
        this.quadSize = 0.10F + this.random.nextFloat() * 0.14F; // крупнее = хуже видимость
        this.pickSprite(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        // порывы ветра
        this.zd += Math.sin((this.age + this.hashCode() % 20) * 0.4) * 0.012;
        float life = (float) this.age / (float) this.lifetime;
        this.alpha = life > 0.75F ? 1.0F - (life - 0.75F) / 0.25F : 1.0F;
        if (this.onGround) this.remove();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z, double xd, double yd, double zd) {
            return new BlizzardSnowParticle(level, x, y, z, xd, yd, zd, sprites);
        }
    }
}
