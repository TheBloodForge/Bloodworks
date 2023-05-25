package com.wiggle1000.bloodworks.Client.BlockRenderers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class RenderHelper
{

    public static void StartRenderingTris(BufferBuilder buffer, ResourceLocation texture)
    {
        buffer.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_TEX_COLOR);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        //RenderSystem.disableCull();
    }

    public static void FinishRendering(BufferBuilder buffer)
    {
        BufferUploader.drawWithShader(buffer.end());
        VertexBuffer.unbind();
    }

    public static void DoTriangle(BufferBuilder builder, Matrix4f matrix, Vec3 a, Vec3 b, Vec3 c, Vec2 uvA, Vec2 uvB, Vec2 uvC, int lightLevel)
    {
        builder.vertex(matrix, (float)a.x, (float)a.y, (float)a.z).uv(uvA.x, uvA.y).color(lightLevel, lightLevel, lightLevel, 255).endVertex();
        builder.vertex(matrix, (float)b.x, (float)b.y, (float)b.z).uv(uvB.x, uvB.y).color(lightLevel, lightLevel, lightLevel, 255).endVertex();
        builder.vertex(matrix, (float)c.x, (float)c.y, (float)c.z).uv(uvC.x, uvC.y).color(lightLevel, lightLevel, lightLevel, 255).endVertex();
    }
    public static void DoQuad(BufferBuilder builder, Matrix4f matrix, Vec3 a, Vec3 b, Vec3 c, Vec3 d, Vec2 uvA, Vec2 uvB, Vec2 uvC, Vec2 uvD, int lightLevel)
    {
        DoTriangle(builder, matrix, d, b, a, uvD, uvB, uvA, lightLevel);
        DoTriangle(builder, matrix, d, c, b, uvD, uvC, uvB, lightLevel);
    }
}