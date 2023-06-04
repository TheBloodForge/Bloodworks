package com.bloodforge.bloodworks.Blocks.BlockEntities;

import com.bloodforge.bloodworks.Common.Config.BloodworksCommonConfig;
import com.bloodforge.bloodworks.Common.IOMode;
import com.bloodforge.bloodworks.Networking.MessageS2CPacket;
import com.bloodforge.bloodworks.Networking.PacketManager;
import com.bloodforge.bloodworks.Registry.BlockRegistry;
import com.bloodforge.bloodworks.Registry.FluidRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.bloodforge.bloodworks.Blocks.BlockFluidPipe.*;

public class BE_FluidPipe extends BlockEntity implements IFluidHandler
{
    private int cooldown = 8;
    private FluidTank PIPE_TANK = new FluidTank(1000);
//    private List<BE_FluidPipe> outputPipes;
    /* Down Up South North West East ??? Why Mojang @Direction.get3DDataValue() */
    boolean[] forcedDisconnects = new boolean[]{false, false, false, false, false, false};
    private IOMode[] pipeModes = new IOMode[]{IOMode.NONE, IOMode.NONE, IOMode.NONE, IOMode.NONE, IOMode.NONE, IOMode.NONE};

    public BE_FluidPipe(BlockPos pos, BlockState state)
    {
        super(BlockRegistry.BLOCK_FLUID_PIPE.blockEntity().get(), pos, state);
        setChanged();
    }

    private FluidTank getTank()
    { return PIPE_TANK; }

    private void tick()
    {
        for (Direction value : Direction.values())
        {
            if (pipeModes[value.get3DDataValue()] == IOMode.INPUT)
            {
                pullFluid(value);
            }
            if (pipeModes[value.get3DDataValue()] == IOMode.OUTPUT)
            {
                pushFluid(value);
            }
            if (pipeModes[value.get3DDataValue()] == IOMode.CONNECTED)
            {
                balanceFluid(value);
            }
        }
    }

    private void balanceFluid(Direction value)
    {
        if (getLevel().getBlockEntity(getBlockPos().relative(value)) instanceof BE_FluidPipe pipe)
        {
            int volumeToBalance = (getTank().getFluidAmount() + pipe.getTank().getFluidAmount()) / 2;
            if (pipe.getTank().getFluidAmount() > volumeToBalance)
            {
                fill(pipe.drain(pipe.getTank().getFluidAmount() - volumeToBalance, FluidAction.EXECUTE), FluidAction.EXECUTE);
            }
            if (getTank().getFluidAmount() > volumeToBalance)
            {
                pipe.fill(drain(getTank().getFluidAmount() - volumeToBalance, FluidAction.EXECUTE), FluidAction.EXECUTE);
            }
        }
    }

    private void pullFluid(Direction value)
    {
        if (getLevel().getBlockEntity(getBlockPos().relative(value)) instanceof IFluidHandler source)
        {
            for (int i = 0; i < source.getTanks(); i++)
            {
                FluidStack sourceStack = source.getFluidInTank(i);
                if (isFluidValid(0, sourceStack) && getSpace() > 0)
                {
                    FluidStack drainedStack = source.drain(Math.min(getSpace(), getTransferRate()), FluidAction.EXECUTE);
                    fill(drainedStack, FluidAction.EXECUTE);
                }
            }
        }
    }

    private void pushFluid(Direction value)
    {
        if (getLevel().getBlockEntity(getBlockPos().relative(value)) instanceof IFluidHandler destination)
        {
            for (int i = 0; i < destination.getTanks(); i++)
            {
                int space = destination.getTankCapacity(i) - destination.getFluidInTank(i).getAmount();
                if (destination.isFluidValid(i, getFluidInTank(0)) && space > 0)
                {
                    FluidStack drainedStack = drain(Math.min(space, getTransferRate()), FluidAction.EXECUTE);
                    destination.fill(drainedStack, FluidAction.EXECUTE);
                }
            }
        }
    }

    private int getTransferRate()
    { return getTier() * BloodworksCommonConfig.TANK_TRANSFER_PER_ACTION.get(); }

    private int getTier()
    { return 1; }

    private int getSpace()
    { return getTank().getSpace(); }

//######################################################################\\
//                           BELOW IS UTILITIES                         \\
//######################################################################\\


    private int getCooldown()
    {
        int cdr = BloodworksCommonConfig.TANK_COOLDOWN_REDUCTION.get();
        return Math.max(0, BloodworksCommonConfig.MAX_TANK_COOLDOWN.get() - cdr);
    }

