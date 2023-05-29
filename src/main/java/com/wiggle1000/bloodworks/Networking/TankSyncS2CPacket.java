package com.wiggle1000.bloodworks.Networking;

import com.wiggle1000.bloodworks.Blocks.BlockEntities.BE_BloodTank;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@SuppressWarnings({"SameReturnValue", "UnusedReturnValue"})
public class TankSyncS2CPacket
{
    private final FluidStack fluidStack;
    private final BlockPos parentPos;
    private final CompoundTag children;

    public TankSyncS2CPacket(FluidStack fluidStack, CompoundTag children, BlockPos parent)
    {
        this.fluidStack = fluidStack;
        this.parentPos = parent;
        this.children = children;
    }

    public TankSyncS2CPacket(FriendlyByteBuf buf)
    {
        this.fluidStack = buf.readFluidStack();
        this.parentPos = buf.readBlockPos();
        this.children = buf.readNbt();
    }

    public void toBytes(FriendlyByteBuf buf)
    {
        buf.writeFluidStack(fluidStack);
        buf.writeBlockPos(parentPos);
        buf.writeNbt(children);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() ->
        {
            if (Minecraft.getInstance().level.getBlockEntity(parentPos) instanceof BE_BloodTank parentEntity)
            {
                parentEntity.createParentTank();
                parentEntity.loadChildrenTagCompound(this.children);
                parentEntity.setFluid(this.fluidStack);
            }
        });
        return true;
    }
}