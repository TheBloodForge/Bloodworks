package com.bloodforge.bloodworks.Client;

import com.bloodforge.bloodworks.Globals;
import com.bloodforge.bloodworks.Server.TankDataProxy;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.nbt.CompoundTag;
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
}