package com.bloodforge.bloodworks.Blocks.BlockEntities;

import com.bloodforge.bloodworks.Globals;
import com.bloodforge.bloodworks.Items.TankItem;
import com.bloodforge.bloodworks.Registry.BlockRegistry;
import com.bloodforge.bloodworks.Registry.FluidRegistry;
import com.bloodforge.bloodworks.Server.TankData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BE_BloodTank extends BlockEntityMachineBase
{
    private LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();
    public String parentName = "";
    public int tankTier = 0;

    public BE_BloodTank(BlockPos pos, BlockState state)
    {
        super(BlockRegistry.BLOCK_BLOOD_TANK.blockEntity().get(), pos, state);
    }

    public BE_BloodTank(TankItem titem, BlockPos pos, BlockState state)
    {
        super(BlockRegistry.BLOCK_BLOOD_TANK.blockEntity().get(), pos, state);
    }

    // ####################### FORGE CAPABILITIES #######################

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
        if (parentName.isEmpty()) return -1;
        return switch (index)
                {
                    case 0 -> TankData.getTankByName(parentName).getFluidAmount();
                    case 1 -> TankData.getTankByName(parentName).getCapacity();
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
        if (parentName.isEmpty()) return;
        lazyFluidHandler = LazyOptional.of(() -> TankData.getTankByName(parentName));
        if (getLevel() != null && !getLevel().isClientSide)
            TankData.syncFluid(parentName);
    }

    @Override
    public void invalidateCaps()
    {
        super.invalidateCaps();
        lazyFluidHandler.invalidate();
    }

// ####################### END OF FORGE CAPABILITIES #######################

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BE_BloodTank entity)
    {
        if (level.isClientSide()) return;

        if (TankData.TankDataTag.getAllKeys().isEmpty())
            TankData.read(level);

        if (!entity.parentName.isEmpty() && Globals.RAND.nextFloat() < 0.2f)
            TankData.syncTankName(blockPos, entity.parentName);

        if (!entity.ensureTank())
        {
            if (!TankData.hasTankByName(entity.parentName))
            {
//                FluidTank tank = TankData.getTankByName(entity.parentName);
//                System.out.println("Tank Data - " + entity.parentName + " - " + tank.getFluidAmount() + " - " + Component.translatable(tank.getFluid().getTranslationKey()).getString() + " - " + tank.getCapacity());
            }
            return;
        }

        setChanged(level, blockPos, blockState);

        List<IFluidHandler> fluidConsumers = getNeighborFluidHandlers(blockPos, level);
        if (fluidConsumers.isEmpty()) return;

        for (IFluidHandler fluidConsumer : fluidConsumers)
            fluidConsumer.fill(entity.drain(Math.min(1000, fluidConsumer.getTankCapacity(0) - fluidConsumer.getFluidInTank(0).getAmount()), FluidAction.EXECUTE), FluidAction.EXECUTE);
    }

// ####################### NBT STUFF #######################

    public void readDataForItemRenderer(CompoundTag nbt)
    {
        createParentTank();
        TankData.getTankByName(parentName).readFromNBT(nbt);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt)
    {
        if (level == null || level.isClientSide) return;
        nbt.putString("tankName", parentName);
        TankData.saveTanksToWorld(level);
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        parentName = nbt.getString("tankName");
        if (!parentName.isEmpty() && level != null)
        {
            TankData.read(level);
            CompoundTag tag = TankData.TankDataTag.getCompound(parentName);
            CompoundTag childrenTags = tag.getCompound("children");
            TankData.getTankByName(parentName).readFromNBT(tag);
            TankData.unwrapChildren(parentName, childrenTags);
        }
        TankData.syncFluid(parentName);
        super.load(nbt);
    }

// ####################### END NBT #######################

    // ####################### FLUID TANK #######################
    @Override
    public int getTanks()
    {
        return 1;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank)
    {
        return !TankData.hasTankByName(parentName) ? FluidStack.EMPTY : TankData.getTankByName(parentName).getFluid();
    }

    @Override
    public int getTankCapacity(int tank)
    {
        return !TankData.hasTankByName(parentName) ? 0 : TankData.getTankByName(parentName).getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack)
    {
        return TankData.hasTankByName(parentName) && TankData.getTankByName(parentName).isFluidValid(stack);
    }

    @Override
    public int fill(FluidStack resource, IFluidHandler.FluidAction action)
    {
        if (!isFluidValid(1, resource) || resource.getAmount() <= 0 || !TankData.hasTankByName(parentName))
            return 0;
        return TankData.getTankByName(parentName).fill(resource, action);
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, IFluidHandler.FluidAction action)
    {
        return !TankData.hasTankByName(parentName) ? FluidStack.EMPTY : TankData.getTankByName(parentName).drain(maxDrain, action);
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action)
    {
        return !TankData.hasTankByName(parentName) ? FluidStack.EMPTY : TankData.getTankByName(parentName).drain(resource, action);
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
            if (level.getBlockEntity(blockPos.relative(value)) instanceof IFluidHandler handler
                    && !(level.getBlockEntity(blockPos.relative(value)) instanceof BE_BloodTank))
                handlers.add(handler);
        return handlers;
    }

