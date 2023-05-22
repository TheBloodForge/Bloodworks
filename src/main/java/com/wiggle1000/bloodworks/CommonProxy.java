package com.wiggle1000.bloodworks;

import com.wiggle1000.bloodworks.Particles.FleshStepParticle;
import com.wiggle1000.bloodworks.Registry.ParticleRegistry;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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
    }

}