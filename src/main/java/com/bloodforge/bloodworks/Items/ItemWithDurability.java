package com.bloodforge.bloodworks.Items;

import com.bloodforge.bloodworks.Client.ClientUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemWithDurability extends ItemGeneric
{

    public ItemWithDurability(int maxDurability)
    {
        super(new Properties().durability(maxDurability).defaultDurability(maxDurability));
    }

    @Override
    public boolean isDamageable(ItemStack stack)
    {
        return true;
    }

    @Override
    public int getBarColor(ItemStack p_150901_)
    {
        return (int) (Math.random() * 3);
    }

    @Override
    public boolean isBarVisible(ItemStack p_150899_)
    {
        return p_150899_.isDamaged();
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag)
    {
        ClientUtils.AddAdditionalShiftInfo(components, "Durability: " + itemStack.getDamageValue() + "/" + itemStack.getMaxDamage());
        super.appendHoverText(itemStack, level, components, tooltipFlag);
    }
}