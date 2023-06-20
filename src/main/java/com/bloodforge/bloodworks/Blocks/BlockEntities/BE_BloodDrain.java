package com.bloodforge.bloodworks.Blocks.BlockEntities;

import com.bloodforge.bloodworks.Blocks.BlockBloodDrain;
import com.bloodforge.bloodworks.Networking.MessageS2CPacket;
import com.bloodforge.bloodworks.Networking.NBTSyncS2CPacket;
import com.bloodforge.bloodworks.Networking.PacketManager;
import com.bloodforge.bloodworks.Registry.BlockRegistry;
import com.bloodforge.bloodworks.Registry.DamageSources;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class BE_BloodDrain extends BlockEntity
{
    private float bloodInDrain = 0;
    public BE_BloodDrain(BlockPos pos, BlockState blockState)
    {
        super(BlockRegistry.BLOCK_BLOOD_DRAIN.blockEntity().get(), pos, blockState);
    }

    public static void tickServer(Level level, BlockPos blockPos, BlockState blockState, BE_BloodDrain self)
    {
        if(level.getBlockState(blockPos).getValue(BlockBloodDrain.MACERATOR_LEVEL) > 0)
        {
            for (Object o : level.getEntities((Entity) null, new AABB(blockPos, new BlockPos(blockPos.getX()+1, blockPos.getY()+1, blockPos.getZ()+1)), EntitySelector.LIVING_ENTITY_STILL_ALIVE).toArray())
            {
                if (o instanceof LivingEntity ent)
                {
                    ent.hurt(DamageSources.MACERATION, 1f);
                    ent.hurt(DamageSource.ANVIL, 1f);
                }
            }
        }
    }

    @Override
    public CompoundTag getUpdateTag()
    {
        CompoundTag updateTag = new CompoundTag();
        updateTag.putFloat("blood_amt", bloodInDrain);
        return updateTag;
    }

    @Override
    protected void saveAdditional(CompoundTag nbt)
    {
        nbt.putFloat("blood_amt", bloodInDrain);
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        bloodInDrain = nbt.getFloat("blood_amt");
        super.load(nbt);
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    public void collectEntityBlood(LivingEntity entity, float damageAmount)
    {
        bloodInDrain += damageAmount;
        PacketManager.sendToClients(new NBTSyncS2CPacket(getBlockPos(), getUpdateTag()));
        PacketManager.sendToClients(new MessageS2CPacket(Component.literal("" + damageAmount), false));
    }
}