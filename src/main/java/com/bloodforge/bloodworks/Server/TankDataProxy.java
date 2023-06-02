package com.bloodforge.bloodworks.Server;

import com.bloodforge.bloodworks.Config.BloodworksCommonConfig;
import com.bloodforge.bloodworks.Globals;
import com.bloodforge.bloodworks.Networking.NBTSyncS2CPacket;
import com.bloodforge.bloodworks.Networking.PacketManager;
import com.bloodforge.bloodworks.Networking.TankDataSyncS2CPacket;
import com.bloodforge.bloodworks.Networking.TankSyncS2CPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.HashMap;
import java.util.Set;

import static com.bloodforge.bloodworks.Globals.KELDON_IS_DEBUGGING_TANKS_AGAIN_FFS;

public class TankDataProxy
{
    public static final HashMap<String, TankDataContainer> MASTER_TANK_CONTAINER = new HashMap<>();
    public static CompoundTag TankDataTag = new CompoundTag();

    static void deleteTank(String tankName)
    {
        MASTER_TANK_CONTAINER.remove(tankName);
        TankDataTag.remove(tankName);
        if (KELDON_IS_DEBUGGING_TANKS_AGAIN_FFS) Globals.LogDebug("Removed [" + tankName + "] from Master and Data", false);
    }

    public static FluidTank getTankByName(String tankName)
    {
        if (!tankExists(tankName)) return new FluidTank(404);
        return getDataForTank(tankName).getTank();
    }

    public static String createNewParent(BlockPos block_pos)
    {
        String tank_id = "TCT-" + (System.currentTimeMillis() - 1685405500000L);
        TankDataContainer tank_data = new TankDataContainer(tank_id, block_pos);
        addToMasterContainer(tank_id, tank_data, false);
        saveTanks(ServerLifecycleHooks.getCurrentServer().overworld());
        return tank_id;
    }

    private static boolean tankExists(String tankName)
    { return MASTER_TANK_CONTAINER.containsKey(tankName); }

    private static TankDataContainer getDataForTank(String tank)
    {
        if (!tankExists(tank))
        {
            Globals.LOGGER.error("No entry exists for [" + tank + "] in MASTER_TANK_CONTAINER.");
            return new TankDataContainer(tank, new BlockPos(0, 0, 0));
        }
        return MASTER_TANK_CONTAINER.get(tank);
    }

    private static final int saveCooldown = 500;
    private static long lastSave = 0;
    public static void saveTanks(Level level)
    {
        if (System.currentTimeMillis() - lastSave < saveCooldown) return;
        Globals.LogInfo("Saving Tanks");
        for (String tankName : MASTER_TANK_CONTAINER.keySet())
        {
            updateDataTag(tankName);
        }
        if (!TankDataTag.isEmpty())
            TankDataManager.saveData();
        lastSave = System.currentTimeMillis();
    }

    public static void loadTanks(boolean isClient)
    {
        if (KELDON_IS_DEBUGGING_TANKS_AGAIN_FFS) Globals.LogDebug("Load Tanks Requested", isClient);
        TankDataManager.read();
        resetMaster(isClient);
        if (KELDON_IS_DEBUGGING_TANKS_AGAIN_FFS) Globals.LogDebug("Loaded TankData : " + TankDataTag.getAllKeys(), isClient);
        for (String tankName : TankDataTag.getAllKeys())
        {
            loadTank(tankName, TankDataTag.getCompound(tankName), isClient);
            syncTankName(tankName);
            syncFluid(tankName);
        }
    }

    public static void loadTank(String tankName, CompoundTag tag, boolean isClient)
    {
        if (KELDON_IS_DEBUGGING_TANKS_AGAIN_FFS)
            Globals.LogDebug("Loading Tank [" + tankName + "]", isClient);
        TankDataContainer tc = TankDataPacker.getTankDataFromCompound(tankName, tag, isClient);
        addToMasterContainer(tankName, tc, isClient);
    }

    private static void resetMaster(boolean isClient)
    {
        MASTER_TANK_CONTAINER.clear();
        if (KELDON_IS_DEBUGGING_TANKS_AGAIN_FFS) Globals.LogDebug("Cleared Master Tank Container", isClient);
    }

