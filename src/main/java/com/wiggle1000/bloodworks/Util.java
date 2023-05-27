package com.wiggle1000.bloodworks;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@SuppressWarnings("unused")
public class Util
{
    public static float Lerp(float a, float b, float interpolation)     { return a + interpolation * (b - a); }
    public static double Lerp(double a, double b, double interpolation)
    {
        return a + interpolation * (b - a);
    }

    public static Vec3 Lerp(Vec3 a, Vec3 b, float interpolation) { return new Vec3(Lerp(a.x, b.x, interpolation), Lerp(a.y, b.y, interpolation), Lerp(a.z, b.z, interpolation)); }


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

}
