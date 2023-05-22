package com.wiggle1000.bloodworks;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@SuppressWarnings("unused")
public class CommonProxy
{

    public static void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        Globals.LogInfo("Initializing Common.");
    }
}