package com.wiggle1000.bloodworks.Blocks.BlockEntities;

import com.wiggle1000.bloodworks.Items.TankItem;
import com.wiggle1000.bloodworks.Networking.FluidSyncS2CPacket;
import com.wiggle1000.bloodworks.Networking.PacketManager;
import com.wiggle1000.bloodworks.Registry.BlockEntityRegistry;
import com.wiggle1000.bloodworks.Registry.FluidRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
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
import java.util.List;

public class BE_BloodTank extends BlockEntity implements IFluidHandler
{
    static int DEFAULT_CAPACITY = 10000;
    private LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();
    boolean isParent = false;
    BlockPos parentPos = null;

    public final List<BlockPos> children = new ArrayList<>();
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

    public BE_BloodTank(BlockPos pos, BlockState state)
    {
        super(BlockEntityRegistry.BE_BLOOD_TANK.get(), pos, state);
    }
    public BE_BloodTank(TankItem titem, BlockPos pos, BlockState state)
    {
        super(BlockEntityRegistry.BE_BLOOD_TANK.get(), pos, state);
//        titem.get
    }


    public ContainerData getContainerData()
    { return this.data; }

    /**
     * this is the data accessor for the game to save the data
     */
    public int getContainerData(int index)
    {
        return switch (index)
                {
                    case 0 -> this.FLUID_TANK.getFluidAmount();
                    case 1 -> this.FLUID_TANK.getCapacity();
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
    { return 2; }

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
        lazyFluidHandler = LazyOptional.of(() -> FLUID_TANK);
    }

    @Override
    public void invalidateCaps()
    {
        super.invalidateCaps();
        lazyFluidHandler.invalidate();
    }

    //  NBT STUFF
    @Override
    protected void saveAdditional(CompoundTag nbt)
    {
        if (FLUID_TANK != null)
            nbt = FLUID_TANK.writeToNBT(nbt);
        nbt.putBoolean("isParent", isParent);
        if (!isParent && parentPos != null)
            nbt.putIntArray("parentPos", new int[]{parentPos.getX(), parentPos.getY(), parentPos.getZ()});
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        isParent = nbt.getBoolean("isParent");
        if (!isParent)
        {
            int[] posArr = nbt.getIntArray("parentPos");
            parentPos = new BlockPos(posArr[0], posArr[1], posArr[2]);
        } else
        {
            createParentTank();
            FLUID_TANK.readFromNBT(nbt);
        }
        super.load(nbt);
    }

    public void readData(CompoundTag nbt)
    {
        isParent = true;
        FLUID_TANK = new FluidTank(10000).readFromNBT(nbt);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BE_BloodTank entity)
    {
        if (level.isClientSide()) return;
        if (!entity.ensureTank()) return;

        setChanged(level, blockPos, blockState);
        List<IFluidHandler> fluidConsumers = getNeighborFluidHandlers(blockPos, level);
        if (fluidConsumers.isEmpty()) return;

        for (IFluidHandler fluidConsumer : fluidConsumers)
            fluidConsumer.fill(new FluidStack(entity.FLUID_TANK.getFluid().getRawFluid(), 1000), FluidAction.EXECUTE);
    }

    private FluidTank FLUID_TANK = null;

    @Override
    public int getTanks()
    {
        return 1;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank)
    {
        return FLUID_TANK == null ? FluidStack.EMPTY : FLUID_TANK.getFluid();
    }

    @Override
    public int getTankCapacity(int tank)
    {
        return FLUID_TANK == null ? 0 : FLUID_TANK.getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack)
    {
        return FLUID_TANK != null && FLUID_TANK.isFluidValid(stack);
    }

    @Override
    public int fill(FluidStack resource, IFluidHandler.FluidAction action)
    {
        if (!isFluidValid(1, resource) || resource.getAmount() <= 0)
        {
            return 0;
        }
        return FLUID_TANK.fill(resource, action);
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, IFluidHandler.FluidAction action)
    {
        return FLUID_TANK.drain(maxDrain, action);
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action)
    {
        return FLUID_TANK.drain(resource, action);
    }

    private static List<BE_BloodTank> getNeighborTanks(BlockPos blockPos, Level level)
    {
        List<BE_BloodTank> tanks = new ArrayList<>();
        for (Direction value : Direction.values())
            if (level.getBlockEntity(blockPos.relative(value)) instanceof BE_BloodTank tank)
                tanks.add(tank);
        return tanks;
    }

    public static List<IFluidHandler> getNeighborFluidHandlers(BlockPos blockPos, Level level)
    {
        List<IFluidHandler> handlers = new ArrayList<>();
        for (Direction value : Direction.values())
            if (    level.getBlockEntity(blockPos.relative(value)) instanceof IFluidHandler handler
                && !(level.getBlockEntity(blockPos.relative(value)) instanceof BE_BloodTank))
                handlers.add(handler);
        return handlers;
    }


    public void breakTank(BlockPos pos, Level level)
    {
        if (isParent)
            if (!getNeighborTanks(pos, level).isEmpty()) {
                getNeighborTanks(pos, level).get(0).adoptChildren(getChildren());
            }
        if (FLUID_TANK != null && !FLUID_TANK.getFluid().isEmpty())
        {
            int numTanks = FLUID_TANK.getCapacity() / DEFAULT_CAPACITY;
            int relativeStorage = FLUID_TANK.getFluidAmount() / numTanks;
            FLUID_TANK.setCapacity(FLUID_TANK.getCapacity() - DEFAULT_CAPACITY);
            FLUID_TANK.drain(relativeStorage, FluidAction.EXECUTE);
        }
    }

    private boolean ensureTank()
    {
        if (FLUID_TANK == null)
            setTank(getBlockPos(), level);
        return FLUID_TANK != null;
    }

    private void setTank(BlockPos blockPos, Level level)
    {
        if (!isParent && parentPos != null)
        {
            FLUID_TANK = getParentTank();
            syncFluid();
            return;
        }
        if (!getNeighborTanks(blockPos, level).isEmpty()) {
            for (BE_BloodTank neighborTank : getNeighborTanks(blockPos, level))
            {
                if (!neighborTank.isParent && neighborTank.parentPos != null) {
                    addTank(this, neighborTank.getParent());
                } else if (neighborTank.isParent) {
                    addTank(this, neighborTank);
                }
            }
            syncFluid();
        } else {
            createParentTank();
            isParent = true;
            syncFluid();
        }
    }

    private void createParentTank()
    {
        FLUID_TANK = new FluidTank(DEFAULT_CAPACITY)
        {
            @Override
            public boolean isFluidValid(FluidStack stack)
            {
                return getFluid().getAmount() == 0 || stack.getFluid() == getFluid().getFluid();
            }

            @Override
            protected void onContentsChanged()
            {
                syncFluid();
                setChanged();
                super.onContentsChanged();
            }

            @Override
            public int getCapacity()
            {
                return super.getCapacity();
            }
        };
    }

    public void syncFluid()
    {
        PacketManager.sendToClients(new FluidSyncS2CPacket(getFluidInTank(0), getBlockPos()));
    }

    public static void addTank(BE_BloodTank tank, BE_BloodTank parent)
    {
        if (tank == null || parent == null) return;
        tank.FLUID_TANK = parent.FLUID_TANK;
        tank.parentPos = parent.getBlockPos();
        parent.addChild(tank);
        tank.FLUID_TANK.setCapacity(tank.FLUID_TANK.getCapacity() + DEFAULT_CAPACITY);
    }

    private void adoptChildren(List<BlockPos> childPoses)
    {
        isParent = true;
        children.clear();
        children.addAll(childPoses);

        for (BlockPos child : children)
            if (getLevel().getBlockEntity(child) instanceof BE_BloodTank childTank)
                childTank.parentPos = getBlockPos();
    }

    private void addChild(BE_BloodTank be_bloodTank)
    { if (isParent) children.add(be_bloodTank.getBlockPos()); }

    private FluidTank getParentTank()
    { return getParent().FLUID_TANK; }

    public List<BlockPos> getChildren()
    { return children; }

    public BE_BloodTank getParent()
    { return isParent ? this : getTankAtPos(parentPos, getLevel()); }

    private FluidTank getTank()
    { return FLUID_TANK; }

    public void setFluid(FluidStack fluidStack)
    { createParentTank(); FLUID_TANK.setFluid(fluidStack); }

    private BE_BloodTank getTankAtPos(BlockPos parentPos, Level level)
    {
        if (level.getBlockEntity(parentPos) instanceof BE_BloodTank bloodTank)
            return bloodTank;

        return null;
    }
}