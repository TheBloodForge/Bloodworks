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
    public static RegistryObject<SoundEvent> FLESH_GROW = registerSoundEvent("bloodworks.flesh.grow");
    public static RegistryObject<SoundEvent> WRENCH_TIGHTEN = registerSoundEvent("bloodworks.tool.wrench.tighten");
    public static RegistryObject<SoundEvent> WRENCH_LOOSEN = registerSoundEvent("bloodworks.tool.wrench.loosen");

    public static RegistryObject<SoundEvent> SELECTOR_OPEN = registerSoundEvent("bloodworks.ui.selector.open");
    public static RegistryObject<SoundEvent> SELECTOR_PIP = registerSoundEvent("bloodworks.ui.selector.pip");
    public static RegistryObject<SoundEvent> SELECTOR_CANCEL = registerSoundEvent("bloodworks.ui.selector.cancel");
    public static RegistryObject<SoundEvent> SELECTOR_WRITE = registerSoundEvent("bloodworks.ui.selector.write");




    private static  RegistryObject<SoundEvent> registerSoundEvent(String name)
    {
        return SOUND_EVENTS.register(name, () -> new SoundEvent(new ResourceLocation(Globals.MODID, name)));
    }
}
