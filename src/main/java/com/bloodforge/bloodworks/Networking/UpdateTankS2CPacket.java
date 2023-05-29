package com.bloodforge.bloodworks.Networking;

import com.bloodforge.bloodworks.Server.TankData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@SuppressWarnings({"SameReturnValue", "UnusedReturnValue"})
public class UpdateTankS2CPacket
{
    private final String parentName;
    private final BlockPos childPos;
    private final boolean wasAdded;

    public UpdateTankS2CPacket(String parentName, BlockPos child, boolean added)
    {
        this.parentName = parentName;
        this.childPos = child;
        this.wasAdded = added;
    }

    public UpdateTankS2CPacket(FriendlyByteBuf buf)
    {
        this.parentName = buf.readUtf();
        this.childPos = buf.readBlockPos();
        this.wasAdded = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf)
    {
        buf.writeUtf(parentName);
        buf.writeBlockPos(childPos);
        buf.writeBoolean(wasAdded);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() ->
        {
            if (wasAdded)
                TankData.addChild(parentName, childPos);
            else
                TankData.removeChild(parentName, childPos);
        });
        return true;
    }
}