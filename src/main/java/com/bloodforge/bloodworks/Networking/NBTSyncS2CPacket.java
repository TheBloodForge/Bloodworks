package com.bloodforge.bloodworks.Networking;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@SuppressWarnings({"SameReturnValue", "UnusedReturnValue"})
public class NBTSyncS2CPacket
{
    private final CompoundTag nbt;
    private final BlockPos pos;

    public NBTSyncS2CPacket(BlockPos pos, CompoundTag nbtToSync)
    {
        this.pos = pos;
        this.nbt = nbtToSync;
    }

    public NBTSyncS2CPacket(FriendlyByteBuf buf)
    {
        this.pos = buf.readBlockPos();
        this.nbt = buf.readNbt();
    }

    public void toBytes(FriendlyByteBuf buf)
    {
        buf.writeBlockPos(pos);
        buf.writeNbt(nbt);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() ->
        {
            BlockEntity blockEntity;
            if ((blockEntity = Minecraft.getInstance().level.getBlockEntity(pos)) != null)
            {
                blockEntity.load(nbt);
            }
        });
        return true;
    }
}