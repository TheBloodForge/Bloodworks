package com.wiggle1000.bloodworks.Blocks.BlockEntities;

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

public class BE_BloodTank extends BlockEntity implements IFluidHandler
{
    int DEFAULT_CAPACITY = 10000;
    private LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();
    protected final ContainerData data = new ContainerData()
    {
        @Override
        public int get(int index)
        { return getContainerData(index); }

        @Override
        public void set(int index, int value)
        { setContainerData(index, value); }

        @Override
        public int getCount()
        { return getContainerCount(); }
    };
    public BE_BloodTank(BlockPos pos, BlockState state)
    {
        super(BlockEntityRegistry.BE_BLOOD_TANK.get(), pos, state);
    }


    public ContainerData getContainerData() {
        return this.data;
    }

    /** this is the data accessor for the game to save the data */
    public int getContainerData(int index) {
        return switch (index) {
            case 0 -> this.FLUID_TANK.getFluidAmount();
            case 1 -> this.FLUID_TANK.getCapacity();
            default -> 0;
        };
    }

    /** this sets the values relevant on loading */
    public void setContainerData(int index, int value)
    {
        switch(index) {
            case 0 -> this.fill(new FluidStack(FluidRegistry.FLUID_BLOOD.source.get().getSource(), value), FluidAction.EXECUTE);
            default -> {}
        }
    }

    /** this returns the number of data entries to be saved */
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

    @Override
    protected void saveAdditional(CompoundTag nbt)
    {
        if (FLUID_TANK != null)
            nbt = FLUID_TANK.writeToNBT(nbt);
        super.saveAdditional(nbt);
    }

    @Override
    public void setRemoved()
    {
        if (FLUID_TANK != null)
            FLUID_TANK.setCapacity(FLUID_TANK.getCapacity() - DEFAULT_CAPACITY);
        if (FLUID_TANK != null && !FLUID_TANK.getFluid().isEmpty())
            FLUID_TANK.getFluid().setAmount(Math.min(FLUID_TANK.getCapacity(), FLUID_TANK.getFluidAmount()));
        super.setRemoved();
    }

    @Override
    public void load(CompoundTag nbt)
    {
        if (FLUID_TANK != null) FLUID_TANK.readFromNBT(nbt);
        super.load(nbt);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BE_BloodTank entity)
    {
        if (level.isClientSide()) return;
        if (entity.FLUID_TANK == null) entity.setTank(blockPos, level);

        setChanged(level, blockPos, blockState);
        IFluidHandler be = getNeighborFluidHandler(blockPos, level);
        if (be == null) return;

        be.fill(new FluidStack(entity.FLUID_TANK.getFluid().getRawFluid(), 1000), FluidAction.EXECUTE);
    }

    private void setTank(BlockPos blockPos, Level level)
    {
        if (getNeighborTank(blockPos, level) != null && getNeighborTank(blockPos, level).getTank() != null) {
            FLUID_TANK = getNeighborTank(blockPos, level).getTank();
            FLUID_TANK.setCapacity(FLUID_TANK.getCapacity() + DEFAULT_CAPACITY);
        } else {
            FLUID_TANK = new FluidTank(DEFAULT_CAPACITY)
            {
                @Override
                public boolean isFluidValid(FluidStack stack)
                {
                    return stack.getFluid() == FluidRegistry.FLUID_BLOOD.source.get().getSource();
                }

                @Override
                protected void onContentsChanged()
                {
                    setChanged();
                }

                @Override
                public int getCapacity()
                {
                    return super.getCapacity();
                }
            };
        }
        System.out.println("Capacity = " + FLUID_TANK.getCapacity());
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

    private static BE_BloodTank getNeighborTank(BlockPos blockPos, Level level)
    {
        BlockEntity be;
        if ((be = level.getBlockEntity(blockPos.east())) instanceof BE_BloodTank) {
            return (BE_BloodTank) be;
        }
        if ((be = level.getBlockEntity(blockPos.west())) instanceof BE_BloodTank) {
            return (BE_BloodTank) be;
        }
        if ((be = level.getBlockEntity(blockPos.north())) instanceof BE_BloodTank) {
            return (BE_BloodTank) be;
        }
        if ((be = level.getBlockEntity(blockPos.south())) instanceof BE_BloodTank) {
            return (BE_BloodTank) be;
        }
        if ((be = level.getBlockEntity(blockPos.above())) instanceof BE_BloodTank) {
            return (BE_BloodTank) be;
        }
        if ((be = level.getBlockEntity(blockPos.below())) instanceof BE_BloodTank) {
            return (BE_BloodTank) be;
        }
        return null;
    }

    public static IFluidHandler getNeighborFluidHandler(BlockPos blockPos, Level level) {
        BlockEntity be;
        if ((be = level.getBlockEntity(blockPos.east())) instanceof IFluidHandler && !(be instanceof BE_BloodTank)) {
            return (IFluidHandler) be;
        }
        if ((be = level.getBlockEntity(blockPos.west())) instanceof IFluidHandler && !(be instanceof BE_BloodTank)) {
            return (IFluidHandler) be;
        }
        if ((be = level.getBlockEntity(blockPos.north())) instanceof IFluidHandler && !(be instanceof BE_BloodTank)) {
            return (IFluidHandler) be;
        }
        if ((be = level.getBlockEntity(blockPos.south())) instanceof IFluidHandler && !(be instanceof BE_BloodTank)) {
            return (IFluidHandler) be;
        }
        if ((be = level.getBlockEntity(blockPos.above())) instanceof IFluidHandler && !(be instanceof BE_BloodTank)) {
            return (IFluidHandler) be;
        }
        if ((be = level.getBlockEntity(blockPos.below())) instanceof IFluidHandler && !(be instanceof BE_BloodTank)) {
            return (IFluidHandler) be;
        }
        return null;
    }

    private FluidTank getTank()
    {
        return FLUID_TANK;
    }
}