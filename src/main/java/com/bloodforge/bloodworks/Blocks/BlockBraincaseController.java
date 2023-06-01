package com.bloodforge.bloodworks.Blocks;

import com.bloodforge.bloodworks.Blocks.BlockEntities.BE_Braincase_Controller;
import com.bloodforge.bloodworks.Registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class BlockBraincaseController extends BlockMachineBase
{

    public BlockBraincaseController()
    {
    }

    // -------- Block entity stuff --------

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return BlockRegistry.BLOCK_BRAINCASE_CONTROLLER.blockEntity().get().create(pos, state);
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos)
    {
        return 12;
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter blockGetter, BlockPos blockPos)
    {
        return 1F;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212level, BlockState state, BlockEntityType<T> type)
    {
        return createTickerHelper(type, (BlockEntityType<BE_Braincase_Controller>) BlockRegistry.BLOCK_BRAINCASE_CONTROLLER.blockEntity().get(), BE_Braincase_Controller::tick);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState cState, Level level, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult)
    {
        if (level.isClientSide())
        {
            return InteractionResult.sidedSuccess(!level.isClientSide());
        }
        if (level.getBlockEntity(pos) instanceof BE_Braincase_Controller controller)
        {
            controller.use(player, interactionHand, blockHitResult);
            return InteractionResult.sidedSuccess(!level.isClientSide());
        }

        return super.use(cState, level, pos, player, interactionHand, blockHitResult);
        //return InteractionResult.sidedSuccess(!level.isClientSide());
    }
}