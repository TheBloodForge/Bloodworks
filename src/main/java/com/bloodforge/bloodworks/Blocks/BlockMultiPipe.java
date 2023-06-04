package com.bloodforge.bloodworks.Blocks;

import com.bloodforge.bloodworks.Blocks.BlockEntities.BE_MultiPipe;
import com.bloodforge.bloodworks.Client.ClientUtils;
import com.bloodforge.bloodworks.Registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("deprecation")
public class BlockMultiPipe extends BaseEntityBlock
{
    public BlockMultiPipe()
    {
        super(
                BlockBehaviour.Properties
                        .of(Material.STONE)
                        .strength(1f, 8f)
                        .sound(SoundType.METAL)
                        .noOcclusion()
        );
    }

    // -------- Block entity stuff --------

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return BlockRegistry.BLOCK_MULTI_PIPE.blockEntity().get().create(pos, state);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter blockGetter, List<Component> components, TooltipFlag tooltipFlag)
    {
        ClientUtils.AddAdditionalShiftInfo(components, "Cancer: Unimplemented!");
        super.appendHoverText(stack, blockGetter, components, tooltipFlag);
    }

    @Override
    public boolean hasDynamicShape()
    { return true; }

    private static final double CONNECTION_SIZE = 0.25f, BLOCK_SIZE = 0.5f;
    private static final VoxelShape DEFAULT_SHAPE = Shapes.box(CONNECTION_SIZE, CONNECTION_SIZE, CONNECTION_SIZE, CONNECTION_SIZE + BLOCK_SIZE, CONNECTION_SIZE + BLOCK_SIZE, CONNECTION_SIZE + BLOCK_SIZE);
    private static final VoxelShape SOUTH_SHAPE = Shapes.box(CONNECTION_SIZE, CONNECTION_SIZE, CONNECTION_SIZE + BLOCK_SIZE, CONNECTION_SIZE + BLOCK_SIZE, CONNECTION_SIZE + BLOCK_SIZE, 1.0f);
    private static final VoxelShape NORTH_SHAPE = Shapes.box(CONNECTION_SIZE, CONNECTION_SIZE, 0.0f, CONNECTION_SIZE + BLOCK_SIZE, CONNECTION_SIZE + BLOCK_SIZE, CONNECTION_SIZE);
    private static final VoxelShape DOWN_SHAPE = Shapes.box(CONNECTION_SIZE, 0.0f, CONNECTION_SIZE, CONNECTION_SIZE + BLOCK_SIZE, CONNECTION_SIZE, CONNECTION_SIZE + BLOCK_SIZE);
    private static final VoxelShape UP_SHAPE = Shapes.box(CONNECTION_SIZE, CONNECTION_SIZE + BLOCK_SIZE, CONNECTION_SIZE, CONNECTION_SIZE + BLOCK_SIZE, 1.0f, CONNECTION_SIZE + BLOCK_SIZE);
    private static final VoxelShape WEST_SHAPE = Shapes.box(0.0f, CONNECTION_SIZE, CONNECTION_SIZE, CONNECTION_SIZE, CONNECTION_SIZE + BLOCK_SIZE, CONNECTION_SIZE + BLOCK_SIZE);
    private static final VoxelShape EAST_SHAPE = Shapes.box(CONNECTION_SIZE + BLOCK_SIZE, CONNECTION_SIZE, CONNECTION_SIZE, 1.0f, CONNECTION_SIZE + BLOCK_SIZE, CONNECTION_SIZE + BLOCK_SIZE);

    @Override /* This handles bounding box */
    public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext cc)
    {
        VoxelShape currentShape = DEFAULT_SHAPE;
//        if (state.getValue(UP)) currentShape = Shapes.join(currentShape, UP_SHAPE, BooleanOp.OR);
//        if (state.getValue(DOWN)) currentShape = Shapes.join(currentShape, DOWN_SHAPE, BooleanOp.OR);
//        if (state.getValue(NORTH)) currentShape = Shapes.join(currentShape, NORTH_SHAPE, BooleanOp.OR);
//        if (state.getValue(SOUTH)) currentShape = Shapes.join(currentShape, SOUTH_SHAPE, BooleanOp.OR);
//        if (state.getValue(WEST)) currentShape = Shapes.join(currentShape, WEST_SHAPE, BooleanOp.OR);
//        if (state.getValue(EAST)) currentShape = Shapes.join(currentShape, EAST_SHAPE, BooleanOp.OR);
        return currentShape;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighbor, boolean idk)
    {
        super.neighborChanged(state, level, pos, block, neighbor, idk);
        if (level.getBlockEntity(pos) instanceof BE_MultiPipe pipe) pipe.neighborChanged(neighbor);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState p_49928_, BlockGetter p_49929_, BlockPos p_49930_)
    { return true; }

    @Override
    public float getShadeBrightness(BlockState p_60472_, BlockGetter p_60473_, BlockPos p_60474_)
    { return 1.0f; }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        return createTickerHelper(type, (BlockEntityType<BE_MultiPipe>) BlockRegistry.BLOCK_MULTI_PIPE.blockEntity().get(), BE_MultiPipe::tick);
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState)
    { return RenderShape.MODEL; }

    @Override
    public VoxelShape getOcclusionShape(BlockState p_60578_, BlockGetter p_60579_, BlockPos p_60580_)
    { return Shapes.empty(); }

    @Override
    public void onRemove(BlockState cState, Level level, BlockPos blockPos, BlockState newState, boolean isMoving)
    {
        if (cState.getBlock() != newState.getBlock())
        {
            BlockEntity ent = level.getBlockEntity(blockPos);
            if (ent instanceof BE_MultiPipe pipe)
                pipe.breakPipe(blockPos, level);
        }
        super.onRemove(cState, level, blockPos, newState, isMoving);
    }

    public boolean skipRendering(BlockState thisState, BlockState neighbor, Direction direction)
    { return neighbor.is(this); }

    @Override
    public InteractionResult use(BlockState cState, Level level, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult)
    {
        if (!level.isClientSide())
        {
            if (level.getBlockEntity(pos) instanceof BE_MultiPipe pipe)
            {
                ItemStack heldItem;
                if ((heldItem = player.getItemInHand(interactionHand)).is(Items.STICK))
                {
                    pipe.handleItemUsed(heldItem);
                } else {
//                    PacketManager.sendToPlayer(new MessageS2CPacket(Component.literal(clickZone + " " + adjustedLoc), true), (ServerPlayer) player);
//                    pipe.printInformation();
                }
            }
        }
        return InteractionResult.sidedSuccess(!level.isClientSide());
    }
}