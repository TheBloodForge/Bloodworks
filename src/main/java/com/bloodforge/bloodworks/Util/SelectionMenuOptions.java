package com.bloodforge.bloodworks.Util;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;

public class SelectionMenuOptions
{
    public static final SelectionMenuOptions DEFAULT = new SelectionMenuOptions();

    public Component titleLabel;
    public ArrayList<SelectionMenuEntry> entries = new ArrayList<>();

    public SelectionMenuOptions(Component title)
    {
        this.titleLabel = title;
    }

    public void toBytes(FriendlyByteBuf buf)
    {
        buf.writeComponent(titleLabel);
        buf.writeInt(entries.size());
        for(SelectionMenuEntry e : entries)
        {
            e.toBytes(buf);
        }
    }
    public SelectionMenuOptions(FriendlyByteBuf buf)
    {
        this.titleLabel = buf.readComponent();
        int bufSize = buf.readInt();
        for(int i = 0; i < bufSize; i++)
        {
            withEntry(new SelectionMenuEntry(buf));
        }
    }

    public SelectionMenuOptions()
    {
        this.titleLabel = Component.literal("NULL");
        this.withEntry(new SelectionMenuEntry(Component.literal("ONE")));
        this.withEntry(new SelectionMenuEntry(Component.literal("TWO")));
        this.withEntry(new SelectionMenuEntry(Component.literal("THREE")));
    }

    public SelectionMenuOptions withEntry(SelectionMenuEntry entry)
    {
        entries.add(entry);
        return this;
    }

    public static class SelectionMenuEntry
    {
        public Component label;
        public SelectionMenuEntry(Component label)
        {
            this.label = label;
        }
        public void toBytes(FriendlyByteBuf buf)
        {
            buf.writeComponent(label);
        }
        public SelectionMenuEntry(FriendlyByteBuf buf)
        {
            this.label = buf.readComponent();
        }
    }
}
