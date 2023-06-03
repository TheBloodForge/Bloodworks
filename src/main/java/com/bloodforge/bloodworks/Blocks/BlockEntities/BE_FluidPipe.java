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
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.bloodforge.bloodworks.Blocks.BlockFluidPipe.*;

public class BE_FluidPipe extends BlockEntity implements IFluidHandler
{
    private int cooldown = 8;
    private FluidTank PIPE_TANK;
    private HashMap<Direction, IOMode> pipeModes;
    private HashMap<IOMode, List<BE_FluidPipe>> connectedPipes;

    public BE_FluidPipe(BlockPos pos, BlockState state)
    {
        super(BlockRegistry.BLOCK_FLUID_PIPE.blockEntity().get(), pos, state);
        pipeModes = new HashMap<>();
        for (Direction value : Direction.values())
        { pipeModes.put(value, IOMode.NONE); }
        setChanged();
    }

    private FluidTank getTank()
    { return PIPE_TANK; }

    private void tick()
    {
        if (connectedPipes == null) return;
        validateTank();
        /*if (pipeModes == IOMode.INPUT && getFluidInTank(0).getAmount() > 0) {
            for (BE_FluidPipe be_fluidPipe : connectedPipes.get(IOMode.OUTPUT))
            {
                if (be_fluidPipe.getSpace() > 0) {
                    be_fluidPipe.fill(drain(250, FluidAction.EXECUTE), FluidAction.EXECUTE);
                }
            }
        }

        List<IFluidHandler> blocks;
        if (pipeModes == IOMode.OUTPUT && !(blocks = getNeighborFluidHandlersNotPipes(getBlockPos(), getLevel())).isEmpty()) {
            for (IFluidHandler block : blocks)
            {
                block.fill(drain(500, FluidAction.EXECUTE), FluidAction.EXECUTE);
            }
        }*/
    }

    private void validateTank()
    {
        if (pipeModes.containsValue(IOMode.INPUT) || pipeModes.containsValue(IOMode.OUTPUT))
        {
            if (PIPE_TANK == null)
                PIPE_TANK = new FluidTank(1000);
        }
    }

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
        if (PIPE_TANK == null) return;
        PacketManager.sendToClients(new MessageS2CPacket(Component.literal(Component.translatable(getFluidInTank(0).getTranslationKey()).getString() + ": " + getFluidInTank(0).getAmount() + getModeListStrings(IOMode.INPUT) + getModeListStrings(IOMode.OUTPUT)), true));
    }

    private String getModeListStrings(IOMode mode)
    {
        StringBuilder ret = new StringBuilder();
        if (connectedPipes.get(mode).isEmpty()) return " NO " + (mode == IOMode.OUTPUT ? "OUTPUTS" : "INPUTS");
        else for (BE_FluidPipe be_fluidPipe : connectedPipes.get(mode))
            ret.append((ret.length() == 0) ? "" : ", ").append(Arrays.toString(be_fluidPipe.worldPosition.toShortString().split(" ")));
        return (mode == IOMode.OUTPUT ? "OUTPUTS: " : "INPUTS: ") + ret;
    }

//######################################################################\\
//                     BELOW IS MANDATORY DEFAULTS                      \\
//                   DO NOT TOUCH, SHOULD NOT HAVE TO                   \\
//######################################################################\\
    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BE_FluidPipe pipe)
    {
        if (level == null || level.isClientSide || !pipe.shouldTick()) return;
        List<BE_FluidPipe> pipes;
        if ((pipes = getNearbyPipes(level, blockPos)).isEmpty())
            pipe.initMap();
        else pipe.copyData(pipes);
        if (pipe.cooldown > 0) pipe.cooldown--;
        if (pipe.cooldown <= 0) pipe.tick();
    }

    private boolean shouldTick()
    {
        return pipeModes.containsValue(IOMode.INPUT) ||
               pipeModes.containsValue(IOMode.OUTPUT) ||
               pipeModes.containsValue(IOMode.INPUT_POWERED) ||
               pipeModes.containsValue(IOMode.OUTPUT_POWERED);
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

    private void initMap()
    {
        connectedPipes = new HashMap<>();
        for (IOMode value : IOMode.values())
        { connectedPipes.put(value, new ArrayList<>()); }

        connectedPipes.get(IOMode.NONE).add(this);
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
        /*if (newState.getValue(BlockFluidPipe.NORTH))
        {
            if (pipeMode == IOMode.NONE)
            {
                PIPE_TANK = new FluidTank(1000);
                pushToMap(newState.getValue(BlockFluidPipe.OUTPUT));
            }
        }*/
    }

    private void pushToMap(IOMode value)
    {
//        connectedPipes.get(pipeModes).remove(this);
//        pipeModes = value;
//        connectedPipes.get(value).add(this);
    }

    public void breakPipe(BlockPos blockPos, Level level)
    {
//        if (connectedPipes == null) return;
//        connectedPipes.get(pipeModes).remove(this);
    }

    public void setMode(Direction direction, boolean isRedstone)
    {

    }

    public void connect(BlockState state, Direction dir)
    {
        System.out.println("Checking Connection");
        if (isConnected(dir)) return;
        System.out.println("Connecting...");
        BlockState oldState = state;
        BlockState newState = state.getBlock().defaultBlockState();
        newState.setValue(DOWN, oldState.getValue(DOWN).booleanValue()).setValue(UP, oldState.getValue(UP).booleanValue()).setValue(NORTH, oldState.getValue(NORTH).booleanValue())
                .setValue(EAST, oldState.getValue(EAST).booleanValue()).setValue(WEST, oldState.getValue(WEST).booleanValue()).setValue(SOUTH, oldState.getValue(SOUTH).booleanValue());
        switch (dir)
        {
            case DOWN -> {
                newState.setValue(DOWN, true);
            }
            case UP -> {
                newState.setValue(UP, true);
            }
            case NORTH -> {
                newState.setValue(NORTH, true);
            }
            case SOUTH -> {
                newState.setValue(SOUTH, true);
            }
            case WEST -> {
                newState.setValue(WEST, true);
            }
            case EAST -> {
                newState.setValue(EAST, true);
            }
        }
        System.out.println("Sending State");
        if (getLevel() != null)
            getLevel().setBlockAndUpdate(getBlockPos(), newState);
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

    /* Down Up South North West East ??? Why Mojang @Direction.get3DDataValue() */
    boolean[] forcedDisconnects = new boolean[]{false, false, false, false, false, false};
    public boolean isDisconnected(Direction dir) {
        return forcedDisconnects[dir.get3DDataValue()];
    }

    // ####################### END OF FORGE CAPABILITIES #######################
}