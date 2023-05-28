package com.wiggle1000.bloodworks.Blocks.Fluids;

import com.wiggle1000.bloodworks.Globals;
import com.wiggle1000.bloodworks.Registry.BlockRegistry;
import com.wiggle1000.bloodworks.Registry.FluidRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("unused")
public class FluidMechanics
{
    public static void tickFluid(LiquidBlock fluidBlock, BlockState state, ServerLevel level, BlockPos pos) {
        if(level.isClientSide) return;
        if (FluidRegistry.FLUID_BLOOD.source.get().getSource() == fluidBlock.getFluid().getSource())
        {
            if(Globals.RAND.nextFloat() > 0.6f) level.setBlockAndUpdate(pos, BlockRegistry.BLOCK_COAGULATED.blockBase().block().get().defaultBlockState());
        }
        else if (FluidRegistry.FLUID_CRANIAL.source.get().getSource() == fluidBlock.getFluid().getSource())
        {
            //TODO: add immersion particles
        }
    }
}