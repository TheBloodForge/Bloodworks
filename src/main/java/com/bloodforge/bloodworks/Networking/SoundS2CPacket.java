package com.bloodforge.bloodworks.Networking;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SoundS2CPacket
{
    public static final float LOCATION_ACCURACY = 8.0F;
    private final ResourceLocation name;
    private final SoundSource source;
    private final float x;
    private final float y;
    private final float z;
    private final float volume;
    private final float pitch;

    public SoundS2CPacket(ResourceLocation p_237699_, SoundSource p_237700_, Vec3 p_237701_, float p_237702_, float p_237703_) {
        this.name = p_237699_;
        this.source = p_237700_;
        this.x = (float)p_237701_.x;
        this.y = (float)p_237701_.y;
        this.z = (float)p_237701_.z;
        this.volume = p_237702_;
        this.pitch = p_237703_;
    }

    public SoundS2CPacket(FriendlyByteBuf p_178839_) {
        this.name = p_178839_.readResourceLocation();
        this.source = p_178839_.readEnum(SoundSource.class);
        this.x = p_178839_.readFloat();
        this.y = p_178839_.readFloat();
        this.z = p_178839_.readFloat();
        this.volume = p_178839_.readFloat();
        this.pitch = p_178839_.readFloat();
    }

    public void toBytes(FriendlyByteBuf p_132068_) {
        p_132068_.writeResourceLocation(this.name);
        p_132068_.writeEnum(this.source);
        p_132068_.writeFloat(this.x);
        p_132068_.writeFloat(this.y);
        p_132068_.writeFloat(this.z);
        p_132068_.writeFloat(this.volume);
        p_132068_.writeFloat(this.pitch);
    }

    public ResourceLocation getName() {
        return this.name;
    }

    public SoundSource getSource() {
        return this.source;
    }

    public double getX() { return this.x; }

    public double getY() { return this.y; }

    public double getZ() { return this.z; }

    public float getVolume() { return this.volume; }

    public float getPitch() { return this.pitch; }

    public void handle(Supplier<NetworkEvent.Context> supplier)
    {
        if(Minecraft.getInstance().level == null) return;
        Minecraft.getInstance().level.playLocalSound(getX(), getY(), getZ(), new SoundEvent(getName()), getSource(), getVolume(), getPitch(), false);
    }
}
