package com.wiggle1000.bloodworks.Client.BlockRenderers;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.wiggle1000.bloodworks.Blocks.BlockEntities.BlockEntityIntestine;
import com.wiggle1000.bloodworks.Config.BloodworksCommonConfig;
import com.wiggle1000.bloodworks.Globals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class BlockEntityRendererIntestine implements BlockEntityRenderer<BlockEntityIntestine> //TODO: specify blockentity
{

    private BlockEntityRendererProvider.Context context;
    public BlockEntityRendererIntestine(BlockEntityRendererProvider.Context context)
    {
        this.context = context;
    }


    private boolean checkLOS(BlockPos blockPos, Vec3 offset)
    {
        BlockHitResult hr = Minecraft.getInstance().level.clip(new ClipContext(Minecraft.getInstance().cameraEntity.getEyePosition(), new Vec3(blockPos.getX() + offset.x, blockPos.getY() + offset.y, blockPos.getZ() + offset.z), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, Minecraft.getInstance().player));
        if(hr.getType() != HitResult.Type.MISS && Minecraft.getInstance().level.getBlockState(hr.getBlockPos()).canOcclude())
        {
            return false;
        }
        return true;
    }

    @Override
    public void render(BlockEntityIntestine ent, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay)
    {
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


        BlockHitResult hr = ent.getLevel().clip(new ClipContext(Minecraft.getInstance().cameraEntity.getEyePosition(), new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, Minecraft.getInstance().player));

        poseStack.pushPose();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        long totalTicks = Minecraft.getInstance().level.getGameTime();

        RenderHelper.StartRenderingTris(buffer, new ResourceLocation(Globals.MODID, "textures/blocks/block_intestine.png"));
        //GL11.glBindTexture(GL31.GL_TEXTURE_BUFFER, 2);//Minecraft.getInstance().getTextureManager().getTexture(new ResourceLocation(Globals.MODID, "textures/blocks/block_intestine.png")).getId());

        int numRings = 20;
        int numSegments = 20;
        boolean renderInside = true;

        int cLight = combinedLight;


        if(Minecraft.getInstance().cameraEntity.blockPosition().distManhattan(blockPos) > 30)
        {
            numRings = 1;
            numSegments = 3;
            renderInside = false;
        }
        else if(Minecraft.getInstance().cameraEntity.blockPosition().distManhattan(blockPos) > 25)
        {
            numRings = 2;
            numSegments = 4;
            renderInside = false;
        }
        else if(Minecraft.getInstance().cameraEntity.blockPosition().distManhattan(blockPos) > 15)
        {
            numRings = 3;
            numSegments = 6;
            renderInside = false;
        }
        else if(Minecraft.getInstance().cameraEntity.blockPosition().distManhattan(blockPos) > 8)
        {
            numRings = 4;
            numSegments = 8;
        }
        else if(Minecraft.getInstance().cameraEntity.blockPosition().distManhattan(blockPos) > 4)
        {
            numRings = 8;
            numSegments = 10;
        }

        double blockOffsetMagnitude = blockPos.getX() + blockPos.getY() + blockPos.getZ();
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
                double basalX = Math.sin(r1) / 2 * (thickness + radialScrumble) + 0.5;
                double basalZ = Math.cos(r1) / 2 * (thickness - radialScrumble) + 0.5;
                double nextX =  Math.sin(r2) / 2 * (thickness + radialScrumble) + 0.5;
                double nextZ =  Math.cos(r2) / 2 * (thickness - radialScrumble) + 0.5;
                double basalXupper = Math.sin(r1) / 2 * (thicknessUpper + radialScrumbleUpper) + 0.5;
                double basalZupper = Math.cos(r1) / 2 * (thicknessUpper - radialScrumbleUpper) + 0.5;
                double nextXupper =  Math.sin(r2) / 2 * (thicknessUpper + radialScrumbleUpper) + 0.5;
                double nextZupper =  Math.cos(r2) / 2 * (thicknessUpper - radialScrumbleUpper) + 0.5;
                Vec3 a = new Vec3(basalX, basalY, basalZ);
                Vec3 b = new Vec3(basalXupper, nextY, basalZupper);
                Vec3 c = new Vec3(nextXupper, nextY, nextZupper);
                Vec3 d = new Vec3(nextX, basalY, nextZ);
                Vec2 uvA = new Vec2((float)U1, (float)basalY/2);
                Vec2 uvB = new Vec2((float)U1, (float)nextY/2);
                Vec2 uvC = new Vec2((float)U2, (float)nextY/2);
                Vec2 uvD = new Vec2((float)U2, (float)basalY/2);

                RenderHelper.DoQuad(buffer, poseStack.last().pose(), a, b, c, d, uvA, uvB, uvC, uvD, 255);//cLight + 30);
                if(renderInside)
                {
                    RenderHelper.DoQuad(buffer, poseStack.last().pose(), d, c, b, a, uvD, uvC, uvB, uvA, 255);//cLight + 30);
                    //Idea: use UV offset to add silia texture to inside?
                }
            }

        }
        RenderHelper.FinishRendering(buffer);

        poseStack.popPose();
    }

    @Override
    public boolean shouldRender(BlockEntityIntestine p_173568_, Vec3 p_173569_)
    {
        return true;
    }

    @Override
    public boolean shouldRenderOffScreen(BlockEntityIntestine p_112306_)
    {
        return true;
    }
}
