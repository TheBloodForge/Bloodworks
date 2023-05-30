package com.bloodforge.bloodworks.Multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;

public class StructureDetectionUtils
{
    public static BlockPos getMinCorner(BlockPos corner1, BlockPos corner2)
    {
        return new BlockPos(
                Math.min(corner1.getX(), corner2.getX()),
                Math.min(corner1.getY(), corner2.getY()),
                Math.min(corner1.getZ(), corner2.getZ())
        );
    }
    public static BlockPos getMaxCorner(BlockPos corner1, BlockPos corner2)
    {
        return new BlockPos(
                Math.max(corner1.getX(), corner2.getX()),
                Math.max(corner1.getY(), corner2.getY()),
                Math.max(corner1.getZ(), corner2.getZ())
        );
    }

    private static void tryAddSpecialBlock(Level level, BlockMask blockMask, BlockPos pos, HashMap<Block, ArrayList<BlockPos>> specialBlocks)
    {
        BlockState s = level.getBlockState(pos);
        if(blockMask.isSpecialBlock(s))
        {
            if(!specialBlocks.containsKey(s.getBlock()))
            {
                specialBlocks.put(s.getBlock(), new ArrayList<>());
            }
            specialBlocks.get(s.getBlock()).add(pos);
        }
    }

    public static boolean blockMatches(Level level, BlockMask blockMask, BlockPos pos)
    {
        return blockMask.DoesBlockFitMask(level.getBlockState(pos));
    }

    public static MultiBlockScanResult isCuboidOf(Level level, BlockMask blockMask, HashMap<Block, ArrayList<BlockPos>> specialBlocks, BlockPos corner1, BlockPos corner2)
    {
        BlockPos cornerLow = getMinCorner(corner1, corner2);
        BlockPos cornerHigh = getMaxCorner(corner1, corner2);
        for(int x = cornerLow.getX(); x < cornerHigh.getX();  x++)
        {
            for(int y = cornerLow.getY(); y < cornerHigh.getY();  y++)
            {
                for(int z = cornerLow.getZ(); z < cornerHigh.getZ();  z++)
                {
                    BlockPos pos = new BlockPos(x, y, z);
                    tryAddSpecialBlock(level, blockMask, pos, specialBlocks);
                    if(!blockMatches(level, blockMask, pos))
                    {
                        return new MultiBlockScanResult(false, pos, blockMask, null);
                    }
                }
            }
        }
        return new MultiBlockScanResult(true, null, null, null);
    }



    public static MultiBlockScanResult isWallsOf(Level level, BlockMask blockMask, HashMap<Block, ArrayList<BlockPos>> specialBlocks, BlockPos corner1, BlockPos corner2)
    {
        BlockPos cornerLow = getMinCorner(corner1, corner2);
        BlockPos cornerHigh = getMaxCorner(corner1, corner2);
        for(int y = cornerLow.getY(); y < cornerHigh.getY();  y++)
        {
            for(int x = cornerLow.getX(); x < cornerHigh.getX();  x++)
            {
                if(x != cornerLow.getX() && x != cornerHigh.getX()) continue;
                for(int z = cornerLow.getZ(); z < cornerHigh.getZ();  z++)
                {
                    if(z != cornerLow.getZ() && z != cornerHigh.getZ()) continue;
                    BlockPos pos = new BlockPos(x, y, z);
                    tryAddSpecialBlock(level, blockMask, pos, specialBlocks);
                    if(!blockMatches(level, blockMask, pos))
                    {
                        return new MultiBlockScanResult(false, pos, blockMask, null);
                    }
                }
            }
        }
        return new MultiBlockScanResult(true, null, null, null);
    }

    public static MultiBlockScanResult isHollowCuboidOf(Level level, BlockMask blockMask, HashMap<Block, ArrayList<BlockPos>> specialBlocks, BlockPos corner1, BlockPos corner2)
    {
        BlockPos cornerLow = getMinCorner(corner1, corner2);
        BlockPos cornerHigh = getMaxCorner(corner1, corner2);
        for(int y = cornerLow.getY(); y < cornerHigh.getY();  y++)
        {
            for(int x = cornerLow.getX(); x < cornerHigh.getX();  x++)
            {
                for(int z = cornerLow.getZ(); z < cornerHigh.getZ();  z++)
                {
                    if ((y != cornerLow.getY() && y != cornerHigh.getY()) &&
                        (x != cornerLow.getX() && x != cornerHigh.getX()) &&
                        (z != cornerLow.getZ() && z != cornerHigh.getZ())) continue;
                    BlockPos pos = new BlockPos(x, y, z);
                    tryAddSpecialBlock(level, blockMask, pos, specialBlocks);
                    if (!blockMatches(level, blockMask, pos)) {
                        return new MultiBlockScanResult(false, pos, blockMask, null);
                    }
                }
            }
        }
        return new MultiBlockScanResult(true, null, null, null);
    }

