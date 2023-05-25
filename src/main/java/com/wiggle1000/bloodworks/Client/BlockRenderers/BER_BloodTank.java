package com.wiggle1000.bloodworks.Client.BlockRenderers;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.wiggle1000.bloodworks.Blocks.BlockEntities.BE_BloodTank;
import com.wiggle1000.bloodworks.Globals;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;

public class BER_BloodTank implements BlockEntityRenderer<BE_BloodTank>
{

    public static final float TANK_THICKNESS = 0.3f;
    public static final float TANK_HEIGHT = 0.2f;
    public static final float TANK_BOTTOM = 0.0f;

    private final BlockEntityRendererProvider.Context context;

    public BER_BloodTank(BlockEntityRendererProvider.Context context)
    {
        this.context = context;
    }

    private void add(BufferBuilder renderer, PoseStack stack, float x, float y, float z, float u, float v, float r, float g, float b, float a)
    {
        renderer.vertex(stack.last().pose(), x, y, z)
                .color(r, g, b, a)
                .uv(u, v)
                .normal(1, 0, 0)
                .endVertex();
    }

    @Override
    public void render(BE_BloodTank tileEntityIn, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        if (tileEntityIn.isRemoved()) return;

        FluidStack fluid = tileEntityIn.getFluidInTank(0);

        Fluid renderFluid = fluid.getFluid();
        if (renderFluid == null) return;

        ResourceLocation fluidStill = new ResourceLocation(Globals.MODID, "textures/blocks/fluid_blood_still.png");

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();

        RenderHelper.StartRenderingTris(builder, fluidStill);
        float scale = (1.0f - TANK_THICKNESS / 2 - TANK_THICKNESS) * fluid.getAmount() / (tileEntityIn.getTankCapacity(0));

        Quaternion rotation = Vector3f.YP.rotationDegrees(0);

        poseStack.pushPose();
        poseStack.translate(.5, 0, .5);
        poseStack.mulPose(rotation);
        if (scale == 0.330f)
        {
            poseStack.translate(0, -.1, 0);
            poseStack.scale(.6f, scale + 0.110f, .6f);
        } else if (scale == 0.440f) {
            poseStack.translate(0, -.2, 0);
            poseStack.scale(.6f, scale + 0.110f, .6f);
        } else if (scale == 0.550f) {
            poseStack.translate(0, -.4, 0);
            poseStack.scale(.6f, scale + 0.210f, .6f);
        } else {
            poseStack.scale(.6f, scale, .6f);
        }
        poseStack.translate(-.5, scale, -.5);
        float xa = 0.2f, xb = 0.8f, ya = 1, yb = 0;
        Vec3 ua = new Vec3(xa, ya, xb);
        Vec3 ub = new Vec3(xb, ya, xa);
        Vec3 uc = new Vec3(xa, yb, xb);
        Vec3 ud = new Vec3(xb, yb, xa);
        Vec2 uva = new Vec2(getMinU(), getMinV());
        Vec2 uvb = new Vec2(getMinU(), getMaxV());
        Vec2 uvc = new Vec2(getMaxU(), getMinV());
        Vec2 uvd = new Vec2(getMinU(), getMaxV());
        RenderHelper.DoQuad(builder, poseStack.last().pose(), ua, ub, uc, ud, uva, uvb, uvc, uvd, (combinedLightIn * 15) + 30);
        RenderHelper.DoQuad(builder, poseStack.last().pose(), ua, ub, uc, ud, uva, uvb, uvc, uvd, (combinedLightIn * 15) + 30);

        poseStack.popPose();
        RenderHelper.FinishRendering(builder);
    }

    private float getMinU()
    {
        return 0;
    }
    private float getMinV()
    {
        return 0;
    }
    private float getMaxU()
    {
        return 1;
    }
    private float getMaxV()
    {
        return 1;
    }
}