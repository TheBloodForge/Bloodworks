package com.bloodforge.bloodworks.Blocks.BlockEntities;

import com.bloodforge.bloodworks.Globals;
import com.bloodforge.bloodworks.Items.TankItem;
import com.bloodforge.bloodworks.Registry.BlockRegistry;
import com.bloodforge.bloodworks.Util;
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
    { return this.data; }

    /**
     * this is the data accessor for the game to save the data
     */
    public int getContainerData(int index)
    {
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
        if (level.isClientSide()) {
            if (entity.parentName.isEmpty())
                entity.setTank(blockPos, level);
            return;
        }
        if (!entity.ensureTank()) return;

        setChanged(level, blockPos, blockState);

        List<IFluidHandler> fluidConsumers = getNeighborFluidHandlers(blockPos, level);
        if (fluidConsumers.isEmpty()) return;

        for (IFluidHandler fluidConsumer : fluidConsumers)
            fluidConsumer.fill(entity.drain(Math.min(1000, fluidConsumer.getTankCapacity(0) - fluidConsumer.getFluidInTank(0).getAmount()), FluidAction.EXECUTE), FluidAction.EXECUTE);
    }

// ####################### NBT STUFF #######################

    public void readData(CompoundTag nbt)
    {
        createParentTank();
        TankData.getTankByName(parentName).readFromNBT(nbt);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt)
    {
        if (level != null && level.isClientSide) return;
        nbt.putString("tankName", parentName);
        saveTankToWorld();
        super.saveAdditional(nbt);
    }

    private void saveTankToWorld()
    {
        CompoundTag tankTag = new CompoundTag();
        CompoundTag localTankTag = new CompoundTag();
        localTankTag = TankData.getTankByName(parentName).writeToNBT(localTankTag);
        localTankTag.put("children", wrapTag());
        tankTag.put(parentName, localTankTag);
        TankData.TankTags.remove(parentName);
        TankData.TankTags.put(parentName, localTankTag);
        if (level != null)
            TankData.save(level);
    }

    private CompoundTag wrapTag() {
        if (TankData.children.isEmpty() || parentName.isEmpty() || !TankData.children.containsKey(parentName))
            return new CompoundTag();
        CompoundTag childrenTags = new CompoundTag();
        childrenTags.putIntArray(getBlockPos().toShortString(), Util.getBlockPosAsIntArr(getBlockPos()));
        for (BlockPos child : TankData.children.get(parentName))
            childrenTags.putIntArray(child.toShortString(), Util.getBlockPosAsIntArr(child));
        return childrenTags;
    }

    @Override
    public void load(CompoundTag nbt)
    {
        parentName = nbt.getString("tankName");
        if (!parentName.isEmpty() && level != null)
        {
            TankData.read(level);
            CompoundTag tag = TankData.TankTags.getCompound(parentName);
            CompoundTag childrenTags = tag.getCompound("children");
            TankData.getTankByName(parentName).readFromNBT(tag);
            unwrapTag(childrenTags);
        }
        TankData.syncFluid(parentName);
        super.load(nbt);
    }

    public void unwrapTag(CompoundTag childrenTag)
    {
        for (String childPos : childrenTag.getAllKeys())
        {
            int[] childPosAsIntArr = childrenTag.getIntArray(childPos);
            if (childPosAsIntArr.length == 3)
                TankData.addChild(parentName, new BlockPos(childPosAsIntArr[0], childPosAsIntArr[1], childPosAsIntArr[2]));
        }
    }
// ####################### END NBT #######################

// ####################### FLUID TANK #######################
    @Override
    public int getTanks()
    { return 1; }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank)
    { return !TankData.hasTankByName(parentName) ? FluidStack.EMPTY : TankData.getTankByName(parentName).getFluid(); }

    @Override
    public int getTankCapacity(int tank)
    { return !TankData.hasTankByName(parentName) ? 0 : TankData.getTankByName(parentName).getCapacity(); }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack)
    { return TankData.hasTankByName(parentName) && TankData.getTankByName(parentName).isFluidValid(stack); }

    @Override
    public int fill(FluidStack resource, IFluidHandler.FluidAction action)
    {
        if (!isFluidValid(1, resource) || resource.getAmount() <= 0)
            return 0;
        return TankData.getTankByName(parentName).fill(resource, action);
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, IFluidHandler.FluidAction action)
    { return TankData.getTankByName(parentName).drain(maxDrain, action); }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action)
    { return TankData.getTankByName(parentName).drain(resource, action); }

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

