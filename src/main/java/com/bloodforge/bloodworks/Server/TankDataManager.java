package com.bloodforge.bloodworks.Server;

import com.bloodforge.bloodworks.Globals;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.server.ServerLifecycleHooks;

import static com.bloodforge.bloodworks.Globals.DEBUG_TANKS;
import static com.bloodforge.bloodworks.Server.TankDataProxy.TankDataTag;
import static com.bloodforge.bloodworks.Server.TankDataProxy.syncFluid;

public class TankDataManager extends SavedData
{
    CompoundTag tagLoadedFromWorld = new CompoundTag();
    public TankDataManager()
    {
        super();
    }

    public static TankDataManager create()
    {
        return new TankDataManager();
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

    public static TankDataManager load(CompoundTag tag)
    {
        if (DEBUG_TANKS) Globals.LogDebug("Loading Compound : " + tag.getAllKeys(), false);
        TankDataManager data = create();
        data.tagLoadedFromWorld = tag;
        return data;
    }
    public static TankDataManager getDataManager(LevelAccessor level)
    {
        if (level.getServer() == null)
        {
            if (DEBUG_TANKS) Globals.LogDebug("Level Server is Null", false);
            return create();
        }
        return level.getServer().overworld().getDataStorage().computeIfAbsent(TankDataManager::load, TankDataManager::create, "BloodworksTankData");
    }

    public static void saveData()
    { save(ServerLifecycleHooks.getCurrentServer().overworld()); }


    public static void read()
    { if (DEBUG_TANKS) Globals.LogDebug("Read Tank Data", false); read(ServerLifecycleHooks.getCurrentServer().overworld()); }

    private static void save(LevelAccessor level)
    {
        if (!level.isClientSide())
        {
            TankDataManager data = TankDataManager.getDataManager(level);
            data.tagLoadedFromWorld = TankDataTag;
            data.save();
        }
    }

    private static void read(LevelAccessor level)
    {
        if (level != null && !level.isClientSide())
        {
            TankDataManager data = TankDataManager.getDataManager(level);
            TankDataTag = data.tagLoadedFromWorld;
            if (DEBUG_TANKS) Globals.LogDebug("Read Tanks from file", false);
            for (String keyTag : TankDataTag.getAllKeys())
                syncFluid(keyTag);
        }
    }
}