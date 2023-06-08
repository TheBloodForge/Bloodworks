package com.bloodforge.bloodworks.Blocks;

import com.bloodforge.bloodworks.Blocks.BlockEntities.BE_InfusionChamber;
import com.bloodforge.bloodworks.Globals;
import com.bloodforge.bloodworks.Registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class BlockInfusionChamber extends BlockMachineBase
{

    public BlockInfusionChamber()
    {
        super();
    }

    // -------- Block entity stuff --------

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return BlockRegistry.BLOCK_INFUSION_CHAMBER.blockEntity().get().create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212level, BlockState state, BlockEntityType<T> type)
    {
        return createTickerHelper(type, (BlockEntityType<BE_InfusionChamber>) BlockRegistry.BLOCK_INFUSION_CHAMBER.blockEntity().get(), BE_InfusionChamber::tick);
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
                ((BE_InfusionChamber) ent).dropInventoryContents();
            }
        }
        super.onRemove(cState, level, blockPos, newState, isMoving);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState cState, Level level, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult)
    {
        if (!level.isClientSide())
        {
            if (level.getBlockEntity(pos) instanceof BE_InfusionChamber machine)
            {
                FluidStack stack;
                if (player.getItemInHand(interactionHand).getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent())
                {
                    stack = FluidUtil.getFluidContained(player.getItemInHand(interactionHand)).get();
                    if (machine.isFluidValid(0, stack))
                    {
                        machine.fill(stack, IFluidHandler.FluidAction.EXECUTE);
                        return InteractionResult.sidedSuccess(!level.isClientSide());
                    }
                }
                NetworkHooks.openScreen(((ServerPlayer) player), machine, pos);
            }
        }

        return InteractionResult.sidedSuccess(!level.isClientSide());
    }


    public ResourceLocation getIdleSoundResourceLocation()
    {
        return new ResourceLocation(Globals.MODID, "bloodworks.blood_infuser.idle");
    }

    public boolean hasIdleSound()
    {
        return true;
    }
}