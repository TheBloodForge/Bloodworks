package com.wiggle1000.bloodworks.Blocks.BlockEntities;

import com.wiggle1000.bloodworks.Items.TankItem;
import com.wiggle1000.bloodworks.Networking.PacketManager;
import com.wiggle1000.bloodworks.Networking.TankSyncS2CPacket;
import com.wiggle1000.bloodworks.Registry.BlockRegistry;
import com.wiggle1000.bloodworks.Registry.FluidRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
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

public class BE_BloodTank extends BlockEntityMachineBase
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
        super(BlockRegistry.BLOCK_BLOOD_TANK.blockEntity().get(), pos, state);
    }
    public BE_BloodTank(TankItem titem, BlockPos pos, BlockState state)
    {
        super(BlockRegistry.BLOCK_BLOOD_TANK.blockEntity().get(), pos, state);
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
        if (!getLevel().isClientSide)
            syncFluid();
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
        if (isParent)
        {
            nbt.put("children", getChildrenTag());
        }
        super.saveAdditional(nbt);
    }

    private CompoundTag getChildrenTag() {
        CompoundTag childrenTags = new CompoundTag();
        for (BlockPos child : children)
        {
            childrenTags.putIntArray(child.toShortString(), getBlockPosAsIntArr(child));
        }
        return childrenTags;
    }

    @Override
    public void load(CompoundTag nbt)
    {
        isParent = nbt.getBoolean("isParent");
        if (!isParent)
        {
            int[] posArr = nbt.getIntArray("parentPos");
            parentPos = new BlockPos(posArr[0], posArr[1], posArr[2]);
        } else {
            createParentTank();
            FLUID_TANK.readFromNBT(nbt);
            loadChildrenTagCompound(nbt.getCompound("children"));
        }
        super.load(nbt);
    }

    public void loadChildrenTagCompound(CompoundTag childrenTag)
    {
        CompoundTag childrenTags = childrenTag.getCompound("children");
        for (String childPos : childrenTags.getAllKeys())
        {
            int[] childPosAsIntArr = childrenTags.getIntArray(childPos);
            children.add(new BlockPos(childPosAsIntArr[0], childPosAsIntArr[1], childPosAsIntArr[2]));
        }
        adoptChildren(children);
    }

    public void readData(CompoundTag nbt)
    {
        createParentTank();
        FLUID_TANK.readFromNBT(nbt);
    }

    private int tickCounter = 5;
    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BE_BloodTank entity)
    {
        if (level.isClientSide())
        { return; }

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

    @Override
    public CompoundTag getUpdateTag()
    {
        CompoundTag syncTag = new CompoundTag();
        syncTag.putBoolean("isParent", isParent);
        if (isParent)
        {
            syncTag.put("children", getChildrenTag());
            syncTag = FLUID_TANK.writeToNBT(syncTag);
        }
        return syncTag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag)
    {
        isParent = tag.getBoolean("isParent");
        if (isParent)
        {
            if (FLUID_TANK == null) createParentTank();
            FLUID_TANK.readFromNBT(tag);
            loadChildrenTagCompound((CompoundTag) tag.get("children"));
        }
        super.handleUpdateTag(tag);
    }

    public void breakTank(BlockPos pos, Level level)
    {
        if (isParent && !getNeighborTanks(pos, level).isEmpty())
        { // @Keldon manage odd case of parent breaking connected groups
            getNeighborTanks(pos, level).get(0).adoptChildren(getChildren());
        }
        if (FLUID_TANK != null && !FLUID_TANK.getFluid().isEmpty())
        {
            int numTanks = getParent().children.size() + 1;
            int relativeStorage = FLUID_TANK.getFluidAmount() / numTanks;
            getParent().removeChild(pos);
            FLUID_TANK.drain(relativeStorage, FluidAction.EXECUTE);
        }
    }

    private void removeChild(BlockPos pos)
    {
        children.remove(pos);
        updateTankCapacity();
    }

    private boolean ensureTank()
    {
        if (FLUID_TANK == null)
            setTank(getBlockPos(), level);
        return FLUID_TANK != null;
    }

    private int[] getBlockPosAsIntArr(BlockPos pos)
    {
        return new int[]{pos.getX(), pos.getY(), pos.getZ()};
    }

    private void setTank(BlockPos blockPos, Level level)
    {
        if (!isParent && parentPos != null)
        {
            FLUID_TANK = getParentTank();
            syncFluid();
            return;
        }
        if (!getNeighborTanks(blockPos, level).isEmpty())
        {
            for (BE_BloodTank neighborTank : getNeighborTanks(blockPos, level))
            {
                if (!neighborTank.isParent && neighborTank.parentPos != null)
                {
                    addTank(this, neighborTank.getParent());
                } else if (neighborTank.isParent) {
                    addTank(this, neighborTank);
                }
            }
            syncFluid();
        } else {
            createParentTank();
            syncFluid();
        }
    }

    public FluidTank createParentTank()
    {
        if (FLUID_TANK != null)
            return FLUID_TANK;

        isParent = true;
        return FLUID_TANK = new FluidTank(DEFAULT_CAPACITY)
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
        if (isParent)
            PacketManager.sendToClients(new TankSyncS2CPacket(getFluidInTank(0), getChildrenTag(), getParent().getBlockPos()));
    }

    public static void addTank(BE_BloodTank tank, BE_BloodTank parent)
    {
        if (tank == null || parent == null) return;
        tank.FLUID_TANK = parent.FLUID_TANK;
        tank.parentPos = parent.getBlockPos();
        parent.addChild(tank);
    }

    private void adoptChildren(List<BlockPos> childPoses)
    {
        isParent = true;
        children.clear();
        children.addAll(childPoses);

        for (BlockPos child : children)
            if (getLevel().getBlockEntity(child) instanceof BE_BloodTank childTank)
            {
                childTank.parentPos = getBlockPos();
                childTank.FLUID_TANK = FLUID_TANK;
                childTank.isParent = false;
            } else {
                System.out.println("PANIK NO CHILD FOUND!");
            }
        updateTankCapacity();
    }

    public void addChild(BE_BloodTank be_bloodTank)
    {
        if (isParent)
        {
            children.add(be_bloodTank.getBlockPos());
            updateTankCapacity();
        }
    }

    private void updateTankCapacity()
    {
        FLUID_TANK.setCapacity(DEFAULT_CAPACITY + (DEFAULT_CAPACITY * children.size()));
    }

    private FluidTank getParentTank()
    { return getParent() == null ? createParentTank() : getParent().FLUID_TANK; }

    public List<BlockPos> getChildren()
    { return children; }

    public BE_BloodTank getParent()
    { return isParent ? this : getTankAtPos(parentPos, getLevel()); }

    private FluidTank getTank()
    { return FLUID_TANK; }

    public void setFluid(FluidStack fluidStack)
    { if (FLUID_TANK == null) createParentTank(); FLUID_TANK.setFluid(fluidStack); }

    private BE_BloodTank getTankAtPos(BlockPos tankPos, Level level)
    {
        if (tankPos == null)
        {
            System.out.println("PANIK");
            return null;
        }

        if (level.getBlockEntity(tankPos) instanceof BE_BloodTank bloodTank)
            return bloodTank;

        return null;
    }
}