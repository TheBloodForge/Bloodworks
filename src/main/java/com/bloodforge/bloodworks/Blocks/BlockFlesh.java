package com.bloodforge.bloodworks.Blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;

@SuppressWarnings({"NullableProblems", "Unused", "unused"})
public class BlockFlesh extends Block
{
    //Raw, Subskin, SkinHealing, Skin
    public static final IntegerProperty SKIN_GROWTH_LEVEL = IntegerProperty.create("growth_level", 0, 3);
    //0 = uncolored
    public static final IntegerProperty SKIN_COLOR = IntegerProperty.create("skin_color", 0, 16);

    public BlockFlesh()
    {
        super(
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
        //ParticleHelper.DoStepParticle(ParticleRegistry.PARTICLE_FLESH_STEP.get(), level, pos, blockState, stepperOnner);
    }

    @Override
    public void randomTick(BlockState p_222954_, ServerLevel p_222955_, BlockPos p_222956_, RandomSource p_222957_)
    {
        super.randomTick(p_222954_, p_222955_, p_222956_, p_222957_);

    }

    @Override
    public boolean isRandomlyTicking(BlockState p_49921_)
    {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_55933_) {
        p_55933_.add(SKIN_GROWTH_LEVEL).add(SKIN_COLOR);
    }

    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        BlockState placedFromState = placeContext.getLevel().getBlockState(placeContext.getClickedPos());
        //TODO: make thing good
        /*if(placedFromState != null && placedFromState.getBlock() == BlockRegistry.BLOCK_FLESH.blockBase())
        {
            return this.defaultBlockState().setValue(SKIN_GROWTH_LEVEL, 0).setValue(SKIN_COLOR, placedFromState.getValue(SKIN_COLOR));
        }*/
        return this.defaultBlockState().setValue(SKIN_GROWTH_LEVEL, 0).setValue(SKIN_COLOR, 0);
    }

}