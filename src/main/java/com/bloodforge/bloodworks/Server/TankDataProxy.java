package com.bloodforge.bloodworks.Server;

import com.bloodforge.bloodworks.Networking.PacketManager;
import com.bloodforge.bloodworks.Networking.TankNameSyncS2CPacket;
import com.bloodforge.bloodworks.Networking.TankSyncS2CPacket;
import com.bloodforge.bloodworks.Networking.UpdateTankS2CPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.HashMap;
import java.util.Set;

import static com.bloodforge.bloodworks.Globals.DEFAULT_TANK_TRANSFER_RATE;

public class TankDataProxy
{
    public static final HashMap<String, TankDataContainer> MASTER_TANK_CONTAINER = new HashMap<>();
    public static CompoundTag TankDataTag = new CompoundTag();

    static void deleteTank(String parentName)
    { MASTER_TANK_CONTAINER.remove(parentName); }

    public static FluidTank getTankByName(String tankName)
    {
        if (!tankExists(tankName)) return new FluidTank(404);
        return getDataForTank(tankName).getTank();
    }

    public static String createNewParent(BlockPos block_pos)
    {
        String tank_id = "TCT-" + (System.currentTimeMillis() - 1685405500000L);
        TankDataContainer tank_data = new TankDataContainer(tank_id, block_pos);
        MASTER_TANK_CONTAINER.put(tank_id, tank_data);
        return tank_id;
    }

    private static boolean tankExists(String tankName)
    { return MASTER_TANK_CONTAINER.containsKey(tankName); }

    private static TankDataContainer getDataForTank(String tank)
    {
        if (!tankExists(tank))
        {
            System.err.println("No entry exists for [" + tank + "] in MASTER_TANK_CONTAINER.");
            return new TankDataContainer(tank, new BlockPos(0, 0, 0));
        }
        return MASTER_TANK_CONTAINER.get(tank);
    }

    public static void saveTanks(Level level)
    {
        for (String tankName : MASTER_TANK_CONTAINER.keySet())
        {
            TankDataTag.remove(tankName);
            TankDataTag.put(tankName, TankDataPacker.getTankDataTagForSavingToMasterTag(tankName));
        }
        TankDataManager.saveData();
    }

    public static void loadTanks(Level level)
    {
        TankDataManager.read();
        MASTER_TANK_CONTAINER.clear();
        for (String tankName : TankDataTag.getAllKeys())
        {
            TankDataContainer tc = TankDataPacker.getTankDataFromCompound(tankName, TankDataTag.getCompound(tankName));
            MASTER_TANK_CONTAINER.put(tankName, tc);
            syncTankName(tankName);
            syncFluid(tankName);
        }
    }

    public static String recoverTankName(BlockPos blockPos)
    {
        Set<String> tankNames = MASTER_TANK_CONTAINER.keySet();
        for (String tankName : tankNames)
            if (getDataForTank(tankName).hasChild(blockPos))
                return tankName;
        return "";
    }

    public static void addChild(String parentName, BlockPos blockPos, Level level)
    {
        getDataForTank(parentName).addChild(blockPos);
        if (level != null && !level.isClientSide())
            PacketManager.sendToClients(new UpdateTankS2CPacket(parentName, blockPos, true));
    }

    public static void removeChild(String parentName, BlockPos blockPos, Level level)
    {
        getDataForTank(parentName).removeChild(blockPos);
        if (level != null && !level.isClientSide())
            PacketManager.sendToClients(new UpdateTankS2CPacket(parentName, blockPos, false));
    }

    public static int getTankTier(String tankName)
    {
        if (tankName.isEmpty() || !tankExists(tankName)) return 0;
        return getDataForTank(tankName).getTier();
    }

    public static int getTankMin(String tankName)
    {
        if (tankName.isEmpty() || !tankExists(tankName)) return 0;
        return getDataForTank(tankName).getBottom();
    }

    public static int getTankMax(String tankName)
    {
        if (tankName.isEmpty() || !tankExists(tankName)) return 0;
        return getDataForTank(tankName).getTop();
    }

    public static int getTankHeight(String tankName)
    {
        if (tankName.isEmpty() || !tankExists(tankName)) return 0;
        return getDataForTank(tankName).getHeight();
    }


    public static void syncFluid(String tankName)
    {
        PacketManager.sendToClients(new TankSyncS2CPacket(tankName, getTankByName(tankName).getFluid()));
    }

    public static boolean hasTankByName(String parentName)
    {
        if (parentName.isEmpty()) return false;
        return tankExists(parentName);
    }

    public static void syncTankName(String parentName, BlockPos blockPos)
    {
        PacketManager.sendToClients(new TankNameSyncS2CPacket(parentName, blockPos));
    }

    public static void syncTankName(String parentName)
    {
        if (!parentName.isEmpty() && hasTankByName(parentName))
            for (BlockPos blockPos : getDataForTank(parentName).getChildren())
                syncTankName(parentName, blockPos);
    }

    public static int getTankTransferRate(String tank_id)
    {
        if (tank_id.isEmpty()) return 0;
        return getTankTier(tank_id) == -1 ? 50000 : getTankTier(tank_id) * DEFAULT_TANK_TRANSFER_RATE;
    }

    public static void changeTier(String tank_id, int i)
    { getDataForTank(tank_id).setTankTier(getTankTier(tank_id) + i); }

    public static void setTankTier(String tank_id, int newTier)
    { getDataForTank(tank_id).setTankTier(newTier); }
}