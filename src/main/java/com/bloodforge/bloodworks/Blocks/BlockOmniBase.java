package com.bloodforge.bloodworks.Blocks;

import com.bloodforge.bloodworks.Registry.ParticleRegistry;
import com.bloodforge.bloodworks.Util;
import com.bloodforge.bloodworks.Particles.ParticleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings({"NullableProblems", "Unused", "deprecation"})
public class BlockOmniBase extends FaceAttachedHorizontalDirectionalBlock
{
    protected VoxelShape CEILING_AABB_X = Block.box(6.0D, 14.0D, 5.0D, 10.0D, 16.0D, 11.0D);
    protected VoxelShape CEILING_AABB_Z = Block.box(5.0D, 14.0D, 6.0D, 11.0D, 16.0D, 10.0D);
    protected VoxelShape FLOOR_AABB_X = Block.box(6.0D, 0.0D, 5.0D, 10.0D, 2.0D, 11.0D);
    protected VoxelShape FLOOR_AABB_Z = Block.box(5.0D, 0.0D, 6.0D, 11.0D, 2.0D, 10.0D);
    protected VoxelShape NORTH_AABB = Block.box(5.0D, 6.0D, 14.0D, 11.0D, 10.0D, 16.0D);
    protected VoxelShape SOUTH_AABB = Block.box(5.0D, 6.0D, 0.0D, 11.0D, 10.0D, 2.0D);
    protected VoxelShape WEST_AABB = Block.box(14.0D, 6.0D, 5.0D, 16.0D, 10.0D, 11.0D);
    protected VoxelShape EAST_AABB = Block.box(0.0D, 6.0D, 5.0D, 2.0D, 10.0D, 11.0D);
    
    public BlockOmniBase(Properties properties,  VoxelShape shapeOnFloor)
    {
        super(properties);
        FLOOR_AABB_X = Util.RotateVoxelShapeOnYAxis(1, shapeOnFloor);
        FLOOR_AABB_Z = Util.RotateVoxelShapeOnYAxis(1, FLOOR_AABB_X);
        NORTH_AABB  = Util.RotateVoxelShapeOnYAxis(3, Util.RotateVoxelShapeOnZAxis(1, FLOOR_AABB_Z));
        EAST_AABB   = Util.RotateVoxelShapeOnYAxis(1, NORTH_AABB);
        SOUTH_AABB  = Util.RotateVoxelShapeOnYAxis(1, EAST_AABB);
        WEST_AABB   = Util.RotateVoxelShapeOnYAxis(1, SOUTH_AABB);
        CEILING_AABB_X = Util.RotateVoxelShapeOnZAxis(2, FLOOR_AABB_X);
        CEILING_AABB_Z = Util.RotateVoxelShapeOnYAxis(1, CEILING_AABB_X);
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter blockGetter, List<Component> components, TooltipFlag tooltipFlag)
    {
        super.appendHoverText(stack, blockGetter, components, tooltipFlag);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState blockState, Entity stepperOnner)
    {
        super.stepOn(level, pos, blockState, stepperOnner);
        ParticleHelper.DoStepParticle(ParticleRegistry.PARTICLE_FLESH_STEP.get(), level, pos, blockState, stepperOnner);
    }

    @Override
    public VoxelShape getShape(BlockState p_51104_, BlockGetter p_51105_, BlockPos p_51106_, CollisionContext p_51107_) {
        Direction direction = p_51104_.getValue(FACING);
        switch ((AttachFace)p_51104_.getValue(FACE)) {
            case FLOOR:
                if (direction.getAxis() == Direction.Axis.X) {
                    return  FLOOR_AABB_X;
                }
                return  FLOOR_AABB_Z;
            case WALL:
                return switch (direction)
                        {
                            case EAST -> EAST_AABB;
                            case WEST -> WEST_AABB;
                            case SOUTH -> SOUTH_AABB;
                            case NORTH -> NORTH_AABB;
                            default -> NORTH_AABB;
                        };
            case CEILING:
            default:
                if (direction.getAxis() == Direction.Axis.X) {
                    return CEILING_AABB_X;
                } else {
                    return  CEILING_AABB_Z;
                }
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_51101_) {
        p_51101_.add(FACING, FACE);
    }
}