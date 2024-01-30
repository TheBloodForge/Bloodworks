package com.bloodforge.bloodworks.Blocks.BlockEntities;

import com.bloodforge.bloodworks.Blocks.BlockBattery;
import com.bloodforge.bloodworks.Energy.EnergyBattery;
import com.bloodforge.bloodworks.Networking.NBTSyncS2CPacket;
import com.bloodforge.bloodworks.Networking.PacketManager;
import com.bloodforge.bloodworks.Registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

public class BE_Battery extends BlockEntity
{
    private final LazyOptional<IEnergyStorage> energy;
    public final EnergyBattery battery;


    private int lastStoredEnergy = 0;

    public BE_Battery(BlockPos pos, BlockState blockState)
    {
        super(BlockRegistry.BLOCK_BATTERY.blockEntity().get(), pos, blockState);
        battery = new EnergyBattery(10000, 1000, 1000);
        energy = LazyOptional.of(() -> battery);
    }

    public static void tickServer(Level level, BlockPos blockPos, BlockState blockState, BE_Battery gen)
    {
        gen.distributeEnergy();
        if (gen.battery.getStored() != gen.lastStoredEnergy)
        {
            CompoundTag updateTag = new CompoundTag();
            updateTag.put("energy", gen.battery.serializeNBT());
            PacketManager.sendToClients(new NBTSyncS2CPacket(blockPos, updateTag));
            gen.lastStoredEnergy = gen.battery.getStored();
        }
    }

    public void distributeEnergy()
    {
        if(level == null) return;
        Direction batteryAxis = Direction.fromAxisAndDirection(getBlockState().getValue(BlockBattery.AXIS), Direction.AxisDirection.POSITIVE);;
        BlockEntity powerOutputP = level.getBlockEntity(getBlockPos().relative(batteryAxis));
        if(powerOutputP != null)
        {
            powerOutputP.getCapability(ForgeCapabilities.ENERGY, batteryAxis.getOpposite()).map(e->
            {
                //if is battery
                if(level.getBlockState(getBlockPos().relative(batteryAxis)).getBlock() == (BlockRegistry.BLOCK_BATTERY.block().get()))
                {
                    EqualizeTo(e);
                    powerOutputP.setChanged();
                }
                else if(getBlockState().getValue(BlockBattery.PILLAR_POS) == 0) //single block battery
                {
                    //if is_output false, output faces negative on one block battery
                    if(!getBlockState().getValue(BlockBattery.IS_OUTPUT))
                    {
                        SendPowerTo(e);
                        powerOutputP.setChanged();
                    }
                }
                else if(getBlockState().getValue(BlockBattery.IS_OUTPUT))
                {
                    SendPowerTo(e);
                    powerOutputP.setChanged();
                }
                return 0;
            });
        }
        BlockEntity powerOutputN = level.getBlockEntity(getBlockPos().relative(batteryAxis.getOpposite()));
        if(powerOutputN != null)
        {
            powerOutputN.getCapability(ForgeCapabilities.ENERGY, batteryAxis).map(e->
            {
                //if is battery
                if(level.getBlockState(getBlockPos().relative(batteryAxis.getOpposite())).getBlock() == (BlockRegistry.BLOCK_BATTERY.block().get()))
                {
                    EqualizeTo(e);
                    powerOutputN.setChanged();
                }
                else if(getBlockState().getValue(BlockBattery.PILLAR_POS) == 0) //single block battery
                {
                    //if is_output true, output faces positive on one block battery
                    if(!getBlockState().getValue(BlockBattery.IS_OUTPUT))
                    {
                        SendPowerTo(e);
                        powerOutputN.setChanged();
                    }
                }
                else if(getBlockState().getValue(BlockBattery.IS_OUTPUT))
                {
                    SendPowerTo(e);
                    powerOutputN.setChanged();
                }
                return 0;
            });
        }
    }

    public void EqualizeTo(IEnergyStorage e)
    {
        if(!e.canReceive()) return;
        int equalizeAmt = battery.getEnergyStored() - e.getEnergyStored();
        if(equalizeAmt <= 0) return;
        if(equalizeAmt <= 1000)
        {
            equalizeAmt /= 2; //prevents passing back and forth
        }
        if(equalizeAmt < 2)
        {
            equalizeAmt = level.random.nextBoolean()?1:0; //prevents passing back and forth
        }
        int maxReceivable = e.receiveEnergy(equalizeAmt, true);
        int extracted = battery.extractEnergy(maxReceivable, false);
        e.receiveEnergy(extracted, false);
        setChanged();
    }
    public void SendPowerTo(IEnergyStorage e)
    {
        if(!e.canReceive()) return;
        int maxReceivable = e.receiveEnergy(battery.getStored(), true);
        int extracted = battery.extractEnergy(maxReceivable, false);
        e.receiveEnergy(extracted, false);
        setChanged();
    }
    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ENERGY && (side == Direction.UP || side == Direction.DOWN))
            return energy.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        CompoundTag updateTag = new CompoundTag();
        return updateTag;
    }

    @Override
    protected void saveAdditional(CompoundTag nbt)
    {
        super.saveAdditional(nbt);

        nbt.put("energy", battery.serializeNBT());
    }

    @Override
    public void load(CompoundTag nbt)
    {
        super.load(nbt);

        Tag energyTag = nbt.get("energy");
        if(energyTag != null)
            battery.deserializeNBT(energyTag);
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }
}