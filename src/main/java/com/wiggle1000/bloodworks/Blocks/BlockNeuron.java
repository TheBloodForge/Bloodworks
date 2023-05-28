package com.wiggle1000.bloodworks.Blocks;

import com.wiggle1000.bloodworks.Blocks.BlockEntities.BE_Neuron;
import com.wiggle1000.bloodworks.Registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class BlockNeuron extends BlockBrainInteriorBase implements EntityBlock
{

//    public static final BlockShape SHAPE = BlockShape.createBlockShape(2.5, 0, 2.5, 13.5, 16, 13.5);
    public static final BooleanProperty OUTPUT = BooleanProperty.create("output");
    public BlockNeuron()
    {
        SetQuotes("A living neuron. A bit large, isn't it?", "Must be placed inside a Braincase.");
    }

    // -------- Block entity stuff --------

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return BlockRegistry.BLOCK_NEURON.blockEntity().get().create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212level, BlockState state, BlockEntityType<T> type)
    {
        return createTickerHelper(type, (BlockEntityType<BE_Neuron>) BlockRegistry.BLOCK_NEURON.blockEntity().get(), BE_Neuron::tick);
    }


    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState cState, Level level, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult)
    {
        if(!level.isClientSide()) {
            System.out.println(cState.getValue(WATERLOGGED));
        }

        return super.use(cState, level, pos, player, interactionHand, blockHitResult);
        //return InteractionResult.sidedSuccess(!level.isClientSide());
    }
}