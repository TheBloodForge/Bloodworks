package com.bloodforge.bloodworks.Client.BlockRenderers;

import com.bloodforge.bloodworks.Blocks.BlockEntities.BE_StirlingGenerator;
import com.bloodforge.bloodworks.Blocks.BlockMachineBase;
import com.bloodforge.bloodworks.Globals;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class BER_StirlingGenerator implements BlockEntityRenderer<BE_StirlingGenerator>
{

    private final BlockEntityRendererProvider.Context context;

    public BER_StirlingGenerator(BlockEntityRendererProvider.Context context)
    {
        this.context = context;
    }

    @Override
    public void render(BE_StirlingGenerator ent, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay)
    {
        Minecraft.getInstance().getProfiler().push("Bloodworks Stirling Renderer");
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(ent.getBlockState().getValue(BlockMachineBase.FACING).getRotation());
        poseStack.mulPose(Quaternion.fromXYZDegrees(new Vector3f(-90, 0, 0)));
        poseStack.translate(-0.5, -0.5, -0.5);

        ent.clientAnimTime += Minecraft.getInstance().getDeltaFrameTime() * ent.energyGenerationF * 0.02f;
        if(ent.clientAnimTime > 1)
        {
            ent.clientAnimTime -= 1;
        }

        boolean isTopBlock = Minecraft.getInstance().level.getBlockState(ent.getBlockPos().below()).is(ent.getBlockState().getBlock());
        VertexConsumer vertexBuilder = RenderHelper.StartRenderingCutout(bufferSource, new ResourceLocation(Globals.MODID, "textures/blocks/stirling_generator.png"));

        //DRAW FLYWHEEL SIDES
        Vec3 wheelCenter = new Vec3(0.85, 0.45, -0.3);
        float wheelRadius = 0.75f;
        Vec3 wheelThicknessOffset = new Vec3(-0.28, 0, 0);
        Vec3 right = new Vec3(1, 0, 0);
        Vec3 up = new Vec3(0, 1, 0);
        Vec3 forward = new Vec3(0, 0, 1);
        Vec3 V1 = GetRotatingVertex((ent.clientAnimTime*360f)+0, wheelCenter, forward, up, right, new Vec3(0, wheelRadius, wheelRadius));
        Vec3 V2 = GetRotatingVertex((ent.clientAnimTime*360f)+90, wheelCenter, forward, up, right, new Vec3(0, wheelRadius, wheelRadius));
        Vec3 V3 = GetRotatingVertex((ent.clientAnimTime*360f)+180, wheelCenter, forward, up, right, new Vec3(0, wheelRadius, wheelRadius));
        Vec3 V4 = GetRotatingVertex((ent.clientAnimTime*360f)+270, wheelCenter, forward, up, right, new Vec3(0, wheelRadius, wheelRadius));
        Vec2 UV1 = new Vec2(00/64f, 32/64f);
        Vec2 UV2 = new Vec2(15/64f, 32/64f);
        Vec2 UV3 = new Vec2(15/64f, 47/64f);
        Vec2 UV4 = new Vec2(00/64f, 47/64f);

        RenderHelper.DoQuad(vertexBuilder, poseStack.last().pose(), V4, V3, V2, V1, UV1, UV2, UV3, UV4, combinedLight);
        RenderHelper.DoQuad(vertexBuilder, poseStack.last().pose(), V1, V2, V3, V4, UV1, UV2, UV3, UV4, combinedLight);
        RenderHelper.DoQuad(vertexBuilder, poseStack.last().pose(), V1.add(wheelThicknessOffset), V2.add(wheelThicknessOffset), V3.add(wheelThicknessOffset), V4.add(wheelThicknessOffset), UV1, UV2, UV3, UV4, combinedLight);
        RenderHelper.DoQuad(vertexBuilder, poseStack.last().pose(), V4.add(wheelThicknessOffset), V3.add(wheelThicknessOffset), V2.add(wheelThicknessOffset), V1.add(wheelThicknessOffset), UV1, UV2, UV3, UV4, combinedLight);

        //DRAW FLYWHEEL FILLS
        Vec3 wheelCenter2 = new Vec3(0.35, 0.7, -0.05);
        Vec3 wheelShaftOffset = new Vec3(-0.4, 0, 0);
        UV1 = new Vec2(00/64f, 16/64f);
        UV2 = new Vec2(03/64f, 16/64f);
        UV3 = new Vec2(03/64f, 20/64f);
        UV4 = new Vec2(00/64f, 20/64f);
        int numSegments = 12;
        float wheelOuterRadius = 1f;
        float wheelInnerRadius = 0.7f;
        float wheelShaftRadius = 0.1f;
        for (int r = 0; r < numSegments; r++)
        {
            double radialScaleR = ((float) r / numSegments) * 6.2831852;
            double radialScaleR2 = ((float) (r + 1) / numSegments) * 6.2831852;
            radialScaleR -= ent.clientAnimTime*6.2831852;
            radialScaleR2 -= ent.clientAnimTime*6.2831852;

            Vec3 vertexA = wheelCenter2.add(0.5f, 0.5f, 0.5f).add(new Vec3(0, (float)(Math.sin(radialScaleR) / 2 * wheelOuterRadius), (float)(Math.cos(radialScaleR) / 2 * wheelOuterRadius)));
            Vec3 vertexB = wheelCenter2.add(0.5f, 0.5f, 0.5f).add(new Vec3(0, (float)(Math.sin(radialScaleR2) / 2 * wheelOuterRadius), (float)(Math.cos(radialScaleR2) / 2 * wheelOuterRadius)));

            Vec3 vertexAI = wheelCenter2.add(0.5f, 0.5f, 0.5f).add(new Vec3(0, (float)(Math.sin(radialScaleR) / 2 * wheelInnerRadius), (float)(Math.cos(radialScaleR) / 2 * wheelInnerRadius)));
            Vec3 vertexBI = wheelCenter2.add(0.5f, 0.5f, 0.5f).add(new Vec3(0, (float)(Math.sin(radialScaleR2) / 2 * wheelInnerRadius), (float)(Math.cos(radialScaleR2) / 2 * wheelInnerRadius)));

            Vec3 vertexAS = wheelCenter2.add(0.5f, 0.5f, 0.5f).add(new Vec3(0, (float)(Math.sin(radialScaleR) / 2 *  wheelShaftRadius), (float)(Math.cos(radialScaleR) / 2 *  wheelShaftRadius)));
            Vec3 vertexBS = wheelCenter2.add(0.5f, 0.5f, 0.5f).add(new Vec3(0, (float)(Math.sin(radialScaleR2) / 2 * wheelShaftRadius), (float)(Math.cos(radialScaleR2) / 2 * wheelShaftRadius)));


            RenderHelper.DoQuad(vertexBuilder, poseStack.last().pose(),
                    vertexB, vertexA, vertexA.add(wheelThicknessOffset), vertexB.add(wheelThicknessOffset),
                    UV2, UV3, UV4, UV1,
                    combinedLight);
            RenderHelper.DoQuad(vertexBuilder, poseStack.last().pose(),
                    vertexAI, vertexBI, vertexBI.add(wheelThicknessOffset), vertexAI.add(wheelThicknessOffset),
                    UV2, UV3, UV4, UV1,
                    combinedLight);
            RenderHelper.DoQuad(vertexBuilder, poseStack.last().pose(),
                    vertexBS, vertexAS, vertexAS.add(wheelShaftOffset), vertexBS.add(wheelShaftOffset),
                    UV2, UV3, UV4, UV1,
                    combinedLight);
        }

        //DRAW MOTOR CONNECTION
        wheelRadius = 0.25f;
        UV1 = new Vec2(25/64f, 32/64f);
        UV2 = new Vec2(32/64f, 32/64f);
        UV3 = new Vec2(32/64f, 39/64f);
        UV4 = new Vec2(25/64f, 39/64f);
        wheelCenter = new Vec3(0.43, 0.95, 0.2);
        wheelThicknessOffset = new Vec3(0.02, 0, 0);
        V1 = GetRotatingVertex((ent.clientAnimTime*360f)+0, wheelCenter, forward, up, right, new Vec3(0, wheelRadius, wheelRadius));
        V2 = GetRotatingVertex((ent.clientAnimTime*360f)+90, wheelCenter, forward, up, right, new Vec3(0, wheelRadius, wheelRadius));
        V3 = GetRotatingVertex((ent.clientAnimTime*360f)+180, wheelCenter, forward, up, right, new Vec3(0, wheelRadius, wheelRadius));
        V4 = GetRotatingVertex((ent.clientAnimTime*360f)+270, wheelCenter, forward, up, right, new Vec3(0, wheelRadius, wheelRadius));
        RenderHelper.DoQuad(vertexBuilder, poseStack.last().pose(), V4.add(wheelThicknessOffset), V3.add(wheelThicknessOffset), V2.add(wheelThicknessOffset), V1.add(wheelThicknessOffset), UV1, UV2, UV3, UV4, combinedLight);
        RenderHelper.DoQuad(vertexBuilder, poseStack.last().pose(), V1, V2, V3, V4, UV1, UV2, UV3, UV4, combinedLight);
        Vec3 wheelCenter2Offset = new Vec3(0, -0.4, 0);
        RenderHelper.DoQuad(vertexBuilder, poseStack.last().pose(), V4.add(wheelCenter2Offset).add(wheelThicknessOffset), V3.add(wheelCenter2Offset).add(wheelThicknessOffset), V2.add(wheelCenter2Offset).add(wheelThicknessOffset), V1.add(wheelCenter2Offset).add(wheelThicknessOffset), UV1, UV2, UV3, UV4, combinedLight);
        RenderHelper.DoQuad(vertexBuilder, poseStack.last().pose(), V1.add(wheelCenter2Offset), V2.add(wheelCenter2Offset), V3.add(wheelCenter2Offset), V4.add(wheelCenter2Offset), UV1, UV2, UV3, UV4, combinedLight);

        //DRAW MOTOR BELT
        int motorAnimFrame = ((int)(ent.clientAnimTime*10f)) % 2;
        float beltW = 0.35f;
        float beltH = 0.7f;
        UV1 = new Vec2(00/64f, (motorAnimFrame * 8 + 48 + 7) / 64f);
        UV2 = new Vec2(15/64f, (motorAnimFrame * 8 + 48 + 7) / 64f);
        UV3 = new Vec2(15/64f, (motorAnimFrame * 8 + 48)     / 64f);
        UV4 = new Vec2(00/64f, (motorAnimFrame * 8 + 48)     / 64f);
        V1 = new Vec3(0,    0,     beltW).add(0.44f, 0.65f, 0.27f);
        V2 = new Vec3(0,    beltH, beltW).add(0.44f, 0.65f, 0.27f);
        V3 = new Vec3(0,    beltH, 0)    .add(0.44f, 0.65f, 0.27f);
        V4 = new Vec3(0,    0,     0)    .add(0.44f, 0.65f, 0.27f);
        RenderHelper.DoQuad(vertexBuilder, poseStack.last().pose(), V1, V2, V3, V4, UV1, UV2, UV3, UV4, combinedLight);
        RenderHelper.DoQuad(vertexBuilder, poseStack.last().pose(), V4, V3, V2, V1, UV1, UV2, UV3, UV4, combinedLight);

        //DRAW PISTON
        Vec3 pistonBasePos = new Vec3(0.5, 0.22, 0.433);
        float pistonHeight = (float)(0.5f + Math.sin(ent.clientAnimTime*6.2831852f)) * 0.16f;
        RenderHelper.renderCuboidWithDifferentSideUVs(poseStack.last().pose(), vertexBuilder,
                pistonBasePos.add(new Vec3(0, pistonHeight, 0)),
                new Vec3(0.75, 0.1, 0.75), 0,
                32/64f, 48/64f,
                42/64f, 58/64f,
                32/64f, 62/64f,
                42/64f, 64/64f,
                combinedLight,
                false
        );
        RenderHelper.renderCuboid(poseStack.last().pose(), vertexBuilder,
                pistonBasePos.add(new Vec3(0, pistonHeight/2, 0)).add(0, 0.2, 0),
                new Vec3(0.1, 0.4-pistonHeight, 0.1), 0,
                00/64f, 00/64f,
                14/64f, 14/64f,
                combinedLight,
                true, true, true, true, true, true,
                false
        );



        poseStack.popPose();

        Minecraft.getInstance().getProfiler().pop();
    }

    public Vec3 GetRotatingVertex(float degrees, Vec3 center, Vec3 forward, Vec3 up, Vec3 right, Vec3 offset)
    {
        float rad = (float)Math.toRadians(degrees);
        return offset.add(forward.scale(Math.sin(rad)).scale(offset.z))
                     .add(up.scale(Math.cos(rad)).scale(offset.y))
                     .add(right.scale(offset.x))
                     .add(center);
    }


    @Override
    public boolean shouldRender(BE_StirlingGenerator p_173568_, Vec3 p_173569_)
    {
        return true;
    }

    @Override
    public boolean shouldRenderOffScreen(BE_StirlingGenerator p_112306_)
    {
        return true;
    }
}