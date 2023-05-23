package com.wiggle1000.bloodworks.Registry;

import com.wiggle1000.bloodworks.Globals;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ParticleRegistry
{
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Globals.MODID);

    public static final RegistryObject<SimpleParticleType> PARTICLE_FLESH_STEP = PARTICLES.register("particle_flesh_step", () -> new SimpleParticleType(true));
}
