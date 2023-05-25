package com.wiggle1000.bloodworks.Blocks;

import com.wiggle1000.bloodworks.ClientUtils;
import com.wiggle1000.bloodworks.Particles.ParticleHelper;
import com.wiggle1000.bloodworks.Registry.BlockEntityRegistry;
import com.wiggle1000.bloodworks.Registry.ItemRegistry;
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
        ClientUtils.AddChatComponents(components, "Some ..Intestines? Smells funny.", "Function: Processes mashed foods to produce blood nutrients.");
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
        return BlockEntityRegistry.BE_INTESTINE.get().create(pos, state);
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
        return this.defaultBlockState().setValue(FACING_FROM, pContext.getClickedFace()).setValue(FACING_TO, pContext.getClickedFace());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateDefinition)
    {
        super.createBlockStateDefinition(blockStateDefinition.add(FACING_FROM).add(FACING_TO));
    }

    public DirectionProperty getFacingTo()
    { return FACING_TO; }

    public DirectionProperty getFacingFrom()
    { return FACING_FROM; }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos blockPos, Player player, InteractionHand interactedHand, BlockHitResult hitResult)
    {
        if(level.isClientSide()) return InteractionResult.FAIL;
        if (!player.getItemInHand(interactedHand).is(ItemRegistry.BLOCK_INTESTINE.get())) {
            return super.use(state, level, blockPos, player, interactedHand, hitResult);
        }
        if (state.getBlock() instanceof BlockIntestine) {
            BlockState newState = this.defaultBlockState().setValue(FACING_FROM, state.getValue(FACING_FROM)).setValue(FACING_TO, hitResult.getDirection());
            level.setBlocksDirty(blockPos, state, newState);
            level.setBlockAndUpdate(blockPos, newState);
        }
        return super.use(state, level, blockPos, player, interactedHand, hitResult);
    }
}