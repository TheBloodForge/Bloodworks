package com.bloodforge.bloodworks.Networking;

import com.bloodforge.bloodworks.Blocks.BlockEntities.BE_BloodTank;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@SuppressWarnings({"SameReturnValue", "UnusedReturnValue"})
public class TankNameSyncS2CPacket
{
    private final String parentName;
    private final BlockPos childPos;

    public TankNameSyncS2CPacket(String parentName, BlockPos child)
    {
        this.parentName = parentName;
        this.childPos = child;
    }

    public TankNameSyncS2CPacket(FriendlyByteBuf buf)
    {
        this.parentName = buf.readUtf();
        this.childPos = buf.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buf)
    {
        buf.writeUtf(parentName);
        buf.writeBlockPos(childPos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() ->
        {
            if (Minecraft.getInstance().level.getBlockEntity(childPos) instanceof BE_BloodTank bloodTank)
                bloodTank.parentName = parentName;
        });
        return true;
    }
}