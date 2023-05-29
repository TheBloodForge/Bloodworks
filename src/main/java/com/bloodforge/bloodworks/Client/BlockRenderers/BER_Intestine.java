package com.bloodforge.bloodworks.Client.BlockRenderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.bloodforge.bloodworks.Blocks.BlockEntities.BE_Intestine;
import com.bloodforge.bloodworks.Blocks.BlockIntestine;
import com.bloodforge.bloodworks.Config.BloodworksCommonConfig;
import com.bloodforge.bloodworks.Globals;
import com.bloodforge.bloodworks.Util;
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
public class BER_Intestine implements BlockEntityRenderer<BE_Intestine>
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

        VertexConsumer buffer = RenderHelper.StartRenderingCutout(bufferSource, new ResourceLocation(Globals.MODID, "textures/blocks/block_intestine.png"));

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


    /*private void DrawStraightSegment(VertexConsumer buffer, PoseStack poseStack, int combinedLight, int numRings, int numSegments, long totalTicks, BlockPos blockPos, boolean renderInside, Direction fromDir)
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
        poseStack.mulPose(Quaternion.fromXYZDegrees(new Vector3f(0, 0, 0)));
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
    }*/

    private void DrawIntestineSegmentAdvanced(VertexConsumer buffer, PoseStack poseStack, int combinedLight, int numRings, int numSegments, long animTime, Direction fromDir, Direction toDir)
    {
        int uvrand1 = Integer.reverse(Integer.reverseBytes(currentBE.getBlockPos().getX() + currentBE.getBlockPos().getY() + currentBE.getBlockPos().getZ()));
        uvrand1 = (int)Math.floor((uvrand1 << 3) * 0.1f);
        float UVXOff = (uvrand1 % 4) / 4f;
        float UVYOff = (uvrand1 % 2) / 2f;
        float UVXScale = 1f;
        float UVYScale = 0.62f;
        double width = 1.0;
        double blockAnimOffsetBase = currentBE.getBlockState().getValue(BlockIntestine.INTESTINE_ID);
        Vec3 initialDir = new Vec3(fromDir.getOpposite().step());
        Vec3 initialPos = new Vec3(fromDir.step()).scale(0.5f);
        Vec3 endDir = new Vec3(toDir.step());
        Vec3 endPos = new Vec3(toDir.step()).scale(0.5f);
        Vec3 middleCurvePoint = initialPos.add(initialDir.scale(0.5f));
        float lastAlongScale = 0;

        Vec3 lastDir = Util.Lerp(initialDir, endDir, lastAlongScale);
        Vec3 lastBezA = Util.Lerp(initialPos, middleCurvePoint, lastAlongScale);
        Vec3 lastBezB = Util.Lerp(middleCurvePoint, endPos, lastAlongScale);
        Vec3 lastSegmentCenter = Util.Lerp(lastBezA, lastBezB, lastAlongScale);
        lastSegmentCenter = AnimateCenter(lastSegmentCenter, lastDir, (animTime/2f), (float)blockAnimOffsetBase);

        Vec3 lN = lastDir;
        Vec3 lCircleRelativeUp = lN.cross(initialDir.subtract(endDir).scale(-1));
        if(fromDir.getOpposite() == toDir)
        {
            lCircleRelativeUp = new Vec3(fromDir.step()).xRot(1.5708f).zRot(1.5708f);
        }
        Vec3 lCircleRelativeRight = lN.cross(lCircleRelativeUp).normalize();
        lCircleRelativeUp = lCircleRelativeUp.normalize();
        for(int i = 1; i <= numRings; i++)
        {
            float alongScale = (float)i/numRings;
            double blockAnimOffset = blockAnimOffsetBase + alongScale;
            double blockAnimOffsetLast = blockAnimOffsetBase + alongScale - 1;

            Vec3 lerpedDir = Util.Lerp(initialDir, endDir, alongScale);
            Vec3 lerpedBezA = Util.Lerp(initialPos, middleCurvePoint, alongScale);
            Vec3 lerpedBezB = Util.Lerp(middleCurvePoint, endPos, alongScale);
            Vec3 finalSegmentCenter = Util.Lerp(lerpedBezA, lerpedBezB, alongScale);
            finalSegmentCenter = AnimateCenter(finalSegmentCenter, lerpedDir, (animTime/2f), (float)blockAnimOffset);


            Vec3 N = lerpedDir;
            Vec3 circleRelativeUp = N.cross(initialDir.subtract(endDir).scale(-1));
            if(fromDir.getOpposite() == toDir)
            {
                circleRelativeUp = new Vec3(fromDir.step()).xRot(1.5708f).zRot(1.5708f);
            }
            Vec3 circleRelativeRight = N.cross(circleRelativeUp).normalize();
            circleRelativeUp = circleRelativeUp.normalize();

            for(int r = 0; r < numSegments; r++)
            {
                float radialScale = (float)r/numSegments;
                float radialScale2 = (float)(r+1)/numSegments;
                double radialScaleR = ((float)r/numSegments)*6.2831852;
                double radialScaleR2 = ((float)(r+1)/numSegments)*6.2831852;

                //TODO: get better at math and add some radial lumpage that doesnt break on seams :(
                /*double skinAnim1last = Math.sin(radialScaleR*2 + animTime + blockAnimOffsetLast)*0.1;
                double skinAnim1     = Math.sin(radialScaleR*2 + animTime + blockAnimOffset)*0.1;
                Vec3 animMultLast = lastSegmentCenter.add(lCircleRelativeRight).add(lCircleRelativeUp);
                Vec3 animMult = finalSegmentCenter.add(circleRelativeRight).add(circleRelativeUp);

                Vec3 anim1Last =    animMultLast.scale(skinAnim1last);
                Vec3 anim1 =        animMult.scale(skinAnim1);*/

                Vec3 vertexA = finalSegmentCenter.add( circleRelativeUp.scale(Math.sin(radialScaleR )/2*width)).add( circleRelativeRight.scale(Math.cos(radialScaleR)/2*width));
                Vec3 vertexB = lastSegmentCenter .add(lCircleRelativeUp.scale(Math.sin(radialScaleR )/2*width)).add(lCircleRelativeRight.scale(Math.cos(radialScaleR)/2*width));
                Vec3 vertexC = lastSegmentCenter .add(lCircleRelativeUp.scale(Math.sin(radialScaleR2)/2*width)).add(lCircleRelativeRight.scale(Math.cos(radialScaleR2)/2*width));
                Vec3 vertexD = finalSegmentCenter.add( circleRelativeUp.scale(Math.sin(radialScaleR2)/2*width)).add( circleRelativeRight.scale(Math.cos(radialScaleR2)/2*width));

                //vertexA = vertexA.add(anim1);
                //vertexB = vertexB.add(anim1);
                //vertexC = vertexC.add(anim1);
                //vertexD = vertexD.add(anim1);
                RenderHelper.DoQuad(buffer, poseStack.last().pose(),
                        vertexA, vertexB, vertexC, vertexD,
                        new Vec2(radialScale*UVXScale + UVXOff,  alongScale*UVYScale/2 + UVYOff),
                        new Vec2(radialScale*UVXScale + UVXOff,  lastAlongScale*UVYScale/2 + UVYOff),
                        new Vec2(radialScale2*UVXScale + UVXOff, lastAlongScale*UVYScale/2 + UVYOff),
                        new Vec2(radialScale2*UVXScale + UVXOff, alongScale*UVYScale/2 + UVYOff),
                        combinedLight);
            }

            lastDir = lerpedDir;
            lastSegmentCenter = finalSegmentCenter;
            lastAlongScale = alongScale;

            lN = N;
            lCircleRelativeRight = circleRelativeRight;
            lCircleRelativeUp = circleRelativeUp;
        }
    }

    private Vec3 AnimateCenter(Vec3 center, Vec3 normal, float animTime, float progTotal)
    {
        Vec3 anim1 = new Vec3(
                (float)Math.sin(animTime + progTotal),
                (float)Math.cos(animTime + progTotal),
                (float)Math.sin(animTime + progTotal)
        );
        Vec3 anim2 = new Vec3(
                (float)Math.sin(animTime + progTotal*5),
                (float)Math.cos(animTime + progTotal*5),
                (float)Math.sin(animTime + progTotal*5)
        );
        return  center.add(anim1.scale(0.01f)).add(anim2.scale(0.01f));
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