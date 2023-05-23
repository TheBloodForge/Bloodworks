package com.wiggle1000.bloodworks.Blocks.BlockEntities;

import com.wiggle1000.bloodworks.Registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityIntestine extends BlockEntity
{

    public BlockEntityIntestine(BlockPos pos, BlockState blockState)
    {
        super(BlockEntityRegistry.BLOCK_ENTITY_INTESTINE.get(), pos, blockState);
    }
}
