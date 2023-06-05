package com.bloodforge.bloodworks.Blocks;

import com.bloodforge.bloodworks.Blocks.BlockEntities.BE_Tank;
import com.bloodforge.bloodworks.Client.ClientUtils;
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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
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
public class BlockBloodTank extends BaseEntityBlock
{
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
    public boolean propagatesSkylightDown(BlockState p_49928_, BlockGetter p_49929_, BlockPos p_49930_)
    {
        return true;
    }

    @Override
    public float getShadeBrightness(BlockState p_60472_, BlockGetter p_60473_, BlockPos p_60474_)
    {
        return 1.0f;
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
    public InteractionResult use(BlockState cState, Level level, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult)
    {
        if (!level.isClientSide())
        {
            if (level.getBlockEntity(pos) instanceof BE_Tank tank)
            {
                FluidStack stack = FluidStack.EMPTY;
                ItemStack heldItem;
                if ((heldItem = player.getItemInHand(interactionHand)).getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent())
                {
                    if (FluidUtil.getFluidContained(player.getItemInHand(interactionHand)).isPresent())
                        stack = FluidUtil.getFluidContained(player.getItemInHand(interactionHand)).get();
                    if (tank.isFluidValid(0, stack))
                    {
                        tank.fill(stack, IFluidHandler.FluidAction.EXECUTE);
                        return InteractionResult.sidedSuccess(!level.isClientSide());
                    }
                } else if (heldItem.is(Items.DEBUG_STICK)) {
                    tank.setTier(0);
                    return InteractionResult.CONSUME;
                } else if (heldItem.getItem() instanceof BlockItem) {
                    return super.use(cState, level, pos, player, interactionHand, blockHitResult);
                } else {
                    PacketManager.sendToClients(new MessageS2CPacket(Component.literal(Component.translatable(tank.getFluidInTank(0).getTranslationKey()).getString() + " : " + tank.getFluidInTank(0).getAmount() + " | " + tank.getTankCapacity(0) + " -- " + tank.getID()), false));
                }
            }
        }
        return InteractionResult.sidedSuccess(!level.isClientSide());
    }
}