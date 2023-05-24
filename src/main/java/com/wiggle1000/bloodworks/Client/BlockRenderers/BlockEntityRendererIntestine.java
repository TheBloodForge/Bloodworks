package com.wiggle1000.bloodworks.Client.BlockRenderers;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.wiggle1000.bloodworks.Blocks.BlockEntities.BlockEntityIntestine;
import com.wiggle1000.bloodworks.Globals;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class BlockEntityRendererIntestine implements BlockEntityRenderer<BlockEntityIntestine> //TODO: specify blockentity
{

    private BlockEntityRendererProvider.Context context;
    public BlockEntityRendererIntestine(BlockEntityRendererProvider.Context context)
    {
        this.context = context;
    }

    private void doTriangle(BufferBuilder builder, Matrix4f matrix, Vec3 a, Vec3 b, Vec3 c, Vec2 uvA, Vec2 uvB, Vec2 uvC)
    {
        builder.vertex(matrix, (float)a.x, (float)a.y, (float)a.z).color(255, 255, 255, 255).uv(uvA.x, uvA.y).endVertex();
        builder.vertex(matrix, (float)b.x, (float)b.y, (float)b.z).color(255, 255, 255, 255).uv(uvB.x, uvB.y).endVertex();
        builder.vertex(matrix, (float)c.x, (float)c.y, (float)c.z).color(255, 255, 255, 255).uv(uvC.x, uvC.y).endVertex();
    }
    private static final Matrix3f IDENTITY_NORMAL = new Matrix3f();
    @Override
    public void render(BlockEntityIntestine ent, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay)
    {
        poseStack.pushPose();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        BlockPos blockPos = ent.getBlockPos();

        RenderHelper.StartRenderingTris(buffer, new ResourceLocation(Globals.MODID, "textures/blocks/block_intestine.png"));
        //GL11.glBindTexture(GL31.GL_TEXTURE_BUFFER, 2);//Minecraft.getInstance().getTextureManager().getTexture(new ResourceLocation(Globals.MODID, "textures/blocks/block_intestine.png")).getId());
        Vec3 a = new Vec3(0, 1, 0);
        Vec3 b = new Vec3(0, 1, 1);
        Vec3 c = new Vec3(0, 0, 0);
        Vec2 uvA = new Vec2(0, 1);
        Vec2 uvB = new Vec2(1, 1);
        Vec2 uvC = new Vec2(0, 0);
        doTriangle(buffer, poseStack.last().pose(), a, b, c, uvA, uvB, uvC);
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
