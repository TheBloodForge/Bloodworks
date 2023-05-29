package com.bloodforge.bloodworks.Client.BlockRenderers;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

public class RenderHelper
{

    public static VertexConsumer StartRenderingCutout(MultiBufferSource bufferSource, ResourceLocation texture)
    {
        return bufferSource.getBuffer(RenderType.entityCutout(texture));
    }
    public static VertexConsumer StartRenderingTranslucent(MultiBufferSource bufferSource, ResourceLocation texture)
    {
        return bufferSource.getBuffer(RenderType.entityTranslucent(texture));
    }
    public static VertexConsumer StartRenderingTranslucentEmissive(MultiBufferSource bufferSource, ResourceLocation texture)
    {
        return bufferSource.getBuffer(RenderType.entityTranslucentEmissive(texture));
    }

    public static void FinishRendering(VertexConsumer buffer)
    {
    }

    public static ResourceLocation getResourceForFluid(FluidStack fluidStack)
    {
        IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        String[] strArr = fluidTypeExtensions.getTextures().findFirst().get().toString().split(":");
        return new ResourceLocation(strArr[0], "textures/" + strArr[1] + ".png");
    }

    public static int getColorFromFluid(FluidStack fluidStack)
    {
        IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        return fluidTypeExtensions.getTintColor();
    }

