package com.wiggle1000.bloodworks.Client.BlockRenderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.wiggle1000.bloodworks.Blocks.BlockEntities.BE_BloodTank;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

public class BER_BloodTank implements BlockEntityRenderer<BE_BloodTank>
{

    private final BlockEntityRendererProvider.Context context;
    public BER_BloodTank(BlockEntityRendererProvider.Context context)
    {
        super();
        this.context = context;
    }

    public static final float TANK_THICKNESS = 0.3f;
    public static final float TANK_HEIGHT = 0.2f;
    public static final float TANK_BOTTOM = 0.0f;
    private static final float SIDE_MARGIN = 0.99f, MIN_Y = 1 / 16f, MAX_Y = 1 - MIN_Y;
//    private static float milliseconds = -1;

    @Override
    public void render(BE_BloodTank tileEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int combinedLight, int combinedOverlay)
    {
        FluidStack fluidStack = tileEntity.getFluidInTank(0);
        /*milliseconds += partialTicks * 50;
        if (milliseconds == -1 || milliseconds >= 750f) {
            milliseconds = 0;
            PacketManager.sendToServer(new FluidSyncRequestC2SPacket(tileEntity.getBlockPos()));
        }*/
        if (fluidStack.isEmpty())
            return;

        float fillPercentage = Math.min(1, (float) fluidStack.getAmount() / tileEntity.getTankCapacity(0));
        if (fluidStack.getFluid().getFluidType().isLighterThanAir())
            renderFluid(matrixStack, renderTypeBuffer, fluidStack, fillPercentage, 1, combinedLight, tileEntity.getBlockPos());
        else
            renderFluid(matrixStack, renderTypeBuffer, fluidStack, 1, fillPercentage, combinedLight, tileEntity.getBlockPos());
    }

    private static void renderFluid(PoseStack matrixStack, MultiBufferSource renderTypeBuffer, FluidStack fluidStack, float alpha, float heightPercentage, int combinedLight, BlockPos blockPos)
    {
        VertexConsumer vertexBuilder = renderTypeBuffer.getBuffer(RenderType.translucent());
        IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        /* @wiggle fucking finally got it, took a whole 18 iterations
        *   However it still has a bit to go, it's apparent it's rendering too much*/
        //ForgeRegistries.FLUIDS.getDelegate(fluidStack.getRawFluid()).get().key().registry()
//        ResourceLocation TEXTURE = ForgeRegistries.FLUIDS.getDelegate(fluidStack.getRawFluid()).get().key().location();
//        String[] starr = ForgeRegistries.FLUIDS.getKey(fluidStack.getFluid()).toString().split(":");
//        ResourceLocation TEXTURE2 = Minecraft.getInstance().getBlockRenderer().getBlockModel(fluidStack.getFluid().defaultFluidState().createLegacyBlock()).getQuads().get(0).getSprite().getName();
//        System.out.println(fluidStack.getFluid().defaultFluidState().createLegacyBlock().getBlock().builtInRegistryHolder().key().location().getPath());
//        ResourceLocation TEXTURE = new ResourceLocation(starr[0], "textures/block" + (starr[0].equals("minecraft") ? "" : "s") + "/" + starr[1].replace("_source", "") + "_still.png");
//        TextureAtlasSprite sprite = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getTexture(fluidStack.getFluid().defaultFluidState().createLegacyBlock(), Minecraft.getInstance().level, blockPos);
//        ResourceLocation TEXTURE2 = sprite.getName();
//        ResourceLocation TEXTURE3 = new ResourceLocation("bloodworks:textures/blocks/fluid_still.png");
        String[] strArr = fluidTypeExtensions.getTextures().findFirst().get().toString().split(":");
        ResourceLocation TEXTURE4 = new ResourceLocation(strArr[0], "textures/" + strArr[1] + ".png");
        int color = fluidTypeExtensions.getTintColor();
        alpha *= (color >> 24 & 255) / 255f;
        float red = (color >> 16 & 255) / 255f;
        float green = (color >> 8 & 255) / 255f;
        float blue = (color & 255) / 255f;

        renderQuads(matrixStack.last().pose(), renderTypeBuffer.getBuffer(RenderType.entityCutout(TEXTURE4)), red, green, blue, alpha, heightPercentage, combinedLight);
    }

