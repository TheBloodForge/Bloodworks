package com.wiggle1000.bloodworks.Blocks;

import com.wiggle1000.bloodworks.Blocks.BlockEntities.BlockEntityMachineInfusionChamber;
import com.wiggle1000.bloodworks.Globals;
import com.wiggle1000.bloodworks.Registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class BlockMachineInfusionChamber extends BlockMachineBase
{

    public BlockMachineInfusionChamber(Properties properties)
    {
        super(properties);
    }

    // -------- Block entity stuff --------

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new BlockEntityMachineInfusionChamber(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, BlockEntityRegistry.BLOCK_ENTITY_INFUSION_CHAMBER.get(), BlockEntityMachineInfusionChamber::tick);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState cState, Level level, BlockPos blockPos, BlockState newState, boolean isMoving)
    {
        if (cState.getBlock() != newState.getBlock())
        {
            BlockEntity ent = level.getBlockEntity(blockPos);
            if(ent instanceof BlockEntityMachineInfusionChamber)
            {
                ((BlockEntityMachineInfusionChamber)ent).dropInventoryContents();
            }
        }
        super.onRemove(cState, level, blockPos, newState, isMoving);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState cState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult)
    {
        if(level.isClientSide()) return InteractionResult.sidedSuccess(level.isClientSide());

        BlockEntity entity = level.getBlockEntity(blockPos);
        if(!(entity instanceof BlockEntityMachineInfusionChamber))
        {
            Globals.LogError("Uh oh, a " + this.getName().getString() + " had the wrong tileEntity at " + blockPos);
            return InteractionResult.FAIL;
        }

        NetworkHooks.openScreen((ServerPlayer) player, (BlockEntityMachineInfusionChamber) entity, blockPos);

        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}