package com.bloodforge.bloodworks.Client.BlockRenderers;

import com.bloodforge.bloodworks.Blocks.BlockEntities.BE_AirlockDoor;
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
public class BER_AirlockDoor implements BlockEntityRenderer<BE_AirlockDoor>
{

    private final BlockEntityRendererProvider.Context context;

    public BER_AirlockDoor(BlockEntityRendererProvider.Context context)
    {
        this.context = context;
    }

    @Override
    public void render(BE_AirlockDoor ent, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay)
    {
        poseStack.pushPose();
        if(ent.isOpen)
        {
            ent.doorClosePercent = Util.Lerp(ent.doorClosePercent, 1, partialTicks/10f);
        }else
        {
            ent.doorClosePercent = Util.Lerp(ent.doorClosePercent, 0, partialTicks/10f);
        }
        if(ent.getBlockState().getValue(BlockMachineBase.FACING).getAxis() == Direction.Axis.Z)
        {
            poseStack.mulPose(Quaternion.fromXYZDegrees(new Vector3f(0, 90, 0)));
            poseStack.translate(-1, 0, 0);
        }
        ModelBlockRenderer mbr = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
        VertexConsumer vertexBuilder = RenderHelper.StartRenderingCutout(bufferSource, new ResourceLocation(Globals.MODID, "textures/blocks/block_braincase_airlock_door.png"));
        float xMin = 4f/16f;
        float xMax = 12f/16f;
        float zMin = 0f;
        float zMax = 1f;
        float yMin = 0f;
        float yMax = 2/16f;
        Vec3 BL  = new Vec3(xMin, yMin, zMin);
        Vec3 BR  = new Vec3(xMax, yMin, zMin);
        Vec3 FL  = new Vec3(xMin, yMin, zMax);
        Vec3 FR  = new Vec3(xMax, yMin, zMax);
        Vec3 BLT = new Vec3(xMin, yMax, zMin);
        Vec3 BRT = new Vec3(xMax, yMax, zMin);
        Vec3 FLT = new Vec3(xMin, yMax, zMax);
        Vec3 FRT = new Vec3(xMax, yMax, zMax);

        Vec2 F_UVNN = new Vec2(1/2f, 1);
        Vec2 F_UVPN = new Vec2(1,    1);
        Vec2 F_UVNP = new Vec2(1/2f, 30/32f);
        Vec2 F_UVPP = new Vec2(1,    30/32f);

        Vec2 S_UVNN = new Vec2(8/32f,   1);
        Vec2 S_UVPN = new Vec2(1/2f,    1);
        Vec2 S_UVNP = new Vec2(8/32f,   30/32f);
        Vec2 S_UVPP = new Vec2(1/2f,    30/32f);

        Vec2 T_UVNN = new Vec2(16/32f,  22/32f);
        Vec2 T_UVPN = new Vec2(16/32f,  30/32f);
        Vec2 T_UVNP = new Vec2(1,       22/32f);
        Vec2 T_UVPP = new Vec2(1,       30/32f);

        Vector4f color = new Vector4f(1f, 1f, 1f, 1f);

        Matrix4f matrix = poseStack.last().pose();
        //------------------------------- Bottom Bit ------------------------------------
        //up
        RenderHelper.DoQuadWithColor(vertexBuilder, matrix, BRT, FRT, FLT, BLT, T_UVPN, T_UVPP, T_UVNP, T_UVNN, combinedLight, color);
        //down
        //RenderHelper.DoQuadWithColor(vertexBuilder, matrix, BL, FL, FR, BR, T_UVPN, T_UVPP, T_UVNP, T_UVNN, combinedLight, color);
        // N
        RenderHelper.DoQuadWithColor(vertexBuilder, matrix, BR, BRT, BLT, BL, S_UVPN, S_UVPP, S_UVNP, S_UVNN, combinedLight, color);
        // S
        RenderHelper.DoQuadWithColor(vertexBuilder, matrix, FL, FLT, FRT, FR, S_UVPN, S_UVPP, S_UVNP, S_UVNN, combinedLight, color);
        // W
        RenderHelper.DoQuadWithColor(vertexBuilder, matrix, BL, BLT, FLT, FL, F_UVPN, F_UVPP, F_UVNP, F_UVNN, combinedLight, color);
        // E
        RenderHelper.DoQuadWithColor(vertexBuilder, matrix, FR, FRT, BRT, BR, F_UVPN, F_UVPP, F_UVNP, F_UVNN, combinedLight, color);


        //------------------------------- Lower Door -----------------------------------
        xMin = 5f/16f;
        xMax = 11f/16f;
        zMin = 0f + 0.0002f;
        zMax = 1f - 0.0002f;
        yMin = -0.95f + (ent.doorClosePercent * 0.63f);
        yMax = 0f + (ent.doorClosePercent * 0.63f);

        BL  = new Vec3(xMin, yMin, zMin);
        BR  = new Vec3(xMax, yMin, zMin);
        FL  = new Vec3(xMin, yMin, zMax);
        FR  = new Vec3(xMax, yMin, zMax);
        BLT = new Vec3(xMin, yMax, zMin);
        BRT = new Vec3(xMax, yMax, zMin);
        FLT = new Vec3(xMin, yMax, zMax);
        FRT = new Vec3(xMax, yMax, zMax);

        F_UVNN = new Vec2(0,    22/32f);
        F_UVPN = new Vec2(1/2f, 22/32f);
        F_UVNP = new Vec2(0,    6/32f);
        F_UVPP = new Vec2(1/2f, 6/32f);

        S_UVNN = new Vec2(5/32f,  7/32f);
        S_UVPN = new Vec2(11/32f, 7/32f);
        S_UVNP = new Vec2(5/32f,  22/32f);
        S_UVPP = new Vec2(11/32f, 22/32f);

        T_UVNN = new Vec2(0,    6/32f);
        T_UVPN = new Vec2(0,    0);
        T_UVNP = new Vec2(1/2f, 6/32f);
        T_UVPP = new Vec2(1/2f, 0);

        //up
        RenderHelper.DoQuadWithColor(vertexBuilder, matrix, BRT, FRT, FLT, BLT, T_UVPN, T_UVPP, T_UVNP, T_UVNN, combinedLight, color);
        //down
        //RenderHelper.DoQuadWithColor(vertexBuilder, matrix, BL, FL, FR, BR, T_UVPN, T_UVPP, T_UVNP, T_UVNN, combinedLight, color);
        // N
        RenderHelper.DoQuadWithColor(vertexBuilder, matrix, BR, BRT, BLT, BL, S_UVPN, S_UVPP, S_UVNP, S_UVNN, combinedLight, color);
        // S
        RenderHelper.DoQuadWithColor(vertexBuilder, matrix, FL, FLT, FRT, FR, S_UVPN, S_UVPP, S_UVNP, S_UVNN, combinedLight, color);
        // W
        RenderHelper.DoQuadWithColor(vertexBuilder, matrix, BL, BLT, FLT, FL, F_UVPN, F_UVPP, F_UVNP, F_UVNN, combinedLight, color);
        // E
        RenderHelper.DoQuadWithColor(vertexBuilder, matrix, FR, FRT, BRT, BR, F_UVPN, F_UVPP, F_UVNP, F_UVNN, combinedLight, color);

        //------------------------------- Upper Door -----------------------------------
        xMin = 6f/16f;
        xMax = 10f/16f;
        zMin = 0f + 0.0004f;
        zMax = 1f - 0.0004f;
        yMin = -0.95f + (ent.doorClosePercent * 0.95f);
        yMax = 0.05f + (ent.doorClosePercent * 0.95f);

        BL  = new Vec3(xMin, yMin, zMin);
        BR  = new Vec3(xMax, yMin, zMin);
        FL  = new Vec3(xMin, yMin, zMax);
        FR  = new Vec3(xMax, yMin, zMax);
        BLT = new Vec3(xMin, yMax, zMin);
        BRT = new Vec3(xMax, yMax, zMin);
        FLT = new Vec3(xMin, yMax, zMax);
        FRT = new Vec3(xMax, yMax, zMax);

        F_UVNN = new Vec2(1/2f, 20/32f);
        F_UVPN = new Vec2(1,    20/32f);
        F_UVNP = new Vec2(1/2f, 4/32f);
        F_UVPP = new Vec2(1,    4/32f);

        S_UVNN = new Vec2(5/32f,  7/32f);
        S_UVPN = new Vec2(11/32f, 7/32f);
        S_UVNP = new Vec2(5/32f,  22/32f);
        S_UVPP = new Vec2(11/32f, 22/32f);

        T_UVNN = new Vec2(1/2f, 4/32f);
        T_UVPN = new Vec2(1/2f, 0);
        T_UVNP = new Vec2(1,    4/32f);
        T_UVPP = new Vec2(1,    0);

        //up
        RenderHelper.DoQuadWithColor(vertexBuilder, matrix, BRT, FRT, FLT, BLT, T_UVPN, T_UVPP, T_UVNP, T_UVNN, combinedLight, color);
        //down
        //RenderHelper.DoQuadWithColor(vertexBuilder, matrix, BL, FL, FR, BR, T_UVPN, T_UVPP, T_UVNP, T_UVNN, combinedLight, color);
        // N
        RenderHelper.DoQuadWithColor(vertexBuilder, matrix, BR, BRT, BLT, BL, S_UVPN, S_UVPP, S_UVNP, S_UVNN, combinedLight, color);
        // S
        RenderHelper.DoQuadWithColor(vertexBuilder, matrix, FL, FLT, FRT, FR, S_UVPN, S_UVPP, S_UVNP, S_UVNN, combinedLight, color);
        // W
        RenderHelper.DoQuadWithColor(vertexBuilder, matrix, BL, BLT, FLT, FL, F_UVPN, F_UVPP, F_UVNP, F_UVNN, combinedLight, color);
        // E
        RenderHelper.DoQuadWithColor(vertexBuilder, matrix, FR, FRT, BRT, BR, F_UVPN, F_UVPP, F_UVNP, F_UVNN, combinedLight, color);

        poseStack.popPose();
    }

    @Override
    public boolean shouldRender(BE_AirlockDoor p_173568_, Vec3 p_173569_)
    {
        return true;
    }

    @Override
    public boolean shouldRenderOffScreen(BE_AirlockDoor p_112306_)
    {
        return true;
    }
}