package com.wiggle1000.bloodworks;

import com.wiggle1000.bloodworks.Networking.PacketManager;
import com.wiggle1000.bloodworks.Registry.RecipeRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = Globals.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonProxy
{

    public static void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        Globals.LogInfo("Initializing Common.");
        RecipeRegistry.init();
        PacketManager.register();
    }

}