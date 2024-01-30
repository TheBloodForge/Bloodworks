package com.bloodforge.bloodworks.Client.Screens;

import com.bloodforge.bloodworks.Globals;
import com.bloodforge.bloodworks.Networking.PacketManager;
import com.bloodforge.bloodworks.Networking.SelectionMenuC2SPacket;
import com.bloodforge.bloodworks.Registry.SoundRegistry;
import com.bloodforge.bloodworks.Util.SelectionMenuOptions;
import com.bloodforge.bloodworks.Util.Util;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class SelectionMenuHudOverlay implements IGuiOverlay
{
    public static final ResourceLocation MENU_BG_IMAGE = new ResourceLocation(Globals.MODID, "textures/gui/neuron.png");

    private static Vec2 lastScreenPos = Vec2.ZERO;
    private static Vec2 menuVelocity = Vec2.ZERO;
    private float cMenuSizeScale = 0;

    public static boolean isMenuOpen = false;
    public static SelectionMenuOptions cMenu = SelectionMenuOptions.DEFAULT;
    public static Vec3 targetPosition = new Vec3(-56.5, 80.5, 4.5);
    public static BlockPos targetBlock;
    public static int menuSelectionIndex = 0;
    public static int cMenuWidth = 100;
    public static double menuCooldown = 0;

    public static boolean isMenuInControl()
    {
        return isMenuOpen || menuCooldown > 0;
    }

    public static void OpenMenu(SelectionMenuOptions menu, BlockPos position, int initialSelection)
    {
        cMenu = menu;
        targetBlock = position;
        targetPosition = new Vec3(position.getX() + 0.5F, position.getY() + 0.5F, position.getZ() + 0.5F);
        isMenuOpen = true;
        menuVelocity = Vec2.ZERO;
        menuSelectionIndex = initialSelection;
        cMenuWidth = 0;
        for(SelectionMenuOptions.SelectionMenuEntry e : menu.entries)
        {
            int width = Minecraft.getInstance().font.width(e.label.getString());
            if(width > cMenuWidth)
                cMenuWidth = width;
        }
        cMenuWidth += 20;
    }
    public static int CloseMenu()
    {
        isMenuOpen = false;
        menuCooldown = 5f;
        return menuSelectionIndex;
    }

    public static boolean TryChangeSelection(double delta)
    {
        if(!isMenuOpen) return false;
        int prevIndex = menuSelectionIndex;
        menuSelectionIndex = Math.floorMod((int)(menuSelectionIndex-delta),cMenu.entries.size());
        if(prevIndex != menuSelectionIndex)
        {
            Minecraft.getInstance().level.playLocalSound(targetBlock, SoundRegistry.SELECTOR_PIP.get(), SoundSource.PLAYERS, 1, 1, false);
            PacketManager.sendToServer(new SelectionMenuC2SPacket(menuSelectionIndex, false, false, targetBlock));
        }
        return isMenuOpen;
    }
    public static void HandleSelect()
    {
        if(!isMenuOpen) return;
        PacketManager.sendToServer(new SelectionMenuC2SPacket(menuSelectionIndex, true, false, targetBlock));
    }
    public static void HandleCancel()
    {
        if(!isMenuOpen) return;
        CloseMenu();
        PacketManager.sendToServer(new SelectionMenuC2SPacket(menuSelectionIndex, false, true, targetBlock));
    }

    private void drawItem(PoseStack poseStack, float x, float y, boolean selected, Component component)
    {
        if(selected)
        {
            TextColor c = component.getStyle().getColor();
            int selectColor = 0xFF00FFAA;
            if(c != null)
                selectColor = c.getValue()|0xFF000000;//HudUtils.blendARGB8Colors(c.getValue(), 0xFF222233, 0.9F);
            //selection bg
            HudUtils.DrawNineSlice(poseStack, MENU_BG_IMAGE, new Vec2(64, 64), new Vec2(x, y).add(new Vec2(5, 0)), new Vec2(cMenuWidth-10, 10).scale(cMenuSizeScale), new Vec2(5, 4), new Vec2(3, 33), new Vec2(3+26, 33+8), selectColor);
            Minecraft.getInstance().font.draw(poseStack, component.copy().withStyle(ChatFormatting.RESET), x + 10, y+1 * cMenuSizeScale, 0xFF222233);
        }
        else
        {
            Minecraft.getInstance().font.draw(poseStack, component, x + 5, y+1 * cMenuSizeScale, 0xFF00FFAA);
        }
    }

    @Override
    public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight)
    {
        Pair<Vec2, Boolean> projected = HudUtils.projectToPlayerView(targetPosition.x, targetPosition.y, targetPosition.z, partialTick, screenWidth, screenHeight);
        boolean isMenuInRange = targetPosition.distanceTo(Minecraft.getInstance().player.position()) < 10;
        float dt = Minecraft.getInstance().getDeltaFrameTime();
        if(menuCooldown > 0){
            menuCooldown -= dt;
        }
        Pair<Vec2, Vec2> springResult = Util.SpringInterpolate(lastScreenPos, new Vec2(projected.first().x + 10, projected.first().y + 10), menuVelocity, 2f / (dt), 0.5f * dt);
        Vec2 targetPos = springResult.first();
        menuVelocity = springResult.second();
        int menuHeight = ((cMenu.entries.size()+1) * 10) - 2;
        if(projected.second() && cMenuSizeScale > 0.2)
        {

            //blit(pPoseStack, x + 65, y + 17, 224, 32 * frame, 22, 22, 256, 172);
            //
            Vec2 intTargetPos = new Vec2((int)targetPos.x, (int)targetPos.y);
            HudUtils.drawColoredLine(poseStack.last().pose(), projected.first().x, projected.first().y, intTargetPos.x + 2, intTargetPos.y + 2, 0.2f, 0.8f, 0xFF00FFAA);
            HudUtils.DrawNineSlice(poseStack, MENU_BG_IMAGE, new Vec2(64, 64), intTargetPos, new Vec2(cMenuWidth, menuHeight).scale(cMenuSizeScale), new Vec2(5, 5), new Vec2(0, 0), new Vec2(32, 32), 0xDDFFFFFF);
            //Minecraft.getInstance().font.drawShadow(poseStack, Component.literal("balls"), intTargetPos.x + 10, intTargetPos.y + 6, 0xFFAA55FF);
            if(cMenuSizeScale > 0.5)
            {
                drawItem(poseStack, intTargetPos.x-2, (intTargetPos.y -9), false, cMenu.titleLabel);
                for(int i = 0; i < cMenu.entries.size(); i++)
                {
                    drawItem(poseStack, intTargetPos.x, (intTargetPos.y + 4) + i*10, i==menuSelectionIndex, cMenu.entries.get(i).label);
                }
                //drawItem(poseStack, intTargetPos.x, intTargetPos.y, true);
                //Minecraft.getInstance().font.draw(poseStack, Component.literal("selected"), intTargetPos.x + 10, intTargetPos.y + 6 * cMenuSizeScale, 0xFF222233);

                //Minecraft.getInstance().font.draw(poseStack, Component.literal("balls"), intTargetPos.x + 5, intTargetPos.y + 16 * cMenuSizeScale, 0xFF00FFAA);
                //Minecraft.getInstance().font.draw(poseStack, Component.literal("whale..?"), intTargetPos.x + 5, intTargetPos.y + 26 * cMenuSizeScale, 0xFF00FFAA);
                //Minecraft.getInstance().font.draw(poseStack, Component.literal("salami"), intTargetPos.x + 5, intTargetPos.y + 36 * cMenuSizeScale, 0xFF00FFAA);
            }
        }
        if(isMenuOpen)
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
