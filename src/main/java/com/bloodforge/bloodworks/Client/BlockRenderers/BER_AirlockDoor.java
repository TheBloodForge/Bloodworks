package com.bloodforge.bloodworks.Client.BlockRenderers;

import com.bloodforge.bloodworks.Blocks.BlockEntities.BE_AirlockDoor;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
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
        /*ModelBlockRenderer mbr = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
        VertexConsumer buff = RenderHelper.StartRenderingCutout(bufferSource, new ResourceLocation("textures/blocks/block_braincase_airlock_door.png"));
        float xOff = 4f/16f;
        float zOff = 0f;
        float height = 1/16f;
        Vec3 Bottom_BL  = new Vec3(xOff, 0, zOff);
        Vec3 Bottom_BR  = new Vec3();
        Vec3 Bottom_FL  = new Vec3();
        Vec3 Bottom_FR  = new Vec3();
        Vec3 Bottom_BLT = new Vec3();
        Vec3 Bottom_BRT = new Vec3();
        Vec3 Bottom_FLT = new Vec3();
        Vec3 Bottom_FRT = new Vec3();

        Vec2 UVNN = new Vec2(minU, minV);
        Vec2 UVPN = new Vec2(maxU, minV);
        Vec2 UVNP = new Vec2(minU, maxV);
        Vec2 UVPP = new Vec2(maxU, maxV);*/
        
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