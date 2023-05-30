package com.bloodforge.bloodworks.Blocks.BlockEntities;

public class BE_BloodTank /*extends BlockEntityMachineBase*/
{/*
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

    *//**
     * this is the data accessor for the game to save the data
     *//*
    public int getContainerData(int index)
    {
        if (parentName.isEmpty()) return -1;
        return switch (index)
                {
                    case 0 -> BrokenTankData.getTankByName(parentName).getFluidAmount();
                    case 1 -> BrokenTankData.getTankByName(parentName).getCapacity();
                    default -> 0;
                };
    }

    *//**
     * this sets the values relevant on loading
     *//*
    public void setContainerData(int index, int value)
    {
        if (index == 0)
        {
            this.fill(new FluidStack(FluidRegistry.FLUID_BLOOD.source.get().getSource(), value), FluidAction.EXECUTE);
        }
    }

    *//**
     * this returns the number of data entries to be saved
     *//*
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
        lazyFluidHandler = LazyOptional.of(() -> BrokenTankData.getTankByName(parentName));
        if (getLevel() != null && !getLevel().isClientSide)
            BrokenTankData.syncFluid(parentName);
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

        if (BrokenTankData.TankDataTag.getAllKeys().isEmpty())
            BrokenTankData.read(level);

        if (!entity.parentName.isEmpty() && Globals.RAND.nextFloat() < 0.2f)
            BrokenTankData.syncTankName(blockPos, entity.parentName);

        if (!entity.ensureTank())
        {
            if (!BrokenTankData.hasTankByName(entity.parentName))
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
        BrokenTankData.getTankByName(parentName).readFromNBT(nbt);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt)
    {
        if (level == null || level.isClientSide) return;
        nbt.putString("tankName", parentName);
        BrokenTankData.saveTanksToWorld(level);
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        parentName = nbt.getString("tankName");
        if (!parentName.isEmpty() && level != null)
        {
            BrokenTankData.read(level);
            CompoundTag tag = BrokenTankData.TankDataTag.getCompound(parentName);
            CompoundTag childrenTags = tag.getCompound("children");
            BrokenTankData.getTankByName(parentName).readFromNBT(tag);
            BrokenTankData.unwrapChildren(parentName, childrenTags);
        }
        BrokenTankData.syncFluid(parentName);
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
        return !BrokenTankData.hasTankByName(parentName) ? FluidStack.EMPTY : BrokenTankData.getTankByName(parentName).getFluid();
    }

    @Override
    public int getTankCapacity(int tank)
    {
        return !BrokenTankData.hasTankByName(parentName) ? 0 : BrokenTankData.getTankByName(parentName).getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack)
    {
        return BrokenTankData.hasTankByName(parentName) && BrokenTankData.getTankByName(parentName).isFluidValid(stack);
    }

    @Override
    public int fill(FluidStack resource, IFluidHandler.FluidAction action)
    {
        if (!isFluidValid(1, resource) || resource.getAmount() <= 0 || !BrokenTankData.hasTankByName(parentName))
            return 0;
        return BrokenTankData.getTankByName(parentName).fill(resource, action);
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, IFluidHandler.FluidAction action)
    {
        return !BrokenTankData.hasTankByName(parentName) ? FluidStack.EMPTY : BrokenTankData.getTankByName(parentName).drain(maxDrain, action);
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action)
    {
        return !BrokenTankData.hasTankByName(parentName) ? FluidStack.EMPTY : BrokenTankData.getTankByName(parentName).drain(resource, action);
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
        CompoundTag clientPackage = new CompoundTag();
        clientPackage.putString("parentName", parentName);
        return clientPackage;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag)
    { parentName = tag.getString("parentName"); }

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
        if (parentName.isEmpty() || !BrokenTankData.TANK_CHILDREN.containsKey(parentName)) return;
        BrokenTankData.removeChild(parentName, pos);
        drain(getFluidContained(), FluidAction.EXECUTE);
        if (!parentName.isEmpty() && BrokenTankData.hasTankByName(parentName) && pos.getY() == BrokenTankData.getTankMin(parentName) || pos.getY() == BrokenTankData.getTankMax(parentName))
            updateTankSize();
    }

    private boolean ensureTank()
    {
        if (level != null && (parentName.isEmpty() || !BrokenTankData.hasTankByName(parentName)))
            setTankLabel();
        return BrokenTankData.hasTankByName(parentName);
    }

    public void addChild(BE_BloodTank childTank)
    {
        BrokenTankData.addChild(parentName, childTank.getBlockPos());
        childTank.parentName = parentName;
        if (childTank.getBlockPos().getY() < BrokenTankData.getTankMin(parentName) || childTank.getBlockPos().getY() > BrokenTankData.getTankMax(parentName))
            updateTankSize();
    }

    public void createParentTank()
    {
        if (parentName.isEmpty())
            parentName = BrokenTankData.createNewParent(getBlockPos());

        BrokenTankData.syncFluid(parentName);
    }

    private void updateTankSize()
    {
        if (!BrokenTankData.hasTankByName(parentName)) return;

        for (BlockPos child : BrokenTankData.TANK_CHILDREN.get(parentName))
            if (child.getY() > BrokenTankData.getTankMax(parentName)) BrokenTankData.TANK_DATA.get(parentName)[2] = child.getY();
            else if (child.getY() < BrokenTankData.getTankMin(parentName))
                BrokenTankData.TANK_DATA.get(parentName)[1] = child.getY();

        BrokenTankData.TANK_DATA.get(parentName)[0] = (BrokenTankData.getTankMax(parentName) - BrokenTankData.getTankMin(parentName)) + 1;
    }

    public void setFluid(FluidStack fluidStack)
    {
        if (BrokenTankData.hasTankByName(parentName)) BrokenTankData.getTankByName(parentName).setFluid(fluidStack);
    }

    public int getRelativeHeight()
    {
        return getBlockPos().getY() - getTotalHeight();
    }

    public int getTotalHeight()
    {
        return BrokenTankData.getTankHeight(parentName);
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
                parentName = BrokenTankData.createNewParent(getBlockPos());

            for (BE_BloodTank tank : tanks)
                if (!tank.parentName.isEmpty())
                {
                    parentName = tank.parentName;
                    tank.addChild(this);
                    break;
                }

            if (parentName.isEmpty())
                parentName = BrokenTankData.recoverTankName(getBlockPos());

            if (BrokenTankData.hasTankByName(parentName))
            {
                BrokenTankData.syncTankName(parentName);
                BrokenTankData.syncFluid(parentName);
            }
        }
    }
*/}