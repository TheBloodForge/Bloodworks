package com.bloodforge.bloodworks.Blocks.BlockEntities;

import com.bloodforge.bloodworks.Globals;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("unused")
public class BlockEntityMachineBase extends BlockEntity
{

    public static final Component TITLE = Component.translatable(Globals.MODID + ".genericMachine");

    public int progress = 0;


    public BlockEntityMachineBase(BlockEntityType block, BlockPos pos, BlockState state)
    {
        super(block, pos, state);
    }


    @Override
    protected void saveAdditional(CompoundTag nbt)
    {
        nbt.putInt("progress", this.progress);
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        progress = nbt.getInt("progress");
        super.load(nbt);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BlockEntityMachineBase entity)
    {
    }
}