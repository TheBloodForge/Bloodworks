package com.wiggle1000.bloodworks.Client.BlockRenderers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.wiggle1000.bloodworks.Globals;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderHelper
{

    public static void StartRenderingTris(BufferBuilder buffer, ResourceLocation texture)
    {
        buffer.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_TEX);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, new ResourceLocation(Globals.MODID, "textures/blocks/block_intestine.png"));
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
    }

    public static void FinishRendering(BufferBuilder buffer)
    {
        BufferUploader.drawWithShader(buffer.end());
        VertexBuffer.unbind();
    }
}
