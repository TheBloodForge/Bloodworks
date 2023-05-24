package com.wiggle1000.bloodworks.Blocks.BlockEntities;

import com.wiggle1000.bloodworks.Registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BlockEntityIntestine extends BlockEntity
{

    public BlockEntityIntestine(BlockPos pos, BlockState blockState)
    {
        super(BlockEntityRegistry.BLOCK_ENTITY_INTESTINE.get(), pos, blockState);
    }

    @Override
    public AABB getRenderBoundingBox()
    {
        return AABB.ofSize(new Vec3(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ()), 1.5, 1.5, 1.5);
    }

}
