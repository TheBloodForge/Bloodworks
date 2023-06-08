package com.bloodforge.bloodworks.Blocks;

import com.bloodforge.bloodworks.Client.Sound.SoundHelper;
import com.bloodforge.bloodworks.Globals;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;

public class BlockMachineBase extends BaseEntityBlock
{
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public BlockMachineBase()
    {
        super(
                BlockBehaviour.Properties
                        .of(Material.STONE)
                        .strength(1f, 8f)
                        .sound(SoundType.METAL)
                        .requiresCorrectToolForDrops()
        );
    }

    public BlockMachineBase(Properties props)
    {
        super(props);
    }

    // -------- Block entity stuff --------
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext)
    {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState)
    {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateDefinition)
    {
        super.createBlockStateDefinition(blockStateDefinition.add(FACING));
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
        super.animateTick(blockState, level, blockPos, randomSource);
        if(hasIdleSound())
        {
            SoundHelper.PlayTileSound(getIdleSoundResourceLocation(), blockPos);
            //SoundHelper.SetTileSoundPitch((float) (Math.sin(level.getGameTime()/10f)+0.5f), blockPos);
        }
    }

    public ResourceLocation getIdleSoundResourceLocation()
    {
        return new ResourceLocation(Globals.MODID, "bloodworks.stirling.idle");
    }

    public boolean hasIdleSound()
    {
        return false;
    }

    @Override
    public void destroy(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {
        super.destroy(levelAccessor, blockPos, blockState);
        if(hasIdleSound()) {
            SoundHelper.StopTileSound(blockPos);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_)
    {
        return null;
    }
}