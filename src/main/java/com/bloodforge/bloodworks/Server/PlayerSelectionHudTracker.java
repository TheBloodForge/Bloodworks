package com.bloodforge.bloodworks.Server;

import com.bloodforge.bloodworks.Networking.PacketManager;
import com.bloodforge.bloodworks.Networking.SelectionMenuS2CPacket;
import com.bloodforge.bloodworks.Util.ISelectionMenuResponder;
import com.bloodforge.bloodworks.Util.SelectionMenuOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import oshi.util.tuples.Triplet;

import java.util.Hashtable;
import java.util.UUID;

public class PlayerSelectionHudTracker
{
    public static Hashtable<UUID, Triplet<BlockPos, SelectionMenuOptions, ISelectionMenuResponder>> openedMenus = new Hashtable<UUID, Triplet<BlockPos, SelectionMenuOptions, ISelectionMenuResponder>>();

    //TODO: PASS CURRENT SELECTION!
    public static void OpenAndTrackMenu(ServerPlayer player, SelectionMenuOptions options, BlockPos pos, int initialSelection, ISelectionMenuResponder responder)
    {
        if(openedMenus.containsKey(player.getUUID())) return;
        openedMenus.put(player.getUUID(), new Triplet<>(pos, options, responder));
        PacketManager.sendToPlayer(new SelectionMenuS2CPacket(options, pos, initialSelection), player);
    }

    public static void ClosePlayerMenu(ServerPlayer player)
    {
        PacketManager.sendToPlayer(new SelectionMenuS2CPacket(true), player);
        openedMenus.remove(player.getUUID());
    }

    public static boolean PlayerHasMenuOpen(ServerPlayer player, BlockPos pos)
    {
        if(!openedMenus.containsKey(player.getUUID())) return false;
        Triplet<BlockPos, SelectionMenuOptions, ISelectionMenuResponder> thisMenu = openedMenus.get(player.getUUID());
        return thisMenu.getA().equals(pos);
    }

    public static void RespondToMenuSelectionPacket(ServerPlayer player, int selection, boolean isFinalSelection, boolean isCancelled, BlockPos pos)
    {
        if(!openedMenus.containsKey(player.getUUID())) return;
        Triplet<BlockPos, SelectionMenuOptions, ISelectionMenuResponder> thisMenu = openedMenus.get(player.getUUID());
        if(isCancelled || !thisMenu.getA().equals(pos) || selection >= thisMenu.getB().entries.size() || selection < 0)
        {
            player.sendSystemMessage(Component.literal("Cancelled."));
            ClosePlayerMenu(player);
            //fire cancel event
            thisMenu.getC().ReceiveSelection(thisMenu.getA(), thisMenu.getB(), selection, false, true);
            return;
        }
        //too far, cancel menu
        if(thisMenu.getA().distToCenterSqr(player.position().x, player.position().y, player.position().z) > player.getReachDistance()+1)
        {
            ClosePlayerMenu(player);
            //fire cancel event
            thisMenu.getC().ReceiveSelection(thisMenu.getA(), thisMenu.getB(), selection, false, true);
            return;
        }
        //player.sendSystemMessage(Component.literal("Selected "+selection + (isFinalSelection?" (FINAL)":" (browsing)")));
        thisMenu.getC().ReceiveSelection(thisMenu.getA(), thisMenu.getB(), selection, isFinalSelection, isCancelled);
        if(isFinalSelection)
        {
            ClosePlayerMenu(player);
        }
    }
}
