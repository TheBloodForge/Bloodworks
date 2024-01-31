package com.bloodforge.bloodworks.Blocks.BlockEntities;

import com.bloodforge.bloodworks.Blocks.BlockBrainInteriorBase;
import com.bloodforge.bloodworks.Networking.NBTSyncS2CPacket;
import com.bloodforge.bloodworks.Networking.PacketManager;
import com.bloodforge.bloodworks.Registry.BlockRegistry;
import com.bloodforge.bloodworks.Util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class BE_Neuron extends BlockEntity
{
    List<BlockPos> neuronLocations = new ArrayList<>();
    private String NEURAL_ID;
    public boolean isDry = false; //doesn't need to be saved into NBT, based on blockstate.

    public BE_Neuron(BlockPos pos, BlockState blockState)
    {
        super(BlockRegistry.BLOCK_NEURON.blockEntity().get(), pos, blockState);
        NEURAL_ID = getNextID();
    }

    private String getNextID()
    {
        return UUID.randomUUID().toString();
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        CompoundTag updateTag = new CompoundTag();
        updateTag.putString("neural_id", getNeuralID());
        updateTag.put("NeuronPositions", wrapNBT());
        return updateTag;
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BE_Neuron entity)
    {
        //entity.isDry = !blockState.getValue(BlockBrainInteriorBase.WATERLOGGED);
    }

    public HashMap<String, BlockPos> neuronMap = new HashMap<>();

    @Override
    protected void saveAdditional(CompoundTag nbt)
    {
        nbt.putString("neural_id", NEURAL_ID);
        nbt.put("NeuronPositions", wrapNBT());
        super.saveAdditional(nbt);
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
        isDry = !getBlockState().getValue(BlockBrainInteriorBase.WATERLOGGED);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        NEURAL_ID = nbt.getString("neural_id");
        unwrapNBT(nbt.getCompound("NeuronPositions"));
        super.load(nbt);
        if(level == null || level.isClientSide) return;
        syncNeuron();
    }

    private static BlockPos firstNeuronPos = null;

    public static void doConnection(BlockPos pos, Level level)
    {
        if (firstNeuronPos == null)
        {
            firstNeuronPos = pos;
        } else
        {
            if (level.getBlockEntity(firstNeuronPos) instanceof BE_Neuron firstNeuron && level.getBlockEntity(pos) instanceof BE_Neuron secondNeuron)
            {
                firstNeuron.neuronMap.put(secondNeuron.getNeuralID(), pos);
                System.out.println("Made a new friend: " + firstNeuronPos.toShortString() + " <3 " + pos.toShortString());
                firstNeuronPos = null;
                firstNeuron.syncNeuron();
            }
        }
    }

    private String getNeuralID()
    {
        return NEURAL_ID;
    }

    private void syncNeuron()
    {
        PacketManager.sendToClients(new NBTSyncS2CPacket(getBlockPos(), getUpdateTag()));
    }

    private CompoundTag wrapNBT()
    {
        CompoundTag posTags = new CompoundTag(); //CompoundTag.TAG_LIST
        neuronMap.forEach((neuronID, neuronPos) -> posTags.putIntArray(neuronID, Util.getBlockPosAsIntArr(neuronPos)));
        return posTags;
    }

    public void unwrapNBT(CompoundTag nbt)
    {
        neuronMap.clear();
        Set<String> neuronIds = nbt.getAllKeys();
        for (String neuronId : neuronIds)
        {
            int[] posArr = nbt.getIntArray(neuronId);
            neuronMap.put(neuronId, new BlockPos(posArr[0], posArr[1], posArr[2]));
        }
    }
}