package com.wiggle1000.bloodworks;

import com.wiggle1000.bloodworks.Blocks.BlockEntities.BE_BloodTank;
import com.wiggle1000.bloodworks.Blocks.BlockEntities.BE_Intestine;
import com.wiggle1000.bloodworks.Blocks.BlockEntities.BE_Neuron;
import com.wiggle1000.bloodworks.Client.BlockRenderers.BER_BloodTank;
import com.wiggle1000.bloodworks.Client.BlockRenderers.BER_Intestine;
import com.wiggle1000.bloodworks.Client.BlockRenderers.BER_Neuron;
import com.wiggle1000.bloodworks.Client.Screens.InfusionChamberScreen;
import com.wiggle1000.bloodworks.Particles.FleshStepParticle;
import com.wiggle1000.bloodworks.Registry.BlockRegistry;
import com.wiggle1000.bloodworks.Registry.FluidRegistry;
import com.wiggle1000.bloodworks.Registry.MenuRegistry;
import com.wiggle1000.bloodworks.Registry.ParticleRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@SuppressWarnings({"unused", "unchecked"})
@Mod.EventBusSubscriber(modid = Globals.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientProxy
{
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event)
    {
        // Some client setup code
        Globals.LogInfo("Initializing Client.");
        MenuScreens.register(MenuRegistry.INFUSION_CHAMBER.get(), InfusionChamberScreen::new);
    }

    @SubscribeEvent
    public static void registerParticleFactories(final RegisterParticleProvidersEvent event)
    {
        event.register(ParticleRegistry.PARTICLE_FLESH_STEP.get(), FleshStepParticle.Provider::new);
    }

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers registerRenderers)
    {
        registerRenderers.registerBlockEntityRenderer((BlockEntityType<BE_Intestine>) BlockRegistry.BLOCK_INTESTINE.blockEntity().get(), BER_Intestine::new);
        registerRenderers.registerBlockEntityRenderer((BlockEntityType<BE_BloodTank>) BlockRegistry.BLOCK_BLOOD_TANK.blockEntity().get(), BER_BloodTank::new);
        registerRenderers.registerBlockEntityRenderer((BlockEntityType<BE_Neuron>) BlockRegistry.BLOCK_NEURON.blockEntity().get(), BER_Neuron::new);

        ItemBlockRenderTypes.setRenderLayer(FluidRegistry.FLUID_BLOOD.source.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(FluidRegistry.FLUID_BLOOD.flowing.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(FluidRegistry.FLUID_CRANIAL.source.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(FluidRegistry.FLUID_CRANIAL.flowing.get(), RenderType.translucent());
    }
}