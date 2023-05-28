package com.wiggle1000.bloodworks.Client.BlockRenderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector4f;
import com.wiggle1000.bloodworks.Blocks.BlockEntities.BE_Neuron;
import com.wiggle1000.bloodworks.Config.BloodworksCommonConfig;
import com.wiggle1000.bloodworks.Globals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class BER_Neuron implements BlockEntityRenderer<BE_Neuron>
{

    public static final float NEURON_SIZE = 1f;
    private final BlockEntityRendererProvider.Context context;
    public BER_Neuron(BlockEntityRendererProvider.Context context)
    {
        this.context = context;
    }


    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean checkLOS(BlockPos blockPos, Vec3 offset)
    {
        BlockHitResult hr = Minecraft.getInstance().level.clip(new ClipContext(Minecraft.getInstance().cameraEntity.getEyePosition(), new Vec3(blockPos.getX() + offset.x, blockPos.getY() + offset.y, blockPos.getZ() + offset.z), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, Minecraft.getInstance().player));
        return hr.getType() == HitResult.Type.MISS || !Minecraft.getInstance().level.getBlockState(hr.getBlockPos()).canOcclude();
    }

    @Override
    public void render(BE_Neuron ent, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay)
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


        poseStack.pushPose();

        long totalTicks = Minecraft.getInstance().level.getGameTime();

        //TODO: why no transparent :(
        VertexConsumer buffer = RenderHelper.StartRenderingTranslucentEmissive(bufferSource, new ResourceLocation(Globals.MODID, "textures/blocks/block_neuron.png"));
        poseStack.translate(0.5,0.5,0.5);

        DrawNeuron(buffer, poseStack, totalTicks, combinedLight, blockPos);

        RenderHelper.FinishRendering(buffer);

        poseStack.popPose();
    }

    private void OrientFromDirection(PoseStack poseStack, Direction fromDir)
    {
        poseStack.mulPose(fromDir.getRotation());
    }


    private void DrawNeuron(VertexConsumer buffer, PoseStack poseStack, long animTime, int combinedLight, BlockPos center)
    {
        int neuronFrame = (int)(animTime/10f)%4;
        poseStack.pushPose();
        for(int i = 1; i <= 5; i++)
        {

            Vec3 neuronCenter = Vec3.ZERO;//new Vec3(center.getX(), center.getY(), center.getZ()).add(0.5, 0.5, 0.5);
            Entity cameraEntity = Minecraft.getInstance().cameraEntity;
            Vec3 up             = cameraEntity.getUpVector(1.0F);
            Vec3 forward        = cameraEntity.getViewVector(1.0F);
            Vec3 right          = forward.cross(up).normalize().scale(-1);

            Vec3 vertexA = neuronCenter.add(up.scale(-NEURON_SIZE/2)).add(right.scale( NEURON_SIZE/2));
            Vec3 vertexB = neuronCenter.add(up.scale( NEURON_SIZE/2)).add(right.scale( NEURON_SIZE/2));
            Vec3 vertexC = neuronCenter.add(up.scale( NEURON_SIZE/2)).add(right.scale(-NEURON_SIZE/2));
            Vec3 vertexD = neuronCenter.add(up.scale(-NEURON_SIZE/2)).add(right.scale(-NEURON_SIZE/2));

            RenderHelper.DoQuadWithColor(buffer, poseStack.last().pose(),
                    vertexA, vertexB, vertexC, vertexD,
                    new Vec2(1, 0.25f * (neuronFrame+1)),
                    new Vec2(1, 0.25f * neuronFrame),
                    new Vec2(0, 0.25f * neuronFrame),
                    new Vec2(0, 0.25f * (neuronFrame+1)),
                    combinedLight,
                    new Vector4f(1f, 1f, 1f, 0.5f));

        }
        poseStack.popPose();
    }


    private void DrawAxonTo(VertexConsumer buffer, PoseStack poseStack, long animTime, int combinedLight, Vec3 fromPos, Vec3 toPos)
    {

    }

    @Override
    public boolean shouldRender(BE_Neuron p_173568_, Vec3 p_173569_)
    {
        return true;
    }

    @Override
    public boolean shouldRenderOffScreen(BE_Neuron p_112306_)
    {
        return true;
    }
}