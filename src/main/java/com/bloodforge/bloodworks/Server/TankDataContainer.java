package com.bloodforge.bloodworks.Server;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Collection;
import java.util.HashMap;

import static com.bloodforge.bloodworks.Globals.DEFAULT_TANK_CAPACITY;
import static com.bloodforge.bloodworks.Server.TankDataManager.save;
import static com.bloodforge.bloodworks.Server.TankDataProxy.syncFluid;
import static com.bloodforge.bloodworks.Server.TankDataProxy.syncTankName;

public class TankDataContainer
{
    public final String tank_name;
    private int tier = 1, bottomY, topY;
    private FluidTank tank;
    private HashMap<String, BlockPos> children = new HashMap<>();
    public TankDataContainer(String name, BlockPos blockPos)
    {
        bottomY = blockPos.getY(); topY = blockPos.getY();
        tank_name = name;
        tank = makeTank(name);
        children.put(blockPos.toShortString(), blockPos);
    }

    public TankDataContainer(String name, int[] params, HashMap<String, BlockPos> readChildren, CompoundTag tankTag)
    {
        tank_name = name;
        children = readChildren;
        tier = params[0]; bottomY = params[1]; topY = params[2];
        tank = makeTank(name);
        tank.readFromNBT(tankTag);
    }

    public FluidTank getTank()
    {
        return tank;
    }

    public boolean hasChild(BlockPos pos)
    {
        return children.containsKey(pos.toShortString());
    }

    public void addChild(BlockPos pos)
    {
        if (hasChild(pos)) return;
        children.put(pos.toShortString(), pos);
        updateSize(pos.getY(), true);
        updateCapacity();
    }

    public void removeChild(BlockPos pos)
    {
        if (!hasChild(pos)) return;
        children.remove(pos.toShortString());
        if (children.isEmpty())
        {
            TankDataProxy.deleteTank(tank_name);
            return;
        }
        updateSize(pos.getY(), false);
        updateCapacity();
    }

    public Collection<BlockPos> getChildren()
    { return children.values(); }

    private void updateCapacity()
    { tank.setCapacity(children.size() * (tier > 0 ? tier : 1) * DEFAULT_TANK_CAPACITY); }

    private void updateSize(int yPos, boolean adding)
    {
        if (!adding && yPos == topY || yPos == bottomY)
        { recalculateSize(); return; }

        if (yPos > topY) topY = yPos;
        if (yPos < bottomY) bottomY = yPos;
    }

    private void recalculateSize()
    {
        int min = topY, max = bottomY;
        for (BlockPos child : children.values())
        {
            if (child.getY() > max) max = child.getY();
            if (child.getY() < min) min = child.getY();
        }
        topY = max; bottomY = min;
    }

    public void setTankTier(int newTier)
    { tier = newTier; updateCapacity(); }

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
                save(ServerLifecycleHooks.getCurrentServer().overworld());
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