package com.bloodforge.bloodworks.Blocks;

import com.bloodforge.bloodworks.Blocks.BlockEntities.BE_Battery;
import com.bloodforge.bloodworks.Blocks.BlockEntities.BE_StirlingGenerator;
import com.bloodforge.bloodworks.Networking.MessageS2CPacket;
import com.bloodforge.bloodworks.Networking.PacketManager;
import com.bloodforge.bloodworks.Registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockBattery extends BlockMachineBase
{
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    //0 = standalone
    //1 = bottom
    //2 = middle
    //3 = top
    public static final IntegerProperty PILLAR_POS = IntegerProperty.create("pillar_pos", 0, 3);

    public BlockBattery()
    {
        super(Properties.of(Material.METAL).color(MaterialColor.METAL).noOcclusion());
    }

    // -------- Block entity stuff --------

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return BlockRegistry.BLOCK_STIRLING_GENERATOR.blockEntity().get().create(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_55933_) {
        p_55933_.add(AXIS).add(PILLAR_POS);
    }

    public BlockState getStateForPlacement(BlockPlaceContext placeContext)
    {
        Direction.Axis axis = placeContext.getClickedFace().getAxis();
        int pillarPos = getPillarPos(placeContext.getLevel(), placeContext.getClickedPos(), axis);
        return this.defaultBlockState().setValue(AXIS, axis).setValue(PILLAR_POS, pillarPos);
    }

    private int getPillarPos(Level level, BlockPos pos, Direction.Axis axis)
    {
        boolean iF = level.getBlockState(pos.relative(axis, -1 )).getBlock() == BlockRegistry.BLOCK_BATTERY.block().get() && level.getBlockState(pos.relative(axis, -1 )).getValue(AXIS) == axis;
        boolean iB = level.getBlockState(pos.relative(axis, 1)).getBlock() == BlockRegistry.BLOCK_BATTERY.block().get() && level.getBlockState(pos.relative(axis, 1 )).getValue(AXIS) == axis;
        if(iF && iB)
        {
            return 2;
        }
        if(iF && !iB)
        {
            return 3;
        }
        if(!iF && iB)
        {
            return 1;
        }
        return 0;
    }

    public RenderShape getRenderShape(BlockState p_49232_) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        if(level.isClientSide()) return null;
        return createTickerHelper(type, (BlockEntityType<BE_Battery>) BlockRegistry.BLOCK_BATTERY.blockEntity().get(), BE_Battery::tickServer);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState cState, Level level, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult)
    {
        if (!level.isClientSide())
        {
            //todo: tell client gen stats
            if(level.getBlockEntity(pos) instanceof BE_StirlingGenerator stirlingGenerator)
            {
                PacketManager.sendToPlayer(new MessageS2CPacket(Component.literal(
                        stirlingGenerator.battery.getStored() + "/" + stirlingGenerator.battery.getCapacity() + " | +"+stirlingGenerator.energyGeneration+"FE/t"),false),
                        (ServerPlayer) player);
            }
            return super.use(cState, level, pos, player, interactionHand, blockHitResult);
        }

        return InteractionResult.sidedSuccess(!level.isClientSide());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block newBlock, @NotNull BlockPos neighbor, boolean p_60514_)
    {
        super.neighborChanged(state, level, pos, newBlock, neighbor, p_60514_);
        if (level.isClientSide()) return;
        int pillarPos = getPillarPos(level, pos, state.getValue(AXIS));
        if (pillarPos != state.getValue(PILLAR_POS))
        {
            BlockState newBlockState = state.getBlock().defaultBlockState().setValue(AXIS, state.getValue(AXIS)).setValue(PILLAR_POS, pillarPos);
            level.setBlockAndUpdate(pos, newBlockState);
        }
    }

    public boolean hasIdleSound()
    {
        return false;
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
        super.animateTick(blockState, level, blockPos, randomSource);
    }
}