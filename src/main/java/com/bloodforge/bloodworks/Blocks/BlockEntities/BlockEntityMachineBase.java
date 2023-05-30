package com.bloodforge.bloodworks.Blocks.BlockEntities;

import com.bloodforge.bloodworks.Globals;
import com.bloodforge.bloodworks.Networking.FluidSyncS2CPacket;
import com.bloodforge.bloodworks.Networking.PacketManager;
import com.bloodforge.bloodworks.Registry.FluidRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class BlockEntityMachineBase extends BlockEntity implements IFluidHandler
{

    public static final Component TITLE = Component.translatable(Globals.MODID + ".genericMachine");
    public LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();

    public int progress = 0;

    public final ContainerData data = new ContainerData()
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

    public BlockEntityMachineBase(BlockEntityType block, BlockPos pos, BlockState state)
    {
        super(block, pos, state);
    }

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
            this.FLUID_TANK.getFluid().setAmount(value);
        }
    }

    /**
     * this returns the number of data entries to be saved
     */
    public int getContainerCount()
    {
        return 3;
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
        nbt.putInt("progress", this.progress);
        nbt = FLUID_TANK.writeToNBT(nbt);
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        progress = nbt.getInt("progress");
        FLUID_TANK.readFromNBT(nbt);
        super.load(nbt);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BE_InfusionChamber entity)
    {
    }

    private final FluidTank FLUID_TANK = new FluidTank(6000)
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
            syncFluid();
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

    public void setFluid(FluidStack fluidStack)
    {
        FLUID_TANK.setFluid(fluidStack);
    }

    public void syncFluid()
    {
        PacketManager.sendToClients(new FluidSyncS2CPacket(getFluidInTank(0), getBlockPos()));
    }
}