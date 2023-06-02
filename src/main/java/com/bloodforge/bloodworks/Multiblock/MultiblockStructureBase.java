package com.bloodforge.bloodworks.Multiblock;

import com.ibm.icu.impl.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class MultiblockStructureBase
{
    public boolean IsAtCoords(Level level, BlockPos minCorner, BlockPos maxCorner)
    {
        return false;
    }

    public BlockPos tryFindLastCornerWithFirstCorner(Level level, BlockPos minCorner, BlockPos minSize, BlockPos maxSize, BlockMask validCornerBlocks)
    {
        for (int xS = minSize.getX()-1; xS < maxSize.getX(); xS++)
        {
            for (int yS = minSize.getY()-1; yS < maxSize.getY(); yS++)
            {
                for (int zS = minSize.getZ()-1; zS < maxSize.getZ(); zS++)
                {
                    BlockPos pos = new BlockPos(minCorner.getX() + xS, minCorner.getY() + yS, minCorner.getZ() + zS);
                    if (validCornerBlocks.Compare(level.getBlockState(pos)).OK() && IsAtCoords(level, minCorner, pos))
                    {
                        return new BlockPos(minCorner.getX() + xS, minCorner.getY() + yS, minCorner.getZ() + zS);
                    }
                }
            }
        }
        return null;
    }

    public Pair<BlockPos, BlockPos> tryFindMultiblock(Level level, BlockPos masterPos, BlockPos minSize, BlockPos maxSize, BlockMask validCornerBlocks)
    {
        Minecraft.getInstance().getProfiler().push("Bloodworks Multiblock Scan");
        BlockPos min;
        BlockPos max;
        //trying to find minimum corner, so only move -xyz
        for (int xS = masterPos.getX()-maxSize.getX(); xS <= masterPos.getX(); xS++)
        {
            for (int yS = masterPos.getY()-maxSize.getY(); yS <= masterPos.getY(); yS++)
            {
                for (int zS = masterPos.getZ()-maxSize.getZ(); zS <= masterPos.getZ(); zS++)
                {
                    min = new BlockPos(xS, yS, zS);
                    if (validCornerBlocks.Compare(level.getBlockState(min)).OK())
                    {
                        System.out.println("Found possible first corner: " + min);
                        if((max = tryFindLastCornerWithFirstCorner(level, min, minSize, maxSize, validCornerBlocks)) != null)
                        {
                            Minecraft.getInstance().getProfiler().pop();
                            return Pair.of(min, max);
                        }
                    }
                }
            }
        }
        Minecraft.getInstance().getProfiler().pop();
        return null;
    }
}