package com.bloodforge.bloodworks.Networking;

import com.bloodforge.bloodworks.Client.Screens.SelectionMenuHudOverlay;
import com.bloodforge.bloodworks.Util.SelectionMenuOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SelectionMenuS2CPacket
{
    //TODO: PASS CURRENT SELECTION!
    private final boolean isCloseRequest;
    private final SelectionMenuOptions menu;
    private final int initialSelection;
    private final BlockPos position;

    public SelectionMenuS2CPacket(SelectionMenuOptions menu, BlockPos pos, int initialSelection)
    {
        this.menu = menu;
        this.position = pos;
        this.isCloseRequest = false;
        this.initialSelection = initialSelection;
    }

    public SelectionMenuS2CPacket(boolean isCloseRequest)
    {
        this.menu = null;
        this.position = null;
        this.initialSelection = 0;
        this.isCloseRequest = isCloseRequest;
    }

    public SelectionMenuS2CPacket(FriendlyByteBuf buf)
    {
        isCloseRequest = buf.readBoolean();
        initialSelection = buf.readInt();
        if(isCloseRequest)
        {
            menu = null;
            position = null;
            return;
        }
        menu = new SelectionMenuOptions(buf);
        position = buf.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buf)
    {
        buf.writeBoolean(isCloseRequest);
        buf.writeInt(initialSelection);
        if(!isCloseRequest)
        {
            menu.toBytes(buf);
            buf.writeBlockPos(position);
        }
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() ->
        {
            if (Minecraft.getInstance().player == null) return;
            if(isCloseRequest)
            {
                SelectionMenuHudOverlay.CloseMenu();
            }
            else
            {
                SelectionMenuHudOverlay.OpenMenu(menu, position, initialSelection);
            }
        });
        return true;
    }
}