package com.wiggle1000.bloodworks.Registry;

import com.wiggle1000.bloodworks.Blocks.BlockCoagulatedBlood;
import com.wiggle1000.bloodworks.Globals;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Globals.MODID);

    public static final RegistryObject<Block> COAGULATED_BLOOD = BLOCKS.register("block_coagulated_blood", BlockCoagulatedBlood::new);
}