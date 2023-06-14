package com.bloodforge.bloodworks.Blocks.BlockEntities;

import com.bloodforge.bloodworks.Energy.EnergyBattery;
import com.bloodforge.bloodworks.Networking.NBTSyncS2CPacket;
import com.bloodforge.bloodworks.Networking.PacketManager;
import com.bloodforge.bloodworks.Registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

public class BE_Battery extends BlockEntity
{
    private final LazyOptional<IEnergyStorage> energy;
    public final EnergyBattery battery;


    private int lastStoredEnergy = 0;

    public BE_Battery(BlockPos pos, BlockState blockState)
    {
        super(BlockRegistry.BLOCK_BATTERY.blockEntity().get(), pos, blockState);
        battery = new EnergyBattery(10000, 1000, 1000);
        energy = LazyOptional.of(() -> battery);
    }

    public static void tickServer(Level level, BlockPos blockPos, BlockState blockState, BE_Battery gen)
    {
        if (gen.battery.getStored() != gen.lastStoredEnergy)
        {
            CompoundTag updateTag = new CompoundTag();
            updateTag.put("energy", gen.battery.serializeNBT());
            PacketManager.sendToClients(new NBTSyncS2CPacket(blockPos, updateTag));
            gen.lastStoredEnergy = gen.battery.getStored();
        }
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ENERGY && (side == Direction.UP || side == Direction.DOWN))
            return energy.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        CompoundTag updateTag = new CompoundTag();
        return updateTag;
    }

    @Override
    protected void saveAdditional(CompoundTag nbt)
    {
        super.saveAdditional(nbt);

        nbt.put("energy", battery.serializeNBT());
    }

    @Override
    public void load(CompoundTag nbt)
    {
        super.load(nbt);

        Tag energyTag = nbt.get("energy");
        if(energyTag != null)
            battery.deserializeNBT(energyTag);
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }
}