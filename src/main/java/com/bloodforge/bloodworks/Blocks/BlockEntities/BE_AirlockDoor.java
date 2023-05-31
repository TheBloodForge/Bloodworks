package com.bloodforge.bloodworks.Blocks.BlockEntities;

import com.bloodforge.bloodworks.Registry.BlockRegistry;
import com.bloodforge.bloodworks.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

@SuppressWarnings({"unused", "SameReturnValue"})
public class BE_AirlockDoor extends BlockEntity
{

    boolean isOpen = false;
    BlockPos masterPos;

    public BE_AirlockDoor(BlockPos pos, BlockState state)
    {
        super(BlockRegistry.BLOCK_INFUSION_CHAMBER.blockEntity().get(), pos, state);
        isOpen = false;
        masterPos = pos;
    }


    @Override
    public void onLoad()
    {
        super.onLoad();
    }


    @Override
    protected void saveAdditional(CompoundTag nbt)
    {
        nbt.putBoolean("isOpen", this.isOpen);
        nbt.putIntArray("masterPos", Util.getBlockPosAsIntArr(masterPos));
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        isOpen = nbt.getBoolean("isOpen");
        masterPos = Util.getBlockPosFromIntArr(nbt.getIntArray("masterPos"));
        super.load(nbt);
    }


    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BE_AirlockDoor entity)
    {
        if (level.isClientSide())
        {
        }

    }

    public void use(Player player, InteractionHand interactionHand, BlockHitResult blockHitResult)
    {
        //TODO: send message to connected controller
    }
}