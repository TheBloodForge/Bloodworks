package com.bloodforge.bloodworks.Blocks;

import com.bloodforge.bloodworks.Particles.ParticleHelper;
import com.bloodforge.bloodworks.Registry.ParticleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

@SuppressWarnings({"NullableProblems", "Unused"})
public class BlockBloodyStairsBase extends StairBlock
{
    public BlockBloodyStairsBase()
    {
        super(
                Blocks.COBBLESTONE_STAIRS::defaultBlockState,
                Properties
                        .of(Material.STONE)
                        .strength(3f, 5f)
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
