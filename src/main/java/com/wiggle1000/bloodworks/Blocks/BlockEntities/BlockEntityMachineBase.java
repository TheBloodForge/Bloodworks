package com.wiggle1000.bloodworks.Blocks.BlockEntities;

import com.wiggle1000.bloodworks.Globals;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockEntityMachineBase extends BaseContainerBlockEntity
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

    private Runnable tickFunction = null;


    public BlockEntityMachineBase(BlockEntityType block, BlockPos pos, BlockState state)
    {
        super(block, pos, state);
        this.data = new ContainerData()
        {
            @Override
            public int get(int index)
            {
                return 0;
            }

            @Override
            public void set(int index, int value)
            {
            }

            @Override
            public int getCount()
            {
                return 2;
            }
        };
    }

    @Override
    protected Component getDefaultName()
    {
        return Component.translatable(Globals.MODID + ".block_entity.machine_base");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player)
    {
        return null; // @Keldon come back here, right now mister.
    }

    @Override
    protected AbstractContainerMenu createMenu(int p_58627_, Inventory p_58628_)
    {
        return null; // @Keldon come back here, right now mister.
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

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BlockEntityMachineBase entity)
    {
        if (level.isClientSide()) return;
        if (entity.getTickFunction() != null)
        {
            entity.getTickFunction().run();
        }
    }

    private Runnable getTickFunction()
    {
        return tickFunction;
    }

    @Override
    public int getContainerSize()
    {
        return 0;
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    @Override
    public ItemStack getItem(int p_18941_)
    {
        return null;
    }

    @Override
    public ItemStack removeItem(int p_18942_, int p_18943_)
    {
        return null;
    }

    @Override
    public ItemStack removeItemNoUpdate(int p_18951_)
    {
        return null;
    }

    @Override
    public void setItem(int p_18944_, ItemStack p_18945_)
    {

    }

    @Override
    public boolean stillValid(Player p_18946_)
    {
        return false;
    }

    @Override
    public void clearContent()
    {

    }
}