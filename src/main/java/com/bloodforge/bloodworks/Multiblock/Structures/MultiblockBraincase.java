package com.bloodforge.bloodworks.Multiblock.Structures;

import com.bloodforge.bloodworks.Multiblock.*;
import com.bloodforge.bloodworks.Registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class MultiblockBraincase extends MultiblockStructureBase
{
    public static final BlockMask BLOCK_MASK_BRAINCASE_WALLS = new BlockMask(BlockRegistry.BLOCK_BRAINCASE.block().get(), BlockRegistry.BLOCK_BRAINCASE_WINDOW.block().get(), BlockRegistry.BLOCK_BRAINCASE_CONTROLLER.block().get());
    public static final BlockMask BLOCK_MASK_BRAINCASE_EDGES_CORNERS = new BlockMask(BlockRegistry.BLOCK_BRAINCASE.block().get(), BlockRegistry.BLOCK_BRAINCASE_CONTROLLER.block().get());
    public static final BlockMask BLOCK_MASK_BRAINCASE_REQUIRED_ANYWHERE = new BlockMask(new SpecialBlockParams[]{new SpecialBlockParams(BlockRegistry.BLOCK_COAGULATED.blockBase().block().get(), true)});

    @Override
    public boolean IsAtCoords(Level level, BlockPos minCorner, BlockPos maxCorner)
    {
        Object structRes = StructureDetectionUtils.scanRoomWithEdgeCornerRequirements(level, BLOCK_MASK_BRAINCASE_WALLS, BLOCK_MASK_BRAINCASE_EDGES_CORNERS, BLOCK_MASK_BRAINCASE_EDGES_CORNERS, BLOCK_MASK_BRAINCASE_REQUIRED_ANYWHERE, minCorner, maxCorner);
        if (structRes instanceof SpecialBlockFindResult specialBlockFindResult)
        {
            if (!specialBlockFindResult.isOK())
            {
                System.out.println(specialBlockFindResult);
                return false;
            }
        } else if (structRes instanceof MultiBlockScanResult multiBlockScanResult)
        {
            if (!multiBlockScanResult.isOK())
            {
                System.out.println(multiBlockScanResult.toString(true));
                return false;
            }
        }
        System.out.println("Found structure! Min: " + minCorner.toShortString() + " | Max: " + maxCorner.toShortString());
        return true;
    }
}