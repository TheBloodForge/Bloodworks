package com.bloodforge.bloodworks.Blocks.BlockEntities;

import com.bloodforge.bloodworks.Crafting.RecipeBloodInfusion;
import com.bloodforge.bloodworks.Globals;
import com.bloodforge.bloodworks.Networking.FluidSyncS2CPacket;
import com.bloodforge.bloodworks.Networking.PacketManager;
import com.bloodforge.bloodworks.Registry.BlockRegistry;
import com.bloodforge.bloodworks.Registry.FluidRegistry;
import com.bloodforge.bloodworks.Registry.ItemRegistry;
import com.bloodforge.bloodworks.Registry.RecipeRegistry;
import com.bloodforge.bloodworks.Server.Menus.InfusionChamberMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"unused", "SameReturnValue"})
public class BE_InfusionChamber extends BlockEntityMachineBase implements IItemHandler, MenuProvider
{

    public static final Component TITLE = Component.translatable(Globals.MODID + ".infusion_chamber");
    private final ItemStackHandler itemHandler = new ItemStackHandler(3)
    {
        @Override
        protected void onContentsChanged(int slot)
        {
            super.onContentsChanged(slot);
            setChanged();
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            return super.extractItem(slot, amount, simulate);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack)
        {
            return switch (slot)
                    {
                        case 0 -> stack.is(ItemRegistry.ITEM_STABILIZER.get());
                        case 1 -> true;
                        default -> activeRecipe != null && activeRecipe.getResultItem().is(stack.getItem());
                    };
        }
    };
    private final ItemStackHandler automationItemHandler = new ItemStackHandler(3)
    {
        @Override
        protected void onContentsChanged(int slot)
        {
            super.onContentsChanged(slot);
            setChanged();
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            if (slot != OUTPUT_SLOT_INDEX) return new ItemStack(Items.AIR);
            return BE_InfusionChamber.this.extractItem(slot, amount, simulate);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack)
        {
            return BE_InfusionChamber.this.isItemValid(slot, stack);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
        {
            return BE_InfusionChamber.this.insertItem(slot, stack, simulate);
        }
    };
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private LazyOptional<IItemHandler> lazyAutomationItemHandler = LazyOptional.empty();
    private LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();

    public static final int COAGULATOR_SLOT_INDEX = 0;
    public static final int INPUT_SLOT_INDEX = 1;
    public static final int OUTPUT_SLOT_INDEX = 2;


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

    private int progress = 0;
    private RecipeBloodInfusion activeRecipe;


    public BE_InfusionChamber(BlockPos pos, BlockState state)
    {
        super(BlockRegistry.BLOCK_INFUSION_CHAMBER.blockEntity().get(), pos, state);
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
                    case 0 -> this.progress;
                    case 1 -> this.activeRecipe != null ? this.activeRecipe.getTicksRequired() : 10000;
                    case 2 -> this.FLUID_TANK.getFluidAmount();
                    default -> 0;
                };
    }

    /**
     * this sets the values relevant on loading
     */
    public void setContainerData(int index, int value)
    {
        switch (index)
        {
            case 0 -> this.progress = value;
            case 2 ->
                    this.fill(new FluidStack(FluidRegistry.FLUID_BLOOD.source.get().getSource(), value), FluidAction.EXECUTE);
            default ->
            {
            }
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
        if (cap == ForgeCapabilities.ITEM_HANDLER)
        {
            if (side != null)
            {
                return lazyAutomationItemHandler.cast();
            }
            return lazyItemHandler.cast();
        }

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
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyAutomationItemHandler = LazyOptional.of(() -> automationItemHandler);
        lazyFluidHandler = LazyOptional.of(() -> FLUID_TANK);
    }

    @Override
    public void invalidateCaps()
    {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyAutomationItemHandler.invalidate();
        lazyFluidHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt)
    {
        nbt.put("inventory", itemHandler.serializeNBT());
        nbt.putInt("progress", this.progress);
        nbt = FLUID_TANK.writeToNBT(nbt);
//        nbt.putInt("blood", storedFluid.getAmount());
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        progress = nbt.getInt("progress");
        FLUID_TANK.readFromNBT(nbt);
//        FLUID_TANK.setAmount(nbt.getInt("blood"));
        super.load(nbt);
    }

    public void dropInventoryContents()
    {
        if (this.level == null) return;

        SimpleContainer inv = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++)
        {
            inv.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inv);
    }


    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BE_InfusionChamber entity)
    {
        if (level.isClientSide()) return;
        if (!entity.isCrafting())
        {
            entity.resetProgress();
            return;
        }

        entity.progress++;
        setChanged(level, blockPos, blockState);

        if (entity.progress >= entity.activeRecipe.getTicksRequired())
        {
            entity.doCraftItem();
            if (entity.resetProgress()) setChanged(level, blockPos, blockState);
        }
    }

    private boolean isCrafting()
    {
        activeRecipe = activeRecipe != null ? activeRecipe : RecipeRegistry.getInfusionRecipeFromInputs(this);
        return activeRecipe != null && activeRecipe.matches(this, level);
    }

    private void doCraftItem()
    {
        if (activeRecipe.matches(this, level))
        {
            itemHandler.insertItem(OUTPUT_SLOT_INDEX, activeRecipe.getResultItem().copy(), false);
            itemHandler.extractItem(INPUT_SLOT_INDEX, activeRecipe.getIngredient().getItems().length, false);
            drain(activeRecipe.getBloodRequired(), FluidAction.EXECUTE);
            syncFluid();
        }
    }

    private boolean resetProgress()
    {
        boolean wasChanged = this.progress != 0;
        if (activeRecipe != null && activeRecipe.matches(this, level))
            this.progress = 20;
        else
            this.progress = 0;
        return wasChanged;
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

    @Override
    public int getSlots()
    {
        return 3;
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot)
    {
        return itemHandler.getStackInSlot(slot);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
    {
        return itemHandler.insertItem(slot, stack, simulate);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        return itemHandler.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return itemHandler.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack)
    {
        return itemHandler.isItemValid(slot, stack);
    }

    public ItemStack getItem(int slot)
    {
        return itemHandler.getStackInSlot(slot);
    }

    public ItemStackHandler getInventory()
    {
        return itemHandler;
    }

    @Override
    public Component getDisplayName()
    {
        return TITLE;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int window_id, Inventory inventory, Player player)
    {
        syncFluid();
        return new InfusionChamberMenu(window_id, inventory, this, this.data);
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