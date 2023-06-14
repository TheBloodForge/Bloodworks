package com.bloodforge.bloodworks.Blocks;

import com.bloodforge.bloodworks.Networking.PacketManager;
import com.bloodforge.bloodworks.Networking.SoundS2CPacket;
import com.bloodforge.bloodworks.Registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@SuppressWarnings({"NullableProblems", "Unused", "unused"})
public class BlockFlesh extends Block
{
    //CriticalDamage, Raw, SkinHealing, Skin
    public static final IntegerProperty SKIN_GROWTH_LEVEL = IntegerProperty.create("growth_level", 0, 3);
    //uncolored, white, orange, magenta, lightblue, yellow, lime, pink, gray, lightgray, cyan, purple, blue, brown, green, red, black
    public static final IntegerProperty SKIN_COLOR = IntegerProperty.create("skin_color", 0, 16);

    public BlockFlesh()
    {
        super(
                Properties
                        .of(Material.STONE)
                        .strength(3f, 5f)
                        .sound(SoundType.SLIME_BLOCK)
        );
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState blockState, Entity stepperOnner)
    {
        super.stepOn(level, pos, blockState, stepperOnner);
        //ParticleHelper.DoStepParticle(ParticleRegistry.PARTICLE_FLESH_STEP.get(), level, pos, blockState, stepperOnner);
    }

    @Override
    public void randomTick(BlockState thisBlockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource)
    {
        super.randomTick(thisBlockState, serverLevel, blockPos, randomSource);
        int highestLevelEncountered = -1;
        for(int x = -1; x <= 1; x++)
        {
            for(int z = -1; z <= 1; z++)
            {
                for(int y = -1; y <= 1; y++)
                {
                    if(x == 0 && y == 0 && z == 0) continue;
                    BlockState blockState = serverLevel.getBlockState(blockPos.offset(new Vec3i(x, y, z)));
                    if(blockState.getBlock().equals(BlockRegistry.BLOCK_FLESH.blockBase().block().get()))
                    {
                        if(blockState.getValue(SKIN_GROWTH_LEVEL) > highestLevelEncountered)
                        {
                            highestLevelEncountered = blockState.getValue(SKIN_GROWTH_LEVEL);
                        }
                    }
                }
            }
        }
        if(highestLevelEncountered == -1)
        {
            if(thisBlockState.getValue(SKIN_GROWTH_LEVEL) > 1)
            {
                BlockState newBlockState = thisBlockState.getBlock().defaultBlockState().setValue(SKIN_GROWTH_LEVEL, Math.max(thisBlockState.getValue(SKIN_GROWTH_LEVEL) - 1, 1)).setValue(SKIN_COLOR, thisBlockState.getValue(SKIN_COLOR));
                serverLevel.setBlockAndUpdate(blockPos, newBlockState);
            }
        }
        else if(highestLevelEncountered > thisBlockState.getValue(SKIN_GROWTH_LEVEL))
        {
            BlockState newBlockState = thisBlockState.getBlock().defaultBlockState().setValue(SKIN_GROWTH_LEVEL, Math.min(thisBlockState.getValue(SKIN_GROWTH_LEVEL) + 1, 3)).setValue(SKIN_COLOR, thisBlockState.getValue(SKIN_COLOR));
            serverLevel.setBlockAndUpdate(blockPos, newBlockState);
            //This is so annoying I'm disabling it. Do it again better, self. -@wiggle1000
            //PacketManager.sendToClients(new SoundS2CPacket(SoundRegistry.FLESH_GROW.get().getLocation(), SoundSource.BLOCKS, new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()), 1.0F, 1.0F));
        }
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState blockState, BlockGetter p_60579_, BlockPos p_60580_)
    {
        if(blockState.getValue(SKIN_GROWTH_LEVEL) == 0)
        {
            return Shapes.empty();
        }
        return super.getOcclusionShape(blockState, p_60579_, p_60580_);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState blockState, BlockGetter p_60573_, BlockPos p_60574_, CollisionContext p_60575_)
    {
        if(blockState.getValue(SKIN_GROWTH_LEVEL) == 0)
        {
            return Shapes.empty();
        }
        return super.getCollisionShape(blockState, p_60573_, p_60574_, p_60575_);
    }