    private static void renderQuads(Matrix4f matrix, VertexConsumer vertexBuilder, float r, float g, float b, float alpha, float heightPercentage, int light)
    {
        float height = MIN_Y + (MAX_Y - MIN_Y) * heightPercentage;
        float minU = SIDE_MARGIN, maxU = (1 - SIDE_MARGIN);
        float minV = MIN_Y, maxV = height;
        // min z
        vertexBuilder.vertex(matrix, SIDE_MARGIN, MIN_Y, SIDE_MARGIN).color(r, g, b, alpha).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0, 0, -1).endVertex();
        vertexBuilder.vertex(matrix, SIDE_MARGIN, height, SIDE_MARGIN).color(r, g, b, alpha).uv(minU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0, 0, -1).endVertex();
        vertexBuilder.vertex(matrix, 1 - SIDE_MARGIN, height, SIDE_MARGIN).color(r, g, b, alpha).uv(maxU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0, 0, -1).endVertex();
        vertexBuilder.vertex(matrix, 1 - SIDE_MARGIN, MIN_Y, SIDE_MARGIN).color(r, g, b, alpha).uv(maxU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0, 0, -1).endVertex();
        // max z
        vertexBuilder.vertex(matrix, SIDE_MARGIN, MIN_Y, 1 - SIDE_MARGIN).color(r, g, b, alpha).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0, 0, 1).endVertex();
        vertexBuilder.vertex(matrix, 1 - SIDE_MARGIN, MIN_Y, 1 - SIDE_MARGIN).color(r, g, b, alpha).uv(maxU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0, 0, 1).endVertex();
        vertexBuilder.vertex(matrix, 1 - SIDE_MARGIN, height, 1 - SIDE_MARGIN).color(r, g, b, alpha).uv(maxU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0, 0, 1).endVertex();
        vertexBuilder.vertex(matrix, SIDE_MARGIN, height, 1 - SIDE_MARGIN).color(r, g, b, alpha).uv(minU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0, 0, 1).endVertex();
        // min x
        vertexBuilder.vertex(matrix, SIDE_MARGIN, MIN_Y, SIDE_MARGIN).color(r, g, b, alpha).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(-1, 0, 0).endVertex();
        vertexBuilder.vertex(matrix, SIDE_MARGIN, MIN_Y, 1 - SIDE_MARGIN).color(r, g, b, alpha).uv(maxU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(-1, 0, 0).endVertex();
        vertexBuilder.vertex(matrix, SIDE_MARGIN, height, 1 - SIDE_MARGIN).color(r, g, b, alpha).uv(maxU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(-1, 0, 0).endVertex();
        vertexBuilder.vertex(matrix, SIDE_MARGIN, height, SIDE_MARGIN).color(r, g, b, alpha).uv(minU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(-1, 0, 0).endVertex();
        // max x
        vertexBuilder.vertex(matrix, 1 - SIDE_MARGIN, MIN_Y, SIDE_MARGIN).color(r, g, b, alpha).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1, 0, 0).endVertex();
        vertexBuilder.vertex(matrix, 1 - SIDE_MARGIN, height, SIDE_MARGIN).color(r, g, b, alpha).uv(minU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1, 0, 0).endVertex();
        vertexBuilder.vertex(matrix, 1 - SIDE_MARGIN, height, 1 - SIDE_MARGIN).color(r, g, b, alpha).uv(maxU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1, 0, 0).endVertex();
        vertexBuilder.vertex(matrix, 1 - SIDE_MARGIN, MIN_Y, 1 - SIDE_MARGIN).color(r, g, b, alpha).uv(maxU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1, 0, 0).endVertex();
        // top
        if (heightPercentage < 1)
        {
            minV = SIDE_MARGIN * 16;
            maxV = (1 - SIDE_MARGIN) * 16;
            vertexBuilder.vertex(matrix, SIDE_MARGIN, height, SIDE_MARGIN).color(r, g, b, alpha).uv(minU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0, 1, 0).endVertex();
            vertexBuilder.vertex(matrix, SIDE_MARGIN, height, 1 - SIDE_MARGIN).color(r, g, b, alpha).uv(minU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0, 1, 0).endVertex();
            vertexBuilder.vertex(matrix, 1 - SIDE_MARGIN, height, 1 - SIDE_MARGIN).color(r, g, b, alpha).uv(maxU, maxV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0, 1, 0).endVertex();
            vertexBuilder.vertex(matrix, 1 - SIDE_MARGIN, height, SIDE_MARGIN).color(r, g, b, alpha).uv(maxU, minV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0, 1, 0).endVertex();
        }
    }
}