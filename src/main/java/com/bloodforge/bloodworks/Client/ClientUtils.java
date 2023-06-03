package com.bloodforge.bloodworks.Client;

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
            String selfQuote = "\"" + Component.translatable(stack.getItem().getDescriptionId() + ".selfquote").getString();
            String use = Component.translatable(stack.getItem().getDescriptionId() + ".use").getString();

            String[] quoteWords = selfQuote.split(" ");
            int lineLenLimit = 40;
            StringBuilder thisLineBuff = new StringBuilder();
            int cLine = 0;
            for (String word : quoteWords)
            {
                thisLineBuff.append(word).append(" ");
                if (thisLineBuff.length() > lineLenLimit || cLine == quoteWords.length - 1)
                {
                    components.add(Component.literal(thisLineBuff.toString()).withStyle(ChatFormatting.DARK_RED).withStyle(ChatFormatting.ITALIC));
                    thisLineBuff.setLength(0);
                }
                cLine++;
            }
            components.add(Component.literal(use).withStyle(ChatFormatting.LIGHT_PURPLE));
        } else
        {
            components.add(Component.literal("Press SHIFT for more info").withStyle(ChatFormatting.DARK_AQUA));
        }
    }

    public static void AddAdditionalShiftInfo(List<Component> components, String whatSayYou)
    {
        if (Screen.hasShiftDown())
        {
            components.add(Component.literal(whatSayYou).withStyle(ChatFormatting.WHITE).withStyle(ChatFormatting.ITALIC));
        }
    }

    public static void AddAdditionalShiftInfo(List<Component> components, Component whatSayYou)
    {
        if (Screen.hasShiftDown())
        {
            components.add(whatSayYou);
        }
    }
}