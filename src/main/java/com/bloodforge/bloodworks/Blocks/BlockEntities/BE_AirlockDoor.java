package com.bloodforge.bloodworks.Blocks.BlockEntities;

import com.bloodforge.bloodworks.Networking.NBTSyncS2CPacket;
import com.bloodforge.bloodworks.Networking.PacketManager;
import com.bloodforge.bloodworks.Registry.BlockRegistry;
import com.bloodforge.bloodworks.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

@SuppressWarnings({"unused", "SameReturnValue"})
public class BE_AirlockDoor extends BlockEntity
{

    public float doorClosePercent = 1f;

    public boolean isOpen = false;
    public BlockPos masterPos;

    public BE_AirlockDoor(BlockPos pos, BlockState state)
    {
        super(BlockRegistry.BLOCK_AIRLOCK_DOOR.blockEntity().get(), pos, state);
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


    public void use(Player player, InteractionHand interactionHand, BlockHitResult blockHitResult)
    {
        //TODO: send message to connected controller
        //if(masterPos == this.getBlockPos())
        {
            isOpen = !isOpen;
            setChanged();
            System.out.println("balls are " + (isOpen ? "open" : "closed"));
            PacketManager.sendToClients(new NBTSyncS2CPacket(this.getBlockPos(), this.getUpdateTag()));
        }
    }

    @Override
    public void handleUpdateTag(CompoundTag nbt) {
        super.handleUpdateTag(nbt);
        //isOpen = nbt.getBoolean("isOpen");
        //masterPos = Util.getBlockPosFromIntArr(nbt.getIntArray("masterPos"));
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("isOpen", this.isOpen);
        nbt.putIntArray("masterPos", Util.getBlockPosAsIntArr(masterPos));
        return nbt;
    }
}