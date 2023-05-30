package com.bloodforge.bloodworks.Server;

import com.bloodforge.bloodworks.Globals;
import com.bloodforge.bloodworks.Networking.PacketManager;
import com.bloodforge.bloodworks.Networking.TankNameSyncS2CPacket;
import com.bloodforge.bloodworks.Networking.TankSyncS2CPacket;
import com.bloodforge.bloodworks.Networking.UpdateTankS2CPacket;
import com.bloodforge.bloodworks.Registry.BlockRegistry;
import com.bloodforge.bloodworks.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.*;

@SuppressWarnings("resource")
public class TankData extends SavedData
{
    public static final HashMap<String, ArrayList<BlockPos>> TANK_CHILDREN = new HashMap<>();
    public static final HashMap<String, int[]> TANK_DATA = new HashMap<>();
    public static final HashMap<String, FluidTank> EXISTING_TANKS = new HashMap<>();
    CompoundTag tagLoadedFromWorld = new CompoundTag();
    public static CompoundTag TankDataTag = new CompoundTag();

    public TankData()
    {
        super();
    }

    public static TankData create()
    {
        return new TankData();
    }

    public static CompoundTag wrapChildren(String parentName)
    {
        if (TANK_CHILDREN.isEmpty() || parentName.isEmpty() || !TANK_CHILDREN.containsKey(parentName))
            return new CompoundTag();
        CompoundTag childrenTags = new CompoundTag();
        for (BlockPos child : TANK_CHILDREN.get(parentName))
            childrenTags.putIntArray(child.toShortString(), Util.getBlockPosAsIntArr(child));
        return childrenTags;
    }

    public static void unwrapChildren(String parentName, CompoundTag childrenTag)
    {
        System.out.println("Unwrapping Children for [" + parentName + "] size = " + childrenTag.getAllKeys().size());
        for (String childPos : childrenTag.getAllKeys())
        {
            int[] childPosAsIntArr = childrenTag.getIntArray(childPos);
            if (childPosAsIntArr.length == 3)
                addChild(parentName, new BlockPos(childPosAsIntArr[0], childPosAsIntArr[1], childPosAsIntArr[2]));
        }
    }

    public static void saveTankToWorld(String parentName, Level level)
    {
        validateChildren(parentName, level);
        if (!hasTankByName(parentName) || !TANK_CHILDREN.containsKey(parentName) || TANK_CHILDREN.get(parentName).isEmpty())
            return;
        CompoundTag tankTag = new CompoundTag();
        CompoundTag localTankTag = new CompoundTag();
        localTankTag = getTankByName(parentName).writeToNBT(localTankTag);
        localTankTag.put("children", wrapChildren(parentName));
        localTankTag.putInt("tankTier", getTankTier(parentName));
        localTankTag.putIntArray("tankData", TANK_DATA.get(parentName));
        tankTag.put(parentName, localTankTag);
        TankDataTag.remove(parentName);
        TankDataTag.put(parentName, localTankTag);
        if (level != null)
            save(level);
    }

    private static void validateChildren(String parentName, Level level)
    {
        if (!TANK_CHILDREN.containsKey(parentName)) return;
        for (BlockPos blockPos : Collections.unmodifiableList(TANK_CHILDREN.get(parentName)))
        {
            if (!level.getBlockState(blockPos).getBlock().equals(BlockRegistry.BLOCK_BLOOD_TANK.block().get()))
                removeChild(parentName, blockPos);
        }
    }

    public static void saveTanksToWorld(Level level)
    {
        if (TankDataTag == null || TankDataTag.getAllKeys().isEmpty())
            return;
        for (String tankName : new ArrayList<>(TankDataTag.getAllKeys()))
            if (!tankName.isEmpty())
                saveTankToWorld(tankName, level);
    }

    public static String recoverTankName(BlockPos blockPos)
    {
        Set<String> tankNames = TankDataTag.getAllKeys();
        for (String tankName : tankNames)
        {
            if (!TANK_CHILDREN.containsKey(tankName)) continue;
            List<BlockPos> children = TANK_CHILDREN.get(tankName);
            for (BlockPos child : children)
                if (Util.isBlockPosSame(blockPos, child))
                    return tankName;
        }
        return "";
    }

    public void save()
    {
        this.setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag tag)
    {
        return tagLoadedFromWorld;
    }

