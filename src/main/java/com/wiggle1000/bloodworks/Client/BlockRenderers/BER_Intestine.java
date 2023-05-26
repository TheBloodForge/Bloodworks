package com.wiggle1000.bloodworks.Client.BlockRenderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.wiggle1000.bloodworks.Blocks.BlockEntities.BE_Intestine;
import com.wiggle1000.bloodworks.Blocks.BlockIntestine;
import com.wiggle1000.bloodworks.Config.BloodworksCommonConfig;
import com.wiggle1000.bloodworks.Globals;
import com.wiggle1000.bloodworks.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class BER_Intestine implements BlockEntityRenderer<BE_Intestine> //TODO: specify blockentity
{

    private final BlockEntityRendererProvider.Context context;
    public BER_Intestine(BlockEntityRendererProvider.Context context)
    {
        this.context = context;
    }


    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean checkLOS(BlockPos blockPos, Vec3 offset)
    {
        BlockHitResult hr = Minecraft.getInstance().level.clip(new ClipContext(Minecraft.getInstance().cameraEntity.getEyePosition(), new Vec3(blockPos.getX() + offset.x, blockPos.getY() + offset.y, blockPos.getZ() + offset.z), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, Minecraft.getInstance().player));
        return hr.getType() == HitResult.Type.MISS || !Minecraft.getInstance().level.getBlockState(hr.getBlockPos()).canOcclude();
    }

    private BE_Intestine currentBE;
    @Override
    public void render(BE_Intestine ent, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay)
    {
        currentBE = ent;
        BlockPos blockPos = ent.getBlockPos();
        if(Minecraft.getInstance().cameraEntity.blockPosition().distSqr(blockPos) > 80*80)
        {
            return;
        }
        if( BloodworksCommonConfig.DO_OCCLUSION_CULLING.get() &&
            !checkLOS(blockPos, new Vec3(0, 0, 0)) &&
            !checkLOS(blockPos, new Vec3(1, 0, 0)) &&
            !checkLOS(blockPos, new Vec3(0, 1, 0)) &&
            !checkLOS(blockPos, new Vec3(0, 0, 1)) &&
            !checkLOS(blockPos, new Vec3(1, 1, 0)) &&
            !checkLOS(blockPos, new Vec3(1, 0, 1)) &&
            !checkLOS(blockPos, new Vec3(0, 1, 1)) &&
            !checkLOS(blockPos, new Vec3(1, 1, 1)) )
        {
            return;
        }


        poseStack.pushPose();

        long totalTicks = Minecraft.getInstance().level.getGameTime();

        VertexConsumer buffer = RenderHelper.StartRendering(bufferSource, new ResourceLocation(Globals.MODID, "textures/blocks/block_intestine.png"));

        int numRings = 20;
        int numSegments = 20;
        boolean renderInside = true;

        int dist = Minecraft.getInstance().cameraEntity.blockPosition().distManhattan(blockPos);

        if(dist > 50)
        {
            numRings = 1;
            numSegments = 3;
            renderInside = false;
        }
        else if(dist > 40)
        {
            numRings = 2;
            numSegments = 4;
            renderInside = false;
        }
        else if(dist > 30)
        {
            numRings = 3;
            numSegments = 6;
            renderInside = false;
        }
        else if(dist > 20)
        {
            numRings = 5;
            numSegments = 8;
        }
        else if(dist > 10)
        {
            numRings = 10;
            numSegments = 10;
        }

        Direction from = ent.getBlockState().getValue(BlockIntestine.FACING_FROM);
        Direction to = ent.getBlockState().getValue(BlockIntestine.FACING_TO);
        poseStack.translate(0.5,0.5,0.5);

        DrawIntestineSegmentAdvanced(buffer, poseStack, combinedLight, numRings, numSegments, totalTicks, from, to);
        /*if(from.getOpposite() == to)
        {
            DrawStraightSegment(buffer, poseStack,  combinedLight, numRings, numSegments, totalTicks, blockPos, renderInside, to);
        }
        else if(from == to)
        {
            DrawEndSegment(buffer, poseStack,  combinedLight, numRings, numSegments, totalTicks, blockPos, renderInside, to);
        }
        else
        {
            DrawLSegment(buffer, poseStack,  combinedLight, numRings, numSegments, totalTicks, blockPos, renderInside, from, to);
        }*/

        RenderHelper.FinishRendering(buffer);

        poseStack.popPose();
    }

    private void OrientFromDirection(PoseStack poseStack, Direction fromDir)
    {
        poseStack.mulPose(fromDir.getRotation());
    }


    private void DrawStraightSegment(VertexConsumer buffer, PoseStack poseStack, int combinedLight, int numRings, int numSegments, long totalTicks, BlockPos blockPos, boolean renderInside, Direction fromDir)
    {

        poseStack.pushPose();
        OrientFromDirection(poseStack, fromDir);
        double blockOffsetMagnitude = currentBE.getBlockState().getValue(BlockIntestine.INTESTINE_ID);
        for(double y = 0; y < numRings; y++)
        {
            double basalY = y/numRings;
            double nextY = (y+1)/numRings;
            double thickness = 0.8 + Math.sin((totalTicks/10.0) + (basalY + blockOffsetMagnitude) * 7) / 30;
            double thicknessUpper = 0.8 + Math.sin((totalTicks/10.0) + (basalY + (1f/numRings) + blockOffsetMagnitude) * 7) / 30;

            for(double radial = 0; radial < numSegments; radial++)
            {
                double r1 = Math.toRadians(radial/numSegments*360);
                double r2 = Math.toRadians((radial+1)/numSegments*360);
                double U1 = radial/numSegments;
                double U2 = (radial + 1)/numSegments;
                double radialScrumble =         Math.sin((totalTicks/30.0) + (blockOffsetMagnitude + basalY) * 10) / 20;
                double radialScrumbleUpper =    Math.sin((totalTicks/30.0) + (blockOffsetMagnitude + nextY)  * 10) / 20;
                radialScrumble +=               Math.cos((totalTicks/51.0) + (blockOffsetMagnitude + basalY) * 11) / 20;
                radialScrumbleUpper +=          Math.cos((totalTicks/51.0) + (blockOffsetMagnitude + nextY)  * 11) / 20;
                double basalX = Math.sin(r1) / 2 * (thickness + radialScrumble);
                double basalZ = Math.cos(r1) / 2 * (thickness - radialScrumble);
                double nextX =  Math.sin(r2) / 2 * (thickness + radialScrumble);
                double nextZ =  Math.cos(r2) / 2 * (thickness - radialScrumble);
                double basalXupper = Math.sin(r1) / 2 * (thicknessUpper + radialScrumbleUpper);
                double basalZupper = Math.cos(r1) / 2 * (thicknessUpper - radialScrumbleUpper);
                double nextXupper =  Math.sin(r2) / 2 * (thicknessUpper + radialScrumbleUpper);
                double nextZupper =  Math.cos(r2) / 2 * (thicknessUpper - radialScrumbleUpper);
                Vec3 a = new Vec3(basalX, basalY - 0.5, basalZ);
                Vec3 b = new Vec3(basalXupper, nextY - 0.5, basalZupper);
                Vec3 c = new Vec3(nextXupper, nextY - 0.5, nextZupper);
                Vec3 d = new Vec3(nextX, basalY - 0.5, nextZ);
                Vec2 uvA = new Vec2((float)U1, (float)basalY/2);
                Vec2 uvB = new Vec2((float)U1, (float)nextY/2);
                Vec2 uvC = new Vec2((float)U2, (float)nextY/2);
                Vec2 uvD = new Vec2((float)U2, (float)basalY/2);

                RenderHelper.DoQuad(buffer, poseStack.last().pose(), a, b, c, d, uvA, uvB, uvC, uvD, combinedLight);//cLight + 30);
                if(renderInside)
                {
                    RenderHelper.DoQuad(buffer, poseStack.last().pose(), d, c, b, a, uvD, uvC, uvB, uvA, combinedLight);//cLight + 30);
                    //Idea: use UV offset to add silia texture to inside?
                }
            }

        }
        poseStack.popPose();
    }


    private void DrawLSegment(VertexConsumer buffer, PoseStack poseStack, int combinedLight, int numRings, int numSegments, long totalTicks, BlockPos blockPos, boolean renderInside, Direction fromDir, Direction toDir)
    {
        poseStack.pushPose();
        OrientFromDirection(poseStack, fromDir);
        poseStack.mulPose(Quaternion.fromXYZDegrees(new Vector3f(0, 0, 0f)));
        double blockOffsetMagnitude = currentBE.getBlockState().getValue(BlockIntestine.INTESTINE_ID);
        float hCurveFactor = 0;
        float vCurveFactor = 0;
        switch (fromDir)
        {
            case NORTH:
                switch (toDir)
                {
                    case EAST -> hCurveFactor = -1;
                    case WEST -> hCurveFactor = 1;
                    case UP -> vCurveFactor = -1;
                    case DOWN -> vCurveFactor = 1;
                }
                break;
            case SOUTH:
                switch (toDir)
                {
                    case EAST -> hCurveFactor = -1;
                    case WEST -> hCurveFactor = 1;
                    case UP -> vCurveFactor = -1;
                    case DOWN -> vCurveFactor = 1;
                }
                break;
            case EAST:
                switch (toDir)
                {
                    case NORTH -> hCurveFactor = -1;
                    case SOUTH -> hCurveFactor = 1;
                    case UP -> vCurveFactor = -1;
                    case DOWN -> vCurveFactor = 1;
                }
                break;
            case WEST:
                switch (toDir)
                {
                    case NORTH -> hCurveFactor = 1;
                    case SOUTH -> hCurveFactor = -1;
                    case UP -> vCurveFactor = -1;
                    case DOWN -> vCurveFactor = 1;
                }
                break;
            case UP:
                switch (toDir)
                {
                    case NORTH -> vCurveFactor = 1;
                    case SOUTH -> vCurveFactor = -1;
                    case EAST -> hCurveFactor = -1;
                    case WEST -> hCurveFactor = 1;
                }
                break;
            case DOWN:
                switch (toDir)
                {
                    case NORTH -> vCurveFactor = -1;
                    case SOUTH -> vCurveFactor = 1;
                    case EAST -> hCurveFactor = 1;
                    case WEST -> hCurveFactor = -1;
                }
                break;
        }
        hCurveFactor *= 1.5707963f/numRings;
        vCurveFactor *= 1.5707963f/numRings;
        float hOffFactor = 0.0f;
        float vOffFactor = 0.0f;
        for(double y = 0; y < numRings; y++)
        {
            double basalY = y/numRings;
            double nextY = (y+1)/numRings;
            double thickness = 0.8 + Math.sin((totalTicks/10.0) + (basalY + blockOffsetMagnitude) * 7) / 30;
            double thicknessUpper = 0.8 + Math.sin((totalTicks/10.0) + (basalY + (1f/numRings) + blockOffsetMagnitude) * 7) / 30;
            Matrix4f premulPose = poseStack.last().pose().copy();
            poseStack.mulPose(Quaternion.fromXYZ(vCurveFactor, 0, hCurveFactor));
            for(double radial = 0; radial < numSegments; radial++)
            {
                double r1 = Math.toRadians(radial/numSegments*360);
                double r2 = Math.toRadians((radial+1)/numSegments*360);
                double U1 = radial/numSegments;
                double U2 = (radial + 1)/numSegments;
                double radialScrumble =         Math.sin((totalTicks/30.0) + (blockOffsetMagnitude + basalY) * 10) / 20;
                double radialScrumbleUpper =    Math.sin((totalTicks/30.0) + (blockOffsetMagnitude + nextY)  * 10) / 20;
                radialScrumble +=               Math.cos((totalTicks/51.0) + (blockOffsetMagnitude + basalY) * 11) / 20;
                radialScrumbleUpper +=          Math.cos((totalTicks/51.0) + (blockOffsetMagnitude + nextY)  * 11) / 20;
                double basalX = Math.sin(r1) / 2 * (thickness + radialScrumble) + hOffFactor;
                double basalZ = Math.cos(r1) / 2 * (thickness - radialScrumble) + vOffFactor;
                double nextX =  Math.sin(r2) / 2 * (thickness + radialScrumble) + hOffFactor;
                double nextZ =  Math.cos(r2) / 2 * (thickness - radialScrumble) + vOffFactor;
                double basalXupper = Math.sin(r1) / 2 * (thicknessUpper + radialScrumbleUpper) + hOffFactor;
                double basalZupper = Math.cos(r1) / 2 * (thicknessUpper - radialScrumbleUpper) + vOffFactor;
                double nextXupper =  Math.sin(r2) / 2 * (thicknessUpper + radialScrumbleUpper) + hOffFactor;
                double nextZupper =  Math.cos(r2) / 2 * (thicknessUpper - radialScrumbleUpper) + vOffFactor;
                Vec3 a = new Vec3(basalX, basalY - 0.5, basalZ);
                Vec3 b = new Vec3(basalXupper, nextY - 0.5, basalZupper);
                Vec3 c = new Vec3(nextXupper, nextY - 0.5, nextZupper);
                Vec3 d = new Vec3(nextX, basalY - 0.5, nextZ);
                Vec2 uvA = new Vec2((float)U1, (float)basalY/2);
                Vec2 uvB = new Vec2((float)U1, (float)nextY/2);
                Vec2 uvC = new Vec2((float)U2, (float)nextY/2);
                Vec2 uvD = new Vec2((float)U2, (float)basalY/2);

                RenderHelper.DoQuadAcrossMatricesAD(buffer, poseStack.last().pose(), premulPose, a, b, c, d, uvA, uvB, uvC, uvD, combinedLight);
                if(renderInside)
                {
                    RenderHelper.DoQuadAcrossMatricesAD(buffer, poseStack.last().pose(), premulPose, d, c, b, a, uvD, uvC, uvB, uvA, combinedLight);
                    //Idea: use UV offset to add silia texture to inside?
                }
            }

        }
        poseStack.popPose();
    }

    private void DrawIntestineSegmentAdvanced(VertexConsumer buffer, PoseStack poseStack, int combinedLight, int numRings, int numSegments, long animTime, Direction fromDir, Direction toDir)
    {
        double width = 1.0;
        double blockAnimOffsetBase = currentBE.getBlockState().getValue(BlockIntestine.INTESTINE_ID);
        Vector3f initialDir = fromDir.getOpposite().step();
        Vector3f initialPos = fromDir.step();
        initialPos.mul(0.5f);
        Vector3f endDir = toDir.step();
        Vector3f endPos = toDir.step();
        Vector3f middleCurvePoint = new Vector3f(new Vec3(initialPos).add(new Vec3(initialDir).scale(0.5f)));//.add(new Vec3(endDir).scale(-1)));
        endPos.mul(0.5f);

        for(int i = 0; i < numRings; i++)
        {
            float alongScale = (float)i/numRings;
            double blockAnimOffset = blockAnimOffsetBase + alongScale;

            Vector3f lerpedDir = Util.Lerp(initialDir, endDir, alongScale);
            Vector3f lerpedBezA = Util.Lerp(initialPos, middleCurvePoint, alongScale);
            Vector3f lerpedBezB = Util.Lerp(middleCurvePoint, endPos, alongScale);
            Vector3f finalSegmentCenter = Util.Lerp(lerpedBezA, lerpedBezB, alongScale);

            Vector3f anim1 = new Vector3f(
                    (float)Math.sin((animTime/2f) + blockAnimOffset),
                    (float)Math.cos((animTime/2f) + blockAnimOffset),
                    (float)Math.sin((animTime/2f) + blockAnimOffset)
            );
            anim1.mul(0.1f);
            finalSegmentCenter.add(anim1);

            for(int r = 0; r < numSegments; r++)
            {
                float radialScale = (float)r/numSegments;
                double radialScaleR = ((float)r/numSegments)*6.2831852;

                Vec3 N = new Vec3(lerpedDir);
                Vec3 circleRelativeUp = N.cross(new Vec3(0, 1, 0.1));
                Vec3 circleRelativeRight = N.cross(circleRelativeUp).normalize();
                circleRelativeUp = circleRelativeUp.normalize();

                Vec3 vertexA = new Vec3(finalSegmentCenter).add(circleRelativeUp.scale(Math.sin(radialScaleR)/2*width)).add(circleRelativeRight.scale(Math.cos(radialScaleR)/2*width));
                Vec3 vertexB = new Vec3(finalSegmentCenter).add(circleRelativeUp.scale(Math.sin(radialScaleR)/2*width)).add(circleRelativeRight.scale(Math.cos(radialScaleR)/2*width));
                Vec3 vertexC = new Vec3(finalSegmentCenter).add(circleRelativeUp.scale(Math.sin(radialScaleR)/2*width)).add(circleRelativeRight.scale(Math.cos(radialScaleR)/2*width));
                Vec3 vertexD = new Vec3(finalSegmentCenter).add(circleRelativeUp.scale(Math.sin(radialScaleR)/2*width)).add(circleRelativeRight.scale(Math.cos(radialScaleR)/2*width));

                RenderHelper.DoQuad(buffer, poseStack.last().pose(),
                        vertexA, vertexB, vertexC, vertexD,
                        new Vec2(0, 0),
                        new Vec2(0, 0.5f),
                        new Vec2(1, 0.5f),
                        new Vec2(1, 0), combinedLight);
            }
        }
    }
    private void DrawEndSegment(VertexConsumer buffer, PoseStack poseStack, int combinedLight, int numRings, int numSegments, long totalTicks, BlockPos blockPos, boolean renderInside, Direction fromDir)
    {

    }

    @Override
    public boolean shouldRender(BE_Intestine p_173568_, Vec3 p_173569_)
    {
        return true;
    }

    @Override
    public boolean shouldRenderOffScreen(BE_Intestine p_112306_)
    {
        return true;
    }
}