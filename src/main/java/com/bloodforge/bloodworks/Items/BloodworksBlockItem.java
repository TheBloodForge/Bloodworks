package com.bloodforge.bloodworks.Items;

import com.bloodforge.bloodworks.ClientUtils;
import com.bloodforge.bloodworks.Globals;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BloodworksBlockItem extends BlockItem
{
    public BloodworksBlockItem(Block block)
    { super(block, new Item.Properties().tab(Globals.CREATIVE_TAB)); }
    public BloodworksBlockItem(Block block, Item.Properties props)
    { super(block, props); }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
        ClientUtils.AddChatComponents(components, itemStack);
        super.appendHoverText(itemStack, level, components, tooltipFlag);
    }
}