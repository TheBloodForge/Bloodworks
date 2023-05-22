package com.wiggle1000.bloodworks;

import com.wiggle1000.bloodworks.Config.BloodworksCommonConfig;
import com.wiggle1000.bloodworks.Registry.BlockRegistry;
import com.wiggle1000.bloodworks.Registry.ItemRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@SuppressWarnings("unused")
@Mod(Globals.MODID)
public class BloodworksMod
{
    public BloodworksMod()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for mod loading
        modEventBus.addListener(CommonProxy::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BlockRegistry.BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ItemRegistry.ITEMS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        //Register common config
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, BloodworksCommonConfig.CONFIG_SPEC);
    }
}