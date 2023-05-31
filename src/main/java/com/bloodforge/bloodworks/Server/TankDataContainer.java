package com.bloodforge.bloodworks.Server;

import com.bloodforge.bloodworks.Globals;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import static com.bloodforge.bloodworks.Globals.DEFAULT_TANK_CAPACITY;
import static com.bloodforge.bloodworks.Globals.KELDON_IS_DEBUGGING_TANKS_AGAIN_FFS;
import static com.bloodforge.bloodworks.Server.TankDataManager.saveData;
import static com.bloodforge.bloodworks.Server.TankDataProxy.syncFluid;
import static com.bloodforge.bloodworks.Server.TankDataProxy.syncTankName;

public class TankDataContainer
{
    public final String tank_name;
    private final FluidTank tank;
    private final HashMap<String, BlockPos> children = new HashMap<>();
    private int tier = 1, bottomY, topY;
    public TankDataContainer(String name, BlockPos blockPos)
    {
        bottomY = blockPos.getY(); topY = blockPos.getY();
        tank_name = name;
        tank = makeTank(name);
        children.put(blockPos.toShortString(), blockPos);
        if (KELDON_IS_DEBUGGING_TANKS_AGAIN_FFS) Globals.LogDebug("Creating Tank Container for [" + name + "] from nothing. First Child : " + blockPos, false);
    }

    public TankDataContainer(String name, int[] params, HashMap<String, BlockPos> readChildren, CompoundTag tankTag, boolean isClient)
    {
        tank_name = name;
        children.putAll(readChildren);
        tier = params[0]; bottomY = params[1]; topY = params[2];
        tank = makeTank(name);
        tank.readFromNBT(tankTag);
        if (KELDON_IS_DEBUGGING_TANKS_AGAIN_FFS) Globals.LogDebug("Creating Tank Container for [" + name + "] with params" + Arrays.toString(params) + " with [" + readChildren.size() + "] children at locations " + readChildren.keySet(), isClient);
    }

    public FluidTank getTank()
    {
        return tank;
    }

    public boolean hasChild(BlockPos pos)
    {
        return children.containsKey(pos.toShortString());
    }

    public void addChild(BlockPos pos, boolean isClient)
    {
        if (hasChild(pos)) return;
        children.put(pos.toShortString(), pos);
        if (KELDON_IS_DEBUGGING_TANKS_AGAIN_FFS) Globals.LogDebug("Added Child to [" + tank_name + "]. Child : " + pos, isClient);
        updateSize(pos.getY(), true, isClient);
        updateCapacity(isClient);
    }

    public void removeChild(BlockPos pos, boolean isClient)
    {
        if (!hasChild(pos)) return;
        children.remove(pos.toShortString());
        if (KELDON_IS_DEBUGGING_TANKS_AGAIN_FFS) Globals.LogDebug("Removed Child from [" + tank_name + "]. Child : " + pos, isClient);
        if (children.isEmpty())
        {
            TankDataProxy.deleteTank(tank_name);
            return;
        }
        updateSize(pos.getY(), false, isClient);
        updateCapacity(isClient);
    }

    public Collection<BlockPos> getChildren()
    { return children.values(); }

    private void updateCapacity(boolean isClient)
    {
        tank.setCapacity(children.size() * (tier > 0 ? tier : 1) * DEFAULT_TANK_CAPACITY);
        if (KELDON_IS_DEBUGGING_TANKS_AGAIN_FFS) Globals.LogDebug("Updating Capacity for [" + tank_name + "]. New Capacity : " + tank.getCapacity(), isClient);
    }

    private void updateSize(int yPos, boolean adding, boolean isClient)
    {
        if (!adding && yPos == topY || yPos == bottomY)
        { recalculateSize(isClient); return; }

        if (yPos > topY) topY = yPos;
        if (yPos < bottomY) bottomY = yPos;
        if (KELDON_IS_DEBUGGING_TANKS_AGAIN_FFS) Globals.LogDebug("Updating Size for [" + tank_name + "]. New Size bottom:" + bottomY + " top:" + topY + " height:" + getHeight(), isClient);
    }

    private void recalculateSize(boolean isClient)
    {
        int min = topY, max = bottomY;
        for (BlockPos child : children.values())
        {
            if (child.getY() > max) max = child.getY();
            if (child.getY() < min) min = child.getY();
        }
        topY = max; bottomY = min;
        if (KELDON_IS_DEBUGGING_TANKS_AGAIN_FFS) Globals.LogDebug("Recalculating Size for [" + tank_name + "].", isClient);
    }

    public void setTankTier(int newTier, boolean isClient)
    { tier = newTier; updateCapacity(isClient); }

    public int getTier()
    { return tier; }

    public int getBottom()
    { return bottomY; }

    public int getTop()
    { return topY; }

    public int getHeight()
    {
        return (topY - bottomY) + 1;
    }

    public static FluidTank makeTank(String tankName)
    {
        return new FluidTank(DEFAULT_TANK_CAPACITY)
        {
            @Override
            public boolean isFluidValid(FluidStack stack)
            {
                return getFluid().getAmount() == 0 || stack.getFluid() == getFluid().getFluid();
            }

            @Override
            protected void onContentsChanged()
            {
                saveData();
                syncTankName(tankName);
                syncFluid(tankName);
                super.onContentsChanged();
            }

            @Override
            public int getCapacity()
            {
                return super.getCapacity();
            }
        };
    }
}