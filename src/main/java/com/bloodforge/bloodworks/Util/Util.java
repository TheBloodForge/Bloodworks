package com.bloodforge.bloodworks.Util;

import com.bloodforge.bloodworks.Globals;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@SuppressWarnings("unused")
public class Util
{
    public static float Lerp(float a, float b, float interpolation)
    {
        return a + interpolation * (b - a);
    }
    public static Pair<Float, Float> SpringInterpolate(float a, float b, float velocity, float bounce, float tension)
    {
        velocity = Lerp(velocity, (b - a) * tension, 1.0f / bounce);
        return Pair.of(a + velocity, velocity);
    }

    public static double Lerp(double a, double b, double interpolation)
    {
        return a + interpolation * (b - a);
    }

    public static Vec3 Lerp(Vec3 a, Vec3 b, float interpolation)
    {
        return new Vec3(Lerp(a.x, b.x, interpolation), Lerp(a.y, b.y, interpolation), Lerp(a.z, b.z, interpolation));
    }

    public static Vec2 Lerp(Vec2 a, Vec2 b, float interpolation)
    {
        return new Vec2(Lerp(a.x, b.x, interpolation), Lerp(a.y, b.y, interpolation));
    }
    public static Pair<Vec2, Vec2> SpringInterpolate(Vec2 a, Vec2 b, Vec2 velocity, float bounce, float tension)
    {
        velocity = Lerp(velocity, (b.add(a.scale(-1))).scale(tension), 1.0f / bounce);
        return Pair.of(a.add(velocity), velocity);
    }

    public static VoxelShape RotateVoxelShapeOnYAxis(Direction from, Direction to, VoxelShape shape)
    {
        int times = (to.ordinal() - from.get2DDataValue() + 4) % 4;
        return RotateVoxelShapeOnYAxis(times, shape);
    }

    public static VoxelShape RotateVoxelShapeOnYAxis(int times, VoxelShape shape)
    {
        VoxelShape[] buffer = new VoxelShape[]{shape, Shapes.empty()};
        for (int i = 0; i < times; i++)
        {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(buffer[1], Shapes.create(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }

        return buffer[0];
    }

    public static VoxelShape RotateVoxelShapeOnZAxis(int times, VoxelShape shape)
    {
        VoxelShape[] buffer = new VoxelShape[]{shape, Shapes.empty()};

        for (int i = 0; i < times; i++)
        {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(buffer[1],
                    Shapes.create(minY, 1 - maxX, minZ, maxY, 1 - minX, maxZ)));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }

        return buffer[0];
    }

    public static int[] getBlockPosAsIntArr(BlockPos pos)
    {
        return new int[]{pos.getX(), pos.getY(), pos.getZ()};
    }

    public static BlockPos getBlockPosFromIntArr(int[] minCorners)
    {
        if (minCorners.length != 3)
        {
            Globals.LogError("Is this what dying feels like?");
            return null;
        }
        return new BlockPos(minCorners[0], minCorners[1], minCorners[2]);
    }

    public static boolean isBlockPosSame(BlockPos p1, BlockPos p2)
    {
        return p1.toShortString().equalsIgnoreCase(p2.toShortString());
    }

    public static Direction getDirectionOf(BlockPos originPos, BlockPos neighborPos)
    {
        for (Direction dir : Direction.values())
            if (originPos.relative(dir).toShortString().equalsIgnoreCase(neighborPos.toShortString()))
                return dir;
        return Direction.UP;
    }

}