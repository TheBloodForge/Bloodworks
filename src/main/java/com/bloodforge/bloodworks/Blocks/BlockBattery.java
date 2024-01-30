package com.bloodforge.bloodworks.Blocks;

import com.bloodforge.bloodworks.Blocks.BlockEntities.BE_Battery;
import com.bloodforge.bloodworks.Networking.MessageS2CPacket;
import com.bloodforge.bloodworks.Networking.PacketManager;
import com.bloodforge.bloodworks.Registry.BlockRegistry;
import com.bloodforge.bloodworks.Registry.SoundRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
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
    public static final BooleanProperty IS_OUTPUT = BooleanProperty.create("is_output");
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
        return BlockRegistry.BLOCK_BATTERY.blockEntity().get().create(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_55933_) {
        p_55933_.add(AXIS).add(PILLAR_POS).add(IS_OUTPUT);
    }

    public BlockState getStateForPlacement(BlockPlaceContext placeContext)
    {
        Direction.Axis axis = placeContext.getClickedFace().getAxis();
        int pillarPos = getPillarPos(placeContext.getLevel(), placeContext.getClickedPos(), axis);
        BlockState placedOff = placeContext.getLevel().getBlockState(placeContext.getClickedPos().relative(placeContext.getClickedFace().getOpposite()));
        boolean isOutput = placeContext.getClickedFace().getAxisDirection() == Direction.AxisDirection.POSITIVE;
        if(placedOff.getBlock() == BlockRegistry.BLOCK_BATTERY.block().get())
        {
            isOutput = true;
        }
        return this.defaultBlockState().setValue(AXIS, axis).setValue(PILLAR_POS, pillarPos).setValue(IS_OUTPUT, isOutput);
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

            if(player.isCrouching())
            {
                BlockState newBlockState = cState.getBlock().defaultBlockState().setValue(AXIS, cState.getValue(AXIS)).setValue(PILLAR_POS, cState.getValue(PILLAR_POS)).setValue(IS_OUTPUT, !cState.getValue(IS_OUTPUT));
                level.setBlockAndUpdate(pos, newBlockState);
                if(newBlockState.getValue(IS_OUTPUT))
                {
                    PacketManager.playSoundToClients(SoundRegistry.WRENCH_TIGHTEN, SoundSource.PLAYERS, pos, 1.0F, 1.0F);
                    PacketManager.sendToPlayer(new MessageS2CPacket(Component.translatable("ui.bloodworks.set_to_output").withStyle(ChatFormatting.RED),false), (ServerPlayer) player);
                }
                else
                {
                    PacketManager.playSoundToClients(SoundRegistry.WRENCH_LOOSEN, SoundSource.PLAYERS, pos, 1.0F, 1.0F);
                    PacketManager.sendToPlayer(new MessageS2CPacket(Component.translatable("ui.bloodworks.set_to_input").withStyle(ChatFormatting.BLUE),false), (ServerPlayer) player);
                }
                return InteractionResult.sidedSuccess(!level.isClientSide());
            }
            else if(level.getBlockEntity(pos) instanceof BE_Battery battery)
            {
                PacketManager.sendToPlayer(new MessageS2CPacket(Component.literal(
                        battery.battery.getStored() + "/" + battery.battery.getCapacity() + " RF"),false),
                        (ServerPlayer) player);
                return InteractionResult.PASS;
            }
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
            boolean isOut = state.getValue(IS_OUTPUT);
            if(state.getValue(PILLAR_POS) == 0) //if was single battery
            {
                isOut = false; //fixes placement of multiple batteries causing inconsistent result based on direction of placement
            }
            BlockState newBlockState = state.getBlock().defaultBlockState().setValue(AXIS, state.getValue(AXIS)).setValue(PILLAR_POS, pillarPos).setValue(IS_OUTPUT, isOut);
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