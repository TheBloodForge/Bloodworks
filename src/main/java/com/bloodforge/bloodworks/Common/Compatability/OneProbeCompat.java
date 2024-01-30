package com.bloodforge.bloodworks.Common.Compatability;

import com.bloodforge.bloodworks.Blocks.BlockEntities.BE_Battery;
import com.bloodforge.bloodworks.Blocks.BlockEntities.BE_StirlingGenerator;
import com.bloodforge.bloodworks.Globals;
import com.bloodforge.bloodworks.Registry.BlockRegistry;
import mcjty.theoneprobe.api.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public class OneProbeCompat implements IProbeInfoProvider, Function<ITheOneProbe, Void>
{
    @Override
    public ResourceLocation getID()
    {
        return new ResourceLocation(Globals.MODID, "data");
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, Player player, Level level, BlockState blockState, IProbeHitData iProbeHitData)
    {
        BlockPos pos = iProbeHitData.getPos();
        if(blockState.getBlock() == BlockRegistry.BLOCK_STIRLING_GENERATOR.block().get())
        {
            BE_StirlingGenerator be = (BE_StirlingGenerator) level.getBlockEntity(pos);
            if(be == null) return;
            iProbeInfo.text(Component.translatable("ui.bloodworks.currently_generating").append(": "+be.energyGeneration+" RF/t"));
            iProbeInfo.text(Component.translatable("ui.bloodworks.temp_difference").append(": "+be.currentTempDiffCelsius/4+" °C"));
            appendEnergyBar(iProbeInfo, be.battery.getStored(), be.battery.getCapacity());
        }
        else if(blockState.getBlock() == BlockRegistry.BLOCK_BATTERY.block().get())
        {
            BE_Battery be = (BE_Battery) level.getBlockEntity(pos);
            if(be == null) return;
            appendEnergyBar(iProbeInfo, be.battery.getStored(), be.battery.getCapacity());
        }
    }

    public void appendEnergyBar(IProbeInfo info, int stored, int capacity)
    {
        info.progress(stored, capacity,
                info.defaultProgressStyle()
                        .prefix(Component.translatable("ui.bloodworks.stored_energy").append(": "))
                        .width(150)
                        .suffix(" RF")
                        .color(Color.rgb(150,255,150), Color.rgb(80,255,80),Color.rgb(50,128,50))
        );
    }

    @Override
    public Void apply(ITheOneProbe oneProbe)
    {
        oneProbe.registerProvider(this);
        return null;
    }
}