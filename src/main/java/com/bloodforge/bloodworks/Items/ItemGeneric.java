package com.bloodforge.bloodworks.Items;

import com.bloodforge.bloodworks.Globals;
import net.minecraft.world.item.Item;

public class ItemGeneric extends Item
{
    public ItemGeneric()
    {
        super(new Item.Properties().tab(Globals.CREATIVE_TAB));
    }
}