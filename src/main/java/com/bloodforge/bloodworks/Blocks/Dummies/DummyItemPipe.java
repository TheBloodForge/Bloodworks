package com.bloodforge.bloodworks.Blocks.Dummies;

import net.minecraftforge.items.ItemStackHandler;

public class DummyItemPipe extends DummyPipe
{
    private final ItemStackHandler itemHandler = new ItemStackHandler(1);
    public DummyItemPipe()
    {
        super(PipeType.ITEM);
    }

    public void tick()
    {
        // return if extraction not enabled
        // get neighbor non-pipe for valid extraction object
        // find insert location
        // determine valid inserts
        // extract item into handler
        // insert into found location
    }
}