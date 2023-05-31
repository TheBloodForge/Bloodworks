package com.bloodforge.bloodworks.Blocks;

import com.bloodforge.bloodworks.Blocks.BlockEntities.BE_Tank;
import com.bloodforge.bloodworks.ClientUtils;
import com.bloodforge.bloodworks.Networking.MessageS2CPacket;
import com.bloodforge.bloodworks.Networking.PacketManager;
import com.bloodforge.bloodworks.Registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings({"unchecked", "deprecation"})
public class BlockBloodTank extends BlockMachineBase
{

    //    public static final BlockShape SHAPE = BlockShape.createBlockShape(2.5, 0, 2.5, 13.5, 16, 13.5);
    public static final BooleanProperty OUTPUT = BooleanProperty.create("output");
    public static final IntegerProperty TIER = IntegerProperty.create("tier", 0, 10);

    public BlockBloodTank()
    {
        super(
                BlockBehaviour.Properties
                        .of(Material.STONE)
                        .strength(1f, 8f)
                        .sound(SoundType.METAL)
                        .noOcclusion()
        );
    }

    // -------- Block entity stuff --------

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return BlockRegistry.BLOCK_BLOOD_TANK.blockEntity().get().create(pos, state);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter blockGetter, List<Component> components, TooltipFlag tooltipFlag)
    {
        ClientUtils.AddAdditionalShiftInfo(components, "Fill level: Unimplemented!");
        super.appendHoverText(stack, blockGetter, components, tooltipFlag);
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos)
    {
        if (level.getBlockEntity(pos) instanceof BE_Tank tank)
        {
            FluidStack fluid = tank.getFluidInTank(0);
            float lightLevel = fluid.getFluid().getFluidType().getLightLevel();
            lightLevel *= tank.getRelativeFill();
            return (int) lightLevel;
        }
        return 0;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        return createTickerHelper(type, (BlockEntityType<BE_Tank>) BlockRegistry.BLOCK_BLOOD_TANK.blockEntity().get(), BE_Tank::tick);
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState)
    {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState p_60578_, BlockGetter p_60579_, BlockPos p_60580_)
    {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getVisualShape(BlockState p_60479_, BlockGetter p_60480_, BlockPos p_60481_, CollisionContext p_60482_)
    {
        return Shapes.box(0.01, 0.01, 0.01, 0.99, 0.99, 0.99);
    }

    @Override
    public void onRemove(BlockState cState, Level level, BlockPos blockPos, BlockState newState, boolean isMoving)
    {
        if (cState.getBlock() != newState.getBlock())
        {
            BlockEntity ent = level.getBlockEntity(blockPos);
            if (ent instanceof BE_Tank tank)
                tank.breakTank(blockPos, level);
        }
        super.onRemove(cState, level, blockPos, newState, isMoving);
    }

    public boolean skipRendering(BlockState thisState, BlockState neighbor, Direction direction)
    {
        return neighbor.is(this);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext)
    {
        return this.defaultBlockState().setValue(TIER, 1);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateDefinition)
    {
        super.createBlockStateDefinition(blockStateDefinition.add(OUTPUT).add(TIER));
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState cState, Level level, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult)
    {
        if (!level.isClientSide())
        {
            if (level.getBlockEntity(pos) instanceof BE_Tank machine)
            {
                FluidStack stack = FluidStack.EMPTY;
                ItemStack heldItem;
                if ((heldItem = player.getItemInHand(interactionHand)).getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent())
                {
                    if (FluidUtil.getFluidContained(player.getItemInHand(interactionHand)).isPresent())
                        stack = FluidUtil.getFluidContained(player.getItemInHand(interactionHand)).get();
                    if (machine.isFluidValid(0, stack))
                    {
                        machine.fill(stack, IFluidHandler.FluidAction.EXECUTE);
                        return InteractionResult.sidedSuccess(!level.isClientSide());
                    }
                } else if (heldItem.is(Items.DEBUG_STICK))
                {
                    machine.setTier(0);
                    return InteractionResult.CONSUME;
                } else if (heldItem.getItem() instanceof BlockItem)
                {
                    return super.use(cState, level, pos, player, interactionHand, blockHitResult);
                } else
                {
                    PacketManager.sendToClients(new MessageS2CPacket(Component.literal(Component.translatable(machine.getFluidInTank(0).getTranslationKey()).getString() + " : " + machine.getFluidInTank(0).getAmount() + " | " + machine.getTankCapacity(0) + " -- " + machine.getID()), false));
                }
            }
        }
        if (level.isClientSide())
        {
            level.getLightEngine().checkBlock(pos);
        }
        return InteractionResult.sidedSuccess(!level.isClientSide());
    }
}