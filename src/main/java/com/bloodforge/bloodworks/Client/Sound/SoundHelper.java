package com.bloodforge.bloodworks.Client.Sound;

import com.bloodforge.bloodworks.Client.Sound.SAM.ALRaw.ALSoundSource;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.*;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

import java.util.concurrent.CompletableFuture;

public class SoundHelper
{
    public static ALSoundSource SAMSoundSource = new ALSoundSource(false, false, -1);
    static {SAMSoundSource.setSampleRate(22050); SAMSoundSource.setHearing(32f, 1f);}

    public static SoundEngine soundEngine;
    private static final Long2ObjectMap<MachineTickableSoundInstance> playingBlockSounds = new Long2ObjectOpenHashMap<>();

    public static void SetSoundEngine(SoundEngine engine)
    {
        soundEngine = engine;
    }

    public static MachineTickableSoundInstance CreateMachineSoundInstance(ResourceLocation soundLocation, BlockPos pos, boolean looping)
    {
        return new MachineTickableSoundInstance(soundLocation, SoundSource.BLOCKS, 1.0f, 1.0f, SoundInstance.createUnseededRandom(), looping, 0, SoundInstance.Attenuation.LINEAR, pos.getX(), pos.getY(), pos.getZ(), false);
    }

    public static void playSound(SoundInstance sound)
    {
        Minecraft.getInstance().getSoundManager().play(sound);
    }

    public static void stopSound(SoundInstance sound)
    {
        Minecraft.getInstance().getSoundManager().stop(sound);
    }

    public static boolean isSoundPlaying(SoundInstance sound)
    {
        return Minecraft.getInstance().getSoundManager().isActive(sound);
    }

    public static void setSoundPitch(float pitch, MachineTickableSoundInstance sound)
    {
        sound.SetPitch(pitch);
    }

    public static void setSoundVolume(float volume, MachineTickableSoundInstance sound)
    {
        sound.SetVolume(volume);
    }

    public static void PlayTileSound(ResourceLocation rl, BlockPos pos)
    {
        if(!playingBlockSounds.containsKey(pos.asLong())) {
            //create sound
            playingBlockSounds.put(pos.asLong(), CreateMachineSoundInstance(rl, pos, true));
        }
        if(!isSoundPlaying(playingBlockSounds.get(pos.asLong())))
            playSound(playingBlockSounds.get(pos.asLong()));
    }

    public static void SetTileSoundPitch(float pitch, BlockPos pos)
    {
        if(!playingBlockSounds.containsKey(pos.asLong())) return;
        setSoundPitch(pitch, playingBlockSounds.get(pos.asLong()));
    }

    public static void SetTileSoundVolume(float volume, BlockPos pos)
    {
        if(!playingBlockSounds.containsKey(pos.asLong())) return;
        setSoundVolume(volume, playingBlockSounds.get(pos.asLong()));
    }

    public static void StopTileSound(BlockPos pos)
    {
        if(playingBlockSounds.containsKey(pos.asLong()))
        {
            stopSound(playingBlockSounds.get(pos.asLong()));
        }
    }
    private static class MachineTickableSoundInstance extends AbstractTickableSoundInstance
    {

        protected MachineTickableSoundInstance(SoundEvent soundEvent, SoundSource soundSource, RandomSource randomSource) {
            super(soundEvent, soundSource, randomSource);
        }

        public void SetPitch(float pitch)
        {
            this.pitch = pitch;
        }

        public void SetVolume(float volume)
        {
            this.volume = volume;
        }

        @Override
        public void tick()
        {

        }

        @Override
        public boolean canStartSilent()
        {
            return true;
        }

        @Override
        public boolean canPlaySound() {
            return super.canPlaySound();
        }

        @Override
        public CompletableFuture<AudioStream> getStream(SoundBufferLibrary soundBuffers, Sound sound, boolean looping) {
            return super.getStream(soundBuffers, sound, looping);
        }

        public MachineTickableSoundInstance(ResourceLocation p_235087_, SoundSource p_235088_, float p_235089_, float p_235090_, RandomSource p_235091_, boolean p_235092_, int p_235093_, SoundInstance.Attenuation p_235094_, double p_235095_, double p_235096_, double p_235097_, boolean p_235098_) {
            super(new SoundEvent(p_235087_), p_235088_, p_235091_);
            this.volume = p_235089_;
            this.pitch = p_235090_;
            this.x = p_235095_;
            this.y = p_235096_;
            this.z = p_235097_;
            this.looping = p_235092_;
            this.delay = p_235093_;
            this.attenuation = p_235094_;
            this.relative = p_235098_;
        }
    }
}