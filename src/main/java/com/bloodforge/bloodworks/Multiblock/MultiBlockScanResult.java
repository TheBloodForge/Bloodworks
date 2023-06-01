package com.bloodforge.bloodworks.Multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;

public record MultiBlockScanResult(boolean OK, @Nullable BlockPos errorLocation, @Nullable BlockMask expectedBlocks,
                                   @Nullable HashMap<Block, ArrayList<BlockPos>> specialBlocks)
{
    @Override
    public String toString()
    {
        return "Structure failed to find a correct block at " + errorLocation.toShortString() + "! \nExpected: " + expectedBlocks.toString();
    }

    public boolean isOK()
    {
        return OK;
    }
}