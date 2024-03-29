package com.bloodforge.bloodworks.Blocks.BlockEntities;

import com.bloodforge.bloodworks.Globals;
import com.bloodforge.bloodworks.Multiblock.Structures.MultiblockBraincase;
import com.bloodforge.bloodworks.Networking.MessageS2CPacket;
import com.bloodforge.bloodworks.Networking.PacketManager;
import com.bloodforge.bloodworks.Registry.BlockRegistry;
import com.bloodforge.bloodworks.Util.Util;
import com.ibm.icu.impl.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

@SuppressWarnings({"unused", "SameReturnValue"})
public class BE_Braincase_Controller extends BlockEntity
{

    public static final MultiblockBraincase MULTIBLOCK_BRAINCASE_GETTER = new MultiblockBraincase();
    public static final Component TITLE = Component.translatable(Globals.MODID + ".braincase_controller");

    BlockPos multiblockCoordMax;
    BlockPos multiblockCoordMin;

    boolean isFilling = false;
    boolean isEmptying = false;
    boolean isFull = false;

    public BE_Braincase_Controller(BlockPos pos, BlockState state)
    {
        super(BlockRegistry.BLOCK_INFUSION_CHAMBER.blockEntity().get(), pos, state);
        multiblockCoordMin = pos;
        multiblockCoordMax = pos;
    }


    @Override
    public void onLoad()
    {
        super.onLoad();
        if(getLevel().isClientSide()) return;
        //verify structure

    }


    @Override
    protected void saveAdditional(CompoundTag nbt)
    {
        nbt.putIntArray("minCorner", Util.getBlockPosAsIntArr(multiblockCoordMin));
        nbt.putIntArray("maxCorner", Util.getBlockPosAsIntArr(multiblockCoordMax));
        nbt.putBoolean("isFilling", isFilling);
        nbt.putBoolean("isEmptying", isEmptying);
        nbt.putBoolean("isFull", isFull);
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        multiblockCoordMin = Util.getBlockPosFromIntArr(nbt.getIntArray("minCorner"));
        multiblockCoordMax = Util.getBlockPosFromIntArr(nbt.getIntArray("maxCorner"));
        isFilling = nbt.getBoolean("isFilling");
        isEmptying = nbt.getBoolean("isEmptying");
        isFull = nbt.getBoolean("isFull");
        super.load(nbt);
    }


    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BE_Braincase_Controller entity)
    {
        if (level.isClientSide()) return;

    }

    private boolean verifyStructureOK()
    {
        return true;
    }

    public void use(Player player, InteractionHand interactionHand, BlockHitResult blockHitResult)
    {
        if(player.level.isClientSide()) return;
        long start = System.nanoTime();
        Pair<BlockPos, BlockPos> found = MULTIBLOCK_BRAINCASE_GETTER.tryFindMultiblock(player.level, getBlockPos(), new BlockPos(4, 3, 4), new BlockPos(12, 10, 12), MultiblockBraincase.BLOCK_MASK_BRAINCASE_CORNERS);
        MultiblockBraincase.MultiblockBraincaseScanResults results = MULTIBLOCK_BRAINCASE_GETTER.mostRecentScanResult;
        float len = System.nanoTime() - start;
        len /= 1000000f;
        /*
        if (found != null)
            PacketManager.sendToClients(new MessageS2CPacket(Component.literal(found.first.toString() + ", " + found.second.toString() + " in " + len + " ms."), false));
        else
            PacketManager.sendToClients(new MessageS2CPacket(Component.literal("Not found" + " in " + len + " ms."), false));
         */

        Component component = Component.literal("");
        for (String s : results.errorMessage().split("\n"))
        {
            PacketManager.sendToClients(new MessageS2CPacket(Component.literal(s).withStyle(ChatFormatting.YELLOW), true));
        }
    }
}