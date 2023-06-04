package com.bloodforge.bloodworks.Blocks;

import com.bloodforge.bloodworks.Blocks.BlockEntities.BE_FluidPipe;
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
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings({"unchecked", "deprecation"})
public class BlockFluidPipe extends BaseEntityBlock
{
    public static final BooleanProperty UP = BooleanProperty.create("up_connected");
    public static final BooleanProperty DOWN = BooleanProperty.create("down_connected");
    public static final BooleanProperty NORTH = BooleanProperty.create("north_connected");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south_connected");
    public static final BooleanProperty EAST = BooleanProperty.create("east_connected");
    public static final BooleanProperty WEST = BooleanProperty.create("west_connected");

    public BlockFluidPipe()
    {
        super(
                BlockBehaviour.Properties
                        .of(Material.STONE)
                        .strength(1f, 8f)
                        .sound(SoundType.METAL)
                        .noOcclusion()
        );

        registerDefaultState(stateDefinition.any()
                .setValue(UP, false)
                .setValue(DOWN, false)
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false)
        );
    }

    // -------- Block entity stuff --------

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return BlockRegistry.BLOCK_FLUID_PIPE.blockEntity().get().create(pos, state);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter blockGetter, List<Component> components, TooltipFlag tooltipFlag)
    {
        ClientUtils.AddAdditionalShiftInfo(components, "Transfer Rate: Unimplemented!");
        super.appendHoverText(stack, blockGetter, components, tooltipFlag);
    }

    @Override
    public boolean hasDynamicShape()
    {
        return true;
    }
    
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
        if (state.getValue(UP)) currentShape = Shapes.join(currentShape, UP_SHAPE, BooleanOp.OR);
        if (state.getValue(DOWN)) currentShape = Shapes.join(currentShape, DOWN_SHAPE, BooleanOp.OR);
        if (state.getValue(NORTH)) currentShape = Shapes.join(currentShape, NORTH_SHAPE, BooleanOp.OR);
        if (state.getValue(SOUTH)) currentShape = Shapes.join(currentShape, SOUTH_SHAPE, BooleanOp.OR);
        if (state.getValue(WEST)) currentShape = Shapes.join(currentShape, WEST_SHAPE, BooleanOp.OR);
        if (state.getValue(EAST)) currentShape = Shapes.join(currentShape, EAST_SHAPE, BooleanOp.OR);
        return currentShape;
    }

    @Override
    public VoxelShape getInteractionShape(BlockState p_60547_, BlockGetter p_60548_, BlockPos p_60549_)
    {
        return Shapes.box(0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f);
    }

    @Override
    public BlockState updateShape(BlockState p_60541_, Direction p_60542_, BlockState p_60543_, LevelAccessor p_60544_, BlockPos p_60545_, BlockPos p_60546_)
    { return super.updateShape(p_60541_, p_60542_, p_60543_, p_60544_, p_60545_, p_60546_); }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighbor, boolean idk)
    {
        super.neighborChanged(state, level, pos, block, neighbor, idk);
        BlockState newState = getRelevantState(level, pos, state);
        if (!state.getProperties().stream().allMatch(property -> state.getValue(property).equals(newState.getValue(property))))
            level.setBlockAndUpdate(pos, newState);
    }

    private Direction getDirection(BlockPos pos, BlockPos neighbor)
    {
        for (Direction dir : Direction.values())
            if (pos.relative(dir).toShortString().equalsIgnoreCase(neighbor.toShortString()))
                return dir;
        return Direction.UP;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState p_49928_, BlockGetter p_49929_, BlockPos p_49930_)
    { return true; }

    @Override
    public float getShadeBrightness(BlockState p_60472_, BlockGetter p_60473_, BlockPos p_60474_)
    { return 0.5f; }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        return createTickerHelper(type, (BlockEntityType<BE_FluidPipe>) BlockRegistry.BLOCK_FLUID_PIPE.blockEntity().get(), BE_FluidPipe::tick);
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState)
    { return RenderShape.MODEL; }

    @Override
    public VoxelShape getOcclusionShape(BlockState p_60578_, BlockGetter p_60579_, BlockPos p_60580_)
    { return Shapes.empty(); }

    @Override
    public VoxelShape getVisualShape(BlockState p_60479_, BlockGetter p_60480_, BlockPos p_60481_, CollisionContext p_60482_)
    { return Shapes.box(0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f); }

    @Override
    public void onRemove(BlockState cState, Level level, BlockPos blockPos, BlockState newState, boolean isMoving)
    {
        if (cState.getBlock() != newState.getBlock())
        {
            BlockEntity ent = level.getBlockEntity(blockPos);
            if (ent instanceof BE_FluidPipe pipe)
                pipe.breakPipe(blockPos, level);
        }
        super.onRemove(cState, level, blockPos, newState, isMoving);
    }

    public boolean skipRendering(BlockState thisState, BlockState neighbor, Direction direction)
    { return neighbor.is(this); }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    { return getRelevantState(context.getLevel(), context.getClickedPos(), null); }

    private BlockState getRelevantState(Level lvl, BlockPos bp, @Nullable BlockState st)
    {
        return defaultBlockState()
                .setValue(UP, hasConnection(lvl, bp, Direction.UP))
                .setValue(DOWN, hasConnection(lvl, bp, Direction.DOWN))
                .setValue(EAST, hasConnection(lvl, bp, Direction.EAST))
                .setValue(WEST, hasConnection(lvl, bp, Direction.WEST))
                .setValue(NORTH, hasConnection(lvl, bp, Direction.NORTH))
                .setValue(SOUTH, hasConnection(lvl, bp, Direction.SOUTH));
    }
    public boolean hasConnection(LevelAccessor world, BlockPos pos, Direction facing)
    {
        if (!(world.getBlockEntity(pos) instanceof BE_FluidPipe self)) return false;
        if (!(world.getBlockEntity(pos.relative(facing)) instanceof BE_FluidPipe other))
        {
            System.out.println("Not a pipe in " + facing.getName() + " returning : " + self.isForceConnected(facing));
            return self.isForceConnected(facing); }

        if (!shouldConnect(world, pos, facing) && self.isDisconnected(facing))
        {
            System.out.println("Returning False because " + (shouldConnect(world, pos, facing) ? "should connect" : "should not connect") + " and " + (self.isDisconnected(facing) ? " is disconnected." : " not disconnected."));
            return false;
        }

        System.out.println("Returning " + !other.isDisconnected(facing.getOpposite()));
        return !other.isDisconnected(facing.getOpposite());
    }

    public boolean shouldConnect(LevelAccessor world, BlockPos pos, Direction facing)
    { return world.getBlockEntity(pos.relative(facing)) instanceof BE_FluidPipe || canConnectTo(world, pos, facing); }

    public boolean isDisconnected(LevelAccessor world, BlockPos pos, Direction side) {
        if (world.getBlockEntity(pos) instanceof BE_FluidPipe self)
            return self.isDisconnected(side);
        return false;
    }

    public boolean canConnectTo(LevelAccessor world, BlockPos pos, Direction facing)
    { return world.getBlockEntity(pos) instanceof IFluidHandler; }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> bsd)
    { bsd.add(UP, DOWN, NORTH, SOUTH, EAST, WEST/*, TIER*/); }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState cState, Level level, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult)
    {
        if (!level.isClientSide())
        {
            if (level.getBlockEntity(pos) instanceof BE_FluidPipe pipe)
            {
                ItemStack heldItem;
                if ((heldItem = player.getItemInHand(interactionHand)).is(Items.STICK) || heldItem.is(Items.REDSTONE))
                {
                    handleModeSwitch(heldItem, pipe, blockHitResult, player, level, cState, pos);
                    return InteractionResult.sidedSuccess(!level.isClientSide());
                } else {
                    Vec3 adjustedLoc = blockHitResult.getLocation().subtract(new Vec3(pos.getX(), pos.getY(), pos.getZ()));
                    Direction dir = getClickedDir(adjustedLoc);
                    String clickZone = dir == null ? "Center" : dir.getName();
                    if (heldItem.isEmpty() && player.isCrouching() && dir != null)
                        pipe.setOutput(dir, level, player, interactionHand, blockHitResult);
//                    PacketManager.sendToPlayer(new MessageS2CPacket(Component.literal(clickZone + " " + adjustedLoc), true), (ServerPlayer) player);
//                    pipe.printInformation();
                }
            }
        }
        return InteractionResult.sidedSuccess(!level.isClientSide());
    }

    private Direction getClickedDir(Vec3 clickLoc)
    {
        if (UP_SHAPE.bounds().inflate(0.005f).contains(clickLoc)) return Direction.UP;
        if (DOWN_SHAPE.bounds().inflate(0.005f).contains(clickLoc)) return Direction.DOWN;
        if (NORTH_SHAPE.bounds().inflate(0.005f).contains(clickLoc)) return Direction.NORTH;
        if (SOUTH_SHAPE.bounds().inflate(0.005f).contains(clickLoc)) return Direction.SOUTH;
        if (EAST_SHAPE.bounds().inflate(0.005f).contains(clickLoc)) return Direction.EAST;
        if (WEST_SHAPE.bounds().inflate(0.005f).contains(clickLoc)) return Direction.WEST;
        return null;
    }

    private void handleModeSwitch(ItemStack heldItem, BE_FluidPipe pipe, BlockHitResult blockHitResult, Player player, Level level, BlockState cState, BlockPos pos)
    { pipe.setMode(blockHitResult.getDirection(), heldItem.is(Items.REDSTONE)); }

    @Override
    public void onBlockStateChange(LevelReader level, BlockPos pos, BlockState oldState, BlockState newState)
    {
        if (level.getBlockEntity(pos) instanceof BE_FluidPipe pipe)
        { pipe.updateState(oldState, newState); }
        super.onBlockStateChange(level, pos, oldState, newState);
    }

    public BooleanProperty sidedProperty(Direction side) {
        return switch (side)
        {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            case UP -> UP;
            case DOWN -> DOWN;
        };
    }
}