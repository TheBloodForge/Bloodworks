package com.bloodforge.bloodworks.Registry;

import com.bloodforge.bloodworks.Globals;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoundRegistry
{
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS.getRegistryName(), Globals.MODID);

    public static RegistryObject<SoundEvent> MUSIC_DISC_INDUSTRY = registerSoundEvent("bloodworks.music.industry");




    private static  RegistryObject<SoundEvent> registerSoundEvent(String name)
    {
        return SOUND_EVENTS.register(name, () -> new SoundEvent(new ResourceLocation(Globals.MODID, name)));
    }
}
