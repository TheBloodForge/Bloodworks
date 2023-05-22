package com.wiggle1000.bloodworks.Particles;

import com.wiggle1000.bloodworks.Globals;
import com.wiggle1000.bloodworks.Util;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class FleshStepParticle extends TextureSheetParticle
{
    protected FleshStepParticle(ClientLevel level, SpriteSet spriteSet, double x, double y, double z, double dx, double dy, double dz)
    {
        super(level, x, y, z, dx, dy, dz);
        this.friction = 2f;
        this.xd = (Globals.RAND.nextFloat() - 0.5f) * 0.02f * dx;
        this.yd = 0.1;
        this.zd = (Globals.RAND.nextFloat() - 0.5f) * 0.02f * dx;
        this.gravity = 1f;
        this.lifetime = 60; //in ticks
        this.alpha = 0;

        this.setBoundingBox(AABB.ofSize(new Vec3(x, y, z), .03,.03,.03));


        this.setSpriteFromAge(spriteSet);
        this.rCol = 1f;
        this.gCol = 1f;
        this.bCol = 1f;
    }

    @Override
    public void tick() {
        super.tick();
        float lifeScaled = age / (float)lifetime; //0 to 1
        if(lifeScaled < 0.1f)
        {
            this.alpha = Util.Lerp(0, 1, lifeScaled / 0.1f);
        }
        else if(lifeScaled > 0.8f)
        {
            this.alpha = Util.Lerp(1, 0, (lifeScaled - 0.8f) /  0.2f);
        }

        this.yd /= 1.5f;
        this.xd /= 1.5f;
        this.zd /= 1.5f;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType>
    {
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet)
        {
            this.sprites = spriteSet;
        }
        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType particleType, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new FleshStepParticle(level, sprites, x, y, z, dx, dy, dz);
        }
    }
}
