package com.wiggle1000.bloodworks.Config;

import net.minecraftforge.common.ForgeConfigSpec;

public class BloodworksCommonConfig
{
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec CONFIG_SPEC;

    public static final ForgeConfigSpec.IntValue TEST_VALUE;

    static
    {
        BUILDER.comment("Common Config for Bloodworks").push("Bloodworks");

        TEST_VALUE = BUILDER.comment("Test Value").defineInRange("Test", 5, 0, 100);

        BUILDER.pop();
        CONFIG_SPEC = BUILDER.build();
    }
}
