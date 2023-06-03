package com.bloodforge.bloodworks;

import com.bloodforge.bloodworks.Common.CommonProxy;
import com.bloodforge.bloodworks.Common.Compatability.OneProbeCompat;
import com.bloodforge.bloodworks.Common.Config.BloodworksCommonConfig;
import com.bloodforge.bloodworks.Registry.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@SuppressWarnings("unused")
@Mod(Globals.MODID)
public class BloodworksMod
{
    public BloodworksMod()
    {
        // -------- Register Listeners --------
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(CommonProxy::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);

        // -------- Register DeferredRegistries --------
        BlockRegistry.BLOCKS.register(modEventBus);
        BlockRegistry.BLOCK_ENTITIES.register(modEventBus);
        ItemRegistry.ITEMS.register(modEventBus);
        ParticleRegistry.PARTICLES.register(modEventBus);
//        BlockEntityRegistry.BLOCK_ENTITIES.register(modEventBus);
        FluidRegistry.FLUID_TYPES.register(modEventBus);
        FluidRegistry.FLUIDS.register(modEventBus);
        MenuRegistry.MENUS.register(modEventBus);
//        POIRegistry.POI_TYPES.register(modEventBus);

//        modEventBus.addListener(this::onInterModEnqueue);
        modEventBus.addListener(this::InterModResponses);

        // -------- Register Configs --------
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, BloodworksCommonConfig.CONFIG_SPEC);
    }


    public void InterModResponses(InterModProcessEvent event)
    {
        /*try
        {
            Class c = event.getIMCStream().findFirst().get().messageSupplier().get().getClass();
            Method test = c.getMethod(event.getIMCStream().findFirst().get().method(), String.class);
            Object response = test.invoke(c, "Hello");
            System.out.println(response);
        } catch (Exception ignored) {}*/
    }


    public void onInterModEnqueue(InterModEnqueueEvent event)
    {
        InterModComms.sendTo("theoneprobe", "getTheOneProbe", OneProbeCompat::new);
    }
}