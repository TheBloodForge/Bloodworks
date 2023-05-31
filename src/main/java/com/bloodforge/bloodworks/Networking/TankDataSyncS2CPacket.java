package com.bloodforge.bloodworks.Networking;

import com.bloodforge.bloodworks.Server.TankDataProxy;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TankDataSyncS2CPacket
{
    private final String tank_name;
    private final CompoundTag tankData;

    public TankDataSyncS2CPacket(String tank_id, CompoundTag tank)
    {
        this.tank_name = tank_id;
        this.tankData = tank;
    }

    public TankDataSyncS2CPacket(FriendlyByteBuf buf)
    {
        this.tank_name = buf.readUtf();
        this.tankData = buf.readNbt();
    }

    public void toBytes(FriendlyByteBuf buf)
    {
        buf.writeUtf(tank_name);
        buf.writeNbt(tankData);
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean handle(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> TankDataProxy.loadTank(tank_name, tankData, true));
        return true;
    }
}