package com.bloodforge.bloodworks.Client.Screens;

import com.bloodforge.bloodworks.Globals;
import com.bloodforge.bloodworks.Util;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class NeuronConnectionHudOverlay implements IGuiOverlay
{
    public static final ResourceLocation MENU_BG_IMAGE = new ResourceLocation(Globals.MODID, "textures/gui/neuron.png");

    private static Vec2 lastScreenPos = Vec2.ZERO;
    private static Vec2 menuVelocity = Vec2.ZERO;
    private float cMenuSizeScale = 0;
    @Override
    public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight)
    {
        Vec3 targetPosition = new Vec3(-56.5, 80.5, 4.5);
        Pair<Vec2, Boolean> projected = HudUtils.projectToPlayerView(targetPosition.x, targetPosition.y, targetPosition.z, partialTick, screenWidth, screenHeight);

        boolean isMenuInRange = targetPosition.distanceTo(Minecraft.getInstance().player.position()) < 10;
        float dt = Minecraft.getInstance().getDeltaFrameTime();
        Pair<Vec2, Vec2> springResult = Util.SpringInterpolate(lastScreenPos, new Vec2(projected.first().x + 10, projected.first().y + 10), menuVelocity, 2f / (dt), 0.5f * dt);
        Vec2 targetPos = springResult.first();
        menuVelocity = springResult.second();

        if(projected.second() && cMenuSizeScale > 0.2)
        {

            //blit(pPoseStack, x + 65, y + 17, 224, 32 * frame, 22, 22, 256, 172);
            //
            Vec2 intTargetPos = new Vec2((int)targetPos.x, (int)targetPos.y);
            HudUtils.drawColoredLine(poseStack.last().pose(), projected.first().x, projected.first().y, intTargetPos.x + 2, intTargetPos.y + 2, 0.2f, 0.8f, 0xFF00FFAA);
            HudUtils.DrawNineSlice(poseStack, MENU_BG_IMAGE, new Vec2(64, 64), intTargetPos, new Vec2(70, 50).scale(cMenuSizeScale), new Vec2(5, 5), new Vec2(0, 0), new Vec2(32, 32), 0xDDFFFFFF);
            HudUtils.DrawNineSlice(poseStack, MENU_BG_IMAGE, new Vec2(64, 64), intTargetPos.add(new Vec2(5, 5)), new Vec2(60, 10).scale(cMenuSizeScale), new Vec2(5, 4), new Vec2(3, 33), new Vec2(3+26, 33+8), 0xFFFFFFFF);
            //Minecraft.getInstance().font.drawShadow(poseStack, Component.literal("balls"), intTargetPos.x + 10, intTargetPos.y + 6, 0xFFAA55FF);
            if(cMenuSizeScale > 0.5)
            {
                Minecraft.getInstance().font.draw(poseStack, Component.literal("selected"), intTargetPos.x + 10, intTargetPos.y + 6 * cMenuSizeScale, 0xFF222233);

                Minecraft.getInstance().font.draw(poseStack, Component.literal("balls"), intTargetPos.x + 5, intTargetPos.y + 16 * cMenuSizeScale, 0xFF00FFAA);
                Minecraft.getInstance().font.draw(poseStack, Component.literal("whale..?"), intTargetPos.x + 5, intTargetPos.y + 26 * cMenuSizeScale, 0xFF00FFAA);
                Minecraft.getInstance().font.draw(poseStack, Component.literal("salami"), intTargetPos.x + 5, intTargetPos.y + 36 * cMenuSizeScale, 0xFF00FFAA);
            }
        }
        if(isMenuInRange)
        {
            cMenuSizeScale = Util.Lerp(cMenuSizeScale, 1, dt);
            if(cMenuSizeScale > 0.95f) {cMenuSizeScale = 1;}
        }
        else
        {
            cMenuSizeScale = Util.Lerp(cMenuSizeScale, 0, dt);
            if(cMenuSizeScale < 0.05f) {cMenuSizeScale = 0;}
        }
        lastScreenPos = targetPos;
    }

}
