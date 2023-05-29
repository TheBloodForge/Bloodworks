package com.bloodforge.bloodworks.Networking;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageS2CPacket
{
    private final Component message;
    private final boolean isChat;

    public MessageS2CPacket(Component msg, boolean isChat)
    {
        this.message = msg;
        this.isChat = isChat;
    }

    public MessageS2CPacket(FriendlyByteBuf buf)
    {
        this.message = buf.readComponent();
        this.isChat = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf)
    {
        buf.writeComponent(message);
        buf.writeBoolean(isChat);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() ->
        {
            if (Minecraft.getInstance().player != null)
            {
                Minecraft.getInstance().player.displayClientMessage(message, !isChat);
            }
        });
        return true;
    }
}