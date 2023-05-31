package com.bloodforge.bloodworks.Server;

import com.bloodforge.bloodworks.Globals;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.server.ServerLifecycleHooks;

import static com.bloodforge.bloodworks.Globals.KELDON_IS_DEBUGGING_TANKS_AGAIN_FFS;
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
        TankDataManager data = create();
        data.tagLoadedFromWorld = tag;
        return data;
    }
    public static TankDataManager getDataManager(LevelAccessor level)
    {
        if (level.getServer() == null) return create();
        return level.getServer().overworld().getDataStorage().computeIfAbsent(TankDataManager::load, TankDataManager::create, "BloodworksTankData");
    }

    public static void saveData()
    { save(ServerLifecycleHooks.getCurrentServer().overworld()); }


    public static void read()
    { if (KELDON_IS_DEBUGGING_TANKS_AGAIN_FFS) Globals.LogDebug("Read Tank Data", false); read(ServerLifecycleHooks.getCurrentServer().overworld()); }

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
            for (String keyTag : TankDataTag.getAllKeys())
                syncFluid(keyTag);
        }
    }
}