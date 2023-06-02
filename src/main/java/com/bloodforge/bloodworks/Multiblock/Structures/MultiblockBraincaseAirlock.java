package com.bloodforge.bloodworks.Multiblock.Structures;

import com.bloodforge.bloodworks.Multiblock.BlockMask;
import com.bloodforge.bloodworks.Multiblock.MultiBlockScanResult;
import com.bloodforge.bloodworks.Multiblock.MultiblockStructureBase;
import com.bloodforge.bloodworks.Multiblock.StructureDetectionUtils;
import com.bloodforge.bloodworks.Registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class MultiblockBraincaseAirlock extends MultiblockStructureBase
{
    public static final BlockMask BLOCK_MASK_AIRLOCK_WALLS = new BlockMask()
            .withWhitelisted(BlockRegistry.BLOCK_BRAINCASE.block().get())
            .withWhitelisted(BlockRegistry.BLOCK_BRAINCASE_WINDOW.block().get())
            .withWhitelisted(BlockRegistry.BLOCK_BRAINCASE_CONTROLLER.block().get())
            .withSpecial(BlockRegistry.BLOCK_AIRLOCK_DOOR.block().get());

    public static final BlockMask BLOCK_MASK_AIRLOCK_FLOOR = new BlockMask()
            .withWhitelisted(BlockRegistry.BLOCK_BRAINCASE.block().get())
            .withWhitelisted(BlockRegistry.BLOCK_BRAINCASE_WINDOW.block().get())
            .withSpecial(BlockRegistry.BLOCK_AIRLOCK_DRAIN.block().get());

    public static final BlockMask BLOCK_MASK_AIRLOCK_CEIL = new BlockMask()
            .withWhitelisted(BlockRegistry.BLOCK_BRAINCASE.block().get())
            .withWhitelisted(BlockRegistry.BLOCK_BRAINCASE_WINDOW.block().get());

    public static final BlockMask BLOCK_MASK_AIRLOCK_EDGES = new BlockMask()
            .withWhitelisted(BlockRegistry.BLOCK_BRAINCASE.block().get())
            .withWhitelisted(BlockRegistry.BLOCK_BRAINCASE_CONTROLLER.block().get());

    public static final BlockMask BLOCK_MASK_AIRLOCK_CORNERS = new BlockMask()
            .withWhitelisted(BlockRegistry.BLOCK_BRAINCASE.block().get())
            .withWhitelisted(BlockRegistry.BLOCK_BRAINCASE_CONTROLLER.block().get());

    public static final BlockMask BLOCK_MASK_AIRLOCK_INSIDE = new BlockMask()
            .withBlacklisted(BlockRegistry.BLOCK_BRAINCASE.block().get())
            .withBlacklisted(BlockRegistry.BLOCK_BRAINCASE_WINDOW.block().get())
            .withBlacklisted(BlockRegistry.BLOCK_BRAINCASE_CONTROLLER.block().get());

    public static final BlockMask BLOCK_MASK_AIRLOCK_REQUIRED_ANYWHERE = new BlockMask()
            .withRequired(BlockRegistry.BLOCK_AIRLOCK_DRAIN.block().get())
            .withRequired(BlockRegistry.BLOCK_AIRLOCK_DOOR.block().get(), 4);

    //used for airlock-search from MultiblockBraincase
    public static final BlockMask BLOCK_MASK_AIRLOCK_WALL_CEIL_FLOOR_SCANMASK = new BlockMask()
            .withWhitelisted(BlockRegistry.BLOCK_BRAINCASE.block().get())
            .withWhitelisted(BlockRegistry.BLOCK_BRAINCASE_WINDOW.block().get())
            .withWhitelisted(BlockRegistry.BLOCK_AIRLOCK_DRAIN.block().get())
            .withWhitelisted(BlockRegistry.BLOCK_AIRLOCK_DOOR.block().get());

    @Override
    public boolean IsAtCoords(Level level, BlockPos minCorner, BlockPos maxCorner)
    {
        System.out.println(minCorner + ", " + maxCorner);
        Object structRes = StructureDetectionUtils.scanRoomWithEdgeCornerRequirements(level, BLOCK_MASK_AIRLOCK_WALLS, BLOCK_MASK_AIRLOCK_FLOOR, BLOCK_MASK_AIRLOCK_CEIL, BLOCK_MASK_AIRLOCK_EDGES, BLOCK_MASK_AIRLOCK_CORNERS, BLOCK_MASK_AIRLOCK_INSIDE, BLOCK_MASK_AIRLOCK_REQUIRED_ANYWHERE, minCorner, maxCorner);
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
        }
        System.out.println("Found structure! Min: " + minCorner.toShortString() + " | Max: " + maxCorner.toShortString());
        return true;
    }


    public MultiBlockScanResult IsAtCoordsWithScanResult(Level level, BlockPos minCorner, BlockPos maxCorner)
    {
        System.out.println(minCorner + ", " + maxCorner);
        Object structRes = StructureDetectionUtils.scanRoomWithEdgeCornerRequirements(level, BLOCK_MASK_AIRLOCK_WALLS, BLOCK_MASK_AIRLOCK_FLOOR, BLOCK_MASK_AIRLOCK_CEIL, BLOCK_MASK_AIRLOCK_EDGES, BLOCK_MASK_AIRLOCK_CORNERS, BLOCK_MASK_AIRLOCK_INSIDE, BLOCK_MASK_AIRLOCK_REQUIRED_ANYWHERE, minCorner, maxCorner);
        if (structRes instanceof BlockMask.BlockMaskRequireResult specialBlockFindResult)
        {
            if (!specialBlockFindResult.OK())
            {
                return new MultiBlockScanResult(false, null, new BlockMask().withWhitelisted(specialBlockFindResult.missing()), null);
            }
        } else if (structRes instanceof MultiBlockScanResult multiBlockScanResult)
        {
            return multiBlockScanResult;
        }
        return new MultiBlockScanResult(false, null, null, null);
    }
}