    @Override
    public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion)
    {
        if(level.isClientSide()) return;
        level.setBlockAndUpdate(pos, state.setValue(SKIN_GROWTH_LEVEL, 0));
    }


    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid)
    {
        if(player.getItemInHand(player.getUsedItemHand()).getItem() instanceof ShearsItem)
        {
            return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
        }
        if(state.getValue(SKIN_GROWTH_LEVEL) == 0)
        {
            return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
        }
        level.setBlockAndUpdate(pos, state.setValue(SKIN_GROWTH_LEVEL, 0));
        return false;
    }

    @Override
    public boolean isRandomlyTicking(BlockState p_49921_)
    {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_55933_) {
        p_55933_.add(SKIN_GROWTH_LEVEL).add(SKIN_COLOR);
    }

    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        BlockState placedFromState = placeContext.getLevel().getBlockState(placeContext.getClickedPos());
        //TODO: make thing good
        /*if(placedFromState != null && placedFromState.getBlock() == BlockRegistry.BLOCK_FLESH.blockBase())
        {
            return this.defaultBlockState().setValue(SKIN_GROWTH_LEVEL, 0).setValue(SKIN_COLOR, placedFromState.getValue(SKIN_COLOR));
        }*/
        return this.defaultBlockState().setValue(SKIN_GROWTH_LEVEL, 1).setValue(SKIN_COLOR, 0);
    }

    public static int getColorIndexByDye(DyeColor color)
    {
        return color.getId() + 1; //offset to leave 0 as "colorless"
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult)
    {
        ItemStack used = player.getItemInHand(interactionHand);
        if(used.getItem() instanceof DyeItem dyeItem)
        {
            if(level.isClientSide()) return InteractionResult.sidedSuccess(level.isClientSide);
            int dyeColor = getColorIndexByDye(dyeItem.getDyeColor());
            if (dyeColor != state.getValue(SKIN_COLOR))
            {
                BlockState newBlockState = state.getBlock().defaultBlockState().setValue(SKIN_GROWTH_LEVEL, state.getValue(SKIN_GROWTH_LEVEL)).setValue(SKIN_COLOR, dyeColor);
                level.setBlockAndUpdate(blockPos, newBlockState);
                used.shrink(1);
                PacketManager.sendToClients(new SoundS2CPacket(SoundEvents.DYE_USE.getLocation(), SoundSource.PLAYERS, new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()), 1.0F, 1.0F));
                return InteractionResult.CONSUME;
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if(used.getItem() == Items.POTION && used.hasTag() && used.getTag().getString("Potion").equals("minecraft:water"))
        {
            if (0 != state.getValue(SKIN_COLOR))
            {
                BlockState newBlockState = state.getBlock().defaultBlockState().setValue(SKIN_GROWTH_LEVEL, state.getValue(SKIN_GROWTH_LEVEL)).setValue(SKIN_COLOR, 0);
                level.setBlockAndUpdate(blockPos, newBlockState);
                PacketManager.sendToClients(new SoundS2CPacket(SoundEvents.DYE_USE.getLocation(), SoundSource.PLAYERS, new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()), 1.0F, 1.0F));
                used.shrink(1);
                player.addItem(new ItemStack(Items.GLASS_BOTTLE, 1));
                return InteractionResult.CONSUME;
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if(used.getItem() == Items.WET_SPONGE)
        {
            if (0 != state.getValue(SKIN_COLOR))
            {
                BlockState newBlockState = state.getBlock().defaultBlockState().setValue(SKIN_GROWTH_LEVEL, state.getValue(SKIN_GROWTH_LEVEL)).setValue(SKIN_COLOR, 0);
                level.setBlockAndUpdate(blockPos, newBlockState);
                level.playSound(player, blockPos, SoundEvents.DYE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
                return InteractionResult.CONSUME;
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.use(state, level, blockPos, player, interactionHand, blockHitResult);
    }
}