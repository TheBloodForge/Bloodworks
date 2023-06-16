package com.bloodforge.bloodworks.Client.Screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class HudUtils
{
    private static int currentBlitColor = 0xFFFFFFFF;

    public static void drawColoredLine(Matrix4f matrix, float v1X, float v1Y, float v2X, float v2Y, float thickness1, float thickness2, int color) {
        float f3 = (float)(color >> 24 & 255) / 255.0F;
        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        Vec2 v1Center = new Vec2(v1X, v1Y);//new Vec2(Math.min(v1X, v2X), Math.min(v1Y, v2Y));
        Vec2 v2Center = new Vec2(v2X, v2Y);//new Vec2(Math.max(v1X, v2X), Math.max(v1Y, v2Y));
        Vec2 lineRight = v2Center.add(v1Center.scale(-1f));
        lineRight = new Vec2(lineRight.y, -lineRight.x).normalized();
        Vec2 v1 = v1Center.add(lineRight.scale(thickness1));
        Vec2 v2 = v1Center.add(lineRight.scale(-thickness1));
        Vec2 v3 = v2Center.add(lineRight.scale(-thickness2));
        Vec2 v4 = v2Center.add(lineRight.scale(thickness2));
        bufferbuilder.vertex(matrix, (float)v1.x, (float)v1.y, (float)0).color(color).endVertex();   //.uv(0, 0)
        bufferbuilder.vertex(matrix, (float)v2.x, (float)v2.y, (float)0).color(color).endVertex();   //.uv(0, 1)
        bufferbuilder.vertex(matrix, (float)v3.x, (float)v3.y, (float)0).color(color).endVertex();   //.uv(1, 1)
        bufferbuilder.vertex(matrix, (float)v4.x, (float)v4.y, (float)0).color(color).endVertex();   //.uv(1, 0)
        BufferUploader.drawWithShader(bufferbuilder.end());
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public static Pair<Vec2, Boolean> projectToPlayerView(double target_x, double target_y, double target_z, float partialTicks, float scaledWidth, float scaledHeight) {

        Minecraft mc = Minecraft.getInstance();
        float dt = mc.getDeltaFrameTime();
        /* The (centered) location on the screen of the given 3d point in the world.
         * Result is <dist right of center screen, dist up from center screen, is target in front of viewing plane> */
        Vec3 camera_pos = mc.gameRenderer.getMainCamera().getPosition();
        Quaternion camera_rotation_conj = mc.gameRenderer.getMainCamera().rotation();
        camera_rotation_conj.conj();

        Vector3f result3f = new Vector3f(
                (float) (camera_pos.x - target_x),
                (float) (camera_pos.y - target_y),
                (float) (camera_pos.z - target_z));
        result3f.transform(camera_rotation_conj);


        // ----- adjust for fov -----
        float fov = mc.options.fov().get(); //?????
        GameRenderer gameRenderer = mc.gameRenderer;
        if(mc.getCameraEntity() instanceof AbstractClientPlayer abstractClientPlayer)
        {
            fov *= abstractClientPlayer.getFieldOfViewModifier();
        }
        //fov = Mth.lerp(dt, NeuronConnectionHudOverlay.lastFOV, fov);

        //float fixer = Mth.lerp((fov - 30)/80f, 2.5f, 3f);

        float f = (float)(1.75D / Math.tan(Math.toRadians(70+(70-fov)) / 2f));  //ugh whatever, good enough projection across FOV range
        //System.out.println(f);
        //f = Mth.lerp((f - 0.7f)/3.032f, 1.035f, 5.671f);

        float half_height = (float) scaledHeight * 2;
        float scale_factor = half_height / (result3f.z() * f);

        Vec2 targetPos = new Vec2(-result3f.x() * scale_factor + scaledWidth/2, -result3f.y() * scale_factor + scaledHeight/2);
        return Pair.of(targetPos, result3f.z() < 0);
    }

    public static void BlitNicely(PoseStack poseStack, ResourceLocation imageLocation, Vec2 imageSize, Vec2 position, Vec2 UVStart, Vec2 UVSize)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, imageLocation);
        GuiComponent.blit(poseStack, (int)position.x, (int)position.y, UVStart.x, UVStart.y, (int)UVSize.x, (int)UVSize.y, (int)imageSize.x, (int)imageSize.y);
    }
    public static void BlitNicelyScaled(PoseStack poseStack, ResourceLocation imageLocation, Vec2 imageSize, Vec2 position, Vec2 UVStart, Vec2 UVSize, Vec2 scale)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, imageLocation);
        GuiComponent.blit(poseStack, (int)position.x, (int)position.y, (int)scale.x, (int)scale.y, UVStart.x, UVStart.y, (int)UVSize.x, (int)UVSize.y, (int)imageSize.x, (int)imageSize.y);
    }

    public static void SetupBlit(ResourceLocation imageLocation, int color)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        currentBlitColor = color;
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, imageLocation);
    }

    public static void BlitNicelyNoSetup(PoseStack poseStack, Vec2 imageSize, Vec2 position, Vec2 UVStart, Vec2 UVSize)
    {
        blitNicely(poseStack, (int)position.x, (int)position.y, UVStart.x, UVStart.y, (int)UVSize.x, (int)UVSize.y, (int)imageSize.x, (int)imageSize.y);
    }
    public static void BlitNicelyScaledNoSetup(PoseStack poseStack, Vec2 imageSize, Vec2 position, Vec2 UVStart, Vec2 UVSize, Vec2 scale)
    {
        blitNicely(poseStack, (int)position.x, (int)position.y, (int)scale.x, (int)scale.y, UVStart.x, UVStart.y, (int)UVSize.x, (int)UVSize.y, (int)imageSize.x, (int)imageSize.y);
    }

    public static void blitNicely(PoseStack p_93134_, int p_93135_, int p_93136_, float p_93137_, float p_93138_, int p_93139_, int p_93140_, int p_93141_, int p_93142_) {
        blitNicely(p_93134_, p_93135_, p_93136_, p_93139_, p_93140_, p_93137_, p_93138_, p_93139_, p_93140_, p_93141_, p_93142_);
    }

    public static void blitNicely(PoseStack p_93161_, int p_93162_, int p_93163_, int p_93164_, int p_93165_, float p_93166_, float p_93167_, int p_93168_, int p_93169_, int p_93170_, int p_93171_) {
        innerBlitNicely(p_93161_, p_93162_, p_93162_ + p_93164_, p_93163_, p_93163_ + p_93165_, 0, p_93168_, p_93169_, p_93166_, p_93167_, p_93170_, p_93171_);
    }

    private static void innerBlitNicely(PoseStack p_93188_, int p_93189_, int p_93190_, int p_93191_, int p_93192_, int p_93193_, int p_93194_, int p_93195_, float p_93196_, float p_93197_, int p_93198_, int p_93199_) {
        innerBlitNicely(p_93188_.last().pose(), p_93189_, p_93190_, p_93191_, p_93192_, p_93193_, (p_93196_ + 0.0F) / (float)p_93198_, (p_93196_ + (float)p_93194_) / (float)p_93198_, (p_93197_ + 0.0F) / (float)p_93199_, (p_93197_ + (float)p_93195_) / (float)p_93199_);
    }

    private static void innerBlitNicely(Matrix4f p_93113_, int p_93114_, int p_93115_, int p_93116_, int p_93117_, int p_93118_, float p_93119_, float p_93120_, float p_93121_, float p_93122_) {
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex(p_93113_, (float)p_93114_, (float)p_93117_, (float)p_93118_).uv(p_93119_, p_93122_).color(currentBlitColor).endVertex();
        bufferbuilder.vertex(p_93113_, (float)p_93115_, (float)p_93117_, (float)p_93118_).uv(p_93120_, p_93122_).color(currentBlitColor).endVertex();
        bufferbuilder.vertex(p_93113_, (float)p_93115_, (float)p_93116_, (float)p_93118_).uv(p_93120_, p_93121_).color(currentBlitColor).endVertex();
        bufferbuilder.vertex(p_93113_, (float)p_93114_, (float)p_93116_, (float)p_93118_).uv(p_93119_, p_93121_).color(currentBlitColor).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
    }

    public static void DrawNineSlice(PoseStack stack, ResourceLocation texture, Vec2 imageSize, Vec2 position, Vec2 size, Vec2 cornerSize, Vec2 UVMin, Vec2 UVMax, int color)
    {
        SetupBlit(texture, color);

        Vec2 UVSize = UVMax.add(UVMin.scale(-1));
        //top left corner
        BlitNicelyNoSetup(stack, imageSize, position, UVMin, cornerSize);
        //top right corner
        BlitNicelyNoSetup(stack, imageSize, new Vec2(position.x + size.x - cornerSize.x, position.y), new Vec2(UVMax.x - cornerSize.x, UVMin.y), cornerSize);
        //bottom left corner
        BlitNicelyNoSetup(stack, imageSize, new Vec2(position.x, position.y + size.y - cornerSize.y), new Vec2(UVMin.x, UVMax.y - cornerSize.y), cornerSize);
        //bottom right corner
        BlitNicelyNoSetup(stack, imageSize, new Vec2(position.x + size.x - cornerSize.x, position.y + size.y - cornerSize.y), new Vec2(UVMax.x - cornerSize.x, UVMax.y - cornerSize.y), cornerSize);

        //top edge
        BlitNicelyScaledNoSetup(stack, imageSize, new Vec2(position.x + cornerSize.x, position.y), new Vec2(UVMin.x + cornerSize.x, UVMin.y), new Vec2(UVSize.x - cornerSize.x*2, cornerSize.y), new Vec2(size.x - cornerSize.x*2, cornerSize.y));
        //bottom edge
        BlitNicelyScaledNoSetup(stack, imageSize, new Vec2(position.x + cornerSize.x, position.y + size.y - cornerSize.y), new Vec2(UVMin.x + cornerSize.x, UVMax.y - cornerSize.y), new Vec2(UVSize.x - cornerSize.x*2, cornerSize.y), new Vec2(size.x - cornerSize.x*2, cornerSize.y));
        //left edge
        BlitNicelyScaledNoSetup(stack, imageSize, new Vec2(position.x, position.y + cornerSize.y), new Vec2(UVMin.x, UVMin.y + cornerSize.y), new Vec2(cornerSize.x, UVSize.y - cornerSize.y*2), new Vec2(cornerSize.x, size.y - cornerSize.y * 2));
        //right edge
        BlitNicelyScaledNoSetup(stack, imageSize, new Vec2(position.x + size.x - cornerSize.x, position.y + cornerSize.y), new Vec2(UVMax.x - cornerSize.x, UVMin.y + cornerSize.y), new Vec2(cornerSize.x, UVSize.y - cornerSize.y*2), new Vec2(cornerSize.x, size.y - cornerSize.y * 2));

        //center
        BlitNicelyScaledNoSetup(stack, imageSize, new Vec2(position.x + cornerSize.x, position.y + cornerSize.y), new Vec2(UVMin.x + cornerSize.x, UVMin.y + cornerSize.y), new Vec2(UVSize.x - cornerSize.x*2, UVSize.y - cornerSize.y*2), new Vec2(size.x - cornerSize.x*2,size.y - cornerSize.y * 2));

    }
}
