package com.wiggle1000.bloodworks;

import com.wiggle1000.bloodworks.Client.BlockRenderers.BlockEntityRendererIntestine;
import com.wiggle1000.bloodworks.Particles.FleshStepParticle;
import com.wiggle1000.bloodworks.Registry.BlockEntityRegistry;
import com.wiggle1000.bloodworks.Registry.ParticleRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = Globals.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientProxy
{
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event)
    {
        // Some client setup code
        Globals.LogInfo("Initializing Client.");
    }

    @SubscribeEvent
    public static void registerParticleFactories(final RegisterParticleProvidersEvent event)
    {
        event.register(ParticleRegistry.PARTICLE_FLESH_STEP.get(), FleshStepParticle.Provider::new);
    }

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers registerRenderers)
    {
        registerRenderers.registerBlockEntityRenderer(BlockEntityRegistry.BLOCK_ENTITY_INTESTINE.get(), context -> new BlockEntityRendererIntestine(context));
    }
}