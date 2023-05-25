package com.wiggle1000.bloodworks.Items;

import net.minecraft.world.item.Item;

import static com.wiggle1000.bloodworks.Globals.CREATIVE_TAB;

public class ItemGeneric extends Item
{
    public ItemGeneric()
    {
        super(new Item.Properties().tab(CREATIVE_TAB));
    }
}