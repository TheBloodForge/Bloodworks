package com.wiggle1000.bloodworks.Blocks;

import com.wiggle1000.bloodworks.Particles.ParticleHelper;
import com.wiggle1000.bloodworks.Registry.ParticleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

@SuppressWarnings({"NullableProblems", "Unused"})
public class BlockBloodySlabBase extends SlabBlock
{
    public BlockBloodySlabBase()
    {
        super(
                BlockBehaviour.Properties
                        .of(Material.STONE)
                        .strength(4f, 1200f)
                        .sound(SoundType.SLIME_BLOCK)
        );
    }
    @Override
    public void stepOn(Level level, BlockPos pos, BlockState blockState, Entity stepperOnner)
    {
        super.stepOn(level, pos, blockState, stepperOnner);
        ParticleHelper.DoStepParticle(ParticleRegistry.PARTICLE_FLESH_STEP.get(), level, pos, blockState, stepperOnner);
    }
}
