package com.bloodforge.bloodworks.Client.BlockRenderers;

import com.bloodforge.bloodworks.Blocks.BlockEntities.BE_StirlingGenerator;
import com.bloodforge.bloodworks.Blocks.BlockMachineBase;
import com.bloodforge.bloodworks.Globals;
import com.bloodforge.bloodworks.Util;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
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
        /*if(ent.getBlockState().getValue(BlockMachineBase.FACING).getAxis() == Direction.Axis.Z)
        {
            poseStack.mulPose(Quaternion.fromXYZDegrees(new Vector3f(0, 90, 0)));
            poseStack.translate(-1, 0, 0);
        }*/

        ent.clientAnimTime += partialTicks * (ent.energyGenerationF * 0.003f);
        if(ent.clientAnimTime > 1)
        {
            ent.clientAnimTime -= 1;
        }

        boolean isTopBlock = Minecraft.getInstance().level.getBlockState(ent.getBlockPos().below()).is(ent.getBlockState().getBlock());
        VertexConsumer vertexBuilder = RenderHelper.StartRenderingCutout(bufferSource, new ResourceLocation(Globals.MODID, "textures/blocks/block_braincase_airlock_door.png"));

        Vec3 wheelCenter = Vec3.ZERO;
        float wheelRadius = 1;
        Vec3 right = new Vec3(1, 0, 0);
        Vec3 up = new Vec3(0, 1, 0);
        Vec3 forward = new Vec3(0, 0, 1);
        Vec3 V1 = GetRotatingVertex((ent.clientAnimTime*360f)+0, wheelCenter, forward, up, right, new Vec3(0, wheelRadius, wheelRadius));
        Vec3 V2 = GetRotatingVertex((ent.clientAnimTime*360f)+90, wheelCenter, forward, up, right, new Vec3(0, wheelRadius, wheelRadius));
        Vec3 V3 = GetRotatingVertex((ent.clientAnimTime*360f)+180, wheelCenter, forward, up, right, new Vec3(0, wheelRadius, wheelRadius));
        Vec3 V4 = GetRotatingVertex((ent.clientAnimTime*360f)+270, wheelCenter, forward, up, right, new Vec3(0, wheelRadius, wheelRadius));
        Vec2 UV1 = new Vec2(0, 0);
        Vec2 UV2 = new Vec2(1, 0);
        Vec2 UV3 = new Vec2(1, 1);
        Vec2 UV4 = new Vec2(0, 1);

        RenderHelper.DoQuad(vertexBuilder, poseStack.last().pose(), V4, V3, V2, V1, UV1, UV2, UV3, UV4, combinedLight);
        poseStack.popPose();

        Minecraft.getInstance().getProfiler().pop();
    }

    public Vec3 GetRotatingVertex(float degrees, Vec3 center, Vec3 forward, Vec3 up, Vec3 right, Vec3 offset)
    {
        float rad = (float)Math.toRadians(degrees);
        return offset.add(forward.scale(Math.sin(rad)).scale(offset.z))
                     .add(up.scale(Math.cos(rad)).scale(offset.y))
                     .add(right.scale(offset.x));
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