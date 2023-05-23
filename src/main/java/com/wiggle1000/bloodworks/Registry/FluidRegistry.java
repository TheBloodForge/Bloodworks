package com.wiggle1000.bloodworks.Registry;

import com.wiggle1000.bloodworks.Blocks.Fluids.FluidBlood;
import com.wiggle1000.bloodworks.Globals;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class FluidRegistry
{

    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, Globals.MODID);

    public static final RegistryObject<Fluid> FLUID_BLOOD_SOURCE       = FLUIDS.register("fluid_blood_source",         FluidBlood.Source::new);
    public static final RegistryObject<Fluid> FLUID_BLOOD_FLOWING      = FLUIDS.register("fluid_blood_flowing",        FluidBlood.Flowing::new);
}