package com.bloodforge.bloodworks.Networking;

import com.bloodforge.bloodworks.Server.TankDataProxy;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

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

    @SuppressWarnings("UnusedReturnValue")
    public boolean handle(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> TankDataProxy.getTankByName(parentName).setFluid(fluidStack));
        return true;
    }
}