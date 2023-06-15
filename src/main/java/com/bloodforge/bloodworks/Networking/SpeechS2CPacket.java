package com.bloodforge.bloodworks.Networking;

import com.bloodforge.bloodworks.Client.Sound.SAM.SAMReciter;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SpeechS2CPacket
{
    private final Component message;
    private final float x, y, z;
    private final int speed, pitch, throat, mouth;

    public SpeechS2CPacket(Component msg, float x, float y, float z, int speed, int pitch, int throat, int mouth)
    {
        this.message = msg;
        this.x = x;
        this.y = y;
        this.z = z;
        this.speed = speed;
        this.pitch = pitch;
        this.throat = throat;
        this.mouth = mouth;
    }

    public SpeechS2CPacket(String msg, float x, float y, float z, int speed, int pitch, int throat, int mouth)
    {
        this(Component.literal(msg), x, y, z, speed, pitch, throat, mouth);
    }

    public SpeechS2CPacket(Component msg, float x, float y, float z)
    {
        this(msg, x, y, z, 72, 64, 128, 128);
    }

    public SpeechS2CPacket(String msg, float x, float y, float z)
    {
        this(Component.literal(msg), x, y, z, 72, 64, 128, 128);
    }

    public SpeechS2CPacket(FriendlyByteBuf buf)
    {
        this.message = buf.readComponent();
        this.x = buf.readFloat();
        this.y = buf.readFloat();
        this.z = buf.readFloat();
        this.speed = buf.readInt();
        this.pitch = buf.readInt();
        this.throat = buf.readInt();
        this.mouth = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf)
    {
        buf.writeComponent(message);
        buf.writeFloat(x);
        buf.writeFloat(y);
        buf.writeFloat(z);
        buf.writeInt(speed);
        buf.writeInt(pitch);
        buf.writeInt(throat);
        buf.writeInt(mouth);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() ->
        {
            if (Minecraft.getInstance().player != null)
            {
                Minecraft.getInstance().player.displayClientMessage(message, true);
                SAMReciter.Speak(new Vec3(x, y, z), message.getString(), speed, pitch, throat, mouth);
            }
        });
        return true;
    }
}