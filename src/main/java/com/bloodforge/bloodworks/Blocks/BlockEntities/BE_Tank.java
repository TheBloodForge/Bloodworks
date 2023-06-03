package com.bloodforge.bloodworks.Blocks.BlockEntities;

import com.bloodforge.bloodworks.Blocks.BlockBloodTank;
import com.bloodforge.bloodworks.Common.Config.BloodworksCommonConfig;
import com.bloodforge.bloodworks.Items.TankItem;
import com.bloodforge.bloodworks.Registry.BlockRegistry;
import com.bloodforge.bloodworks.Registry.FluidRegistry;
import com.bloodforge.bloodworks.Server.TankDataProxy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BE_Tank extends BlockEntity implements IFluidHandler
{
    private String tank_id = "";
    private int cooldown = 8;
    public BE_Tank(BlockPos pos, BlockState state)
    {
        super(BlockRegistry.BLOCK_BLOOD_TANK.blockEntity().get(), pos, state);
    }

    public BE_Tank(TankItem titem, BlockPos pos, BlockState state)
    {
        super(BlockRegistry.BLOCK_BLOOD_TANK.blockEntity().get(), pos, state);
    }

    public void tick()
    {
        if (level.getBlockState(getBlockPos()).getValue(BlockBloodTank.TIER) != TankDataProxy.getTankTier(tank_id))
            level.getBlockState(getBlockPos()).setValue(BlockBloodTank.TIER, TankDataProxy.getTankTier(tank_id));
        tryAndFillNeighbors();
        cooldown = getCooldown();
    }

    @OnlyIn(Dist.CLIENT)
    private void clientTick()
    {
        if (getFluidInTank(0).getFluid().getFluidType().getLightLevel() > 0)
            level.getLightEngine().checkBlock(getBlockPos());
    }

    private void tryAndFillNeighbors()
    {
        for (IFluidHandler neighbor : getNeighborFluidHandlers(getBlockPos(), getLevel()))
            for (int i = 0; i < neighbor.getTanks(); i++)
                if (neighbor.isFluidValid(i, getFluidInTank(0)))
                {
                    int space = neighbor.getTankCapacity(i) - neighbor.getFluidInTank(i).getAmount();
                    int transferAmount = Math.min(TankDataProxy.getTankTransferRate(tank_id), space);
                    FluidStack fs = getTank().getFluid().copy();
                    fs.setAmount(transferAmount); // To avoid sending update packets to client if tank level didn't change
                    if (TankDataProxy.getTankTier(tank_id) != 0)
                        drain(fs.copy(), FluidAction.EXECUTE);
                    neighbor.fill(fs.copy(), FluidAction.EXECUTE);
                }
    }

//######################################################################\\
//                        BELOW IS DATA HANDLING                        \\
//######################################################################\\

    private int getCooldown()
    {
        int cdr = TankDataProxy.getTankTier(tank_id) * BloodworksCommonConfig.TANK_COOLDOWN_REDUCTION.get();
        return TankDataProxy.getTankTier(tank_id) == 0 ? 0 : Math.max(0, BloodworksCommonConfig.MAX_TANK_COOLDOWN.get() - cdr);
    }

    private void setTankLabel()
    {
        System.out.println("Attempting to set label");
        if (getNeighborTanks(getBlockPos(), ServerLifecycleHooks.getCurrentServer().overworld()).isEmpty())
            setID(TankDataProxy.createNewParent(getBlockPos()));
        if (getID().isEmpty())
            for (BE_Tank be_tank : getNeighborTanks(getBlockPos(), ServerLifecycleHooks.getCurrentServer().overworld()))
                if (!be_tank.getID().isEmpty())
                {
                    setID(be_tank.getID());
                    TankDataProxy.addChild(be_tank.getID(), getBlockPos(), false);
                    break; // Returning once an id was found.
                }
    }

    public float getRelativeFill()
    {
        float fillPercentage = Math.min(1f, (float) getFluidInTank(0).getAmount() / getTankCapacity(0));
        return Mth.clamp((fillPercentage * getTotalHeight()) - getRelativeHeight(), -0.01f, 1.01f);
    }

    public int getFluidContained()
    { return Math.round(Mth.clamp(getRelativeFill(), 0f, 1f) * BloodworksCommonConfig.TANK_STORAGE_PER_TIER.get()); }

    public int getRelativeHeight()
    { return getBlockPos().getY() - TankDataProxy.getTankMin(tank_id); }

    public int getTotalHeight()
    { return TankDataProxy.getTankHeight(tank_id); }

    public void breakTank(BlockPos pos, Level level)
    {
        TankDataProxy.removeChild(tank_id, pos, level.isClientSide());
        drain(getFluidContained(), FluidAction.EXECUTE);
    }

    public void setID(String newTankID)
    {
        if (newTankID.isEmpty()) return;
        if (tank_id.isEmpty() || (level != null && level.isClientSide))
        {
            tank_id = newTankID;
            System.out.println("Tank ID Was Set");
            setChanged();
        }
    }

    public String getID()
    { return tank_id; }

    private FluidTank getTank()
    { return TankDataProxy.getTankByName(tank_id); }

    public void changeTier(int i)
    { TankDataProxy.changeTier(tank_id, i, getLevel().isClientSide); }

    public void setTier(int i)
    { TankDataProxy.setTankTier(tank_id, i, getLevel().isClientSide); }

//######################################################################\\
//                           BELOW IS UTILITIES                         \\
//######################################################################\\

    private static List<BE_Tank> getNeighborTanks(BlockPos blockPos, Level level)
    {
        List<BE_Tank> tanks = new ArrayList<>();
        for (Direction value : Direction.values())
            if (level.getBlockEntity(blockPos.relative(value)) instanceof BE_Tank tank)
                tanks.add(tank);
        return tanks;
    }

    public static List<IFluidHandler> getNeighborFluidHandlers(BlockPos blockPos, Level level)
    {
        List<IFluidHandler> handlers = new ArrayList<>();
        for (Direction value : Direction.values())
            if (level.getBlockEntity(blockPos.relative(value)) instanceof IFluidHandler handler
                    && !(level.getBlockEntity(blockPos.relative(value)) instanceof BE_Tank))
                handlers.add(handler);
        return handlers;
    }

    public void readDataForItemRenderer(CompoundTag nbt)
    {
//        BrokenTankData.getTankByName(parentName).readFromNBT(nbt);
    }

//######################################################################\\
//                     BELOW IS MANDATORY DEFAULTS                      \\
//                   DO NOT TOUCH, SHOULD NOT HAVE TO                   \\
//######################################################################\\
    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BE_Tank entity)
    {
        if (level == null) return;
        if (level.isClientSide)
        {
            entity.clientTick();
        }
        if (!entity.tank_id.isEmpty() && entity.getTank().getCapacity() == 404)
            TankDataProxy.loadTanks(false);
        if (entity.tank_id.isEmpty()) // Try to find Neighbor ID before attempting to Recover.
            entity.setTankLabel();
        if (entity.tank_id.isEmpty())
            entity.setID(TankDataProxy.recoverTankName(blockPos, level));
        if (entity.cooldown > 0) entity.cooldown--;
        if (entity.cooldown <= 0) entity.tick();
    }

    @Override
    public int getTanks()
    { return 1; }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank)
    { return getTank().getFluid(); }

    @Override
    public int getTankCapacity(int tank)
    { return getTank().getCapacity(); }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack)
    { return getTank().isFluidValid(stack); }

    @Override
    public int fill(FluidStack resource, FluidAction action)
    { return getTank().fill(resource, action); }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action)
    { return getTank().drain(resource, action); }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action)
    { return getTank().drain(maxDrain, action); }

    @Override
    public void load(CompoundTag tag)
    {
        tank_id = tag.getString("tank_id");
        super.load(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        if (level == null || level.isClientSide) return;
        if (tank_id.isEmpty())
            setTankLabel();
        tag.putString("tank_id", tank_id);
        TankDataProxy.saveTanks(ServerLifecycleHooks.getCurrentServer().overworld());
        super.saveAdditional(tag);
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        CompoundTag clientPackage = new CompoundTag();
        clientPackage.putString("tank_id", tank_id);
        return clientPackage;
    }

    @Override
    public CompoundTag getPersistentData()
    {
        CompoundTag tag = super.getPersistentData();
        tag.putString("tank_id", tank_id);
        return tag;
    }

    // ####################### FORGE CAPABILITIES #######################

    private LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();
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
        if (tank_id.isEmpty()) return -1;
        return switch (index)
                {
                    case 0 -> TankDataProxy.getTankByName(tank_id).getFluidAmount();
                    case 1 -> TankDataProxy.getTankByName(tank_id).getCapacity();
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
        if (tank_id.isEmpty()) return;
        lazyFluidHandler = LazyOptional.of(() -> TankDataProxy.getTankByName(tank_id));
    }

    @Override
    public void invalidateCaps()
    {
        super.invalidateCaps();
        lazyFluidHandler.invalidate();
    }

    // ####################### END OF FORGE CAPABILITIES #######################
}