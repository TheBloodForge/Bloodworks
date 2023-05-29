package com.bloodforge.bloodworks.Items;

import com.bloodforge.bloodworks.Globals;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class BloodworksBlockItem extends BlockItem
{
    public BloodworksBlockItem(Block block)
    { super(block, new Item.Properties().tab(Globals.CREATIVE_TAB)); }
}