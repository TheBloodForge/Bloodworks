package com.wiggle1000.bloodworks.Blocks;

import com.wiggle1000.bloodworks.Blocks.BlockEntities.BE_BloodTank;
import com.wiggle1000.bloodworks.Networking.MessageS2CPacket;
import com.wiggle1000.bloodworks.Networking.PacketManager;
import com.wiggle1000.bloodworks.Registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
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

@SuppressWarnings("unchecked")
public class BlockBloodTank extends BlockMachineBase
{

//    public static final BlockShape SHAPE = BlockShape.createBlockShape(2.5, 0, 2.5, 13.5, 16, 13.5);
    public static final BooleanProperty OUTPUT = BooleanProperty.create("output");
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

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        return createTickerHelper(type, (BlockEntityType<BE_BloodTank>) BlockRegistry.BLOCK_BLOOD_TANK.blockEntity().get(), BE_BloodTank::tick);
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState)
    {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getVisualShape(BlockState p_60479_, BlockGetter p_60480_, BlockPos p_60481_, CollisionContext p_60482_)
    {
        return Shapes.box(0.01, 0.01, 0.01, 0.99, 0.99, 0.99);
    }
    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState cState, Level level, BlockPos blockPos, BlockState newState, boolean isMoving)
    {
        if (cState.getBlock() != newState.getBlock())
        {
            BlockEntity ent = level.getBlockEntity(blockPos);
            if (ent instanceof BE_BloodTank tank)
            {
//                ItemStack stack = new ItemStack(ItemRegistry.BLOCK_BLOOD_TANK.get());
//                if (stack.getItem() instanceof IFluidHandlerItem) {
//                    stack.addTagElement(tank.getTags());
//                }
//                ItemEntity entity = new ItemEntity(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), stack);
//                level.addFreshEntity(entity);
                tank.breakTank(blockPos, level);
            }
        }
        super.onRemove(cState, level, blockPos, newState, isMoving);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState cState, Level level, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult)
    {
        if(!level.isClientSide()) {
            if(level.getBlockEntity(pos) instanceof BE_BloodTank machine) {
                FluidStack stack = FluidStack.EMPTY;
                if (player.getItemInHand(interactionHand).getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent()) {
                    if (FluidUtil.getFluidContained(player.getItemInHand(interactionHand)).isPresent())
                        stack = FluidUtil.getFluidContained(player.getItemInHand(interactionHand)).get();
                    if (machine.isFluidValid(0, stack))
                    {
                        machine.fill(stack, IFluidHandler.FluidAction.EXECUTE);
                        return InteractionResult.sidedSuccess(!level.isClientSide());
                    }
                } else {
                    PacketManager.sendToClients(new MessageS2CPacket(Component.literal(Component.translatable(machine.getFluidInTank(0).getTranslationKey()).getString() + " : " + machine.getFluidInTank(0).getAmount() + " | " + machine.getTankCapacity(0)), false));
                }
            }
        }

        return InteractionResult.sidedSuccess(!level.isClientSide());
    }
}