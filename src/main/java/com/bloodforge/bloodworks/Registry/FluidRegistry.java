package com.bloodforge.bloodworks.Registry;

import com.bloodforge.bloodworks.Blocks.Fluids.FluidRegistryContainer;
import com.bloodforge.bloodworks.Globals;
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
    public static final Material MATERIAL_CRANIAL = (new Material.Builder(MaterialColor.TERRACOTTA_CYAN)).noCollider().nonSolid().replaceable().liquid().build();
    public static final FluidRegistryContainer FLUID_BLOOD = new FluidRegistryContainer(
            "fluid_blood",
            FluidType.Properties.create().canSwim(true).canDrown(true).canPushEntity(true).supportsBoating(true).viscosity(4000),
            () -> FluidRegistryContainer.createExtension(
                    new FluidRegistryContainer.ClientExtensions(
                            Globals.MODID,
                            "fluid_blood"
                    ).fogColor(0.15f, 0.0f, 0.01f).tint(0xFAAA0011).fogDistance(1, 3)
                    , 0xFAAA0011),

            Block.Properties.of(MATERIAL_BLOOD).noCollission().strength(100.0F).noLootTable().color(MATERIAL_BLOOD.getColor()).speedFactor(0.8F).jumpFactor(0.8F),
            new Item.Properties()
                    .tab(Globals.CREATIVE_TAB)
                    .stacksTo(1)
    );
    public static final FluidRegistryContainer FLUID_CRANIAL = new FluidRegistryContainer(
            "fluid_cranial",
            FluidType.Properties.create().canSwim(true).canDrown(false).canPushEntity(false).supportsBoating(false).lightLevel(2).viscosity(2000),
            () -> FluidRegistryContainer.createExtension(
                    new FluidRegistryContainer.ClientExtensions(
                            Globals.MODID,
                            "fluid_cranial"
                    ).fogColor(0.1f, 0.2f, 0.2f).tint(0xAA00AAAA).fogDistance(1, 30)
                    , 0xAA00AAAA),
            Block.Properties.of(MATERIAL_CRANIAL).noCollission().strength(100.0F).noLootTable().color(MATERIAL_CRANIAL.getColor()).randomTicks().isViewBlocking((a, b, c) -> false),
            new Item.Properties()
                    .tab(Globals.CREATIVE_TAB)
                    .stacksTo(1)
    );
}