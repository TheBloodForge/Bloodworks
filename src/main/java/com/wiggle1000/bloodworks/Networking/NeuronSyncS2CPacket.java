package com.wiggle1000.bloodworks.Networking;

import com.wiggle1000.bloodworks.Blocks.BlockEntities.BE_Neuron;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@SuppressWarnings({"SameReturnValue", "UnusedReturnValue"})
public class NeuronSyncS2CPacket
{
    private final BlockPos pos;
    private final CompoundTag connections;

    public NeuronSyncS2CPacket(BlockPos pos, CompoundTag tag)
    {
        this.pos = pos;
        this.connections = tag;
    }

    public NeuronSyncS2CPacket(FriendlyByteBuf buf)
    {
        this.pos = buf.readBlockPos();
        this.connections = buf.readNbt();
    }

    public void toBytes(FriendlyByteBuf buf)
    {
        buf.writeBlockPos(pos);
        buf.writeNbt(connections);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() ->
        {
            if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof BE_Neuron neuron)
            {
                neuron.unwrapNBT(connections);
            }
        });
        return true;
    }
}