package com.wiggle1000.bloodworks.Blocks.BlockEntities;

import com.wiggle1000.bloodworks.Registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BE_Neuron extends BlockEntity
{
    public BE_Neuron(BlockPos pos, BlockState blockState)
    {
        super(BlockRegistry.BLOCK_NEURON.blockEntity().get(), pos, blockState);
    }


    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BE_Neuron entity)
    {

    }

    @Override
    public AABB getRenderBoundingBox()
    {
        return AABB.ofSize(new Vec3(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ()), 1.5, 1.5, 1.5);
    }
}