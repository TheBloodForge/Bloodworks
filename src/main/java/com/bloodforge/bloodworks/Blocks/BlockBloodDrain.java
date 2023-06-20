package com.bloodforge.bloodworks.Blocks;

import com.bloodforge.bloodworks.Blocks.BlockEntities.BE_BloodDrain;
import com.bloodforge.bloodworks.Networking.MessageS2CPacket;
import com.bloodforge.bloodworks.Networking.PacketManager;
import com.bloodforge.bloodworks.Registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class BlockBloodDrain extends BlockMachineBase
{
    //0 = no macerator
    public static final IntegerProperty MACERATOR_LEVEL = IntegerProperty.create("macerator_level", 0, 1);
    public BlockBloodDrain()
    {
        super(Properties.of(Material.METAL).color(MaterialColor.METAL).noOcclusion());
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_55933_)
    {
        p_55933_.add(MACERATOR_LEVEL);
    }

    public BlockState getStateForPlacement(BlockPlaceContext placeContext)
    {
        return this.defaultBlockState().setValue(MACERATOR_LEVEL, 0);
    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_)
    {
        return Shapes.box(0, 0, 0, 1, 0.1, 1);
    }

    // -------- Block entity stuff --------

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return BlockRegistry.BLOCK_BLOOD_DRAIN.blockEntity().get().create(pos, state);
    }


    public RenderShape getRenderShape(BlockState p_49232_) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        if(level.isClientSide()) return null;
        return createTickerHelper(type, (BlockEntityType<BE_BloodDrain>) BlockRegistry.BLOCK_BLOOD_DRAIN.blockEntity().get(), BE_BloodDrain::tickServer);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState cState, Level level, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult)
    {
        if (!level.isClientSide())
        {
            if(cState.getValue(MACERATOR_LEVEL) == 0)
            {
                level.setBlockAndUpdate(pos, this.defaultBlockState().setValue(MACERATOR_LEVEL, 1));
            }
            else
            {
                level.setBlockAndUpdate(pos, this.defaultBlockState().setValue(MACERATOR_LEVEL, 0));
            }
            PacketManager.sendToPlayer(new MessageS2CPacket(Component.literal(level.getBlockState(pos).getValue(MACERATOR_LEVEL).toString()),false),(ServerPlayer) player);
            return super.use(cState, level, pos, player, interactionHand, blockHitResult);
        }

        return InteractionResult.sidedSuccess(!level.isClientSide());
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