package com.bloodforge.bloodworks.Server.ServerData;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;

import java.util.ArrayList;
import java.util.UUID;

public class BodyData
{
    public String bloodType = "none";
    public UUID ownerUUID;
    public BlockPos corePosition;
    public ArrayList<UUID> allowedUUIDs = new ArrayList<>();
    public ArrayList<BlockPos> includedBlocks = new ArrayList<>();
    public ArrayList<BlockPos> bloodPumpingBlocks = new ArrayList<>();

    public BodyData(BlockPos corePosition, Player owner)
    {
        this.corePosition = corePosition;
        this.ownerUUID = owner.getUUID();
        AddBlock(corePosition);
    }

    public void AddBlock(BlockPos pos)
    {
        if(includedBlocks.contains(pos)) return;
        includedBlocks.add(pos);
    }
    public void RemoveBlock(BlockPos pos)
    {
        if(!includedBlocks.contains(pos)) return;
        includedBlocks.remove(pos);
    }

    public void Rescan(LevelAccessor world)
    {
        includedBlocks.clear();
        bloodPumpingBlocks.clear();
        Rescan(corePosition);
    }
    public void Rescan(BlockPos fromPos)
    {

    }
}
