package com.bloodforge.bloodworks.Blocks;

import com.bloodforge.bloodworks.Particles.ParticleHelper;
import com.bloodforge.bloodworks.Registry.ParticleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@SuppressWarnings({"NullableProblems", "Unused", "unused"})
public class GenericBlockBase extends Block
{

    private boolean isHalfTransparent = false;

    public GenericBlockBase(Properties props)
    {
        super(props);
    }

    public GenericBlockBase withGlasslikeProperties()
    {
        this.isHalfTransparent = true;
        return this;
    }

    public boolean skipRendering(BlockState p_53972_, BlockState p_53973_, Direction p_53974_)
    {
        if (!this.isHalfTransparent) return super.skipRendering(p_53972_, p_53973_, p_53974_);
        return p_53973_.is(this) || super.skipRendering(p_53972_, p_53973_, p_53974_);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState blockState, Entity stepperOnner)
    {
        super.stepOn(level, pos, blockState, stepperOnner);
        ParticleHelper.DoStepParticle(ParticleRegistry.PARTICLE_FLESH_STEP.get(), level, pos, blockState, stepperOnner);
    }


    public VoxelShape getVisualShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return isHalfTransparent?Shapes.empty():super.getVisualShape(blockState, blockGetter, blockPos, collisionContext);
    }

    public float getShadeBrightness(BlockState blockState, BlockGetter shadeBrightness, BlockPos pos) {
        return isHalfTransparent?1.0f:super.getShadeBrightness(blockState, shadeBrightness, pos);
    }

    public boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return isHalfTransparent;
    }
}