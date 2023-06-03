package com.bloodforge.bloodworks.Common;

import com.bloodforge.bloodworks.Globals;
import com.bloodforge.bloodworks.Networking.PacketManager;
import com.bloodforge.bloodworks.Registry.RecipeRegistry;
import net.minecraftforge.common.MinecraftForge;
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

        MinecraftForge.EVENT_BUS.register(new ForgeCommonEvents());
    }
}