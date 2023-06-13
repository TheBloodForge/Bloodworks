package com.bloodforge.bloodworks.Neurons.NodeTypes;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

public class FloatNode extends NeuronIONode
{
    public FloatNode()
    {
        data = 0.0f;
    }

    @Override
    public void drawContentsAt(PoseStack poseStack, MultiBufferSource bufferSource, Vec3 pos)
    {
        if(data instanceof Float data)
        {
            poseStack.pushPose();
            poseStack.setIdentity();
            poseStack.translate(pos.x, pos.y, pos.z);
            poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
            Component dataComponent = Component.literal(data.toString());
            Font font = Minecraft.getInstance().font;
            float f2 = (float)(-font.width(dataComponent) / 2);
            float f1 = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
            int j = (int)(f1 * 255.0F) << 24;
            //TODO: what is this last param???
            font.drawInBatch(dataComponent, f2, 0, 553648127, false, poseStack.last().pose(), bufferSource, true, j, 0);
            poseStack.popPose();
            return;
        }
        super.drawContentsAt(poseStack, bufferSource, pos);
    }
}