    public static void DoTriangle(VertexConsumer builder, Matrix4f matrix, Vec3 a, Vec3 b, Vec3 c, Vec2 uvA, Vec2 uvB, Vec2 uvC, int lightLevel)
    {
        Vector3f normal = calculateNormal((float)a.x, (float)a.y, (float)a.z, (float)b.x, (float)b.y, (float)b.z, (float)c.x, (float)c.y, (float)c.z);
        builder.vertex(matrix, (float)c.x, (float)c.y, (float)c.z).color(255, 255, 255, 255).uv(uvC.x, uvC.y).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightLevel).normal(normal.x(), normal.y(), normal.z()).endVertex();
        builder.vertex(matrix, (float)b.x, (float)b.y, (float)b.z).color(255, 255, 255, 255).uv(uvB.x, uvB.y).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightLevel).normal(normal.x(), normal.y(), normal.z()).endVertex();
        builder.vertex(matrix, (float)a.x, (float)a.y, (float)a.z).color(255, 255, 255, 255).uv(uvA.x, uvA.y).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightLevel).normal(normal.x(), normal.y(), normal.z()).endVertex();
        builder.vertex(matrix, (float)a.x, (float)a.y, (float)a.z).color(255, 255, 255, 255).uv(uvA.x, uvA.y).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightLevel).normal(normal.x(), normal.y(), normal.z()).endVertex();
    }

    public static void DoQuad(VertexConsumer builder, Matrix4f matrix, Vec3 a, Vec3 b, Vec3 c, Vec3 d, Vec2 uvA, Vec2 uvB, Vec2 uvC, Vec2 uvD, int lightLevel)
    {
        Vector3f normal = calculateNormal((float)a.x, (float)a.y, (float)a.z, (float)b.x, (float)b.y, (float)b.z, (float)c.x, (float)c.y, (float)c.z);
        builder.vertex(matrix, (float)d.x, (float)d.y, (float)d.z).color(255, 255, 255, 255).uv(uvD.x, uvD.y).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightLevel).normal(normal.x(), normal.y(), normal.z()).endVertex();
        builder.vertex(matrix, (float)c.x, (float)c.y, (float)c.z).color(255, 255, 255, 255).uv(uvC.x, uvC.y).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightLevel).normal(normal.x(), normal.y(), normal.z()).endVertex();
        builder.vertex(matrix, (float)b.x, (float)b.y, (float)b.z).color(255, 255, 255, 255).uv(uvB.x, uvB.y).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightLevel).normal(normal.x(), normal.y(), normal.z()).endVertex();
        builder.vertex(matrix, (float)a.x, (float)a.y, (float)a.z).color(255, 255, 255, 255).uv(uvA.x, uvA.y).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightLevel).normal(normal.x(), normal.y(), normal.z()).endVertex();
        //DoTriangle(builder, matrix, d, b, a, uvD, uvB, uvA, cLight);
        //DoTriangle(builder, matrix, d, c, b, uvD, uvC, uvB, cLight);
    }
    public static void DoQuadWithColor(VertexConsumer builder, Matrix4f matrix, Vec3 a, Vec3 b, Vec3 c, Vec3 d, Vec2 uvA, Vec2 uvB, Vec2 uvC, Vec2 uvD, int lightLevel, Vector4f color)
    {
        Vector3f normal = calculateNormal((float)a.x, (float)a.y, (float)a.z, (float)b.x, (float)b.y, (float)b.z, (float)c.x, (float)c.y, (float)c.z);
        builder.vertex(matrix, (float)d.x, (float)d.y, (float)d.z).color(color.x(), color.y(), color.z(), color.w()).uv(uvD.x, uvD.y).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightLevel).normal(normal.x(), normal.y(), normal.z()).endVertex();
        builder.vertex(matrix, (float)c.x, (float)c.y, (float)c.z).color(color.x(), color.y(), color.z(), color.w()).uv(uvC.x, uvC.y).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightLevel).normal(normal.x(), normal.y(), normal.z()).endVertex();
        builder.vertex(matrix, (float)b.x, (float)b.y, (float)b.z).color(color.x(), color.y(), color.z(), color.w()).uv(uvB.x, uvB.y).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightLevel).normal(normal.x(), normal.y(), normal.z()).endVertex();
        builder.vertex(matrix, (float)a.x, (float)a.y, (float)a.z).color(color.x(), color.y(), color.z(), color.w()).uv(uvA.x, uvA.y).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightLevel).normal(normal.x(), normal.y(), normal.z()).endVertex();
        //DoTriangle(builder, matrix, d, b, a, uvD, uvB, uvA, cLight);
        //DoTriangle(builder, matrix, d, c, b, uvD, uvC, uvB, cLight);
    }

    public static Vector3f GetVertexWorldLocation(Matrix4f matrix, Vector3f a)
    {
        Vector4f f = new Vector4f(a.x(), a.y(), a.z(), 1.0F);
        Matrix4f t = matrix.copy();
        t.invert();
        f.transform(t);
        return new Vector3f(f.x(), f.y(), f.z());
    }

    public static void DoQuadAcrossMatricesAD(VertexConsumer builder, Matrix4f matrixBC, Matrix4f matrixAD, Vec3 a, Vec3 b, Vec3 c, Vec3 d, Vec2 uvA, Vec2 uvB, Vec2 uvC, Vec2 uvD, int lightLevel)
    {
        Vector3f normal = calculateNormal((float)a.x, (float)a.y, (float)a.z, (float)b.x, (float)b.y, (float)b.z, (float)c.x, (float)c.y, (float)c.z);
        builder.vertex(matrixAD, (float)d.x, (float)d.y, (float)d.z).color(255, 255, 255, 255).uv(uvD.x, uvD.y).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightLevel).normal(normal.x(), normal.y(), normal.z()).endVertex();
        builder.vertex(matrixBC, (float)c.x, (float)c.y, (float)c.z).color(255, 255, 255, 255).uv(uvC.x, uvC.y).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightLevel).normal(normal.x(), normal.y(), normal.z()).endVertex();
        builder.vertex(matrixBC, (float)b.x, (float)b.y, (float)b.z).color(255, 255, 255, 255).uv(uvB.x, uvB.y).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightLevel).normal(normal.x(), normal.y(), normal.z()).endVertex();
        builder.vertex(matrixAD, (float)a.x, (float)a.y, (float)a.z).color(255, 255, 255, 255).uv(uvA.x, uvA.y).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(lightLevel).normal(normal.x(), normal.y(), normal.z()).endVertex();
        //DoTriangle(builder, matrix, d, b, a, uvD, uvB, uvA, cLight);
        //DoTriangle(builder, matrix, d, c, b, uvD, uvC, uvB, cLight);
    }

    public static Vector3f calculateNormal(Matrix4f matrix, float vX1, float vY1, float vZ1,
                                           float vX2, float vY2, float vZ2,
                                           float vX3, float vY3, float vZ3) {

        Vector4f edge1 = new Vector4f(vX1 - vX2, vY1 - vY2, vZ1 - vZ2, 1f);
        Vector4f edge2 = new Vector4f(vX2 - vX3, vY2 - vY3, vZ2 - vZ3, 1f);
        edge1.transform(matrix);
        edge2.transform(matrix);
        Vector3f e1v = new Vector3f(edge1.x(), edge1.y(), edge1.z());
        Vector3f e2v = new Vector3f(edge2.x(), edge2.y(), edge2.z());

        e1v.cross(e2v); // Cross product between edge1 and edge2
        edge1.normalize();

        return e1v;
    }
    public static Vector3f calculateNormal(float vX1, float vY1, float vZ1,
                                           float vX2, float vY2, float vZ2,
                                           float vX3, float vY3, float vZ3) {

        Vector3f edge1 = new Vector3f(vX1, vY1, vZ1);
        edge1.sub(new Vector3f(vX2, vY2, vZ2));
        Vector3f edge2 = new Vector3f(vX2, vY2, vZ2);
        edge2.sub(new Vector3f(vX3, vY3, vZ3));

        edge1.cross(edge2); // Cross product between edge1 and edge2
        edge1.normalize();

        return edge1;
    }

}