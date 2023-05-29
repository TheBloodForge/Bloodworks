package com.bloodforge.bloodworks.Items;

import net.minecraft.world.item.ItemStack;

public class ItemWithDurability extends ItemGeneric
{

    public ItemWithDurability(int durability, int maxDurability, int stackSize)
    {
        super(new Properties().durability(maxDurability).defaultDurability(maxDurability).stacksTo(stackSize));
    }
    @Override
    public boolean isDamageable(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarColor(ItemStack p_150901_) {
        return (int)(Math.random()*10);
    }
}