// ####################### END OF FLUID TANK #######################


// ######################## CUSTOM STUFF ########################

    @Override
    public CompoundTag getUpdateTag()
    {
        TankData.read(level);
        CompoundTag clientPackage = new CompoundTag();
        clientPackage.put("TankTags", TankData.TankDataTag);
        clientPackage.putString("parentName", parentName);
        return TankData.TankDataTag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag)
    {
        parentName = tag.getString("parentName");
        TankData.TankDataTag = tag.getCompound("TankTags");
    }

    public float getRelativeFill()
    {
        float fillPercentage = Math.min(1, (float) getFluidInTank(0).getAmount() / getTankCapacity(0));
        return Mth.clamp((fillPercentage * getTotalHeight()) - getRelativeHeight(), 0f, 1f);
    }

    public int getFluidContained()
    {
        if (getRelativeFill() == 0) return 0;
        if (getRelativeFill() == 1) return Globals.DEFAULT_TANK_CAPACITY;
        return Math.round(getRelativeFill() * Globals.DEFAULT_TANK_CAPACITY);
    }

    private void removeChild(BlockPos pos)
    {
        if (parentName.isEmpty() || !TankData.TANK_CHILDREN.containsKey(parentName)) return;
        TankData.removeChild(parentName, pos);
        drain(getFluidContained(), FluidAction.EXECUTE);
        if (!parentName.isEmpty() && TankData.hasTankByName(parentName) && pos.getY() == TankData.getTankMin(parentName) || pos.getY() == TankData.getTankMax(parentName))
            updateTankSize();
    }

    private boolean ensureTank()
    {
        if (level != null && (parentName.isEmpty() || !TankData.hasTankByName(parentName)))
            setTankLabel();
        return TankData.hasTankByName(parentName);
    }

    public void addChild(BE_BloodTank childTank)
    {
        TankData.addChild(parentName, childTank.getBlockPos());
        childTank.parentName = parentName;
        if (childTank.getBlockPos().getY() < TankData.getTankMin(parentName) || childTank.getBlockPos().getY() > TankData.getTankMax(parentName))
            updateTankSize();
    }

    public void createParentTank()
    {
        if (parentName.isEmpty())
            parentName = TankData.createNewParent(getBlockPos());

        TankData.syncFluid(parentName);
    }

    private void updateTankSize()
    {
        if (!TankData.hasTankByName(parentName)) return;

        for (BlockPos child : TankData.TANK_CHILDREN.get(parentName))
            if (child.getY() > TankData.getTankMax(parentName)) TankData.TANK_DATA.get(parentName)[2] = child.getY();
            else if (child.getY() < TankData.getTankMin(parentName))
                TankData.TANK_DATA.get(parentName)[1] = child.getY();

        TankData.TANK_DATA.get(parentName)[0] = (TankData.getTankMax(parentName) - TankData.getTankMin(parentName)) + 1;
    }

    public void setFluid(FluidStack fluidStack)
    {
        if (TankData.hasTankByName(parentName)) TankData.getTankByName(parentName).setFluid(fluidStack);
    }

    public int getRelativeHeight()
    {
        return getBlockPos().getY() - getTotalHeight();
    }

    public int getTotalHeight()
    {
        return TankData.getTankHeight(parentName);
    }

    public void breakTank(BlockPos pos, Level level)
    {
        removeChild(pos);
    }

    public void setTankLabel()
    {
        if (level != null)
        {
            List<BE_BloodTank> tanks;
            if ((tanks = getNeighborTanks(getBlockPos(), level)).isEmpty())
                parentName = TankData.createNewParent(getBlockPos());

            for (BE_BloodTank tank : tanks)
                if (!tank.parentName.isEmpty())
                {
                    parentName = tank.parentName;
                    tank.addChild(this);
                    break;
                }

            if (parentName.isEmpty())
                parentName = TankData.recoverTankName(getBlockPos());

            if (TankData.hasTankByName(parentName))
            {
                TankData.syncTankName(parentName);
                TankData.syncFluid(parentName);
            }
        }
    }
}