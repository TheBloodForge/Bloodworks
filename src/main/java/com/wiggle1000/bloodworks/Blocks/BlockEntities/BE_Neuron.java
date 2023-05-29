package com.wiggle1000.bloodworks.Blocks.BlockEntities;

import com.wiggle1000.bloodworks.Registry.BlockRegistry;
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


    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BE_Neuron entity)
    {

    }

    HashMap<String, BlockPos> neuronMap = new HashMap<>();
    @Override
    protected void saveAdditional(CompoundTag nbt)
    {
        nbt.putString("neural_id", NEURAL_ID);
        CompoundTag posTags = new CompoundTag(); //CompoundTag.TAG_LIST
        neuronMap.forEach((neuronID, neuronPos) -> posTags.putIntArray(neuronID, getIntArrFromPos(neuronPos)));
        nbt.put("NeuronPositions", posTags);
        super.saveAdditional(nbt);
    }


    @Override
    public void load(CompoundTag nbt)
    {
        CompoundTag posTags = nbt.getCompound("NeuronPositions");
        NEURAL_ID = nbt.getString("neural_id");
        Set<String> neuronIds = posTags.getAllKeys();
        for (String neuronId : neuronIds)
        {
            int[] posArr = posTags.getIntArray(neuronId);
            neuronMap.put(neuronId, new BlockPos(posArr[0], posArr[1], posArr[2]));
        }
        super.load(nbt);
    }

    private int[] getIntArrFromPos(BlockPos neuronPos)
    {
        return new int[]{neuronPos.getX(), neuronPos.getY(), neuronPos.getZ()};
    }

    private static BlockPos firstNeuronPos = null;
    public static void doConnection(BlockPos pos, Level level)
    {
        if (firstNeuronPos == null) {
            firstNeuronPos = pos;
        } else {
            if (level.getBlockEntity(firstNeuronPos) instanceof BE_Neuron firstNeuron && level.getBlockEntity(pos) instanceof BE_Neuron secondNeuron) {
                firstNeuron.neuronMap.put(secondNeuron.getNeuralID(), pos);
                firstNeuronPos = null;
            }
        }
    }

    private String getNeuralID()
    {
        return NEURAL_ID;
    }
}