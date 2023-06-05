package com.bloodforge.bloodworks.Blocks.BlockEntities;

import com.bloodforge.bloodworks.Energy.EnergyBattery;
import com.bloodforge.bloodworks.Networking.NBTSyncS2CPacket;
import com.bloodforge.bloodworks.Networking.PacketManager;
import com.bloodforge.bloodworks.Registry.BlockRegistry;
import com.bloodforge.bloodworks.Util;
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

import java.lang.annotation.*;

import java.util.*;

public class BE_StirlingGenerator extends BlockEntity
{

    public float clientAnimTime = 0;

    public int targetEnergyGeneration;
    public int energyGeneration;
    public float energyGenerationF; //used to smoothly change energy level, using the int version causes lerp to stop short :(
    private final LazyOptional<IEnergyStorage> energy;
    public final EnergyBattery battery;

    public BE_StirlingGenerator(BlockPos pos, BlockState blockState)
    {
        super(BlockRegistry.BLOCK_STIRLING_GENERATOR.blockEntity().get(), pos, blockState);
        battery = new EnergyBattery(10000, 10000);
        energy = LazyOptional.of(() -> battery);
    }

    public static void tickServer(Level level, BlockPos blockPos, BlockState blockState, BE_StirlingGenerator gen) {
        //lerp temperature to give that authentic "hmmm yes we're chaning temperature" vibe
        gen.energyGenerationF = Util.Lerp(gen.energyGenerationF, gen.targetEnergyGeneration, 0.01f);
        gen.energyGeneration = Math.round(gen.energyGenerationF);
        gen.battery.generatePower(gen.energyGeneration);
        CompoundTag updateTag = new CompoundTag();
        updateTag.putInt("energyGen", gen.energyGeneration);
        updateTag.putFloat("energyGenF", gen.energyGenerationF);
        updateTag.put("energy", gen.battery.serializeNBT());
        PacketManager.sendToClients(new NBTSyncS2CPacket(blockPos, updateTag));
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
        if(cap == ForgeCapabilities.ENERGY && side != Direction.UP && side != Direction.DOWN) {
            return energy.cast();
        }
        return super.getCapability(cap, side);
    }
    @Override
    public CompoundTag getUpdateTag()
    {
        CompoundTag updateTag = new CompoundTag();
        return updateTag;
    }

    public void updateEnergyProduction(int newValue)
    {
        if (targetEnergyGeneration != newValue)
        {
            targetEnergyGeneration = newValue;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt)
    {
        super.saveAdditional(nbt);

        nbt.put("energy", battery.serializeNBT());
        //nbt.putInt("energyGen", energyGeneration); //don't load this, calculated.
    }

    @Override
    public void load(CompoundTag nbt)
    {
        super.load(nbt);

        Tag energyTag = nbt.get("energy");
        if(energyTag != null)
            battery.deserializeNBT(energyTag);

        //read by sync packet
        if(nbt.get("energyGen") != null) energyGeneration = nbt.getInt("energyGen");
        if(nbt.get("energyGenF") != null) energyGenerationF = nbt.getFloat("energyGenF");
    }
}