    public static TankData load(CompoundTag tag)
    {
        TankData data = create();
        data.tagLoadedFromWorld = tag;
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
            data.tagLoadedFromWorld = TankDataTag;
            data.save();
        }
    }

    public static void read(LevelAccessor level)
    {
        if (level != null && !level.isClientSide())
        {
            TankData data = TankData.getDataManager(level);
            TankDataTag = data.tagLoadedFromWorld;
            for (String keyTag : TankDataTag.getAllKeys())
                syncFluid(keyTag);
        }
    }

    public static void addChild(String parentName, BlockPos blockPos)
    {
        if (!TANK_CHILDREN.containsKey(parentName)) TANK_CHILDREN.put(parentName, new ArrayList<>());
        System.out.println("Adding child to [" + parentName + "]");
        TANK_CHILDREN.get(parentName).add(blockPos);
        updateTankCapacity(parentName);
        if (Minecraft.getInstance().level != null && !Minecraft.getInstance().level.isClientSide())
            PacketManager.sendToClients(new UpdateTankS2CPacket(parentName, blockPos, true));
    }

    public static void removeChild(String parentName, BlockPos blockPos)
    {
        if (!TANK_CHILDREN.containsKey(parentName)) return;
        TANK_CHILDREN.get(parentName).remove(blockPos);
        updateTankCapacity(parentName);
        if (TANK_CHILDREN.get(parentName).isEmpty()) deleteTank(parentName);
        if (Minecraft.getInstance().level != null && !Minecraft.getInstance().level.isClientSide())
            PacketManager.sendToClients(new UpdateTankS2CPacket(parentName, blockPos, false));
    }

    private static void deleteTank(String parentName)
    {
        TANK_CHILDREN.remove(parentName);
        TANK_DATA.remove(parentName);
        EXISTING_TANKS.remove(parentName);
    }

    public static FluidTank getTankByName(String tankName)
    {
        if (!EXISTING_TANKS.containsKey(tankName))
            EXISTING_TANKS.put(tankName, makeTank(tankName));
        updateTankCapacity(tankName);
        return EXISTING_TANKS.get(tankName);
    }

    private static void updateTankCapacity(String tankName)
    {
        if (hasTankByName(tankName))
            EXISTING_TANKS.get(tankName).setCapacity(TANK_CHILDREN.get(tankName).size() * Globals.DEFAULT_TANK_CAPACITY);
        if (Minecraft.getInstance().level != null)
            save(Minecraft.getInstance().level);
    }

    public static String createNewParent(BlockPos pos)
    {
//        if (Minecraft.getInstance().level == null || Minecraft.getInstance().level.isClientSide) return "";
        String parentName = "TCT-" + (System.currentTimeMillis() - 1685405500000L);
        TankData.TANK_CHILDREN.put(parentName, new ArrayList<>(List.of(pos)));
        TankData.TANK_DATA.put(parentName, new int[]{1, pos.getY(), pos.getY(), 1});
        TankData.EXISTING_TANKS.put(parentName, makeTank(parentName));
        return parentName;
    }

    public static int getTankTier(String tankName)
    {
        if (!TANK_DATA.containsKey(tankName) || TANK_DATA.get(tankName).length != 4) return 0;
        return TankData.TANK_DATA.get(tankName)[3];
    }

    public static int getTankMin(String tankName)
    {
        if (!TANK_DATA.containsKey(tankName) || TANK_DATA.get(tankName).length != 4)
        {
            recalculateTankData(tankName);
            return 0;
        }
        return TankData.TANK_DATA.get(tankName)[1];
    }

    private static void recalculateTankData(String tankName)
    {
        List<BlockPos> children = TANK_CHILDREN.get(tankName);
        if (children == null || children.isEmpty()) return;
        int size = 1, min = 1, max = 1, tier = 1;
        for (BlockPos child : children)
        {
            if (child.getY() > max) max = child.getY();
            if (child.getY() < min) min = child.getY();
        }
        size = max - min;
        TANK_DATA.replace(tankName, new int[]{size, min, max, tier});
    }

    public static int getTankMax(String tankName)
    {
        if (!TANK_DATA.containsKey(tankName) || TANK_DATA.get(tankName).length != 4) return 0;
        return TankData.TANK_DATA.get(tankName)[2];
    }

    public static int getTankHeight(String tankName)
    {
        if (!TANK_DATA.containsKey(tankName) || TANK_DATA.get(tankName).length != 4) return 0;
        return TankData.TANK_DATA.get(tankName)[0];
    }

    public static FluidTank makeTank(String tankName)
    {
        return new FluidTank(Globals.DEFAULT_TANK_CAPACITY)
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
        if (TankDataTag.getAllKeys().contains(parentName))
        {
            if (!EXISTING_TANKS.containsKey(parentName))
            {
                System.out.println("Tank for [" + parentName + "] is empty");
                FluidTank tank = makeTank(parentName);
                tank.readFromNBT(TankDataTag.getCompound(parentName));
                EXISTING_TANKS.put(parentName, tank);
            }
            if (!TANK_DATA.containsKey(parentName))
            {
                System.out.println("Tank Data for [" + parentName + "] is empty");
                TANK_DATA.put(parentName, TankDataTag.getCompound(parentName).getIntArray("tankData"));
                if (TANK_DATA.get(parentName).length != 4)
                    recalculateTankData(parentName);
            }
            if (!TANK_CHILDREN.containsKey(parentName))
            {
                System.out.println("Tank Children for [" + parentName + "] is empty");
                unwrapChildren(parentName, TankDataTag.getCompound(parentName).getCompound("children"));
            }
        }
        return EXISTING_TANKS.containsKey(parentName) && TANK_DATA.containsKey(parentName) && TANK_CHILDREN.containsKey(parentName);
    }

    public static void syncTankName(BlockPos blockPos, String parentName)
    {
        if (!parentName.isEmpty())
            PacketManager.sendToClients(new TankNameSyncS2CPacket(parentName, blockPos));
    }

    public static void syncTankName(String parentName)
    {
        if (!parentName.isEmpty())
            for (BlockPos blockPos : TANK_CHILDREN.get(parentName))
                PacketManager.sendToClients(new TankNameSyncS2CPacket(parentName, blockPos));
    }
}