// ####################### END OF FLUID TANK #######################



// ######################## CUSTOM STUFF ########################

    @Override
    public CompoundTag getUpdateTag()
    {
        TankData.read(level);
        CompoundTag clientPackage = new CompoundTag();
        clientPackage.put("TankTags", TankData.TankTags);
        clientPackage.putString("parentName", parentName);
        return TankData.TankTags;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag)
    {
        parentName = tag.getString("parentName");
        TankData.TankTags = tag.getCompound("TankTags");
        createParentTank();
    }

    public float getRelativeFill() {
        float fillPercentage = Math.min(1, (float) TankData.getTankByName(parentName).getFluidAmount() / TankData.getTankByName(parentName).getCapacity());
        return Mth.clamp((fillPercentage * getTotalHeight()) - getRelativeHeight(), 0f, 1f);
    }

    public int getFluidContained() {
        if (getRelativeFill() == 0) return 0;
        if (getRelativeFill() == 1) return Globals.DEFAULT_CAPACITY;
        return Math.round(getRelativeFill() * Globals.DEFAULT_CAPACITY);
    }

    private void removeChild(BlockPos pos)
    {
        if (parentName.isEmpty() || !TankData.children.containsKey(parentName)) return;
        TankData.removeChild(parentName, pos);
        TankData.getTankByName(parentName).drain(getFluidContained(), FluidAction.EXECUTE);
        if (pos.getY() == TankData.tankSizes.get(parentName)[1] || pos.getY() == TankData.tankSizes.get(parentName)[2])
            updateTankSize();
    }

    private boolean ensureTank()
    {
        if (!TankData.hasTankByName(parentName))
            setTank(getBlockPos(), level);
        return TankData.hasTankByName(parentName);
    }

    private void setTank(BlockPos blockPos, Level level)
    {
        List<BE_BloodTank> tanks;
        if ((tanks = getNeighborTanks(blockPos, level)).isEmpty())
        { createParentTank(); return; }

        for (BE_BloodTank neighbor : tanks)
            if (!neighbor.parentName.isEmpty())
            { neighbor.addChild(this); break; }
        if (!level.isClientSide && TankData.hasTankByName(parentName))
        {
            TankData.syncTankName(blockPos, parentName);
            TankData.syncFluid(parentName);
        }
    }

    public void addChild(BE_BloodTank childTank)
    {
        TankData.addChild(parentName, childTank.getBlockPos());
        childTank.parentName = parentName;
        if(!TankData.tankSizes.containsKey(parentName)) return; //wiggle was here
        if (childTank.getBlockPos().getY() < TankData.tankSizes.get(parentName)[1] || childTank.getBlockPos().getY() > TankData.tankSizes.get(parentName)[2])
            updateTankSize();
    }

    public void createParentTank()
    {
        if (parentName.isEmpty()) parentName = TankData.createNewParent(getBlockPos());

        TankData.syncFluid(parentName);
    }

    private void updateTankSize()
    {
        for (BlockPos child : TankData.children.get(parentName))
            if (child.getY() > TankData.tankSizes.get(parentName)[2]) TankData.tankSizes.get(parentName)[2] = child.getY();
            else if (child.getY() < TankData.tankSizes.get(parentName)[1]) TankData.tankSizes.get(parentName)[1] = child.getY();

        TankData.tankSizes.get(parentName)[0] = (TankData.tankSizes.get(parentName)[2] - TankData.tankSizes.get(parentName)[1]) + 1;
    }

    public void setFluid(FluidStack fluidStack)
    { TankData.getTankByName(parentName).setFluid(fluidStack); }

    public int getRelativeHeight()
    {
        if(!TankData.tankSizes.containsKey(parentName)) return 0; //wiggle was here
        return getBlockPos().getY() - TankData.tankSizes.get(parentName)[1];
    }

    public int getTotalHeight()
    {
        if(!TankData.tankSizes.containsKey(parentName)) return 0; //wiggle was here
        return TankData.tankSizes.get(parentName)[0];
    }

    public void breakTank(BlockPos pos, Level level)
    { removeChild(pos); }
}