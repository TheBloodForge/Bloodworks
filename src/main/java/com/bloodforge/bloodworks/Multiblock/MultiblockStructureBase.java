package com.bloodforge.bloodworks.Multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class MultiblockStructureBase
{
    public boolean IsAtCoords(Level level, BlockPos minCorner, BlockPos maxCorner)
    {
        return false;
    }

    public BlockPos tryFindLastCornerWithFirstCorner(Level level, BlockPos minCorner, BlockPos minSize, BlockPos maxSize)
    {
        for (int xS = minSize.getX(); xS < maxSize.getX(); xS++)
        {
            for (int yS = minSize.getY(); yS < maxSize.getY(); yS++)
            {
                for (int zS = minSize.getZ(); zS < maxSize.getZ(); zS++)
                {
                    if (IsAtCoords(level, minCorner, new BlockPos(minCorner.getX() + xS, minCorner.getY() + yS, minCorner.getZ() + zS)))
                    {
                        return new BlockPos(minCorner.getX() + xS, minCorner.getY() + yS, minCorner.getZ() + zS);
                    }
                }
            }
        }
        return null;
    }
}