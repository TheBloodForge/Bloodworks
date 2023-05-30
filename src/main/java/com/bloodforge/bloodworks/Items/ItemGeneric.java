package com.bloodforge.bloodworks.Items;

import com.bloodforge.bloodworks.ClientUtils;
import com.bloodforge.bloodworks.Globals;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemGeneric extends Item
{
    public ItemGeneric()
    {
        super(new Item.Properties().tab(Globals.CREATIVE_TAB));
    }

    public ItemGeneric(Properties props)
    {
        super(props.tab(Globals.CREATIVE_TAB));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag)
    {
        ClientUtils.AddChatComponents(components, itemStack);
        super.appendHoverText(itemStack, level, components, tooltipFlag);
    }
}