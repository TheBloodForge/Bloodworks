package com.bloodforge.bloodworks.Multiblock.Structures;

import com.bloodforge.bloodworks.Multiblock.*;
import com.bloodforge.bloodworks.Registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class MultiblockBraincase extends MultiblockStructureBase
{
    public static final BlockMask BLOCK_MASK_BRAINCASE_WALLS = new BlockMask()
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
        .withRequired(BlockRegistry.BLOCK_BRAINCASE_CONTROLLER.block().get());

    @Override
    public boolean IsAtCoords(Level level, BlockPos minCorner, BlockPos maxCorner)
    {
        System.out.println(minCorner + ", " + maxCorner);
        Object structRes = StructureDetectionUtils.scanRoomWithEdgeCornerRequirements(level, BLOCK_MASK_BRAINCASE_WALLS, BLOCK_MASK_BRAINCASE_EDGES, BLOCK_MASK_BRAINCASE_CORNERS, BLOCK_MASK_BRAINCASE_INSIDE, BLOCK_MASK_BRAINCASE_REQUIRED_ANYWHERE, minCorner, maxCorner);
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
}