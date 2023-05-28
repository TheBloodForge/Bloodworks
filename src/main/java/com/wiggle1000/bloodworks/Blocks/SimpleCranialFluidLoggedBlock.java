package com.wiggle1000.bloodworks.Blocks;

import com.wiggle1000.bloodworks.Registry.FluidRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import java.util.Optional;

public interface SimpleCranialFluidLoggedBlock extends BucketPickup, LiquidBlockContainer
{

    @Override
    default boolean canPlaceLiquid(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, Fluid fluid) {
        return !blockState.getValue(BlockStateProperties.WATERLOGGED) && fluid == FluidRegistry.FLUID_CRANIAL.source.get().getSource();
    }

    @Override
    default boolean placeLiquid(LevelAccessor level, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
        if (!blockState.getValue(BlockStateProperties.WATERLOGGED) && fluidState.getType() == FluidRegistry.FLUID_CRANIAL.source.get().getSource()) {
            if (!level.isClientSide()) {
                level.setBlock(blockPos, blockState.setValue(BlockStateProperties.WATERLOGGED, true), 3);
                level.scheduleTick(blockPos, fluidState.getType(), fluidState.getType().getTickDelay(level));
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    default ItemStack pickupBlock(LevelAccessor p_154560_, BlockPos p_154561_, BlockState p_154562_) {
        if (p_154562_.getValue(BlockStateProperties.WATERLOGGED)) {
            p_154560_.setBlock(p_154561_, p_154562_.setValue(BlockStateProperties.WATERLOGGED, false), 3);
            if (!p_154562_.canSurvive(p_154560_, p_154561_)) {
                p_154560_.destroyBlock(p_154561_, true);
            }

            return new ItemStack(FluidRegistry.FLUID_CRANIAL.bucket.get());
        } else {
            return ItemStack.EMPTY;
        }
    }


    default Optional<SoundEvent> getPickupSound() {
        return FluidRegistry.FLUID_CRANIAL.source.get().getPickupSound();
    }
}
