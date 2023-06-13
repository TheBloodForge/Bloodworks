package com.bloodforge.bloodworks.Blocks.BlockEntities;

import com.bloodforge.bloodworks.Common.Config.BloodworksCommonConfig;
import com.bloodforge.bloodworks.Common.IOMode;
import com.bloodforge.bloodworks.Networking.MessageS2CPacket;
import com.bloodforge.bloodworks.Networking.PacketManager;
import com.bloodforge.bloodworks.Registry.BlockRegistry;
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
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.bloodforge.bloodworks.Blocks.BlockEnergyPipe.*;

public class BE_EnergyPipe extends BlockEntity implements IEnergyStorage
{
    private int cooldown = 8;
    private EnergyStorage battery = new EnergyStorage(1000, 500, 500);
    /* Down Up South North West East ??? Why Mojang @Direction.get3DDataValue() */
    boolean[] forcedDisconnects = new boolean[]{false, false, false, false, false, false};
    private IOMode[] pipeModes = new IOMode[]{IOMode.NONE, IOMode.NONE, IOMode.NONE, IOMode.NONE, IOMode.NONE, IOMode.NONE};

    public BE_EnergyPipe(BlockPos pos, BlockState state)
    {
        super(BlockRegistry.BLOCK_ENERGY_PIPE.blockEntity().get(), pos, state);
        setChanged();
    }

    private void tick()
    {
        for (Direction value : Direction.values())
        {
            if (getLevel().getBlockEntity(getBlockPos().relative(value)) == null) continue;
            if (pipeModes[value.get3DDataValue()] == IOMode.INPUT)
            {
                pullEnergy(value);
            }
            if (pipeModes[value.get3DDataValue()] == IOMode.OUTPUT)
            {
                pushEnergy(value);
            }
            if (pipeModes[value.get3DDataValue()] == IOMode.CONNECTED)
            {
                balanceEnergy(value);
            }
        }
    }

    private void balanceEnergy(Direction value)
    {
        if (getLevel().getBlockEntity(getBlockPos().relative(value)) instanceof BE_EnergyPipe pipe)
        {
            int energyToBalance = (getEnergyStored() + pipe.getEnergyStored()) / 2;
            if (pipe.getEnergyStored() > energyToBalance)
            {
                receiveEnergy(pipe.extractEnergy(pipe.getEnergyStored() - energyToBalance, false), false);
            }
            if (getEnergyStored() > energyToBalance)
            {
                pipe.receiveEnergy(extractEnergy(getEnergyStored() - energyToBalance, false), false);
            }
        }
    }

    private void pullEnergy(Direction value)
    {
        if (getLevel().getBlockEntity(getBlockPos().relative(value)).getCapability(ForgeCapabilities.ENERGY, value.getOpposite()).isPresent())
        {
//            System.out.println("Pulling");
            IEnergyStorage source = (IEnergyStorage) getLevel().getBlockEntity(getBlockPos().relative(value)).getCapability(ForgeCapabilities.ENERGY, value.getOpposite()).cast().resolve().get();
            if (source.canExtract())
                receiveEnergy(source.extractEnergy(Math.min(battery.getMaxEnergyStored() - battery.getEnergyStored(), 500), false), false);
        }
    }

    private void pushEnergy(Direction value)
    {
        if (getLevel().getBlockEntity(getBlockPos().relative(value)).getCapability(ForgeCapabilities.ENERGY, value.getOpposite()).isPresent())
        {
            IEnergyStorage destination = (IEnergyStorage) getLevel().getBlockEntity(getBlockPos().relative(value)).getCapability(ForgeCapabilities.ENERGY, value.getOpposite()).cast().resolve().get();
            if (destination.canReceive())
                destination.receiveEnergy(extractEnergy(500, false), false);
        }
    }

//######################################################################\\
//                           BELOW IS UTILITIES                         \\
//######################################################################\\


    private int getCooldown()
    {
        int cdr = BloodworksCommonConfig.TANK_COOLDOWN_REDUCTION.get();
        return Math.max(0, BloodworksCommonConfig.MAX_TANK_COOLDOWN.get() - cdr);
    }

    public static List<IEnergyStorage> getNeighborEnergyHandlers(BlockPos blockPos, Level level)
    {
        List<IEnergyStorage> handlers = new ArrayList<>();
        for (Direction value : Direction.values())
            if (level.getBlockEntity(blockPos.relative(value)) instanceof IEnergyStorage handler)
                handlers.add(handler);
        return handlers;
    }

    public static List<IEnergyStorage> getNeighborEnergyHandlersNotPipes(BlockPos blockPos, Level level)
    {
        List<IEnergyStorage> handlers = new ArrayList<>();
        for (Direction value : Direction.values())
            if (level.getBlockEntity(blockPos.relative(value)) instanceof IEnergyStorage handler
                    && !(level.getBlockEntity(blockPos.relative(value)) instanceof BE_EnergyPipe))
                handlers.add(handler);
        return handlers;
    }

    private static List<BE_EnergyPipe> getNearbyPipes(Level level, BlockPos pos)
    {
        List<BE_EnergyPipe> pipes = new ArrayList<>();
        for (Direction value : Direction.values())
            if (level.getBlockEntity(pos.relative(value)) instanceof BE_EnergyPipe pipe)
                pipes.add(pipe);
        return pipes;
    }

    public void printInformation()
    {
        PacketManager.sendToClients(new MessageS2CPacket(Component.literal("isConnected:" + getBlockState().getValues().values()), true));
//        if (PIPE_TANK == null) return;
//        PacketManager.sendToClients(new MessageS2CPacket(Component.literal(Component.translatable(getEnergyInTank(0).getTranslationKey()).getString() + ": " + getEnergyInTank(0).getAmount()), true));
    }

//######################################################################\\
//                     BELOW IS MANDATORY DEFAULTS                      \\
//                   DO NOT TOUCH, SHOULD NOT HAVE TO                   \\
//######################################################################\\
    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BE_EnergyPipe pipe)
    {
        if (level == null || level.isClientSide) return;
        List<BE_EnergyPipe> pipes;
        if (pipe.cooldown > 0) pipe.cooldown--;
        if (pipe.cooldown <= 0) pipe.tick();
    }

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

    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();
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
                    case 0 -> battery.getEnergyStored();
                    case 1 -> battery.getMaxEnergyStored();
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
            battery.receiveEnergy(value, false);
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
        if (cap == ForgeCapabilities.ENERGY)
        {
            return lazyEnergyHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
//        lazyEnergyHandler = LazyOptional.of(energyHandler);
    }

    @Override
    public void invalidateCaps()
    {
        super.invalidateCaps();
//        lazyEnergyHandler.invalidate();
    }

    public void updateState(BlockState oldState, BlockState newState)
    {
        if (oldState.getBlock() != BlockRegistry.BLOCK_ENERGY_PIPE.block().get() || newState.getBlock() != BlockRegistry.BLOCK_ENERGY_PIPE.block().get()) return;
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

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate)
    {
        return battery.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate)
    {
        return battery.extractEnergy(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored()
    {
        return battery.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored()
    {
        return battery.getMaxEnergyStored();
    }

    @Override
    public boolean canExtract()
    {
        return true;
    }

    @Override
    public boolean canReceive()
    {
        return true;
    }

    // ####################### END OF FORGE CAPABILITIES #######################
}