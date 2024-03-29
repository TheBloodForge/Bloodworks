package com.bloodforge.bloodworks.Client.BlockRenderers;

import com.bloodforge.bloodworks.Blocks.BlockEntities.BE_Tank;
import com.bloodforge.bloodworks.Registry.BlockRegistry;
import com.bloodforge.bloodworks.Util.Util;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

public class BER_BloodTank implements BlockEntityRenderer<BE_Tank>
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
    private static final float FLUID_SIDE_MARGIN = 0.001f, MIN_Y = 0.0002f, MAX_Y = 1 - MIN_Y;
    private static final float TANKBLOCK_ALLSIDE_MARGIN = 0.0001f; //to prevent z-fighting
    private static final int NUM_FLUID_FRAMES = 32;
    private static final float WAVE_SIZE_MAX = 0.08f;
    private static final float WAVE_SIZE_MIN = 0.01f;

    private static int cFluidFrame = 0;

    @Override
    public void render(BE_Tank tank, float partialTicks, PoseStack poseStack, MultiBufferSource MBR, int cLight, int cOverlay)
    {
        Minecraft.getInstance().getProfiler().push("Bloodworks Tank Renderer");
        boolean connectU = shouldConnectTo(tank.getBlockPos().above());
        boolean connectD = shouldConnectTo(tank.getBlockPos().below());
        boolean connectN = shouldConnectTo(tank.getBlockPos().north());
        boolean connectE = shouldConnectTo(tank.getBlockPos().east());
        boolean connectS = shouldConnectTo(tank.getBlockPos().south());
        boolean connectW = shouldConnectTo(tank.getBlockPos().west());


        FluidStack fluidStack = tank.getFluidInTank(0);

        //renderTankOuter(matrixStack.last().pose(), renderTypeBuffer.getBuffer(RenderType.entityCutout(new ResourceLocation(Globals.MODID, "textures/blocks/block_blood_tank.png"))), 1f, 1f, 1f, 1f, combinedLight, tileEntity.getBlockPos(), connectU, connectD, connectN, connectE, connectS, connectW);

        if (!fluidStack.isEmpty())
        {
            float relativeFill = tank.getRelativeFill();
            //water viscosity = 1000
            //lava viscosity = 6000
            float flowAmt = Util.Lerp(WAVE_SIZE_MIN, WAVE_SIZE_MAX, 1-Mth.clamp((fluidStack.getFluid().getFluidType().getViscosity()/6000), 0, 1));
            if (relativeFill >= 0.0f)
            {
                //TODO: this is currently set by every renderer, which isn't really ideal though it should be _fine_ and the fix would be annoying
                cFluidFrame = (int) Math.floor(Minecraft.getInstance().level.getGameTime() / 3.0) % NUM_FLUID_FRAMES; //don't use individual iterators so animations stay in sync
                renderFluid(poseStack, MBR, fluidStack, 1, relativeFill, flowAmt, cLight, tank.getBlockPos(), connectU, connectD, connectN, connectE, connectS, connectW);
            }
        }
        Minecraft.getInstance().getProfiler().pop();
    }

    private static boolean shouldConnectTo(BlockPos target)
    {
        //TODO: check if these are part of the same tank
        return Minecraft.getInstance().level.getBlockState(target).is(BlockRegistry.BLOCK_BLOOD_TANK.block().get());

    }

    private static void setupLiquidRendering(FluidStack stack)
    {
        IClientFluidTypeExtensions.of(stack.getFluid()).getStillTexture(stack);
    }

    private static void renderFluid(PoseStack matrixStack, MultiBufferSource bufferSource, FluidStack fluidStack, float alpha, float heightPercentage, float flowAmt, int combinedLight, BlockPos blockPos,
                                    boolean connectU, boolean connectD, boolean connectN, boolean connectE, boolean connectS, boolean connectW)
    {
        VertexConsumer vertexBuilder = bufferSource.getBuffer(RenderType.translucent());
        ResourceLocation fluidResource = RenderHelper.getResourceForFluid(fluidStack);
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidResource);
        int color = RenderHelper.getColorFromFluid(fluidStack);
        alpha *= (color >> 24 & 255) / 255f;
        float red = (color >> 16 & 255) / 255f;
        float green = (color >> 8 & 255) / 255f;
        float blue = (color & 255) / 255f;
        int lighting = combinedLight;
        if(fluidStack.getFluid().getFluidType().getLightLevel() > 1)
        {
            lighting = 15728880;
        }
        VertexConsumer buf = bufferSource.getBuffer(ItemBlockRenderTypes.getRenderLayer(fluidStack.getFluid().defaultFluidState()));
        renderLiquidQuads(matrixStack.last().pose(), buf, sprite, red, green, blue, alpha, heightPercentage, flowAmt, lighting, blockPos, connectU, connectD, connectN, connectE, connectS, connectW);
    }

    private static Vec3 clampVec3Y(Vec3 in, float minY, float maxY)
    {
        return new Vec3(in.x, Mth.clamp(in.y, minY, maxY), in.z);
    }

    private static void renderLiquidQuads(Matrix4f matrix, VertexConsumer vertexBuilder, TextureAtlasSprite sprite, float r, float g, float b, float alpha, float heightPercentage, float flowAmt, int light, BlockPos blockPos,
                                          boolean connectU, boolean connectD, boolean connectN, boolean connectE, boolean connectS, boolean connectW)
    {

        float clampedHeightPercentage = Mth.clamp(heightPercentage, 0, 1);
        float height = MIN_Y + (MAX_Y - MIN_Y) * clampedHeightPercentage;
        float minU = sprite.getU0(), maxU = sprite.getU1();
        float frameVOff = cFluidFrame / (float) NUM_FLUID_FRAMES;
        float minV = sprite.getV0();
        float maxV = Util.Lerp(sprite.getV0(), sprite.getV1(), clampedHeightPercentage);
        float maxVFlat = sprite.getV1();
//      Globals.LogInfo(heightPercentage + "");
        Vec3 BL = new Vec3(connectW ? 0 : FLUID_SIDE_MARGIN, connectD ? 0 : MIN_Y, connectN ? 0 : FLUID_SIDE_MARGIN);
        Vec3 BR = new Vec3(connectE ? 1 : 1 - FLUID_SIDE_MARGIN, connectD ? 0 : MIN_Y, connectN ? 0 : FLUID_SIDE_MARGIN);
        Vec3 FL = new Vec3(connectW ? 0 : FLUID_SIDE_MARGIN, connectD ? 0 : MIN_Y, connectS ? 1 : 1 - FLUID_SIDE_MARGIN);
        Vec3 FR = new Vec3(connectE ? 1 : 1 - FLUID_SIDE_MARGIN, connectD ? 0 : MIN_Y, connectS ? 1 : 1 - FLUID_SIDE_MARGIN);
        Vec3 BLT = new Vec3(connectW ? 0 : FLUID_SIDE_MARGIN, (connectU && heightPercentage >= 1) ? 1 : height - 0.001f, connectN ? 0 : FLUID_SIDE_MARGIN);
        Vec3 BRT = new Vec3(connectE ? 1 : 1 - FLUID_SIDE_MARGIN, (connectU && heightPercentage >= 1) ? 1 : height - 0.001f, connectN ? 0 : FLUID_SIDE_MARGIN);
        Vec3 FLT = new Vec3(connectW ? 0 : FLUID_SIDE_MARGIN, (connectU && heightPercentage >= 1) ? 1 : height - 0.001f, connectS ? 1 : 1 - FLUID_SIDE_MARGIN);
        Vec3 FRT = new Vec3(connectE ? 1 : 1 - FLUID_SIDE_MARGIN, (connectU && heightPercentage >= 1) ? 1 : height - 0.001f, connectS ? 1 : 1 - FLUID_SIDE_MARGIN);
        Vec2 UVNN = new Vec2(minU, minV);
        Vec2 UVPN = new Vec2(maxU, minV);
        Vec2 UVNP = new Vec2(minU, maxV);
        Vec2 UVPP = new Vec2(maxU, maxV);
        Vec2 UVNPFlat = new Vec2(minU, maxVFlat);
        Vec2 UVPPFlat = new Vec2(maxU, maxVFlat);


        Vector4f color = new Vector4f(r, g, b, alpha);

        // top
        if (heightPercentage < 1 || !connectU) //should we render top at all?
        {
            double waveTime = Minecraft.getInstance().level.getGameTime() / 10.0;
            if(heightPercentage < 1 || connectU) { //should we do waves?
                float minY = connectD ? -1 : MIN_Y;
                float maxY = connectU ? 1 : MAX_Y;
                BLT = clampVec3Y(BLT.add(new Vec3(0, Math.sin((blockPos.getX() + blockPos.getZ() + BLT.x + BLT.z) + (waveTime)), 0).scale(flowAmt)), minY, maxY);
                BRT = clampVec3Y(BRT.add(new Vec3(0, Math.sin((blockPos.getX() + blockPos.getZ() + BRT.x + BRT.z) + (waveTime)), 0).scale(flowAmt)), minY, maxY);
                FLT = clampVec3Y(FLT.add(new Vec3(0, Math.sin((blockPos.getX() + blockPos.getZ() + FLT.x + FLT.z) + (waveTime)), 0).scale(flowAmt)), minY, maxY);
                FRT = clampVec3Y(FRT.add(new Vec3(0, Math.sin((blockPos.getX() + blockPos.getZ() + FRT.x + FRT.z) + (waveTime)), 0).scale(flowAmt)), minY, maxY);
            }
            RenderHelper.DoQuadWithColor(vertexBuilder, matrix, BRT, FRT, FLT, BLT, UVPN, UVPPFlat, UVNPFlat, UVNN, light, color);
        }
        if(clampedHeightPercentage == 0) //render special sides with fixed UVs
        {
            float sMinV = sprite.getV0();
            float sMaxV = Util.Lerp(sprite.getV0(), sprite.getV1(), 0.05f);
            Vec2 sUVNN = new Vec2(minU, sMinV);
            Vec2 sUVPN = new Vec2(maxU, sMinV);
            Vec2 sUVNP = new Vec2(minU, sMaxV);
            Vec2 sUVPP = new Vec2(maxU, sMaxV);
            // min z
            if (!connectN)
                RenderHelper.DoQuadWithColorAndNormal(vertexBuilder, matrix, BR, BRT, BLT, BL, sUVPN, sUVPP, sUVNP, sUVNN, light, color, new Vector3f(0, 0, 1));
            // max z
            if (!connectS)
                RenderHelper.DoQuadWithColorAndNormal(vertexBuilder, matrix, FL, FLT, FRT, FR, sUVPN, sUVPP, sUVNP, sUVNN, light, color, new Vector3f(0, 0, 1));
            // min x
            if (!connectW)
                RenderHelper.DoQuadWithColorAndNormal(vertexBuilder, matrix, FR, FRT, BRT, BR, sUVPN, sUVPP, sUVNP, sUVNN, light, color, new Vector3f(-1, 0, 0));
            // max x
            if (!connectE)
                RenderHelper.DoQuadWithColorAndNormal(vertexBuilder, matrix, BL, BLT, FLT, FL, sUVPN, sUVPP, sUVNP, sUVNN, light, color, new Vector3f(-1, 0, 0));
            return;
        }
        //bottom
        if (!connectD)
            RenderHelper.DoQuadWithColorAndNormal(vertexBuilder, matrix, BL, FL, FR, BR, UVPN, UVPPFlat, UVNPFlat, UVNN, light, color, new Vector3f(0, -1, 0));
        // min z
        if (!connectN)
            RenderHelper.DoQuadWithColorAndNormal(vertexBuilder, matrix, BR, BRT, BLT, BL, UVPN, UVPP, UVNP, UVNN, light, color, new Vector3f(0, 0, 1));
        // max z
        if (!connectS)
            RenderHelper.DoQuadWithColorAndNormal(vertexBuilder, matrix, FL, FLT, FRT, FR, UVPN, UVPP, UVNP, UVNN, light, color, new Vector3f(0, 0, 1));
        // min x
        if (!connectW)
            RenderHelper.DoQuadWithColorAndNormal(vertexBuilder, matrix, BL, BLT, FLT, FL, UVPN, UVPP, UVNP, UVNN, light, color, new Vector3f(-1, 0, 0));
        // max x
        if (!connectE)
            RenderHelper.DoQuadWithColorAndNormal(vertexBuilder, matrix, FR, FRT, BRT, BR, UVPN, UVPP, UVNP, UVNN, light, color, new Vector3f(-1, 0, 0));
    }

    private static void renderTankOuter(Matrix4f matrix, VertexConsumer vertexBuilder, float r, float g, float b, float alpha, int light, BlockPos blockPos,
                                        boolean connectU, boolean connectD, boolean connectN, boolean connectE, boolean connectS, boolean connectW)
    {
        float minU = 0;
        float maxU = 0.5f;
        float minV = 0f;
        float maxV = 0.5f;

        Vec3 BL = new Vec3(connectW ? 0 : TANKBLOCK_ALLSIDE_MARGIN, connectD ? 0 : TANKBLOCK_ALLSIDE_MARGIN, connectN ? 0 : TANKBLOCK_ALLSIDE_MARGIN);
        Vec3 BR = new Vec3(connectE ? 1 : 1 - TANKBLOCK_ALLSIDE_MARGIN, connectD ? 0 : TANKBLOCK_ALLSIDE_MARGIN, connectN ? 0 : TANKBLOCK_ALLSIDE_MARGIN);
        Vec3 FL = new Vec3(connectW ? 0 : TANKBLOCK_ALLSIDE_MARGIN, connectD ? 0 : TANKBLOCK_ALLSIDE_MARGIN, connectS ? 1 : 1 - TANKBLOCK_ALLSIDE_MARGIN);
        Vec3 FR = new Vec3(connectE ? 1 : 1 - TANKBLOCK_ALLSIDE_MARGIN, connectD ? 0 : TANKBLOCK_ALLSIDE_MARGIN, connectS ? 1 : 1 - TANKBLOCK_ALLSIDE_MARGIN);
        Vec3 BLT = new Vec3(connectW ? 0 : TANKBLOCK_ALLSIDE_MARGIN, connectU ? 1 : 1 - TANKBLOCK_ALLSIDE_MARGIN, connectN ? 0 : TANKBLOCK_ALLSIDE_MARGIN);
        Vec3 BRT = new Vec3(connectE ? 1 : 1 - TANKBLOCK_ALLSIDE_MARGIN, connectU ? 1 : 1 - TANKBLOCK_ALLSIDE_MARGIN, connectN ? 0 : TANKBLOCK_ALLSIDE_MARGIN);
        Vec3 FLT = new Vec3(connectW ? 0 : TANKBLOCK_ALLSIDE_MARGIN, connectU ? 1 : 1 - TANKBLOCK_ALLSIDE_MARGIN, connectS ? 1 : 1 - TANKBLOCK_ALLSIDE_MARGIN);
        Vec3 FRT = new Vec3(connectE ? 1 : 1 - TANKBLOCK_ALLSIDE_MARGIN, connectU ? 1 : 1 - TANKBLOCK_ALLSIDE_MARGIN, connectS ? 1 : 1 - TANKBLOCK_ALLSIDE_MARGIN);
        Vec2 UVNN = new Vec2(minU, minV);
        Vec2 UVPN = new Vec2(maxU, minV);
        Vec2 UVNP = new Vec2(minU, maxV);
        Vec2 UVPP = new Vec2(maxU, maxV);


        Vector4f color = new Vector4f(r, g, b, alpha);

        // top
        if (!connectU)
        {
            RenderHelper.DoQuadWithColor(vertexBuilder, matrix, BRT, FRT, FLT, BLT, UVPN, UVPP, UVNP, UVNN, light, color);
            RenderHelper.DoQuadWithColor(vertexBuilder, matrix, BLT, FLT, FRT, BRT, UVPN, UVPP, UVNP, UVNN, light, color);
        }
        //bottom
        if (!connectD)
        {
            RenderHelper.DoQuadWithColor(vertexBuilder, matrix, BL, FL, FR, BR, UVPN.add(new Vec2(0.5f, 0f)), UVPP.add(new Vec2(0.5f, 0f)), UVNP.add(new Vec2(0.5f, 0f)), UVNN.add(new Vec2(0.5f, 0f)), light, color);
            RenderHelper.DoQuadWithColor(vertexBuilder, matrix, BR, FR, FL, BL, UVPN.add(new Vec2(0.5f, 0f)), UVPP.add(new Vec2(0.5f, 0f)), UVNP.add(new Vec2(0.5f, 0f)), UVNN.add(new Vec2(0.5f, 0f)), light, color);
        }
        // min z
        if (!connectN)
        {
            RenderHelper.DoQuadWithColor(vertexBuilder, matrix, BR, BRT, BLT, BL, UVPN, UVPP, UVNP, UVNN, light, color);
            RenderHelper.DoQuadWithColor(vertexBuilder, matrix, BL, BLT, BRT, BR, UVPN, UVPP, UVNP, UVNN, light, color);
        }
        // max z
        if (!connectS)
        {
            RenderHelper.DoQuadWithColor(vertexBuilder, matrix, FL, FLT, FRT, FR, UVPN, UVPP, UVNP, UVNN, light, color);
            RenderHelper.DoQuadWithColor(vertexBuilder, matrix, FR, FRT, FLT, FL, UVPN, UVPP, UVNP, UVNN, light, color);
        }
        // min x
        if (!connectW)
        {
            RenderHelper.DoQuadWithColor(vertexBuilder, matrix, BL, BLT, FLT, FL, UVPN, UVPP, UVNP, UVNN, light, color);
            RenderHelper.DoQuadWithColor(vertexBuilder, matrix, BLT, BL, FL, FLT, UVPN, UVPP, UVNP, UVNN, light, color);
        }
        // max x
        if (!connectE)
        {
            RenderHelper.DoQuadWithColor(vertexBuilder, matrix, FR, FRT, BRT, BR, UVPN, UVPP, UVNP, UVNN, light, color);
            RenderHelper.DoQuadWithColor(vertexBuilder, matrix, FRT, FR, BR, BRT, UVPN, UVPP, UVNP, UVNN, light, color);
        }
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