    private static void addToMasterContainer(String tankName, TankDataContainer tc, boolean isClient)
    {
        MASTER_TANK_CONTAINER.remove(tankName);
        MASTER_TANK_CONTAINER.put(tankName, tc);
        updateDataTag(tankName);
        if (KELDON_IS_DEBUGGING_TANKS_AGAIN_FFS) Globals.LogDebug("Put Tank Into Master Tank Container", isClient);
        if (!isClient)
            syncTankName(tankName);
    }

    public static String recoverTankName(BlockPos blockPos, Level level)
    {
        if (KELDON_IS_DEBUGGING_TANKS_AGAIN_FFS) Globals.LogDebug("Recovering Tank Name [" + blockPos + "]", level.isClientSide());
        Set<String> tankNames = MASTER_TANK_CONTAINER.keySet();
        for (String tankName : tankNames)
            if (getDataForTank(tankName).hasChild(blockPos))
                return tankName;
        if (KELDON_IS_DEBUGGING_TANKS_AGAIN_FFS) Globals.LogDebug("Tank Name for [" + blockPos + "] wasn't found.", level.isClientSide());
        return "";
    }

    public static void addChild(String tankName, BlockPos blockPos, boolean isClient)
    {
        getDataForTank(tankName).addChild(blockPos, isClient);
        updateDataTag(tankName);
    }

    public static void removeChild(String tankName, BlockPos blockPos, boolean isClient)
    {
        getDataForTank(tankName).removeChild(blockPos, isClient);
        updateDataTag(tankName);
    }

    private static void updateDataTag(String tankName)
    {
        if (TankDataTag.contains(tankName))
            TankDataTag.remove(tankName);
        CompoundTag nbt;
        if (!(nbt = TankDataPacker.getTankDataTag(tankName)).isEmpty())
            TankDataTag.put(tankName, nbt);
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
        if (KELDON_IS_DEBUGGING_TANKS_AGAIN_FFS) Globals.LogDebug("Synced Fluid For Tank [" + tankName + "] to Client", false);
    }

    public static boolean hasTankByName(String tankName)
    {
        if (tankName.isEmpty()) return false;
        return tankExists(tankName);
    }

    public static void syncTankName(String tankName, BlockPos blockPos)
    {
        CompoundTag tag = new CompoundTag();
        tag.putString("tank_id", tankName);
        PacketManager.sendToClients(new NBTSyncS2CPacket(blockPos, tag));
        if (KELDON_IS_DEBUGGING_TANKS_AGAIN_FFS) Globals.LogDebug("Synced Tank [" + tankName + "] @[" + blockPos + "] to Client", false);
    }

    public static void syncTankName(String tankName)
    {
        if (!tankName.isEmpty() && hasTankByName(tankName))
        {
            if (KELDON_IS_DEBUGGING_TANKS_AGAIN_FFS) Globals.LogDebug("Syncing Tank Children for [" + tankName + "] to Client", false);
            PacketManager.sendToClients(new TankDataSyncS2CPacket(tankName, TankDataTag.getCompound(tankName)));
            for (BlockPos blockPos : getDataForTank(tankName).getChildren())
                syncTankName(tankName, blockPos);
        }
    }

    public static int getTankTransferRate(String tank_id)
    {
        if (tank_id.isEmpty()) return 0;
        return getTankTier(tank_id) == 0 ? getDataForTank(tank_id).getTank().getCapacity() : getTankTier(tank_id) * BloodworksCommonConfig.TANK_TRANSFER_PER_ACTION.get();
    }

    public static void changeTier(String tank_id, int i, boolean isClient)
    { getDataForTank(tank_id).setTankTier(getTankTier(tank_id) + i, isClient); }

    public static void setTankTier(String tank_id, int newTier, boolean isClient)
    { getDataForTank(tank_id).setTankTier(newTier, isClient); }

    public static void updateLighting(String parentName)
    {
        for (BlockPos child : getDataForTank(parentName).getChildren())
        {
            Level level = ServerLifecycleHooks.getCurrentServer().overworld();
            if (level == null) continue;
            level.getLightEngine().checkBlock(child);
        }
    }
}