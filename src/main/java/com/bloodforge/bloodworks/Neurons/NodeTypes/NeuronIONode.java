package com.bloodforge.bloodworks.Neurons.NodeTypes;

import com.bloodforge.bloodworks.Client.BlockRenderers.RenderHelper;
import com.bloodforge.bloodworks.Globals;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class NeuronIONode
{
    public static enum NEURON_IO_TYPES
    {
        UNKNOWN,
        FLOAT,
        ITEMSTACK
    }
    public Object data;
    NeuronIONode destination;

    public void drawContentsAt(PoseStack poseStack, MultiBufferSource bufferSource, Vec3 pos)
    {
        poseStack.pushPose();
        VertexConsumer consumer = RenderHelper.StartRenderingCutout(bufferSource, new ResourceLocation(Globals.MODID, "neurons/signal_unknown"));
        RenderHelper.DoQuadFacingCamera(consumer, poseStack.last().pose(), LevelRenderer.getLightColor(Minecraft.getInstance().level, new BlockPos(pos)), pos, -0.2f,1, 0, 0, 1, 1);
        poseStack.popPose();

    }
}
