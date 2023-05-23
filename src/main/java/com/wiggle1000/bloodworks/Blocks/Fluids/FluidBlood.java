package com.wiggle1000.bloodworks.Blocks.Fluids;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import org.jetbrains.annotations.Nullable;

public class FluidBlood extends ForgeFlowingFluid
{

    protected FluidBlood()
    {
        super(null);
    }

    @Override
    public boolean isSource(FluidState p_76140_)
    {
        return false;
    }

    @Override
    public int getAmount(FluidState p_164509_)
    {
        return 0;
    }

    @Override
    public float getExplosionResistance(FluidState state, BlockGetter level, BlockPos pos, Explosion explosion)
    {
        return super.getExplosionResistance(state, level, pos, explosion);
    }

    @Override
    public boolean move(FluidState state, LivingEntity entity, Vec3 movementVector, double gravity)
    {
        return super.move(state, entity, movementVector, gravity);
    }

    @Override
    public boolean supportsBoating(FluidState state, Boat boat)
    {
        return super.supportsBoating(state, boat);
    }

    @Override
    public @Nullable BlockPathTypes getBlockPathType(FluidState state, BlockGetter level, BlockPos pos, @Nullable Mob mob, boolean canFluidLog)
    {
        return super.getBlockPathType(state, level, pos, mob, canFluidLog);
    }

    @Override
    public @Nullable BlockPathTypes getAdjacentBlockPathType(FluidState state, BlockGetter level, BlockPos pos, @Nullable Mob mob, BlockPathTypes originalType)
    {
        return super.getAdjacentBlockPathType(state, level, pos, mob, originalType);
    }

    @Override
    public boolean canHydrate(FluidState state, BlockGetter getter, BlockPos pos, BlockState source, BlockPos sourcePos)
    {
        return false;
    }

    @Override
    public boolean canExtinguish(FluidState state, BlockGetter getter, BlockPos pos)
    {
        return super.canExtinguish(state, getter, pos);
    }

    public static class Flowing extends FluidBlood
    {
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> fluidState) {
            super.createFluidStateDefinition(fluidState);
            fluidState.add(LEVEL);
        }

        public int getAmount(FluidState fluidState) {
            return fluidState.getValue(LEVEL);
        }

        public boolean isSource(FluidState fluidState) {
            return false;
        }
    }

    public static class Source extends FluidBlood {
        public int getAmount(FluidState fluidState) {
            return 8;
        }

        public boolean isSource(FluidState fluidState) {
            return true;
        }
    }
}