package com.bloodforge.bloodworks.Registry;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class ItemProvidor extends ItemModelProvider
{
    public static List<RegistryObject<ForgeFlowingFluid.Source>> Buckets;

    public ItemProvidor(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper)
    {
        super(generator, modid, existingFileHelper);
    }

    @Override
    protected void registerModels()
    {
        System.out.println("REGGING MODELS");
        registerBucket(FluidRegistry.FLUID_BLOOD.source);
        registerBucket(FluidRegistry.FLUID_CRANIAL.source);
        System.out.println("TRIGGERED REGGY MODELS");
    }

    protected void registerBucket(RegistryObject<ForgeFlowingFluid.Source> fluidRO)
    {
        withExistingParent("forge:item/bucket", new ResourceLocation("forge", "item/bucket"))
                .customLoader(DynamicFluidContainerModelBuilder::begin)
                .fluid(fluidRO.get());
    }
}