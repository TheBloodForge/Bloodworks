package com.wiggle1000.bloodworks.Blocks.BlockEntities;

import com.wiggle1000.bloodworks.Crafting.RecipeBloodInfusion;
import com.wiggle1000.bloodworks.Globals;
import com.wiggle1000.bloodworks.Registry.BlockEntityRegistry;
import com.wiggle1000.bloodworks.Registry.FluidRegistry;
import com.wiggle1000.bloodworks.Registry.RecipeRegistry;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class BlockEntityMachineInfusionChamber extends BlockEntity implements MenuProvider, IItemHandler, IFluidHandler
{

    private final ItemStackHandler itemHandler = new ItemStackHandler(3)
    {
        @Override
        protected void onContentsChanged(int slot)
        {
            super.onContentsChanged(slot);
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    public static final int COAGULATOR_SLOT_INDEX = 0;
    public static final int INPUT_SLOT_INDEX = 1;
    public static final int OUTPUT_SLOT_INDEX = 2;


    protected final ContainerData data;
    private int progress = 0;
    private RecipeBloodInfusion activeRecipe;


    public BlockEntityMachineInfusionChamber(BlockPos pos, BlockState state)
    {
        super(BlockEntityRegistry.BLOCK_ENTITY_INFUSION_CHAMBER.get(), pos, state);
        this.data = new ContainerData()
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
    }

    public int getContainerData(int index)
    {
        return 0;
    }

    public void setContainerData(int index, int value)
    {
    }

    public int getContainerCount()
    {
        return 0;
    }

    @Override
    public Component getDisplayName()
    {
        return Component.translatable(Globals.MODID + ".block_entity.infusion_chamber");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player)
    {
        return null;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
    {
        if (cap == ForgeCapabilities.ITEM_HANDLER)
        {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps()
    {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt)
    {
        nbt.put("inventory", itemHandler.serializeNBT());
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
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


    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BlockEntityMachineInfusionChamber entity)
    {
        if (level.isClientSide()) return;
        if (!entity.isCrafting()) return;

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
        return activeRecipe != null;
    }

    private void doCraftItem()
    {
        itemHandler.insertItem(OUTPUT_SLOT_INDEX, activeRecipe.getResultItem(), false);
        itemHandler.extractItem(INPUT_SLOT_INDEX, activeRecipe.getIngredient().getItems().length, false);
        drain(activeRecipe.getBloodRequired(), FluidAction.EXECUTE);
        activeRecipe = null;
    }

    private boolean resetProgress()
    {
        boolean wasChanged = this.progress != 0;
        this.progress = 0;
        return wasChanged;
    }

    private final FluidStack storedFluid = new FluidStack(FluidRegistry.BLOOD_FLUID.source.get(), 0);

    public int getFluidAmount()
    { return storedFluid.getAmount(); }

    @Override
    public int getTanks()
    {
        return 1;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank)
    {
        return storedFluid;
    }

    @Override
    public int getTankCapacity(int tank)
    {
        return 6000;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack)
    {
        return stack.containsFluid(new FluidStack(FluidRegistry.BLOOD_FLUID.source.get(), 1));
    }

    @Override
    public int fill(FluidStack resource, IFluidHandler.FluidAction action)
    {
        if (!isFluidValid(1, resource) || resource.getAmount() <= 0)
        {
            return 0;
        }
        int sourceVolume = resource.getAmount();
        int currentVolume = getFluidAmount();
        if (currentVolume + sourceVolume <= getTankCapacity(0))
        {
            storedFluid.grow(sourceVolume);
            return sourceVolume;
        } else {
            int remainder = getTankCapacity(0) - currentVolume;
            int drainedVolume = sourceVolume - remainder;
            storedFluid.grow(drainedVolume);
            return drainedVolume;
        }
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, IFluidHandler.FluidAction action)
    {
        int amount = storedFluid.getAmount();
        storedFluid.shrink(Math.min(maxDrain, amount));
        return new FluidStack(storedFluid.getFluid(), amount);
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action)
    {
        return drain(resource.getAmount(), action);
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
}