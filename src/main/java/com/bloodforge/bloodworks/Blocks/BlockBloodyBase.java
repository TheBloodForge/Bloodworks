package com.bloodforge.bloodworks.Blocks;

import com.bloodforge.bloodworks.Particles.ParticleHelper;
import com.bloodforge.bloodworks.Registry.ParticleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

@SuppressWarnings({"NullableProblems", "Unused", "unused"})
public class BlockBloodyBase extends Block
{
    private String selfQuoteText = "Clotted blood. Still a bit mushy..";
    private String useText = "Used for decoration";
    public BlockBloodyBase()
    {
        super(
                BlockBehaviour.Properties
                        .of(Material.STONE)
                        .strength(3f, 5f)
                        .sound(SoundType.SLIME_BLOCK)
        );
    }

    public BlockBloodyBase(String selfQuote, String use)
    {
        super(
                BlockBehaviour.Properties
                        .of(Material.STONE)
                        .strength(3f, 5f)
                        .sound(SoundType.SLIME_BLOCK)
        );
        if(selfQuoteText != null) selfQuoteText = selfQuote;
        if(useText != null) useText = use;
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState blockState, Entity stepperOnner)
    {
        super.stepOn(level, pos, blockState, stepperOnner);
        ParticleHelper.DoStepParticle(ParticleRegistry.PARTICLE_FLESH_STEP.get(), level, pos, blockState, stepperOnner);
    }
}