    public static MultiBlockScanResult isFacesOf(Level level, BlockMask blockMask, HashMap<Block, ArrayList<BlockPos>> specialBlocks, BlockPos corner1, BlockPos corner2)
    {
        BlockPos cornerLow = getMinCorner(corner1, corner2);
        BlockPos cornerHigh = getMaxCorner(corner1, corner2);
        for(int y = cornerLow.getY(); y < cornerHigh.getY();  y++)
        {
            for(int x = cornerLow.getX(); x < cornerHigh.getX();  x++)
            {
                for(int z = cornerLow.getZ(); z < cornerHigh.getZ();  z++)
                {
                    if( (x == cornerLow.getX() || x == cornerHigh.getX()) &&
                        (y == cornerLow.getY() || y == cornerHigh.getY()) &&
                        (z == cornerLow.getZ() || z == cornerHigh.getZ()) ) continue;
                    BlockPos pos = new BlockPos(x, y, z);
                    tryAddSpecialBlock(level, blockMask, pos, specialBlocks);
                    if(!blockMatches(level, blockMask, pos))
                    {
                        return new MultiBlockScanResult(false, pos, blockMask, null);
                    }
                }
            }
        }
        return new MultiBlockScanResult(true, null, null, null);
    }

    public static MultiBlockScanResult isEdgesOf(Level level, BlockMask blockMask, HashMap<Block, ArrayList<BlockPos>> specialBlocks, BlockPos corner1, BlockPos corner2)
    {
        BlockPos cornerLow = getMinCorner(corner1, corner2);
        BlockPos cornerHigh = getMaxCorner(corner1, corner2);
        for(int y = cornerLow.getY(); y < cornerHigh.getY();  y++)
        {
            for(int x = cornerLow.getX(); x < cornerHigh.getX();  x++)
            {
                for(int z = cornerLow.getZ(); z < cornerHigh.getZ();  z++)
                {
                    if ((x != cornerLow.getX() && x != cornerHigh.getX()) ||
                            (y != cornerLow.getY() && y != cornerHigh.getY()) ||
                            (z != cornerLow.getZ() && z != cornerHigh.getZ())) continue;
                    BlockPos pos = new BlockPos(x, y, z);
                    tryAddSpecialBlock(level, blockMask, pos, specialBlocks);
                    if (!blockMatches(level, blockMask, pos)) {
                        return new MultiBlockScanResult(false, pos, blockMask, null);
                    }
                }
            }
        }
        return new MultiBlockScanResult(true, null, null, null);
    }
    public static MultiBlockScanResult isCornersOf(Level level, BlockMask blockMask, HashMap<Block, ArrayList<BlockPos>> specialBlocks, BlockPos corner1, BlockPos corner2)
    {
        BlockPos cornerLow = getMinCorner(corner1, corner2);
        BlockPos cornerHigh = getMaxCorner(corner1, corner2);

        if (!blockMatches(level, blockMask, cornerHigh))
        {
            tryAddSpecialBlock(level, blockMask, cornerHigh, specialBlocks);
            return new MultiBlockScanResult(false, cornerHigh, blockMask, null);
        }
        if (!blockMatches(level, blockMask, cornerHigh))
        {
            tryAddSpecialBlock(level, blockMask, cornerLow, specialBlocks);
            return new MultiBlockScanResult(false, cornerHigh, blockMask, null);
        }
        BlockPos toCheck = new BlockPos(cornerLow.getX(), cornerLow.getY(), cornerHigh.getZ());
        if (!blockMatches(level, blockMask, toCheck))
        {
            tryAddSpecialBlock(level, blockMask, toCheck, specialBlocks);
            return new MultiBlockScanResult(false, toCheck, blockMask, null);
        }
        toCheck = new BlockPos(cornerLow.getX(), cornerHigh.getY(), cornerLow.getZ());
        if (!blockMatches(level, blockMask, toCheck))
        {
            tryAddSpecialBlock(level, blockMask, toCheck, specialBlocks);
            return new MultiBlockScanResult(false, toCheck, blockMask, null);
        }
        toCheck = new BlockPos(cornerLow.getX(), cornerHigh.getY(), cornerHigh.getZ());
        if (!blockMatches(level, blockMask, toCheck))
        {
            tryAddSpecialBlock(level, blockMask, toCheck, specialBlocks);
            return new MultiBlockScanResult(false, toCheck, blockMask, null);
        }
        toCheck = new BlockPos(cornerHigh.getX(), cornerLow.getY(), cornerLow.getZ());
        if (!blockMatches(level, blockMask, toCheck))
        {
            tryAddSpecialBlock(level, blockMask, toCheck, specialBlocks);
            return new MultiBlockScanResult(false, toCheck, blockMask, null);
        }
        toCheck = new BlockPos(cornerHigh.getX(), cornerLow.getY(), cornerHigh.getZ());
        if (!blockMatches(level, blockMask, toCheck))
        {
            tryAddSpecialBlock(level, blockMask, toCheck, specialBlocks);
            return new MultiBlockScanResult(false, toCheck, blockMask, null);
        }
        toCheck = new BlockPos(cornerHigh.getX(), cornerHigh.getY(), cornerLow.getZ());
        if (!blockMatches(level, blockMask, toCheck))
        {
            tryAddSpecialBlock(level, blockMask, toCheck, specialBlocks);
            return new MultiBlockScanResult(false, toCheck, blockMask, null);
        }

        return new MultiBlockScanResult(true, null, null, null);
    }

