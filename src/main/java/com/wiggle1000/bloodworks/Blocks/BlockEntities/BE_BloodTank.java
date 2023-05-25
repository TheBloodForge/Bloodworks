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
        nbt = FLUID_TANK.writeToNBT(nbt);
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        FLUID_TANK.readFromNBT(nbt);
        super.load(nbt);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BE_BloodTank entity)
    {
        if (level.isClientSide()) return;
        setChanged(level, blockPos, blockState);
        BlockEntity be;
        if ((be = level.getBlockEntity(blockPos.east())) instanceof IFluidHandler) {
            ((IFluidHandler) be).fill(new FluidStack(FluidRegistry.FLUID_BLOOD.source.get(), 1000), FluidAction.EXECUTE);
        }
        if ((be = level.getBlockEntity(blockPos.west())) instanceof IFluidHandler) {
            ((IFluidHandler) be).fill(new FluidStack(FluidRegistry.FLUID_BLOOD.source.get(), 1000), FluidAction.EXECUTE);
        }
        if ((be = level.getBlockEntity(blockPos.north())) instanceof IFluidHandler) {
            ((IFluidHandler) be).fill(new FluidStack(FluidRegistry.FLUID_BLOOD.source.get(), 1000), FluidAction.EXECUTE);
        }
        if ((be = level.getBlockEntity(blockPos.south())) instanceof IFluidHandler) {
            ((IFluidHandler) be).fill(new FluidStack(FluidRegistry.FLUID_BLOOD.source.get(), 1000), FluidAction.EXECUTE);
        }
        if ((be = level.getBlockEntity(blockPos.above())) instanceof IFluidHandler) {
            ((IFluidHandler) be).fill(new FluidStack(FluidRegistry.FLUID_BLOOD.source.get(), 1000), FluidAction.EXECUTE);
        }
        if ((be = level.getBlockEntity(blockPos.below())) instanceof IFluidHandler) {
            ((IFluidHandler) be).fill(new FluidStack(FluidRegistry.FLUID_BLOOD.source.get(), 1000), FluidAction.EXECUTE);
        }
    }
    private final FluidTank FLUID_TANK = new FluidTank(10000) {
        @Override
        public boolean isFluidValid(FluidStack stack)
        {
            return stack.getFluid() == FluidRegistry.FLUID_BLOOD.source.get().getSource();
        }

        @Override
        protected void onContentsChanged()
        {
            setChanged();
//            super.onContentsChanged();
        }
    };

    @Override
    public int getTanks()
    {
        return 1;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank)
    {
        return FLUID_TANK.getFluid();
    }

    @Override
    public int getTankCapacity(int tank)
    {
        return FLUID_TANK.getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack)
    {
        return FLUID_TANK.isFluidValid(stack);
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
}