    public static List<IFluidHandler> getNeighborFluidHandlers(BlockPos blockPos, Level level)
    {
        List<IFluidHandler> handlers = new ArrayList<>();
        for (Direction value : Direction.values())
            if (level.getBlockEntity(blockPos.relative(value)) instanceof IFluidHandler handler)
                handlers.add(handler);
        return handlers;
    }

    public static List<IFluidHandler> getNeighborFluidHandlersNotPipes(BlockPos blockPos, Level level)
    {
        List<IFluidHandler> handlers = new ArrayList<>();
        for (Direction value : Direction.values())
            if (level.getBlockEntity(blockPos.relative(value)) instanceof IFluidHandler handler
                    && !(level.getBlockEntity(blockPos.relative(value)) instanceof BE_FluidPipe))
                handlers.add(handler);
        return handlers;
    }

    private static List<BE_FluidPipe> getNearbyPipes(Level level, BlockPos pos)
    {
        List<BE_FluidPipe> pipes = new ArrayList<>();
        for (Direction value : Direction.values())
            if (level.getBlockEntity(pos.relative(value)) instanceof BE_FluidPipe pipe)
                pipes.add(pipe);
        return pipes;
    }

    public void printInformation()
    {
        PacketManager.sendToClients(new MessageS2CPacket(Component.literal("isConnected:" + getBlockState().getValues().values()), true));
//        if (PIPE_TANK == null) return;
//        PacketManager.sendToClients(new MessageS2CPacket(Component.literal(Component.translatable(getFluidInTank(0).getTranslationKey()).getString() + ": " + getFluidInTank(0).getAmount()), true));
    }

//######################################################################\\
//                     BELOW IS MANDATORY DEFAULTS                      \\
//                   DO NOT TOUCH, SHOULD NOT HAVE TO                   \\
//######################################################################\\
    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BE_FluidPipe pipe)
    {
        if (level == null || level.isClientSide) return;
        List<BE_FluidPipe> pipes;
        if (pipe.cooldown > 0) pipe.cooldown--;
        if (pipe.cooldown <= 0) pipe.tick();
    }

    private void copyData(List<BE_FluidPipe> pipes)
    {
//        for (BE_FluidPipe pipe : pipes)
//        {
//            if (pipe.connectedPipes != null)
//            {
//                pipe.connectedPipes.get(pipeModes).add(this);
//                connectedPipes = pipe.connectedPipes;
//                break;
//            }
//        }
    }

    @Override
    public int getTanks()
    { return 1; }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank)
    { return getTank().getFluid(); }

    @Override
    public int getTankCapacity(int tank)
    { return getTank().getCapacity(); }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack)
    { return getTank().isFluidValid(stack); }

    @Override
    public int fill(FluidStack resource, FluidAction action)
    { return getTank().fill(resource, action); }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action)
    { return getTank().drain(resource, action); }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action)
    { return getTank().drain(maxDrain, action); }

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        if (level == null || level.isClientSide) return;
        super.saveAdditional(tag);
    }

    // ####################### FORGE CAPABILITIES #######################

    private LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();
    protected final ContainerData data = new ContainerData()
    {
        @Override
        public int get(int index)
        {
            return getContainerData(index);
        }

        @Override
        public void set(int index, int value)
        {
            setContainerData(index, value);
        }

        @Override
        public int getCount()
        {
            return getContainerCount();
        }
    };

    public ContainerData getContainerData()
    {
        return this.data;
    }

    /**
     * this is the data accessor for the game to save the data
     */
    public int getContainerData(int index)
    {
        return switch (index)
                {
                    case 0 -> getTank().getFluidAmount();
                    case 1 -> getTank().getCapacity();
                    default -> 0;
                };
    }

    /**
     * this sets the values relevant on loading
     */
    public void setContainerData(int index, int value)
    {
        if (index == 0)
        {
            this.fill(new FluidStack(FluidRegistry.FLUID_BLOOD.source.get().getSource(), value), FluidAction.EXECUTE);
        }
    }

    /**
     * this returns the number of data entries to be saved
     */
    public int getContainerCount()
    {
        return 2;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
    {
        if (cap == ForgeCapabilities.FLUID_HANDLER)
        {
            return lazyFluidHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
        lazyFluidHandler = LazyOptional.of(this::getTank);
    }

    @Override
    public void invalidateCaps()
    {
        super.invalidateCaps();
        lazyFluidHandler.invalidate();
    }

    public void updateState(BlockState oldState, BlockState newState)
    {
        if (oldState.getBlock() != BlockRegistry.BLOCK_FLUID_PIPE.block().get() || newState.getBlock() != BlockRegistry.BLOCK_FLUID_PIPE.block().get()) return;
        System.out.println("updating modes");
        if (newState.getValue(NORTH))
        {
            System.out.println("Connecting North.");
            if (pipeModes[Direction.NORTH.get3DDataValue()] == IOMode.NONE)
                pipeModes[Direction.NORTH.get3DDataValue()] = IOMode.CONNECTED;
        }
        else if (oldState.getValue(NORTH))
            pipeModes[Direction.NORTH.get3DDataValue()] = IOMode.NONE;
        if (newState.getValue(SOUTH))
        {
            System.out.println("Connecting South.");
            if (pipeModes[Direction.SOUTH.get3DDataValue()] == IOMode.NONE)
                pipeModes[Direction.SOUTH.get3DDataValue()] = IOMode.CONNECTED;
        }
        else if (oldState.getValue(SOUTH))
            pipeModes[Direction.SOUTH.get3DDataValue()] = IOMode.NONE;
        if (newState.getValue(UP))
        {
            System.out.println("Connecting Up.");
            if (pipeModes[Direction.UP.get3DDataValue()] == IOMode.NONE)
                pipeModes[Direction.UP.get3DDataValue()] = IOMode.CONNECTED;

        }
        else if (oldState.getValue(UP))
            pipeModes[Direction.UP.get3DDataValue()] = IOMode.NONE;
        if (newState.getValue(DOWN))
        {
            System.out.println("Connecting Down.");
            if (pipeModes[Direction.DOWN.get3DDataValue()] == IOMode.NONE)
                pipeModes[Direction.DOWN.get3DDataValue()] = IOMode.CONNECTED;
        }
        else if (oldState.getValue(DOWN))
            pipeModes[Direction.DOWN.get3DDataValue()] = IOMode.NONE;
        if (newState.getValue(EAST))
        {
            System.out.println("Connecting East.");
            if (pipeModes[Direction.EAST.get3DDataValue()] == IOMode.NONE)
                pipeModes[Direction.EAST.get3DDataValue()] = IOMode.CONNECTED;
        }
        else if (oldState.getValue(EAST))
            pipeModes[Direction.EAST.get3DDataValue()] = IOMode.NONE;
        if (newState.getValue(WEST))
        {
            System.out.println("Connecting West.");
            if (pipeModes[Direction.WEST.get3DDataValue()] == IOMode.NONE)
                pipeModes[Direction.WEST.get3DDataValue()] = IOMode.CONNECTED;
        }
        else if (oldState.getValue(WEST))
            pipeModes[Direction.WEST.get3DDataValue()] = IOMode.NONE;
    }

    public void breakPipe(BlockPos blockPos, Level level)
    {
//        if (connectedPipes == null) return;
//        connectedPipes.get(pipeModes).remove(this);
    }

    public void setMode(Direction direction, boolean isRedstone)
    {

    }

    private boolean isConnected(Direction dir)
    {
        return switch (dir)
        {
            case DOWN -> getBlockState().getValue(DOWN);
            case UP -> getBlockState().getValue(UP);
            case NORTH -> getBlockState().getValue(NORTH);
            case SOUTH -> getBlockState().getValue(SOUTH);
            case WEST -> getBlockState().getValue(WEST);
            case EAST -> getBlockState().getValue(EAST);
        };
    }

    public boolean isDisconnected(Direction dir) {
        return forcedDisconnects[dir.get3DDataValue()];
    }

    public void setOutput(Direction dir, Level level, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        pipeModes[dir.get3DDataValue()] = IOMode.getNext(pipeModes[dir.get3DDataValue()]);
        PacketManager.sendToClients(new MessageS2CPacket(Component.literal(pipeModes[dir.get3DDataValue()].name()), false));
        level.setBlockAndUpdate(getBlockPos(), getBlockState().getBlock().getStateForPlacement(new BlockPlaceContext(level, player, hand, player.getItemInHand(hand), hitResult)));
    }

    public boolean isForceConnected(Direction facing)
    {
        return pipeModes[facing.get3DDataValue()] != IOMode.NONE;
    }

    // ####################### END OF FORGE CAPABILITIES #######################
}