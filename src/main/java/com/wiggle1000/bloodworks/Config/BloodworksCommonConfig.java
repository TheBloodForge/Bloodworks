package com.wiggle1000.bloodworks.Config;

import net.minecraftforge.common.ForgeConfigSpec;

public class BloodworksCommonConfig
{
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec CONFIG_SPEC;

    public static final ForgeConfigSpec.DoubleValue PARTICLE_DENSITY;
    public static final ForgeConfigSpec.BooleanValue PARTICLE_ENABLED_STEP;

    static
    {
        BUILDER.comment("Common Config for Bloodworks").push("Bloodworks");

        BUILDER.comment("Particles").push("Bloodworks");
        PARTICLE_DENSITY = BUILDER.comment("Global Particle Density")
                .comment("Sets the density of all particles in the mod.")
                .defineInRange("Global_Particle_Density", 1.0, 0, 1);
        PARTICLE_ENABLED_STEP = BUILDER.comment("Enable Step Particles")
                .define("Enable_Step_Particles", true);

        BUILDER.pop();
        CONFIG_SPEC = BUILDER.build();
    }
}
