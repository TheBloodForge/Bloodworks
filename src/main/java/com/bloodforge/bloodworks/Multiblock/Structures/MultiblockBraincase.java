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

import java.util.ArrayList;
import java.util.List;

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

    public record MultiblockBraincaseScanResults(boolean wasFound,
                                                 String errorMessage,
                                                 Pair<BlockPos, BlockPos> airlockCoords,
                                                 List<BlockPos> airlockDoorsInside,
                                                 List<BlockPos> airlockDoorsOutside,
                                                 List<BlockPos> airlockVents){}

    public MultiblockBraincaseScanResults mostRecentScanResult = new MultiblockBraincaseScanResults(false, "Not scanned.", null, null, null, null);

    @Override
    public boolean IsAtCoords(Level level, BlockPos minCorner, BlockPos maxCorner)
    {
        //System.out.println(minCorner + ", " + maxCorner);
        Object structRes = StructureDetectionUtils.scanRoomWithEdgeCornerRequirements(level, BLOCK_MASK_BRAINCASE_WALLS, BLOCK_MASK_BRAINCASE_FLOOR, BLOCK_MASK_BRAINCASE_CEIL, BLOCK_MASK_BRAINCASE_EDGES, BLOCK_MASK_BRAINCASE_CORNERS, BLOCK_MASK_BRAINCASE_INSIDE, BLOCK_MASK_BRAINCASE_REQUIRED_ANYWHERE, minCorner, maxCorner);
        if (structRes instanceof BlockMask.BlockMaskRequireResult specialBlockFindResult)
        {
            if (!specialBlockFindResult.OK())
            {
                System.out.println(specialBlockFindResult);
                mostRecentScanResult = new MultiblockBraincaseScanResults(false, specialBlockFindResult.toString(), null, null, null, null);
                return false;
            }
        } else if (structRes instanceof MultiBlockScanResult multiBlockScanResult)
        {
            if (!multiBlockScanResult.isOK())
            {
                mostRecentScanResult = new MultiblockBraincaseScanResults(false, multiBlockScanResult.toString(), null, null, null, null);
                //System.out.println(multiBlockScanResult.toString());
                return false;
            }
            System.out.println("Found braincase main body! Searching for airlock. Min: " + minCorner.toShortString() + " | Max: " + maxCorner.toShortString());
            mostRecentScanResult = new MultiblockBraincaseScanResults(false, "Found main body. Airlock not found.", null, null, null, null);
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
                    mostRecentScanResult = new MultiblockBraincaseScanResults(false, "One or more doors didn't lead to an airlock!", null, null, null, null);
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
                    mostRecentScanResult = new MultiblockBraincaseScanResults(false, "Braincases do not support having multiple airlocks.", null, null, null, null);
                    return false;
                }
            }
            if(airlockMin == null || airlockMax == null)
            {
                mostRecentScanResult = new MultiblockBraincaseScanResults(false, "No airlock found.", null, null, null, null);
                return true; //failed to find airlock, but this is the correct location.
            }
            System.out.println("Airlock located! Min: "+airlockMin.toShortString()+" | Max: "+airlockMax.toShortString()+". Validating airlock..");
            MultiBlockScanResult res = MULTIBLOCK_BRAINCASE_AIRLOCK.IsAtCoordsWithScanResult(level, airlockMin, airlockMax);
            if(!(res.isOK()))
            {
                mostRecentScanResult = new MultiblockBraincaseScanResults(false, "Airlock Malformed at "+res.errorLocation().toShortString() + ".\n"+res.expectedBlocks().toString(), null, null, null, null);
                return true; //failed to find airlock, but this is the correct location.
            }
            List<BlockPos> airlockDoorsInside = multiBlockScanResult.specialBlocks().get(BlockRegistry.BLOCK_AIRLOCK_DOOR.block().get());
            List<BlockPos> airlockDoorsOutside = new ArrayList<>();
            for (BlockPos blockPos : res.specialBlocks().get(BlockRegistry.BLOCK_AIRLOCK_DOOR.block().get())) {
                if(!airlockDoorsInside.contains(blockPos))
                {
                    airlockDoorsOutside.add(blockPos);
                }
            }
            List<BlockPos> airlockVents = res.specialBlocks().get(BlockRegistry.BLOCK_AIRLOCK_DRAIN.block().get());
            mostRecentScanResult = new MultiblockBraincaseScanResults(true, "Braincase and airlock were found successfully! :D\n  Inner doors: "+airlockDoorsInside.size()+"\n  Outer doors: "+airlockDoorsOutside.size()+"\n  Drains:"+airlockVents.size(), Pair.of(airlockMin, airlockMax), airlockDoorsInside, airlockDoorsOutside, airlockVents);
            return true;
        }
        mostRecentScanResult = new MultiblockBraincaseScanResults(false, "Braincase not found.", null, null, null, null);
        return false;
    }
}