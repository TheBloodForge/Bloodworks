package com.wiggle1000.bloodworks.Blocks.BlockEntities;

import com.wiggle1000.bloodworks.Globals;
import com.wiggle1000.bloodworks.Registry.BlockEntityRegistry;
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
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class BlockEntityMachineInfusionChamber extends BlockEntity implements MenuProvider
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

    public static final int INPUT_SLOT_INDEX = 1;
    public static final int OUTPUT_SLOT_INDEX = 2;


    protected final ContainerData data;
    private int progress = 0;
    private int processingTicks = 40;


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

        if (entity.isCrafting())
        {
            entity.progress++;
            setChanged(level, blockPos, blockState);

            if (entity.progress >= entity.processingTicks)
            {
                entity.doCraftItem();
            }
        } else {
            if (entity.resetProgress()) setChanged(level, blockPos, blockState);
        }
    }

    private boolean isCrafting()
    {
        SimpleContainer inv = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++)
        {
            inv.setItem(i, itemHandler.getStackInSlot(i));
        }
      /*  if(canPutIntoOutput(inv, ItemStack stack))
        {

        }*/
        return false;
    }

    private boolean canPutIntoOutput(SimpleContainer inv, ItemStack stack)
    {
//        if(inv.getItem(OUTPUT_SLOT_INDEX).getMaxStackSize() > invent)
        return false;
    }

    private void doCraftItem()
    {

    }

    private boolean resetProgress()
    {
        boolean wasChanged = this.progress != 0;
        this.progress = 0;
        return wasChanged;
    }
}