package com.wiggle1000.bloodworks;

import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class CommonProxy
{

    public static void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        Globals.LOGGER.info("HELLO FROM COMMON SETUP");
        Globals.LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
    }
}