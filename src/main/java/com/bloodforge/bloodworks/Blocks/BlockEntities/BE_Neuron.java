package com.bloodforge.bloodworks.Blocks.BlockEntities;

import com.bloodforge.bloodworks.Registry.BlockRegistry;
import com.bloodforge.bloodworks.Util;
import com.bloodforge.bloodworks.Networking.NeuronSyncS2CPacket;
import com.bloodforge.bloodworks.Networking.PacketManager;
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
        return wrapNBT();
    }

    @Override
    public void handleUpdateTag(CompoundTag tag)
    {
        unwrapNBT(tag);
        super.handleUpdateTag(tag);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BE_Neuron entity)
    {
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
    public void load(CompoundTag nbt)
    {
        NEURAL_ID = nbt.getString("neural_id");
        unwrapNBT(nbt.getCompound("NeuronPositions"));
        super.load(nbt);
        syncNeuron();
    }

    private static BlockPos firstNeuronPos = null;
    public static void doConnection(BlockPos pos, Level level)
    {
        if (firstNeuronPos == null)
        {
            firstNeuronPos = pos;
        } else {
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

    private void syncNeuron() {
        PacketManager.sendToClients(new NeuronSyncS2CPacket(getBlockPos(), wrapNBT()));
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
        //CompoundTag posTags = nbt.getCompound("NeuronPositions");
        Set<String> neuronIds = nbt.getAllKeys();
        for (String neuronId : neuronIds)
        {
            int[] posArr = nbt.getIntArray(neuronId);
            neuronMap.put(neuronId, new BlockPos(posArr[0], posArr[1], posArr[2]));
        }
    }
}