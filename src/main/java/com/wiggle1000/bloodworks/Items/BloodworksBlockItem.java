package com.wiggle1000.bloodworks.Items;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static com.wiggle1000.bloodworks.Globals.CREATIVE_TAB;

public class BloodworksBlockItem extends BlockItem
{
    public BloodworksBlockItem(Block block)
    { super(block, new Item.Properties().tab(CREATIVE_TAB)); }
}