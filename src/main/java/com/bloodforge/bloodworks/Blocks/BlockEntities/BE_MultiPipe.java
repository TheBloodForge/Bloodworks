package com.bloodforge.bloodworks.Blocks.BlockEntities;

import com.bloodforge.bloodworks.Blocks.Dummies.DummyPipe;
import com.bloodforge.bloodworks.Common.Config.BloodworksCommonConfig;
import com.bloodforge.bloodworks.Networking.MessageS2CPacket;
import com.bloodforge.bloodworks.Networking.PacketManager;
import com.bloodforge.bloodworks.Registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.List;

import static com.bloodforge.bloodworks.Blocks.BlockFluidPipe.*;

public class BE_MultiPipe extends BlockEntity
{
    private List<DummyPipe> internal_pipes = new ArrayList<>();

    public BE_MultiPipe(BlockPos pos, BlockState state)
    {
        super(BlockRegistry.BLOCK_MULTI_PIPE.blockEntity().get(), pos, state);
    }

    /*
        Handle Forge Capabilities by checking internal pipes and returning relevant information
        Or don't at all as this pipe will do all the stuff itself, pulling/pushing
        maybe support auto-push/auto-pull machines

        can do 9 pipes if, renderer allows array of 9 [3x3] grid so sub blocks get 0.33 of a block to draw within
        cleaner look is 0.25 of a block, and allow this tile itself to render as junction/facade block to hide rendering entirely
    */

    private void tick()
    {
        // loop internal pipes and call their tick function
    }

//######################################################################\\
//                           BELOW IS UTILITIES                         \\
//######################################################################\\


    private int getCooldown()
    {
        int cdr = BloodworksCommonConfig.TANK_COOLDOWN_REDUCTION.get();
        return Math.max(0, BloodworksCommonConfig.MAX_TANK_COOLDOWN.get() - cdr);
    }

    public static List<IFluidHandler> getNeighborFluidHandlers(BlockPos blockPos, Level level)
    {
        List<IFluidHandler> handlers = new ArrayList<>();
        for (Direction value : Direction.values())
            if (level.getBlockEntity(blockPos.relative(value)) instanceof IFluidHandler handler)
                handlers.add(handler);
        return handlers;
    }

    public static List<IFluidHandler> getNeighborFluidHandlersNotPipes(BlockPos blockPos, Level level)
    {
        List<IFluidHandler> handlers = new ArrayList<>();
        for (Direction value : Direction.values())
            if (level.getBlockEntity(blockPos.relative(value)) instanceof IFluidHandler handler
                    && !(level.getBlockEntity(blockPos.relative(value)) instanceof BE_MultiPipe))
                handlers.add(handler);
        return handlers;
    }

    private static List<BE_MultiPipe> getNearbyPipes(Level level, BlockPos pos)
    {
        List<BE_MultiPipe> pipes = new ArrayList<>();
        for (Direction value : Direction.values())
            if (level.getBlockEntity(pos.relative(value)) instanceof BE_MultiPipe pipe)
                pipes.add(pipe);
        return pipes;
    }

    public void printInformation()
    {
        PacketManager.sendToClients(new MessageS2CPacket(Component.literal("isConnected:" + getBlockState().getValues().values()), true));
//        if (PIPE_TANK == null) return;
//        PacketManager.sendToClients(new MessageS2CPacket(Component.literal(Component.translatable(getFluidInTank(0).getTranslationKey()).getString() + ": " + getFluidInTank(0).getAmount()), true));
    }

//######################################################################\\
//                     BELOW IS MANDATORY DEFAULTS                      \\
//                   DO NOT TOUCH, SHOULD NOT HAVE TO                   \\
//######################################################################\\
    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BE_MultiPipe pipe)
    {
        if (level == null || level.isClientSide) return;
        pipe.tick();
    }

    @Override
    public void load(CompoundTag tag)
    {
        super.load(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        if (level == null || level.isClientSide) return;
        super.saveAdditional(tag);
    }

    // ####################### FORGE CAPABILITIES #######################


    public void breakPipe(BlockPos blockPos, Level level)
    {
//        if (connectedPipes == null) return;
//        connectedPipes.get(pipeModes).remove(this);
    }

    public void setMode(Direction direction, boolean isRedstone)
    {

    }

    private boolean isConnected(Direction dir)
    {
        return switch (dir)
        {
            case DOWN -> getBlockState().getValue(DOWN);
            case UP -> getBlockState().getValue(UP);
            case NORTH -> getBlockState().getValue(NORTH);
            case SOUTH -> getBlockState().getValue(SOUTH);
            case WEST -> getBlockState().getValue(WEST);
            case EAST -> getBlockState().getValue(EAST);
        };
    }

    public void neighborChanged(BlockPos neighbor)
    {

    }

    public void handleItemUsed(ItemStack heldItem)
    {

    }

    // ####################### END OF FORGE CAPABILITIES #######################
}