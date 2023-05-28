package com.wiggle1000.bloodworks.Blocks.BlockEntities;

import com.wiggle1000.bloodworks.Registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class BE_Neuron extends BlockEntity
{
    List<BlockPos> neuronLocations = new ArrayList<>();
    public BE_Neuron(BlockPos pos, BlockState blockState)
    {
        super(BlockRegistry.BLOCK_NEURON.blockEntity().get(), pos, blockState);
    }


    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BE_Neuron entity)
    {

    }

    @Override
    public AABB getRenderBoundingBox()
    {
        return super.getRenderBoundingBox();
        //return AABB.ofSize(new Vec3(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ()), 1.5, 1.5, 1.5);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt)
    {
        nbt.putString("axonConnections", getLocationsAsString());
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        loadNeuronLocations(nbt.getString("axonConnections"));
        super.load(nbt);
    }

    private void loadNeuronLocations(String axonConnections)
    {
        String[] neuronPosStrArr = axonConnections.split("\\|");
        for (String s : neuronPosStrArr)
        {
            String[] xyz = s.split(" ");
            BlockPos pos = new BlockPos(Integer.parseInt(xyz[0]), Integer.parseInt(xyz[1]), Integer.parseInt(xyz[2]));
            neuronLocations.add(pos);
        }
    }

    private String getLocationsAsString()
    {
        String nPosStr = "";
        for (BlockPos neuronLocation : neuronLocations)
        {
            nPosStr += (nPosStr.isEmpty() ? neuronLocation.toShortString() : "|" + neuronLocation.toShortString());
        }
        return nPosStr;
    }

    private static BlockPos firstNeuronPos = null;
    public static void doConnection(BlockPos pos, Level level)
    {
        if (firstNeuronPos == null) {
            firstNeuronPos = pos;
        } else {
            if (level.getBlockEntity(firstNeuronPos) instanceof BE_Neuron neuron1) {
                neuron1.neuronLocations.add(pos);
                firstNeuronPos = null;
            }
        }
    }
}