package com.bloodforge.bloodworks.Blocks;

import com.bloodforge.bloodworks.Blocks.BlockEntities.BE_Neuron;
import com.bloodforge.bloodworks.Registry.BlockRegistry;
import com.bloodforge.bloodworks.Registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class BlockNeuron extends BlockBrainInteriorBase implements EntityBlock
{
    protected VoxelShape BLOCK_SHAPE = box(4, 4, 4, 12, 12, 12);

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

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return 12;
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter blockGetter, BlockPos blockPos) {
        return 1F;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212level, BlockState state, BlockEntityType<T> type)
    {
        return createTickerHelper(type, (BlockEntityType<BE_Neuron>) BlockRegistry.BLOCK_NEURON.blockEntity().get(), BE_Neuron::tick);
    }
    @Override
    public VoxelShape getShape(BlockState p_51104_, BlockGetter p_51105_, BlockPos p_51106_, CollisionContext p_51107_) {
        return BLOCK_SHAPE;
    }
    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState cState, Level level, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult)
    {
        if(level.isClientSide()) {
            System.out.println(cState.getValue(WATERLOGGED));
            return super.use(cState, level, pos, player, interactionHand, blockHitResult);
        }
        if (player.getItemInHand(interactionHand).is(ItemRegistry.ITEM_AXON.get())) {
            if (level.getBlockEntity(pos) instanceof BE_Neuron) {
                BE_Neuron.doConnection(pos, level);
            }
        }

        return super.use(cState, level, pos, player, interactionHand, blockHitResult);
        //return InteractionResult.sidedSuccess(!level.isClientSide());
    }
}