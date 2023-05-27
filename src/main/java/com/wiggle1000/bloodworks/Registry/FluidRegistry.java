package com.wiggle1000.bloodworks.Registry;

import com.wiggle1000.bloodworks.Blocks.Fluids.FluidRegistryContainer;
import com.wiggle1000.bloodworks.Globals;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class FluidRegistry
{
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, Globals.MODID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, Globals.MODID);
    public static final Material MATERIAL_BLOOD = (new Material.Builder(MaterialColor.COLOR_RED)).noCollider().nonSolid().replaceable().liquid().build();
    public static final FluidRegistryContainer FLUID_BLOOD = new FluidRegistryContainer(
            "fluid_blood",
            FluidType.Properties.create().canSwim(true).canDrown(true).canPushEntity(true).supportsBoating(true),
            () -> FluidRegistryContainer.createExtension(
                    new FluidRegistryContainer.ClientExtensions(
                            Globals.MODID,
                            "fluid_blood"
                    ).fogColor(0.15f, 0.0f, 0.01f).tint(0xAAAA0011)
            , 0xAAAA0011),

            Block.Properties.of(MATERIAL_BLOOD).noCollission().strength(100.0F).noLootTable().color(MATERIAL_BLOOD.getColor()).speedFactor(0.3F).jumpFactor(0.3F).friction(1.0f),
            new Item.Properties()
                    .tab(Globals.CREATIVE_TAB)
                    .stacksTo(1)
    );
}