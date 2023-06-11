package com.bloodforge.bloodworks.Neurons.NodeTypes;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class ItemStackNode extends NeuronIONode
{
    public ItemStackNode()
    {

    }

    @Override
    public void drawContentsAt(PoseStack poseStack, MultiBufferSource bufferSource, Vec3 pos)
    {
        if(data instanceof ItemStack stack)
        {
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.GROUND, LevelRenderer.getLightColor(Minecraft.getInstance().level, new BlockPos(pos)), 0, poseStack, bufferSource, 0);
            return;
        }
        super.drawContentsAt(poseStack, bufferSource, pos);
    }
}
