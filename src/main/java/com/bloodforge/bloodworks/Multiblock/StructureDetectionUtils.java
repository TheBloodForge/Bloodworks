package com.bloodforge.bloodworks.Multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;

public class StructureDetectionUtils
{
    //#################################################
    //              Utility Functions
    //#################################################
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

    public static boolean blockMatches(Level level, BlockMask blockMask, BlockPos pos, HashMap<Block, ArrayList<BlockPos>> specialBlocks) {
        BlockState blockState = level.getBlockState(pos);
        //System.out.println(pos.toShortString() + "- " + blockState.getBlock());
        BlockMask.BlockMaskCompareResult res = blockMask.Compare(blockState);
        if (res.isRequired() || res.isSpecial())
        {
            if (!specialBlocks.containsKey(blockState.getBlock()))
            {
                specialBlocks.put(blockState.getBlock(), new ArrayList<>());
            }
            specialBlocks.get(blockState.getBlock()).add(pos);
            System.out.println("Added special block: "+blockState.getBlock().getName());
        }
        return res.OK();
    }

    //#################################################
    //     Generic Shape-scan Helpers, not currently in use
    //#################################################

    public static MultiBlockScanResult isCuboidOf(Level level, BlockMask blockMask, HashMap<Block, ArrayList<BlockPos>> specialBlocks, BlockPos corner1, BlockPos corner2)
    {
        BlockPos cornerLow = getMinCorner(corner1, corner2);
        BlockPos cornerHigh = getMaxCorner(corner1, corner2);
        for (int x = cornerLow.getX(); x < cornerHigh.getX(); x++)
        {
            for (int y = cornerLow.getY(); y < cornerHigh.getY(); y++)
            {
                for (int z = cornerLow.getZ(); z < cornerHigh.getZ(); z++)
                {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (!blockMatches(level, blockMask, pos, specialBlocks))
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
        for (int y = cornerLow.getY(); y < cornerHigh.getY(); y++)
        {
            for (int x = cornerLow.getX(); x < cornerHigh.getX(); x++)
            {
                if (x != cornerLow.getX() && x != cornerHigh.getX()) continue;
                for (int z = cornerLow.getZ(); z < cornerHigh.getZ(); z++)
                {
                    if (z != cornerLow.getZ() && z != cornerHigh.getZ()) continue;
                    BlockPos pos = new BlockPos(x, y, z);
                    if (!blockMatches(level, blockMask, pos, specialBlocks))
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
        for (int y = cornerLow.getY(); y < cornerHigh.getY(); y++)
        {
            for (int x = cornerLow.getX(); x < cornerHigh.getX(); x++)
            {
                for (int z = cornerLow.getZ(); z < cornerHigh.getZ(); z++)
                {
                    if ((y != cornerLow.getY() && y != cornerHigh.getY()) &&
                            (x != cornerLow.getX() && x != cornerHigh.getX()) &&
                            (z != cornerLow.getZ() && z != cornerHigh.getZ())) continue;
                    BlockPos pos = new BlockPos(x, y, z);
                    if (!blockMatches(level, blockMask, pos, specialBlocks))
                    {
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
        for (int y = cornerLow.getY(); y < cornerHigh.getY(); y++)
        {
            for (int x = cornerLow.getX(); x < cornerHigh.getX(); x++)
            {
                for (int z = cornerLow.getZ(); z < cornerHigh.getZ(); z++)
                {
                    int edgeIntersectionCount = 0;
                    if(x == cornerLow.getX() || x == cornerHigh.getX()) edgeIntersectionCount++;
                    if(y == cornerLow.getY() || y == cornerHigh.getY()) edgeIntersectionCount++;
                    if(z == cornerLow.getZ() || z == cornerHigh.getZ()) edgeIntersectionCount++;
                    if(edgeIntersectionCount != 1) continue;
                    BlockPos pos = new BlockPos(x, y, z);
                    if (!blockMatches(level, blockMask, pos, specialBlocks)) {
                        return new MultiBlockScanResult(false, pos, blockMask, null);
                    }
                }
            }
        }
        return new MultiBlockScanResult(true, null, null, null);
    }
    public static MultiBlockScanResult isInteriorOf(Level level, BlockMask blockMask, HashMap<Block, ArrayList<BlockPos>> specialBlocks, BlockPos corner1, BlockPos corner2)
    {
        BlockPos cornerLow = getMinCorner(corner1, corner2);
        BlockPos cornerHigh = getMaxCorner(corner1, corner2);
        for (int y = cornerLow.getY(); y < cornerHigh.getY(); y++)
        {
            for (int x = cornerLow.getX(); x < cornerHigh.getX(); x++)
            {
                for (int z = cornerLow.getZ(); z < cornerHigh.getZ(); z++)
                {
                    int edgeIntersectionCount = 0;

                    if(x == cornerLow.getX() || x == cornerHigh.getX()) edgeIntersectionCount++;
                    if(y == cornerLow.getY() || y == cornerHigh.getY()) edgeIntersectionCount++;
                    if(z == cornerLow.getZ() || z == cornerHigh.getZ()) edgeIntersectionCount++;
                    if(edgeIntersectionCount != 0) continue;
                    BlockPos pos = new BlockPos(x, y, z);
                    if (!blockMatches(level, blockMask, pos, specialBlocks))
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
        for (int y = cornerLow.getY(); y < cornerHigh.getY(); y++)
        {
            for (int x = cornerLow.getX(); x < cornerHigh.getX(); x++)
            {
                for (int z = cornerLow.getZ(); z < cornerHigh.getZ(); z++)
                {
                    int edgeIntersectionCount = 0;

                    if(x == cornerLow.getX() || x == cornerHigh.getX()) edgeIntersectionCount++;
                    if(y == cornerLow.getY() || y == cornerHigh.getY()) edgeIntersectionCount++;
                    if(z == cornerLow.getZ() || z == cornerHigh.getZ()) edgeIntersectionCount++;
                    if(edgeIntersectionCount != 2) continue;
                    BlockPos pos = new BlockPos(x, y, z);
                    if (!blockMatches(level, blockMask, pos, specialBlocks))
                    {
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

        if (!blockMatches(level, blockMask, cornerHigh, specialBlocks))
        {
            return new MultiBlockScanResult(false, cornerHigh, blockMask, null);
        }
        if (!blockMatches(level, blockMask, cornerHigh, specialBlocks))
        {
            return new MultiBlockScanResult(false, cornerHigh, blockMask, null);
        }
        BlockPos toCheck = new BlockPos(cornerLow.getX(), cornerLow.getY(), cornerHigh.getZ());
        if (!blockMatches(level, blockMask, toCheck, specialBlocks))
        {
            return new MultiBlockScanResult(false, toCheck, blockMask, null);
        }
        toCheck = new BlockPos(cornerLow.getX(), cornerHigh.getY(), cornerLow.getZ());
        if (!blockMatches(level, blockMask, toCheck, specialBlocks))
        {
            return new MultiBlockScanResult(false, toCheck, blockMask, null);
        }
        toCheck = new BlockPos(cornerLow.getX(), cornerHigh.getY(), cornerHigh.getZ());
        if (!blockMatches(level, blockMask, toCheck, specialBlocks))
        {
            return new MultiBlockScanResult(false, toCheck, blockMask, null);
        }
        toCheck = new BlockPos(cornerHigh.getX(), cornerLow.getY(), cornerLow.getZ());
        if (!blockMatches(level, blockMask, toCheck, specialBlocks))
        {
            return new MultiBlockScanResult(false, toCheck, blockMask, null);
        }
        toCheck = new BlockPos(cornerHigh.getX(), cornerLow.getY(), cornerHigh.getZ());
        if (!blockMatches(level, blockMask, toCheck, specialBlocks))
        {
            return new MultiBlockScanResult(false, toCheck, blockMask, null);
        }
        toCheck = new BlockPos(cornerHigh.getX(), cornerHigh.getY(), cornerLow.getZ());
        if (!blockMatches(level, blockMask, toCheck, specialBlocks))
        {
            return new MultiBlockScanResult(false, toCheck, blockMask, null);
        }

        return new MultiBlockScanResult(true, null, null, null);
    }


    //#################################################
    //                smart-scan cuboid
    //#################################################
    public static MultiBlockScanResult doComplexCuboidScan(Level level, BlockPos corner1, BlockPos corner2,
           BlockMask walls, BlockMask floor, BlockMask ceil, BlockMask edges, BlockMask corners, BlockMask inside, HashMap<Block, ArrayList<BlockPos>> specialBlocks)
    {
        BlockPos cornerLow = getMinCorner(corner1, corner2);
        BlockPos cornerHigh = getMaxCorner(corner1, corner2);
        for (int y = cornerLow.getY(); y <= cornerHigh.getY(); y++)
        {
            for (int x = cornerLow.getX(); x <= cornerHigh.getX(); x++)
            {
                for (int z = cornerLow.getZ(); z <= cornerHigh.getZ(); z++)
                {
                    int edgeIntersectionCount = 0;

                    if(x == cornerLow.getX() || x == cornerHigh.getX()) edgeIntersectionCount++;
                    if(y == cornerLow.getY() || y == cornerHigh.getY()) edgeIntersectionCount++;
                    if(z == cornerLow.getZ() || z == cornerHigh.getZ()) edgeIntersectionCount++;
                    BlockPos pos = new BlockPos(x, y, z);
                    if(edgeIntersectionCount == 0)
                    {
                        //inside
                        if (!blockMatches(level, inside, pos, specialBlocks))
                        {
                            return new MultiBlockScanResult(false, pos, inside, null);
                        }
                    }
                    else if(edgeIntersectionCount == 1)
                    {
                        //floor
                        if(y == cornerLow.getY())
                        {
                            if(!blockMatches(level, floor, pos, specialBlocks))
                                return new MultiBlockScanResult(false, pos, floor, null);
                        }
                        //ceiling
                        else if(y == cornerHigh.getY())
                        {
                            if(!blockMatches(level, ceil, pos, specialBlocks))
                                return new MultiBlockScanResult(false, pos, ceil, null);
                        }
                        //wall
                        else
                        {
                            if (!blockMatches(level, walls, pos, specialBlocks))
                                return new MultiBlockScanResult(false, pos, walls, null);
                        }
                    }
                    else if(edgeIntersectionCount == 2)
                    {
                        //edge
                        if (!blockMatches(level, edges, pos, specialBlocks))
                        {
                            return new MultiBlockScanResult(false, pos, edges, null);
                        }
                    }
                    else if(edgeIntersectionCount == 3)
                    {
                        //corner
                        if (!blockMatches(level, corners, pos, specialBlocks))
                        {
                            return new MultiBlockScanResult(false, pos, corners, null);
                        }
                    }
                }
            }
        }
        return new MultiBlockScanResult(true, null, null, null);
    }


    //#################################################
    //              Conducts a cuboid scan
    //#################################################
    public static Object scanRoomWithEdgeCornerRequirements(Level level, BlockMask walls, BlockMask floor, BlockMask ceil, BlockMask edges, BlockMask corner, BlockMask inside, BlockMask specialsAnywhere, BlockPos corner1, BlockPos corner2)
    {
        BlockPos cornerLow = getMinCorner(corner1, corner2);
        BlockPos cornerHigh = getMaxCorner(corner1, corner2);

        HashMap<Block, ArrayList<BlockPos>> specialBlocks = new HashMap<>();

        MultiBlockScanResult r;
        BlockMask.BlockMaskRequireResult s;
        if (!(r = doComplexCuboidScan(level, cornerLow, cornerHigh, walls, floor, ceil, edges, corner, inside, specialBlocks)).isOK()) return r;

        if (!(s = specialsAnywhere.areRequirementsSatisfiedBy(specialBlocks)).OK()) return s;
        //past this point, multiblock is OK as far as masks go. Check for specials

        return new MultiBlockScanResult(true, null, null, specialBlocks);
    }

    //old version for reference
    /*public static Object scanRoomWithEdgeCornerRequirements(Level level, BlockMask faces, BlockMask edges, BlockMask corner, BlockMask specialsAnywhere, BlockPos corner1, BlockPos corner2)
    {
        BlockPos cornerLow = getMinCorner(corner1, corner2);
        BlockPos cornerHigh = getMaxCorner(corner1, corner2);

        HashMap<Block, ArrayList<BlockPos>> specialBlocksFaces = new HashMap<>();
        HashMap<Block, ArrayList<BlockPos>> specialBlocksEdges = new HashMap<>();
        HashMap<Block, ArrayList<BlockPos>> specialBlocksCorners = new HashMap<>();

        MultiBlockScanResult r;
        BlockMask.BlockMaskRequireResult s;
        System.out.println("Scanning faces");
        if (!(r = isFacesOf(level, faces, specialBlocksFaces, cornerLow, cornerHigh)).isOK()) return r;
        if (!(s = faces.areRequirementsSatisfiedBy(specialBlocksFaces)).OK()) return s;

        System.out.println("Scanning edges");
        if (!(r = isEdgesOf(level, edges, specialBlocksEdges, cornerLow, cornerHigh)).isOK()) return r;
        if (!(s = edges.areRequirementsSatisfiedBy(specialBlocksEdges)).OK()) return s;

        System.out.println("Scanning corners");
        if (!(r = isCornersOf(level, corner, specialBlocksCorners, cornerLow, cornerHigh)).isOK()) return r;
        if (!(s = corner.areRequirementsSatisfiedBy(specialBlocksCorners)).OK()) return s;

        HashMap<Block, ArrayList<BlockPos>> specialBlocks = new HashMap<>();
        //merge lists down
        specialBlocksFaces.forEach((key, val) ->
        {
            if (!specialBlocks.containsKey(key)) specialBlocks.put(key, new ArrayList<>());
            val.forEach((a) -> specialBlocks.get(key).add(a));
        });
        specialBlocksEdges.forEach((key, val) ->
        {
            if (!specialBlocks.containsKey(key)) specialBlocks.put(key, new ArrayList<>());
            val.forEach((a) -> specialBlocks.get(key).add(a));
        });
        specialBlocksCorners.forEach((key, val) ->
        {
            if (!specialBlocks.containsKey(key)) specialBlocks.put(key, new ArrayList<>());
            val.forEach((a) -> specialBlocks.get(key).add(a));
        });
        if (!(s = specialsAnywhere.areRequirementsSatisfiedBy(specialBlocks)).OK()) return s;
        //past this point, multiblock is OK as far as masks go. Check for specials

        return new MultiBlockScanResult(true, null, null, specialBlocks);
    }*/
}