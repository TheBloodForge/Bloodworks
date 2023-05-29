package com.wiggle1000.bloodworks.Server;

import com.wiggle1000.bloodworks.Globals;
import com.wiggle1000.bloodworks.Networking.PacketManager;
import com.wiggle1000.bloodworks.Networking.TankNameSyncS2CPacket;
import com.wiggle1000.bloodworks.Networking.TankSyncS2CPacket;
import com.wiggle1000.bloodworks.Networking.UpdateTankS2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TankData extends SavedData
{
    public static final HashMap<String, ArrayList<BlockPos>> children = new HashMap<>();
    public static final HashMap<String, Integer[]> tankSizes = new HashMap<>();
    public static final HashMap<String, FluidTank> tanks = new HashMap<>();
    CompoundTag loadedTag = new CompoundTag();
    public static CompoundTag TankTags = new CompoundTag();
    public TankData()
    { super(); }

    public static TankData create()
    { return new TankData(); }

    public void save()
    { this.setDirty(); }

    @Override
    public CompoundTag save(CompoundTag tag)
    {
        return loadedTag;
    }

    public static TankData load(CompoundTag tag)
    {
        TankData data = create();
        data.loadedTag = tag;
        return data;
    }

    public static TankData getDataManager(LevelAccessor level)
    {
        if (level.getServer() == null) return create();
        return level.getServer().overworld().getDataStorage().computeIfAbsent(TankData::load, TankData::create, "BloodworksTankData");
    }

    public static void save(LevelAccessor level)
    {
        if (!level.isClientSide())
        {
            TankData data = TankData.getDataManager(level);
            data.loadedTag = TankTags;
            data.save();
        }
    }

    public static void read(LevelAccessor level)
    {
        if (level != null && !level.isClientSide())
        {
            TankData data = TankData.getDataManager(level);
            TankTags = data.loadedTag;
        }
    }


    public static void addChild(String parentName, BlockPos blockPos)
    {
        if (!children.containsKey(parentName)) return;
        children.get(parentName).add(blockPos);
        updateTankCapacity(parentName);
        if (!Minecraft.getInstance().level.isClientSide())
            PacketManager.sendToClients(new UpdateTankS2CPacket(parentName, blockPos, false));
    }

    public static void removeChild(String parentName, BlockPos blockPos)
    {
        if (!children.containsKey(parentName)) return;
        children.get(parentName).remove(blockPos);
        updateTankCapacity(parentName);
        if (!Minecraft.getInstance().level.isClientSide())
            PacketManager.sendToClients(new UpdateTankS2CPacket(parentName, blockPos, false));
    }

    public static FluidTank getTankByName(String tankName)
    {
        if (!tanks.containsKey(tankName))
            tanks.put(tankName, makeTank(tankName));
        updateTankCapacity(tankName);
        return tanks.get(tankName);
    }

    private static void updateTankCapacity(String tankName)
    {
        if (hasTankByName(tankName))
            tanks.get(tankName).setCapacity(children.get(tankName).size() * Globals.DEFAULT_CAPACITY);
        if (Minecraft.getInstance().level != null)
            save(Minecraft.getInstance().level);
    }

    public static String createNewParent(BlockPos pos)
    {
        String parentName = "TCT-" + System.currentTimeMillis();
        TankData.children.put(parentName, new ArrayList<>(List.of(pos)));
        TankData.tankSizes.put(parentName, new Integer[]{1, pos.getY(), pos.getY()});
        return parentName;
    }

    public static FluidTank makeTank(String tankName)
    {
        return new FluidTank(Globals.DEFAULT_CAPACITY)
        {
            @Override
            public boolean isFluidValid(FluidStack stack)
            {
                return getFluid().getAmount() == 0 || stack.getFluid() == getFluid().getFluid();
            }

            @Override
            protected void onContentsChanged()
            {
                if (Minecraft.getInstance().level != null)
                    save(Minecraft.getInstance().level);
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

    public static void syncFluid(String tankName)
    {
        if (!tankName.isEmpty())
            PacketManager.sendToClients(new TankSyncS2CPacket(tankName, getTankByName(tankName).getFluid()));
    }


    public static boolean hasTankByName(String parentName)
    {
        if (parentName.isEmpty()) return false;
        return tanks.containsKey(parentName) && tankSizes.containsKey(parentName) && children.containsKey(parentName);
    }


    public static void syncTankName(BlockPos blockPos, String parentName)
    {
        if (!parentName.isEmpty())
            PacketManager.sendToClients(new TankNameSyncS2CPacket(parentName, blockPos));
    }

    public static void syncTankName(String parentName)
    {
        if (!parentName.isEmpty())
            for (BlockPos blockPos : children.get(parentName))
                PacketManager.sendToClients(new TankNameSyncS2CPacket(parentName, blockPos));
    }
}