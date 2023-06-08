package com.bloodforge.bloodworks.Client;

import com.bloodforge.bloodworks.Blocks.BlockEntities.*;
import com.bloodforge.bloodworks.Client.BlockRenderers.*;
import com.bloodforge.bloodworks.Client.Screens.InfusionChamberScreen;
import com.bloodforge.bloodworks.Client.Sound.SoundHelper;
import com.bloodforge.bloodworks.Globals;
import com.bloodforge.bloodworks.Particles.FleshStepParticle;
import com.bloodforge.bloodworks.Registry.BlockRegistry;
import com.bloodforge.bloodworks.Registry.FluidRegistry;
import com.bloodforge.bloodworks.Registry.MenuRegistry;
import com.bloodforge.bloodworks.Registry.ParticleRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.event.sound.SoundEngineLoadEvent;
import net.minecraftforge.common.MinecraftForge;
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
        Globals.IS_CLIENT = true;
        MinecraftForge.EVENT_BUS.register(new ForgeClientEvents());
    }

    @SubscribeEvent
    public static void registerParticleFactories(final RegisterParticleProvidersEvent event)
    {
        event.register(ParticleRegistry.PARTICLE_FLESH_STEP.get(), FleshStepParticle.Provider::new);
    }

    @SubscribeEvent
    public static void onSoundEngineLoad(SoundEngineLoadEvent event)
    {
        if(SoundHelper.soundEngine != null) return;
        SoundHelper.SetSoundEngine(event.getEngine());
    }

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers registerRenderers)
    {
        registerRenderers.registerBlockEntityRenderer((BlockEntityType<BE_StirlingGenerator>) BlockRegistry.BLOCK_STIRLING_GENERATOR.blockEntity().get(), BER_StirlingGenerator::new);
        registerRenderers.registerBlockEntityRenderer((BlockEntityType<BE_Intestine>) BlockRegistry.BLOCK_INTESTINE.blockEntity().get(), BER_Intestine::new);
        registerRenderers.registerBlockEntityRenderer((BlockEntityType<BE_Tank>) BlockRegistry.BLOCK_BLOOD_TANK.blockEntity().get(), BER_BloodTank::new);
        registerRenderers.registerBlockEntityRenderer((BlockEntityType<BE_Neuron>) BlockRegistry.BLOCK_NEURON.blockEntity().get(), BER_Neuron::new);
        registerRenderers.registerBlockEntityRenderer((BlockEntityType<BE_Braincase_Controller>) BlockRegistry.BLOCK_BRAINCASE_CONTROLLER.blockEntity().get(), BER_Braincase_Controller::new);
        registerRenderers.registerBlockEntityRenderer((BlockEntityType<BE_AirlockDoor>) BlockRegistry.BLOCK_AIRLOCK_DOOR.blockEntity().get(), BER_AirlockDoor::new);

        ItemBlockRenderTypes.setRenderLayer(FluidRegistry.FLUID_BLOOD.source.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(FluidRegistry.FLUID_BLOOD.flowing.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(FluidRegistry.FLUID_CRANIAL.source.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(FluidRegistry.FLUID_CRANIAL.flowing.get(), RenderType.translucent());
    }
}