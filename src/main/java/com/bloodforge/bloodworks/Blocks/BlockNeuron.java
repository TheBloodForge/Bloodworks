package com.bloodforge.bloodworks.Blocks;

import com.bloodforge.bloodworks.Blocks.BlockEntities.BE_Neuron;
import com.bloodforge.bloodworks.Globals;
import com.bloodforge.bloodworks.Networking.PacketManager;
import com.bloodforge.bloodworks.Registry.BlockRegistry;
import com.bloodforge.bloodworks.Registry.ItemRegistry;
import com.bloodforge.bloodworks.Server.PlayerSelectionHudTracker;
import com.bloodforge.bloodworks.Util.ISelectionMenuResponder;
import com.bloodforge.bloodworks.Util.SelectionMenuOptions;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class BlockNeuron extends BlockBrainInteriorBase implements EntityBlock, ISelectionMenuResponder
{
    protected VoxelShape BLOCK_SHAPE = box(4, 4, 4, 12, 12, 12);

    //    public static final BlockShape SHAPE = BlockShape.createBlockShape(2.5, 0, 2.5, 13.5, 16, 13.5);
    public static final BooleanProperty OUTPUT = BooleanProperty.create("output");

    public static SelectionMenuOptions neuronTypeMenu = new SelectionMenuOptions(Component.translatable("neuron.bloodworks.title"))
            .withEntry(new SelectionMenuOptions.SelectionMenuEntry(Component.translatable("neuron.bloodworks.and")))
            .withEntry(new SelectionMenuOptions.SelectionMenuEntry(Component.translatable("neuron.bloodworks.or")))
            .withEntry(new SelectionMenuOptions.SelectionMenuEntry(Component.translatable("neuron.bloodworks.xor")))
            .withEntry(new SelectionMenuOptions.SelectionMenuEntry(Component.translatable("neuron.bloodworks.not")))
            .withEntry(new SelectionMenuOptions.SelectionMenuEntry(Component.translatable("neuron.bloodworks.add").withStyle(ChatFormatting.AQUA)))
            .withEntry(new SelectionMenuOptions.SelectionMenuEntry(Component.translatable("neuron.bloodworks.mul").withStyle(ChatFormatting.AQUA)))
            .withEntry(new SelectionMenuOptions.SelectionMenuEntry(Component.translatable("neuron.bloodworks.recip").withStyle(ChatFormatting.AQUA)))
            .withEntry(new SelectionMenuOptions.SelectionMenuEntry(Component.translatable("neuron.bloodworks.rand").withStyle(ChatFormatting.GREEN)));

    public BlockNeuron()
    {
    }

    // -------- Block entity stuff --------

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return BlockRegistry.BLOCK_NEURON.blockEntity().get().create(pos, state);
    }
    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos)
    {
        return 12;
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter blockGetter, BlockPos blockPos)
    {
        return 1F;
    }

    @Override
    public void onBlockStateChange(LevelReader level, BlockPos pos, BlockState oldState, BlockState newState)
    {
        super.onBlockStateChange(level, pos, oldState, newState);

        if (level.getBlockEntity(pos) instanceof BE_Neuron be_neuron)
        {
            be_neuron.isDry = !newState.getValue(BlockBrainInteriorBase.WATERLOGGED);
        }

    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212level, BlockState state, BlockEntityType<T> type)
    {
        return createTickerHelper(type, (BlockEntityType<BE_Neuron>) BlockRegistry.BLOCK_NEURON.blockEntity().get(), BE_Neuron::tick);
    }

    @Override
    public VoxelShape getShape(BlockState p_51104_, BlockGetter p_51105_, BlockPos p_51106_, CollisionContext p_51107_)
    {
        return BLOCK_SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState cState, Level level, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult)
    {
        if (level.isClientSide()) return super.use(cState, level, pos, player, interactionHand, blockHitResult);

        if(!cState.getValue(BlockBrainInteriorBase.WATERLOGGED))
        {
            PacketManager.showErrorToClient(pos, Component.translatable("ui.bloodworks.error.neuron_dry"), (ServerPlayer) player);
            return super.use(cState, level, pos, player, interactionHand, blockHitResult);
        }
        if (player.getItemInHand(interactionHand).is(ItemRegistry.ITEM_NEURAL_CATALYST.get()))
        {
            if (level.getBlockEntity(pos) instanceof BE_Neuron)
            {
                BE_Neuron.doConnection(pos, level);
            }
        }
        else
        {
            if(!PlayerSelectionHudTracker.PlayerHasMenuOpen((ServerPlayer) player, pos))
            {

                PlayerSelectionHudTracker.OpenAndTrackMenu((ServerPlayer) player, neuronTypeMenu, pos, 0, this);
            }
        }


        return super.use(cState, level, pos, player, interactionHand, blockHitResult);
        //return InteractionResult.sidedSuccess(!level.isClientSide());
    }

    @Override
    public void ReceiveSelection(BlockPos pos, SelectionMenuOptions menu, int selection, boolean isFinalSelection, boolean isCancelled)
    {
        Globals.LogDebug("PLAYER SELECTED " + selection + " IN MENU! FINAL:"+isFinalSelection+" CANCEL:"+isCancelled, false);
    }
}