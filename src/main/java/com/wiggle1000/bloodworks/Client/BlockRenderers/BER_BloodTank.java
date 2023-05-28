package com.wiggle1000.bloodworks.Client.BlockRenderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector4f;
import com.wiggle1000.bloodworks.Blocks.BlockEntities.BE_BloodTank;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
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
    private static final float SIDE_MARGIN = 0.01f, MIN_Y = 1 / 16f, MAX_Y = 1 - MIN_Y;
    private static final int NUM_FLUID_FRAMES = 32;
    private static final float WAVE_SIZE = 0.02f;

    private static int cFluidFrame = 0;
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

        //TODO: this is currently set by every renderer, which isn't really ideal though it should be _fine_ and the fix would be annoying
        cFluidFrame = (int)Math.floor(Minecraft.getInstance().level.getGameTime()/3.0) % NUM_FLUID_FRAMES; //don't use individual iterators so animations stay in sync
        float fillPercentage = Math.min(1, (float) fluidStack.getAmount() / tileEntity.getTankCapacity(0));
        if (fluidStack.getFluid().getFluidType().isLighterThanAir())
            renderFluid(matrixStack, renderTypeBuffer, fluidStack, fillPercentage, 1, combinedLight, tileEntity.getBlockPos());
        else
            renderFluid(matrixStack, renderTypeBuffer, fluidStack, 1, fillPercentage, combinedLight, tileEntity.getBlockPos());
    }

    private static void renderFluid(PoseStack matrixStack, MultiBufferSource renderTypeBuffer, FluidStack fluidStack, float alpha, float heightPercentage, int combinedLight, BlockPos blockPos)
    {
        VertexConsumer vertexBuilder = renderTypeBuffer.getBuffer(RenderType.translucent());
        ResourceLocation fluidResource = RenderHelper.getResourceForFluid(fluidStack);
        int color = RenderHelper.getColorFromFluid(fluidStack);
        alpha *= (color >> 24 & 255) / 255f;
        float red = (color >> 16 & 255) / 255f;
        float green = (color >> 8 & 255) / 255f;
        float blue = (color & 255) / 255f;

        renderQuads(matrixStack.last().pose(), renderTypeBuffer.getBuffer(RenderType.entityTranslucent(fluidResource)), red, green, blue, alpha, heightPercentage, combinedLight, blockPos);
    }

    private static void renderQuads(Matrix4f matrix, VertexConsumer vertexBuilder, float r, float g, float b, float alpha, float heightPercentage, int light, BlockPos blockPos)
    {
        float height = MIN_Y + (MAX_Y - MIN_Y) * heightPercentage;
        float minU = SIDE_MARGIN, maxU = (1 - SIDE_MARGIN);
        float frameVOff = cFluidFrame/(float)NUM_FLUID_FRAMES;
        float minV = MIN_Y + frameVOff;
        float maxV = minV + (((height)/NUM_FLUID_FRAMES));
        float maxVFlat = (1f/NUM_FLUID_FRAMES) + frameVOff;
//        Globals.LogInfo(heightPercentage + "");
        Vec3 BL = new Vec3(SIDE_MARGIN, MIN_Y, SIDE_MARGIN);
        Vec3 BR = new Vec3(1-SIDE_MARGIN, MIN_Y, SIDE_MARGIN);
        Vec3 FL = new Vec3(SIDE_MARGIN, MIN_Y, 1-SIDE_MARGIN);
        Vec3 FR = new Vec3(1-SIDE_MARGIN, MIN_Y, 1-SIDE_MARGIN);
        Vec3 BLT = new Vec3(SIDE_MARGIN, height - 0.001f, SIDE_MARGIN);
        Vec3 BRT = new Vec3(1-SIDE_MARGIN, height - 0.001f, SIDE_MARGIN);
        Vec3 FLT = new Vec3(SIDE_MARGIN, height - 0.001f, 1-SIDE_MARGIN);
        Vec3 FRT = new Vec3(1-SIDE_MARGIN, height - 0.001f, 1-SIDE_MARGIN);
        Vec2 UVNN = new Vec2(minU, minV);
        Vec2 UVPN = new Vec2(maxU, minV);
        Vec2 UVNP = new Vec2(minU, maxV);
        Vec2 UVPP = new Vec2(maxU, maxV);
        Vec2 UVNPFlat = new Vec2(minU, maxVFlat);
        Vec2 UVPPFlat = new Vec2(maxU, maxVFlat);


        double waveTime = Minecraft.getInstance().level.getGameTime()/10.0;
        BLT = BLT.add(new Vec3(0, Math.sin((blockPos.getX() + blockPos.getZ() + BLT.x + BLT.z) + (waveTime)), 0).scale(WAVE_SIZE));
        BRT = BRT.add(new Vec3(0, Math.sin((blockPos.getX() + blockPos.getZ() + BRT.x + BRT.z) + (waveTime)), 0).scale(WAVE_SIZE));
        FLT = FLT.add(new Vec3(0, Math.sin((blockPos.getX() + blockPos.getZ() + FLT.x + FLT.z) + (waveTime)), 0).scale(WAVE_SIZE));
        FRT = FRT.add(new Vec3(0, Math.sin((blockPos.getX() + blockPos.getZ() + FRT.x + FRT.z) + (waveTime)), 0).scale(WAVE_SIZE));
        Vector4f color = new Vector4f(r, g, b, alpha);
        // min z
        RenderHelper.DoQuadWithColor(vertexBuilder, matrix, BR, BRT, BLT, BL, UVPN, UVPP, UVNP, UVNN, light, color);
        // max z
        RenderHelper.DoQuadWithColor(vertexBuilder, matrix, FL, FLT, FRT, FR, UVPN, UVPP, UVNP, UVNN, light, color);
        // min x
        RenderHelper.DoQuadWithColor(vertexBuilder, matrix, BL, BLT, FLT, FL, UVPN, UVPP, UVNP, UVNN, light, color);
        // max x
        RenderHelper.DoQuadWithColor(vertexBuilder, matrix, FR, FRT, BRT, BR, UVPN, UVPP, UVNP, UVNN, light, color);
        // top
        if(heightPercentage < 1)
        {
            RenderHelper.DoQuadWithColor(vertexBuilder, matrix, BLT, FLT, FRT, BRT, UVPN, UVPPFlat, UVNPFlat, UVNN, light, color);
        }
        //bottom
        RenderHelper.DoQuadWithColor(vertexBuilder, matrix, BR, FR, FL, BL, UVPN, UVPPFlat, UVNPFlat, UVNN, light, color);
    }

    /*
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
    }*/
}