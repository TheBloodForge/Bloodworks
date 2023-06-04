package com.bloodforge.bloodworks.Blocks.Dummies;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.*;

public class DummyPipe
{
    public PipeType pipeType;

    public DummyPipe(PipeType type)
    {
        pipeType = type;
    }

    public DummyPipe walkPipe(PipeType type)
    {
        DummyPipe foundPipe = null; // might be list
        Direction[] dirs; // search directions make map<pos, dir[]> for all forks
        // do walking on connected blocks, following only provided type
        // step down valid paths, when fork returned, RNG a path
        return foundPipe;
    }

    private void step() // temp ver, will return dir[]
    {
        // get surrounding tiles only excluding previous tile AKA No going backwards
        // return only valid directions to continue walking
    }

    public Capability<?> getCapabilities()
    {
        return switch (pipeType)
        {
            case FLUID -> ForgeCapabilities.FLUID_HANDLER;
            case ITEM -> ForgeCapabilities.ITEM_HANDLER;
            case GAS, APPLIED_ENERGISTICS -> CapabilityManager.get(new CapabilityToken<>(){});
            case REDSTONE, LOGIC -> null;
            case ENERGY -> ForgeCapabilities.ENERGY;
        };
    }
}