package com.wiggle1000.bloodworks.Registry;

import com.wiggle1000.bloodworks.Blocks.*;
import com.wiggle1000.bloodworks.Globals;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockRegistry
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Globals.MODID);

    public static final RegistryObject<Block> BLOCK_COAGULATED_BLOOD            = BLOCKS.register("block_coagulated_blood",         BlockBloodyBase::new);
    public static final RegistryObject<Block> BLOCK_COAGULATED_BLOOD_STAIRS     = BLOCKS.register("block_coagulated_blood_stairs",  BlockBloodyStairsBase::new);
    public static final RegistryObject<Block> BLOCK_COAGULATED_BLOOD_SLAB       = BLOCKS.register("block_coagulated_blood_slab",    BlockBloodySlabBase::new);
    public static final RegistryObject<Block> BLOCK_INFUSION_CHAMBER            = BLOCKS.register("block_infusion_chamber",         BlockInfusionChamber::new);
    public static final RegistryObject<Block> BLOCK_INTESTINE                   = BLOCKS.register("block_intestine",                BlockIntestine::new);
    public static final RegistryObject<Block> BLOCK_FLESH_LIGHT                 = BLOCKS.register("block_flesh_light",              () -> new BlockFleshLight(false));
    public static final RegistryObject<Block> BLOCK_FLESH_LIGHT_LARGE           = BLOCKS.register("block_flesh_light_large",        () -> new BlockFleshLight(true));
    public static final RegistryObject<Block> BLOCK_FLESH_PORTHOLE              = BLOCKS.register("block_flesh_porthole",           () -> new BlockBloodyTransparentBase("Flesh with a window embedded.", "Used for decoration."));

    public static final RegistryObject<Block> BLOCK_FLESH                       = BLOCKS.register("block_flesh",                    BlockBloodyBase::new);

    public static final RegistryObject<Block> BLOCK_BLOOD_TANK                  = BLOCKS.register("block_blood_tank",               BlockBloodTank::new);
}