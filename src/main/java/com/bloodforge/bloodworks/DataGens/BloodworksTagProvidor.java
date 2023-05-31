package com.bloodforge.bloodworks.DataGens;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public class BloodworksTagProvidor
{
    public static final TagKey<Fluid> CREATE_NO_INFINITE_FLUID = FluidTags.create(new ResourceLocation("create", "no_infinite_draining"));
}