package com.bloodforge.bloodworks.Util;

import net.minecraft.core.BlockPos;

public interface ISelectionMenuResponder
{
    public abstract void ReceiveSelection(BlockPos pos, SelectionMenuOptions menu, int selection, boolean isFinalSelection, boolean isCancelled);
}
