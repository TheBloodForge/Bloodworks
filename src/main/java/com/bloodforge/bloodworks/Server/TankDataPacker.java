package com.bloodforge.bloodworks.Server;

import com.bloodforge.bloodworks.Globals;
import com.bloodforge.bloodworks.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.Collection;
import java.util.HashMap;

import static com.bloodforge.bloodworks.Globals.KELDON_IS_DEBUGGING_TANKS_AGAIN_FFS;
import static com.bloodforge.bloodworks.Server.TankDataProxy.MASTER_TANK_CONTAINER;

public class TankDataPacker
{
    public static CompoundTag getTankDataTag(String tankName)
    {
        CompoundTag masterTag = new CompoundTag();
        if (MASTER_TANK_CONTAINER.isEmpty() || !MASTER_TANK_CONTAINER.containsKey(tankName))
            return masterTag;
        TankDataContainer tankData = MASTER_TANK_CONTAINER.get(tankName);
        CompoundTag childrenTag = wrapChildren(tankData.getChildren());
        CompoundTag dataTag = wrapData(tankData.getTier(), tankData.getBottom(), tankData.getTop());
        CompoundTag fluidTankTag = wrapTank(tankData.getTank());
        masterTag.put("tankChildren", childrenTag);
        masterTag.put("tankData", dataTag);
        masterTag.put("fluidTank", fluidTankTag);
        return masterTag;
    }

    public static TankDataContainer getTankDataFromCompound(String tankName, CompoundTag masterTag, boolean isClient)
    {
        if (KELDON_IS_DEBUGGING_TANKS_AGAIN_FFS) Globals.LogDebug("Loading Tank [" + tankName + "] from compound", isClient);
        int[] params = unwrapData(masterTag.getCompound("tankData"));
        HashMap<String, BlockPos> children = unwrapChildren(masterTag.getCompound("tankChildren"));
        return new TankDataContainer(tankName, params, children, masterTag.getCompound("fluidTank"), isClient);
    }

    private static CompoundTag wrapTank(FluidTank tank)
    {
        CompoundTag tankTag = new CompoundTag();
        tankTag = tank.writeToNBT(tankTag);
        return tankTag;
    }

    private static CompoundTag wrapData(int... data)
    {
        CompoundTag dataTag = new CompoundTag();
        dataTag.putIntArray("tankParameters", data);
        return dataTag;
    }

    private static int[] unwrapData(CompoundTag paramTag)
    {
        return paramTag.getIntArray("tankParameters");
    }

    private static CompoundTag wrapChildren(Collection<BlockPos> children)
    {
        CompoundTag childrenTags = new CompoundTag();
        for (BlockPos child : children)
            childrenTags.putIntArray(child.toShortString(), Util.getBlockPosAsIntArr(child));
        return childrenTags;
    }

    private static HashMap<String, BlockPos> unwrapChildren(CompoundTag childrenTag)
    {
        HashMap<String, BlockPos> childrenPos = new HashMap<>();
        for (String childKey : childrenTag.getAllKeys())
            childrenPos.put(childKey, Util.getBlockPosFromIntArr(childrenTag.getIntArray(childKey)));
        return childrenPos;
    }
}