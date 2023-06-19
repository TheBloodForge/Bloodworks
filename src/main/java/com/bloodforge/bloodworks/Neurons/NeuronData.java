package com.bloodforge.bloodworks.Neurons;

import com.bloodforge.bloodworks.Neurons.NodeTypes.FloatNode;
import com.bloodforge.bloodworks.Neurons.NodeTypes.ItemStackNode;
import com.bloodforge.bloodworks.Neurons.NodeTypes.NeuronIONode;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;

public class NeuronData
{
    public BlockPos position;
    public ArrayList<NeuronIONode> inputs = new ArrayList<>();
    public ArrayList<NeuronIONode> outputs = new ArrayList<>();

    public NeuronData(BlockPos position)
    {
        this.position = position;
    }

    public NeuronData withInputs(NeuronIONode.NEURON_IO_TYPES... inputTypes)
    {
        for(NeuronIONode.NEURON_IO_TYPES neuronType : inputTypes)
        {
            switch (neuronType)
            {
                case FLOAT:
                    inputs.add(new FloatNode());
                    break;
                case ITEMSTACK:
                    inputs.add(new ItemStackNode());
                    break;
            }
        }
        return this;
    }
    public NeuronData withOutputs(NeuronIONode.NEURON_IO_TYPES... inputTypes)
    {
        for(NeuronIONode.NEURON_IO_TYPES neuronType : inputTypes)
        {
            switch (neuronType)
            {
                case FLOAT:
                    outputs.add(new FloatNode());
                    break;
                case ITEMSTACK:
                    outputs.add(new ItemStackNode());
                    break;
            }
        }
        return this;
    }

    public void process()
    {

    }
}
