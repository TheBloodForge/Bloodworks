package com.bloodforge.bloodworks.Client;

import com.bloodforge.bloodworks.Client.Screens.SelectionMenuHudOverlay;
import com.bloodforge.bloodworks.Globals;
import com.bloodforge.bloodworks.Server.TankDataProxy;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeClientEvents
{
    @SubscribeEvent
    public void onScreenDraw(ScreenEvent.Opening event)
    {
        if (event.getScreen() instanceof TitleScreen)
        {
            TankDataProxy.TankDataTag = new CompoundTag();
            TankDataProxy.MASTER_TANK_CONTAINER.clear();
            Globals.LogDebug("Resetting Client Tank Data", true);
        }
    }
    @SubscribeEvent
    public void onMouseScrollInput(InputEvent.MouseScrollingEvent event)
    {
        event.setCanceled(SelectionMenuHudOverlay.TryChangeSelection(event.getScrollDelta()));
    }
    @SubscribeEvent
    public void onMouseInput(InputEvent.InteractionKeyMappingTriggered event)
    {
        boolean isMenuInControl = SelectionMenuHudOverlay.isMenuInControl();
        if(event.isUseItem())
        {
            SelectionMenuHudOverlay.HandleSelect();
        }
        else if (event.isAttack())
        {
            SelectionMenuHudOverlay.HandleCancel();
        }
        event.setCanceled(isMenuInControl);
    }
}