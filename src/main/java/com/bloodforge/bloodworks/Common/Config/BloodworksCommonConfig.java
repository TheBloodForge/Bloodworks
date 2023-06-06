package com.bloodforge.bloodworks.Common.Config;

import net.minecraftforge.common.ForgeConfigSpec;

public class BloodworksCommonConfig
{
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec CONFIG_SPEC;

    public static final ForgeConfigSpec.DoubleValue STIRLING_GENERATOR_GENERATION_MOD;

    public static final ForgeConfigSpec.DoubleValue PARTICLE_DENSITY;
    public static final ForgeConfigSpec.BooleanValue PARTICLE_ENABLED_STEP;
    public static final ForgeConfigSpec.BooleanValue DO_OCCLUSION_CULLING;
    public static final ForgeConfigSpec.IntValue MAX_TANK_COOLDOWN, TANK_COOLDOWN_REDUCTION, TANK_STORAGE_PER_TIER, TANK_TRANSFER_PER_ACTION;

    static
    {
        BUILDER.comment("Common Config for Bloodworks").push("Bloodworks");

        BUILDER.comment("Performance Settings").push("Performance");
        PARTICLE_DENSITY = BUILDER.comment("Global Particle Density")
                .comment("Sets the density of all particles in the mod.")
                .defineInRange("Global_Particle_Density", 1.0, 0, 1);
        PARTICLE_ENABLED_STEP = BUILDER.comment("Enable Step Particles")
                .define("Enable_Step_Particles", true);
        DO_OCCLUSION_CULLING = BUILDER.comment("Enable occlusion culling on advanced rendered entities.")
                .comment("Effectiveness questionable, but doesnt seem to reduce FPS?")
                .define("Do_Occlusion_Culling", true);

        BUILDER.pop().comment("Tank Settings").push("Tank");
        MAX_TANK_COOLDOWN = BUILDER.comment("Maximum Tank Cooldown in Ticks Between Actions")
                .defineInRange("Maximum_Tank_Cooldown", 20, 0, 200);
        TANK_COOLDOWN_REDUCTION = BUILDER.comment("Tank Cooldown Reduction Per Tier")
                .defineInRange("Tank_Cooldown_Reduction", 2, 0, 200);
        TANK_STORAGE_PER_TIER = BUILDER.comment("Tank Fluid Storage Per Tier")
                .defineInRange("Tank_Storage_Per_Tier", 5000, 1000, 2000000);
        TANK_TRANSFER_PER_ACTION = BUILDER.comment("Tank Transfer Amount Per Tier Per Action")
                .defineInRange("Tank_Transfer_Per_Action", 100, 10, 200000);

        BUILDER.pop().comment("Machine Settings").push("Machines");
        STIRLING_GENERATOR_GENERATION_MOD = BUILDER.comment("How much power the Stirling Generator generates. FE/t/°C")
                .defineInRange("Stirling_FE_T_C", 150.0, 1.0, 200000.0);

        BUILDER.pop();
        CONFIG_SPEC = BUILDER.build();
    }
}