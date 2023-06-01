package com.bloodforge.bloodworks.Multiblock.Structures;

import com.bloodforge.bloodworks.Multiblock.BlockMask;
import com.bloodforge.bloodworks.Multiblock.MultiBlockScanResult;
import com.bloodforge.bloodworks.Multiblock.MultiblockStructureBase;
import com.bloodforge.bloodworks.Multiblock.StructureDetectionUtils;
import com.bloodforge.bloodworks.Registry.BlockRegistry;
import com.ibm.icu.impl.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public class MultiblockBraincase extends MultiblockStructureBase
{

    public static final MultiblockBraincaseAirlock MULTIBLOCK_BRAINCASE_AIRLOCK = new MultiblockBraincaseAirlock();
    public static final BlockMask BLOCK_MASK_BRAINCASE_WALLS = new BlockMask()
            .withWhitelisted(BlockRegistry.BLOCK_BRAINCASE.block().get())
            .withWhitelisted(BlockRegistry.BLOCK_BRAINCASE_WINDOW.block().get())
            .withSpecial(BlockRegistry.BLOCK_AIRLOCK_DOOR.block().get())
            .withSpecial(BlockRegistry.BLOCK_BRAINCASE_CONTROLLER.block().get());

    public static final BlockMask BLOCK_MASK_BRAINCASE_FLOOR = new BlockMask()
            .withWhitelisted(BlockRegistry.BLOCK_BRAINCASE.block().get())
            .withWhitelisted(BlockRegistry.BLOCK_BRAINCASE_WINDOW.block().get());

    public static final BlockMask BLOCK_MASK_BRAINCASE_CEIL = new BlockMask()
            .withWhitelisted(BlockRegistry.BLOCK_BRAINCASE.block().get())
            .withWhitelisted(BlockRegistry.BLOCK_BRAINCASE_WINDOW.block().get());

    public static final BlockMask BLOCK_MASK_BRAINCASE_EDGES = new BlockMask()
            .withWhitelisted(BlockRegistry.BLOCK_BRAINCASE.block().get())
            .withWhitelisted(BlockRegistry.BLOCK_BRAINCASE_CONTROLLER.block().get())
            .withSpecial(BlockRegistry.BLOCK_BRAINCASE_CONTROLLER.block().get());

    public static final BlockMask BLOCK_MASK_BRAINCASE_CORNERS = new BlockMask()
            .withWhitelisted(BlockRegistry.BLOCK_BRAINCASE.block().get())
            .withWhitelisted(BlockRegistry.BLOCK_BRAINCASE_CONTROLLER.block().get());

    public static final BlockMask BLOCK_MASK_BRAINCASE_INSIDE = new BlockMask()
            .withBlacklisted(BlockRegistry.BLOCK_BRAINCASE.block().get())
            .withBlacklisted(BlockRegistry.BLOCK_BRAINCASE_WINDOW.block().get())
            .withBlacklisted(BlockRegistry.BLOCK_BRAINCASE_CONTROLLER.block().get());

    public static final BlockMask BLOCK_MASK_BRAINCASE_REQUIRED_ANYWHERE = new BlockMask()
            .withRequired(BlockRegistry.BLOCK_BRAINCASE_CONTROLLER.block().get(), 1)
            .withRequired(BlockRegistry.BLOCK_AIRLOCK_DOOR.block().get(), 1, 4);

    @Override
    public boolean IsAtCoords(Level level, BlockPos minCorner, BlockPos maxCorner)
    {
        System.out.println(minCorner + ", " + maxCorner);
        Object structRes = StructureDetectionUtils.scanRoomWithEdgeCornerRequirements(level, BLOCK_MASK_BRAINCASE_WALLS, BLOCK_MASK_BRAINCASE_FLOOR, BLOCK_MASK_BRAINCASE_CEIL, BLOCK_MASK_BRAINCASE_EDGES, BLOCK_MASK_BRAINCASE_CORNERS, BLOCK_MASK_BRAINCASE_INSIDE, BLOCK_MASK_BRAINCASE_REQUIRED_ANYWHERE, minCorner, maxCorner);
        if (structRes instanceof BlockMask.BlockMaskRequireResult specialBlockFindResult)
        {
            if (!specialBlockFindResult.OK())
            {
                System.out.println(specialBlockFindResult);
                return false;
            }
        } else if (structRes instanceof MultiBlockScanResult multiBlockScanResult)
        {
            if (!multiBlockScanResult.isOK())
            {
                System.out.println(multiBlockScanResult.toString());
                return false;
            }
            System.out.println("Found braincase main body! Searching for airlock. Min: " + minCorner.toShortString() + " | Max: " + maxCorner.toShortString());
            int numDoors = multiBlockScanResult.specialBlocks().get(BlockRegistry.BLOCK_AIRLOCK_DOOR.block().get()).size();
            BlockPos airlockMin = null;
            BlockPos airlockMax = null;
            for (BlockPos blockPos : multiBlockScanResult.specialBlocks().get(BlockRegistry.BLOCK_AIRLOCK_DOOR.block().get()))
            {
                Direction doorFacing = StructureDetectionUtils.getSideFacingOfCuboidThisBlockIsOn(minCorner, maxCorner, blockPos);
                System.out.println("Scanning door facing " + doorFacing.toString());
                Pair<BlockPos, BlockPos> foundAirLock = StructureDetectionUtils.findAdjacentRoomCorners(level, blockPos, doorFacing, MultiblockBraincaseAirlock.BLOCK_MASK_AIRLOCK_WALL_CEIL_FLOOR_SCANMASK, 10, 10, 5);
                if(foundAirLock == null)
                {
                    System.out.println("One or more doors didn't lead to an airlock!");
                    return false;
                }
                System.out.println("Airlock door scan; door pos: "+blockPos.toShortString()+". Result: Min: "+foundAirLock.first.toShortString()+" | Max: "+foundAirLock.second.toShortString());
                if(airlockMin == null || airlockMax == null)
                {
                    airlockMin = foundAirLock.first;
                    airlockMax = foundAirLock.second;
                }
                else if(airlockMin.distManhattan(foundAirLock.first)!=0 ||airlockMax.distManhattan(foundAirLock.second)!=0 )
                {
                    System.out.println("Braincases cannot support multiple airlocks.");
                    return false;
                }
            }
            if(airlockMin == null || airlockMax == null)
            {
                System.out.println("Failed to find an airlock.");
                return false;
            }
            System.out.println("Airlock located! Min: "+airlockMin.toShortString()+" | Max: "+airlockMax.toShortString()+". Validating airlock..");
            if(!MULTIBLOCK_BRAINCASE_AIRLOCK.IsAtCoords(level, airlockMin, airlockMax))
            {
                System.out.println("Airlock Malformed!");
                return false;
            }
            return true;
        }
        return false;
    }
}