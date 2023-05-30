package com.bloodforge.bloodworks.Client.ItemRenderers;

import com.bloodforge.bloodworks.Blocks.BlockBloodTank;
import com.bloodforge.bloodworks.Blocks.BlockEntities.BE_Tank;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

public class TankRenderer extends BlockEntityWithoutLevelRenderer
{

    public TankRenderer(BlockEntityRenderDispatcher entityRenderer)
    {
        super(entityRenderer, new EntityModelSet());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType cameraTransforms, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        BakedModel model = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(stack);
        renderDefaultItem(stack, matrixStack, cameraTransforms, buffer, combinedLight, combinedOverlay, model);

        if (!stack.hasTag() || !stack.getTag().contains("tileData"))
            return;

        BE_Tank tile = (BE_Tank) ((BlockBloodTank) ((BlockItem) stack.getItem()).getBlock()).newBlockEntity(new BlockPos(0, 0, 0), ((BlockItem) stack.getItem()).getBlock().defaultBlockState());
        tile.readDataForItemRenderer(stack.getTag().getCompound("tileData"));

        Minecraft.getInstance().getBlockEntityRenderDispatcher().renderItem(tile, matrixStack, buffer, combinedLight, combinedOverlay);
    }

    private static void renderDefaultItem(ItemStack itemStack, PoseStack matrixStack, ItemTransforms.TransformType cameraTransforms, MultiBufferSource renderTypeBuffer, int combinedLight, int combinedOverlay, BakedModel model)
    {
        for (BakedModel passModel : model.getRenderPasses(itemStack, true))
        {
            for (RenderType renderType : passModel.getRenderTypes(itemStack, true))
            {
                VertexConsumer vertexConsumer = ItemRenderer.getFoilBufferDirect(renderTypeBuffer, renderType, true, itemStack.hasFoil());
                Minecraft.getInstance().getItemRenderer().renderModelLists(passModel, itemStack, combinedLight, combinedOverlay, matrixStack, vertexConsumer);
            }
        }
    }
}