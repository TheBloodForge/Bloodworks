package com.wiggle1000.bloodworks.Blocks;

import com.wiggle1000.bloodworks.ClientUtils;
import com.wiggle1000.bloodworks.Particles.ParticleHelper;
import com.wiggle1000.bloodworks.Registry.BlockRegistry;
import com.wiggle1000.bloodworks.Registry.FluidRegistry;
import com.wiggle1000.bloodworks.Registry.ParticleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings({"NullableProblems", "Unused", "unused"})
public class BlockBrainInteriorBase extends BaseEntityBlock implements SimpleWaterloggedBlock
{
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private String selfQuoteText = "A block that goes in cranial fluid.";
    private String useText = "Must be placed inside a Braincase";
    public BlockBrainInteriorBase()
    {
        super(
                Properties
                        .of(Material.STONE)
                        .strength(3f, 5f)
                        .sound(SoundType.SLIME_BLOCK)
                        .noCollission()
                        .noOcclusion()
                .isViewBlocking((blockState, blockGetter, blockPos) -> false)
        );
    }

    public BlockBrainInteriorBase(String selfQuote, String use)
    {
        super(
                Properties
                        .of(Material.STONE)
                        .strength(3f, 5f)
                        .sound(SoundType.SLIME_BLOCK)
        );
        if(selfQuoteText != null) selfQuoteText = selfQuote;
        if(useText != null) useText = use;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter blockGetter, List<Component> components, TooltipFlag tooltipFlag)
    {
        ClientUtils.AddChatComponents(components, selfQuoteText, useText);
        super.appendHoverText(stack, blockGetter, components, tooltipFlag);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState blockState, Entity stepperOnner)
    {
        super.stepOn(level, pos, blockState, stepperOnner);
        ParticleHelper.DoStepParticle(ParticleRegistry.PARTICLE_FLESH_STEP.get(), level, pos, blockState, stepperOnner);
    }



    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        Direction direction = placeContext.getClickedFace();
        BlockPos blockpos = placeContext.getClickedPos();
        FluidState fluidstate = placeContext.getLevel().getFluidState(blockpos);
        return this.defaultBlockState().setValue(WATERLOGGED, fluidstate.getType() == FluidRegistry.FLUID_CRANIAL.source.get().getFlowing());
    }

    @Override
    public boolean canPlaceLiquid(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, Fluid fluid) {
        return !blockState.getValue(BlockStateProperties.WATERLOGGED) && fluid == FluidRegistry.FLUID_CRANIAL.source.get().getFlowing();
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
        if (!blockState.getValue(BlockStateProperties.WATERLOGGED) && fluidState.getType() == FluidRegistry.FLUID_CRANIAL.source.get().getFlowing()) {
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
    public ItemStack pickupBlock(LevelAccessor p_154560_, BlockPos p_154561_, BlockState p_154562_) {
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

    @Override
    public RenderShape getRenderShape(BlockState blockState)
    {
        return RenderShape.INVISIBLE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return BlockRegistry.BLOCK_NEURON.blockEntity().get().create(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateDefinition)
    {
        super.createBlockStateDefinition(blockStateDefinition.add(WATERLOGGED));
    }
}