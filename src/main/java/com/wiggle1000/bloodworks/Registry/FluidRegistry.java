package com.wiggle1000.bloodworks.Registry;

import com.wiggle1000.bloodworks.Blocks.Fluids.FluidRegistryContainer;
import com.wiggle1000.bloodworks.Globals;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class FluidRegistry
{

    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, Globals.MODID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, Globals.MODID);
    public static final FluidRegistryContainer FLUID_BLOOD = new FluidRegistryContainer(
            "fluid_blood",
            FluidType.Properties.create().canSwim(true).canDrown(true).canPushEntity(true).supportsBoating(true),
            () -> FluidRegistryContainer.createExtension(
                    new FluidRegistryContainer.ClientExtensions(
                            Globals.MODID,
                            "fluid_blood"
                    ).tint(0xAA1122)
                    .fogColor(0.15f, 0.0f, 0.01f)
            ),
            BlockBehaviour.Properties.copy(Blocks.WATER),
            new Item.Properties()
                    .tab(Globals.CREATIVE_TAB)
                    .stacksTo(1)
    );

//    public static final RegistryObject<Fluid> FLUID_BLOOD_SOURCE       = FLUIDS.register("fluid_blood_source",         FluidBlood.Source::new);
//    public static final RegistryObject<Fluid> FLUID_BLOOD_FLOWING      = FLUIDS.register("fluid_blood_flowing",        FluidBlood.Flowing::new);
}