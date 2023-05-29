package com.bloodforge.bloodworks;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ClientUtils
{
    public static void AddChatComponents(List<Component> components, ItemStack stack)
    {
        if (Screen.hasShiftDown())
        {
            String selfQuote    = Component.translatable(stack.getItem().getDescriptionId() + ".selfquote").getString();
            String use          = Component.translatable(stack.getItem().getDescriptionId() + ".use"      ).getString();

            components.add(Component.literal("\"" + selfQuote + "\"").withStyle(ChatFormatting.DARK_RED));
            String[] useWords = use.split(" ");
            int lineLenLimit = 30;
            StringBuilder thisLineBuff = new StringBuilder();
            int cLine = 0;
            for( String word : useWords)
            {
                thisLineBuff.append(word).append(" ");
                if(thisLineBuff.length() > lineLenLimit || cLine == useWords.length - 1)
                {
                    components.add(Component.literal(thisLineBuff.toString()).withStyle(ChatFormatting.LIGHT_PURPLE));
                    thisLineBuff = new StringBuilder();
                }
                cLine++;
            }
        } else {
            components.add(Component.literal("Press SHIFT for more info").withStyle(ChatFormatting.DARK_AQUA));
        }
    }
}