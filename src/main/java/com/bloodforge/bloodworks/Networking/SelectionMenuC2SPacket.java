package com.bloodforge.bloodworks.Networking;

import com.bloodforge.bloodworks.Server.PlayerSelectionHudTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SelectionMenuC2SPacket
{
    private final int selectionIndex;
    private final boolean isFinalSelection;
    private final boolean isCancelled;
    private final BlockPos position; //for sanity check

    public SelectionMenuC2SPacket(int selectionIndex, boolean isFinalSelection, boolean isCancelled, BlockPos pos)
    {
        this.selectionIndex = selectionIndex;
        this.isFinalSelection = isFinalSelection;
        this.isCancelled = isCancelled;
        this.position = pos;
    }

    public SelectionMenuC2SPacket(FriendlyByteBuf buf)
    {
        this.selectionIndex = buf.readInt();
        this.isFinalSelection = buf.readBoolean();
        this.isCancelled = buf.readBoolean();
        this.position = buf.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buf)
    {
        buf.writeInt(selectionIndex);
        buf.writeBoolean(isFinalSelection);
        buf.writeBoolean(isCancelled);
        buf.writeBlockPos(position);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() ->
        {
            ServerPlayer player = context.getSender();
            if(player == null) return;
            PlayerSelectionHudTracker.RespondToMenuSelectionPacket(player, selectionIndex, isFinalSelection, isCancelled, position);
        });
        return true;
    }
}