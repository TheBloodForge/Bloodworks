package com.wiggle1000.bloodworks;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ClientUtils
{
    public static void AddChatComponents(List<Component> components, String selfQuote, String use)
    {
        if (Screen.hasShiftDown())
        {
            components.add(Component.literal("\"" + selfQuote + "\"").withStyle(ChatFormatting.DARK_RED));
            String[] useWords = use.split(" ");
            int lineLenLimit = 30;
            String thisLineBuff = "";
            int cLine = 0;
            for( String word : useWords)
            {
                thisLineBuff += word + " ";
                if(thisLineBuff.length() > lineLenLimit || cLine == useWords.length - 1)
                {
                    components.add(Component.literal(thisLineBuff).withStyle(ChatFormatting.LIGHT_PURPLE));
                    thisLineBuff = "";
                }
                cLine++;
            }
        } else {
            components.add(Component.literal("Press SHIFT for more info").withStyle(ChatFormatting.DARK_AQUA));
        }
    }
}
