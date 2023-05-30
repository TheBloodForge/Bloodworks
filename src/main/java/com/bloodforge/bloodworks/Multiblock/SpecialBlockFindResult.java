package com.bloodforge.bloodworks.Multiblock;

import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;

public record SpecialBlockFindResult(boolean OK, @Nullable Block missing)
{

    @Override
    public String toString()
    {
        return "Structure failed to find a required block: " + missing.getName();
    }

    public boolean isOK()
    {
        return OK;
    }
}