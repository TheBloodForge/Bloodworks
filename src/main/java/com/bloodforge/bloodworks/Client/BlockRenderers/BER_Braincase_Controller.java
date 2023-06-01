package com.bloodforge.bloodworks.Client.BlockRenderers;

import com.bloodforge.bloodworks.Blocks.BlockEntities.BE_Braincase_Controller;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class BER_Braincase_Controller implements BlockEntityRenderer<BE_Braincase_Controller>
{

    private final BlockEntityRendererProvider.Context context;

    public BER_Braincase_Controller(BlockEntityRendererProvider.Context context)
    {
        this.context = context;
    }

    @Override
    public void render(BE_Braincase_Controller ent, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay)
    {
        Minecraft.getInstance().getProfiler().push("Bloodworks Braincase Renderer");
        Minecraft.getInstance().getProfiler().pop();
    }

    @Override
    public boolean shouldRender(BE_Braincase_Controller p_173568_, Vec3 p_173569_)
    {
        return true;
    }

    @Override
    public boolean shouldRenderOffScreen(BE_Braincase_Controller p_112306_)
    {
        return true;
    }
}