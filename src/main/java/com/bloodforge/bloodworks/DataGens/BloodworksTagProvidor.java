package com.bloodforge.bloodworks.DataGens;

import com.bloodforge.bloodworks.Globals;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class BloodworksTagProvidor
{
    //FLUID TAGS
    public static final TagKey<Fluid> CREATE_NO_INFINITE_FLUID = FluidTags.create(new ResourceLocation("create", "no_infinite_draining"));

    //BLOCK TAGS
    public static final TagKey<Block> BODY_PART = BlockTags.create(new ResourceLocation(Globals.MODID, "body_part"));
}