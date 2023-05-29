package com.wiggle1000.bloodworks.Blocks;

import com.wiggle1000.bloodworks.ClientUtils;
import com.wiggle1000.bloodworks.Networking.MessageS2CPacket;
import com.wiggle1000.bloodworks.Networking.PacketManager;
import com.wiggle1000.bloodworks.Particles.ParticleHelper;
import com.wiggle1000.bloodworks.Registry.BlockRegistry;
import com.wiggle1000.bloodworks.Registry.ParticleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("deprecation")
public class BlockIntestine extends BaseEntityBlock
{
    public static final DirectionProperty FACING_TO = DirectionProperty.create("facing_to"), FACING_FROM = DirectionProperty.create("facing_from");
    public static final IntegerProperty INTESTINE_ID = IntegerProperty.create("intestine_id", 0, 800);
    public BlockIntestine()
    {
        super(
                BlockBehaviour.Properties
                        .of(Material.STONE)
                        .strength(4f, 5f)
                        .sound(SoundType.SLIME_BLOCK)
                        .noOcclusion()
        );
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter blockGetter, List<Component> components, TooltipFlag tooltipFlag)
    {
        ClientUtils.AddChatComponents(components, stack);
        super.appendHoverText(stack, blockGetter, components, tooltipFlag);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState blockState, Entity stepperOnner)
    {
        super.stepOn(level, pos, blockState, stepperOnner);
        ParticleHelper.DoStepParticle(ParticleRegistry.PARTICLE_FLESH_STEP.get(), level, pos, blockState, stepperOnner);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return BlockRegistry.BLOCK_INTESTINE.blockEntity().get().create(pos, state);
    }

    @Override
    public VoxelShape getVisualShape(BlockState p_60479_, BlockGetter p_60480_, BlockPos p_60481_, CollisionContext p_60482_)
    {
        return Shapes.box(0.01, 0.01, 0.01, 0.99, 0.99, 0.99);
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState)
    {
        return RenderShape.INVISIBLE;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos)
    {
        return true;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext)
    {
        return this.defaultBlockState().setValue(FACING_FROM, pContext.getClickedFace().getOpposite()).setValue(FACING_TO, pContext.getClickedFace().getOpposite()).setValue(INTESTINE_ID, 1);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateDefinition)
    {
        super.createBlockStateDefinition(blockStateDefinition.add(FACING_FROM).add(FACING_TO).add(INTESTINE_ID));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos blockPos, Player player, InteractionHand interactedHand, BlockHitResult hitResult)
    {
        if(level.isClientSide()) return InteractionResult.sidedSuccess(level.isClientSide());
        PacketManager.sendToClients(new MessageS2CPacket(Component.literal("to = " + state.getValue(FACING_TO) + " from = " + state.getValue(FACING_FROM) + " ID = " + state.getValue(INTESTINE_ID)), false));
        if (!player.getItemInHand(interactedHand).is(BlockRegistry.BLOCK_INTESTINE.item().get())) {
            return super.use(state, level, blockPos, player, interactedHand, hitResult);
        }
        if (state.getBlock() instanceof BlockIntestine) {
            BlockState newState = this.defaultBlockState().setValue(FACING_FROM, state.getValue(FACING_FROM)).setValue(FACING_TO, hitResult.getDirection()).setValue(INTESTINE_ID, state.getValue(INTESTINE_ID));
            level.setBlocksDirty(blockPos, state, newState);
            level.setBlockAndUpdate(blockPos, newState);
            player.level.setBlockAndUpdate(blockPos.relative(hitResult.getDirection()), BlockRegistry.BLOCK_INTESTINE.block().get().defaultBlockState().setValue(FACING_FROM, hitResult.getDirection().getOpposite()).setValue(INTESTINE_ID, state.getValue(INTESTINE_ID) + 1));
            return InteractionResult.sidedSuccess(!level.isClientSide);
        }
        return super.use(state, level, blockPos, player, interactedHand, hitResult);
    }
}