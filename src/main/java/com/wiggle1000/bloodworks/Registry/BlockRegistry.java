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
}