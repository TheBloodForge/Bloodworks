package com.wiggle1000.bloodworks.Client.Screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wiggle1000.bloodworks.Globals;
import com.wiggle1000.bloodworks.Server.Menus.InfusionChamberMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class InfusionChamberScreen extends AbstractContainerScreen<InfusionChamberMenu>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Globals.MODID, "textures/gui/machine/infusion_chamber.png");
    private FluidTankRenderer renderer;

    public InfusionChamberScreen(InfusionChamberMenu menu, Inventory playerInv, Component title)
    {
        super(menu, playerInv, title);
        this.leftPos = 0;
        this.topPos = 0;
        this.imageWidth = 176;
        this.imageHeight = 172;
        this.inventoryLabelY += 7;
    }

    @Override
    protected void init()
    {
        super.init();
        assignFluidRenderer();
    }

    private void assignFluidRenderer()
    {
        renderer = new FluidTankRenderer(6000, true, 16, 59, 0xFF990022);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float deltaInTicks, int mouseX, int mouseY)
    {
        float deltaTime = deltaInTicks * 50; // 50 ms per tick
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        blit(poseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 172);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderProgressArrow(poseStack, x, y, deltaTime);
        renderer.render(poseStack, x + 24, y + 18, menu.getFluidStack());
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.25f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        blit(poseStack, x + 23, y + 18, 176, 64, 16, 80, 256, 172);
    }

    int frame = 0;
    int pp = 0;

    private void renderProgressArrow(PoseStack pPoseStack, int x, int y, float deltaTime)
    {
        if (menu.isCrafting())
        {
            int progress = menu.getProgress();
            if (pp != progress)
            {
                frame++;
                pp = progress;
                if (frame > 4) frame = 0;
            }
            blit(pPoseStack, x + 65, y + 17, 224, 32 * frame, 22, 22, 256, 172);
            blit(pPoseStack, x + 50, y + 42, 176, 32, 15, menu.getScaledProgressBlood(), 256, 172);
            blit(pPoseStack, x + 72, y + 42, 176, 0, menu.getScaledProgressArrow(), 29, 256, 172);
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
    {
        super.render(poseStack, mouseX, mouseY, partialTicks);
        renderTooltip(poseStack, mouseX, mouseY);
    }
}