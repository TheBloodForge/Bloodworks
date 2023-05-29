package com.wiggle1000.bloodworks.Networking;

import com.wiggle1000.bloodworks.Server.TankData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@SuppressWarnings({"SameReturnValue", "UnusedReturnValue"})
public class TankSyncS2CPacket
{
    private final String parentName;
    private final FluidStack fluidStack;

    public TankSyncS2CPacket(String tankName, FluidStack fluidStack)
    {
        this.parentName = tankName;
        this.fluidStack = fluidStack;
    }

    public TankSyncS2CPacket(FriendlyByteBuf buf)
    {
        this.parentName = buf.readUtf();
        this.fluidStack = buf.readFluidStack();
    }

    public void toBytes(FriendlyByteBuf buf)
    {
        buf.writeUtf(parentName);
        buf.writeFluidStack(fluidStack);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() ->
                TankData.getTankByName(parentName).setFluid(fluidStack));
        return true;
    }
}