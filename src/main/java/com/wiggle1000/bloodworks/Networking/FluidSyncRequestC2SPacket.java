package com.wiggle1000.bloodworks.Networking;

import com.wiggle1000.bloodworks.Blocks.BlockEntities.BE_BloodTank;
import com.wiggle1000.bloodworks.Blocks.BlockEntities.BE_InfusionChamber;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@SuppressWarnings({"SameReturnValue", "UnusedReturnValue"})
public class FluidSyncRequestC2SPacket
{
    public final BlockPos pos;
    public FluidSyncRequestC2SPacket(BlockPos pos)
    {
        this.pos = pos;
    }

    public FluidSyncRequestC2SPacket(FriendlyByteBuf buf)
    {
        this.pos = buf.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buf)
    {
        buf.writeBlockPos(pos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() ->
        {
            if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof BE_InfusionChamber blockEntity)
            { blockEntity.syncFluid(); }

            if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof BE_BloodTank blockEntity)
            { blockEntity.syncFluid(); }
        });
        return true;
    }
}