    public static Object scanRoomWithEdgeCornerRequirements(Level level, BlockMask faces, BlockMask edges, BlockMask corner, BlockMask specialsAnywhere, BlockPos corner1, BlockPos corner2)
    {
        BlockPos cornerLow = getMinCorner(corner1, corner2);
        BlockPos cornerHigh = getMaxCorner(corner1, corner2);

        HashMap<Block, ArrayList<BlockPos>> specialBlocksFaces = new HashMap<>();
        HashMap<Block, ArrayList<BlockPos>> specialBlocksEdges = new HashMap<>();
        HashMap<Block, ArrayList<BlockPos>> specialBlocksCorners = new HashMap<>();

        MultiBlockScanResult r;
        SpecialBlockFindResult s;
        if(!(r = isFacesOf  (level,  faces, specialBlocksFaces, cornerLow, cornerHigh)).isOK()) return r;
        if(!(s = faces.areSpecialsSatisfiedBy(specialBlocksFaces)).isOK()) return s;

        if(!(r = isEdgesOf  (level,  edges, specialBlocksEdges, cornerLow, cornerHigh)).isOK()) return r;
        if(!(s = edges.areSpecialsSatisfiedBy(specialBlocksEdges)).isOK()) return s;

        if(!(r = isCornersOf(level, corner, specialBlocksCorners, cornerLow, cornerHigh)).isOK()) return r;
        if(!(s = corner.areSpecialsSatisfiedBy(specialBlocksCorners)).isOK()) return s;

        HashMap<Block, ArrayList<BlockPos>> specialBlocks = new HashMap<>();
        //merge lists down
        specialBlocksFaces.forEach((key, val)->{ if(!specialBlocks.containsKey(key)) specialBlocks.put(key, new ArrayList<>()); val.forEach((a) -> specialBlocks.get(key).add(a)); });
        specialBlocksEdges.forEach((key, val)->{ if(!specialBlocks.containsKey(key)) specialBlocks.put(key, new ArrayList<>()); val.forEach((a) -> specialBlocks.get(key).add(a)); });
        specialBlocksCorners.forEach((key, val)->{ if(!specialBlocks.containsKey(key)) specialBlocks.put(key, new ArrayList<>()); val.forEach((a) -> specialBlocks.get(key).add(a)); });
        if(!(s =specialsAnywhere.areSpecialsSatisfiedBy(specialBlocks)).isOK()) return s;
        //past this point, multiblock is OK as far as masks go. Check for specials

        return new MultiBlockScanResult(true, null, null, specialBlocks);
    }
}
