package com.bloodforge.bloodworks.Blocks;

import com.bloodforge.bloodworks.Particles.ParticleHelper;
import com.bloodforge.bloodworks.Registry.ParticleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@SuppressWarnings({"NullableProblems", "Unused", "unused"})
public class BlockBloodyTransparentBase extends Block
{
    public BlockBloodyTransparentBase()
    {
        super(
                Properties
                        .of(Material.STONE)
                        .strength(3f, 5f)
                        .sound(SoundType.SLIME_BLOCK)
                        .noOcclusion()
        );
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState blockState, Entity stepperOnner)
    {
        super.stepOn(level, pos, blockState, stepperOnner);
        ParticleHelper.DoStepParticle(ParticleRegistry.PARTICLE_FLESH_STEP.get(), level, pos, blockState, stepperOnner);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState p_60578_, BlockGetter p_60579_, BlockPos p_60580_)
    {
        return Shapes.empty();
    }

    @Override
    public boolean skipRendering(BlockState myState, BlockState neighborState, Direction direction)
    {
        return neighborState.is(myState.getBlock());
    }

    @Override
    public boolean propagatesSkylightDown(BlockState p_49928_, BlockGetter p_49929_, BlockPos p_49930_)
    {
        return true;
    }
}