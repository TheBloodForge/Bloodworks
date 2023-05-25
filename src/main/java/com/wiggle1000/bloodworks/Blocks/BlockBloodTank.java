package com.wiggle1000.bloodworks.Blocks;

import com.wiggle1000.bloodworks.Blocks.BlockEntities.BE_BloodTank;
import com.wiggle1000.bloodworks.Blocks.BlockEntities.BE_InfusionChamber;
import com.wiggle1000.bloodworks.Registry.BlockEntityRegistry;
import com.wiggle1000.bloodworks.Registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class BlockBloodTank extends BlockMachineBase
{

    public BlockBloodTank()
    {
        super();
    }

    // -------- Block entity stuff --------

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return BlockEntityRegistry.BE_BLOOD_TANK.get().create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212level, BlockState state, BlockEntityType<T> type)
    {
        return createTickerHelper(type, BlockEntityRegistry.BE_BLOOD_TANK.get(), BE_BloodTank::tick);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState cState, Level level, BlockPos blockPos, BlockState newState, boolean isMoving)
    {
        if (cState.getBlock() != newState.getBlock())
        {
            BlockEntity ent = level.getBlockEntity(blockPos);
            if (ent instanceof BE_InfusionChamber)
            {
                CompoundTag nbt = ent.serializeNBT();
                ItemStack stack = new ItemStack(ItemRegistry.BLOCK_BLOOD_TANK.get());
                stack.deserializeNBT(nbt);
                ItemEntity entity = new ItemEntity(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), stack);
                level.addFreshEntity(entity);
            }
        }
        super.onRemove(cState, level, blockPos, newState, isMoving);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState cState, Level level, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult)
    {
        if(!level.isClientSide()) {
            if(level.getBlockEntity(pos) instanceof BE_BloodTank machine) {
                FluidStack stack;
                if (player.getItemInHand(interactionHand).getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent()) {
                    stack = FluidUtil.getFluidContained(player.getItemInHand(interactionHand)).get();
                    if (machine.isFluidValid(0, stack))
                    {
                        machine.fill(stack, IFluidHandler.FluidAction.EXECUTE);
                        return InteractionResult.sidedSuccess(!level.isClientSide());
                    }
                }
            }
        }

        return InteractionResult.sidedSuccess(!level.isClientSide());
    }
}