package com.bloodforge.bloodworks.Multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlockMask
{
    ArrayList<Block> allowedBlocks;
    ArrayList<SpecialBlockParams> specialBlocks;
    public boolean isBlacklist = false;

    public BlockMask(Block... allowed)
    {
        allowedBlocks = new ArrayList<>(List.of(allowed));
    }
    public BlockMask(boolean isBlacklist, Block... allowed)
    {
        this.isBlacklist = isBlacklist;
        allowedBlocks = new ArrayList<>(List.of(allowed));
    }
    public BlockMask(SpecialBlockParams[] specials, boolean isBlacklist, Block... allowed)
    {
        specialBlocks = new ArrayList<>(List.of(specials));
        this.isBlacklist = isBlacklist;
        allowedBlocks = new ArrayList<>(List.of(allowed));
    }

    public BlockMask(SpecialBlockParams[] specials, Block... allowed)
    {
        specialBlocks = new ArrayList<>(List.of(specials));
        allowedBlocks = new ArrayList<>(List.of(allowed));
    }

    public boolean DoesBlockFitMask(BlockState state)
    {
        return isBlacklist != allowedBlocks.contains(state.getBlock());
    }

    public boolean isSpecialBlock(BlockState state)
    {
        if(specialBlocks == null) return false;
        return specialBlocks.contains(state.getBlock());
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (Block b : allowedBlocks)
        {
            s.append(b.getDescriptionId()).append(", ");
        }
        s.setLength(s.length()-2);
        return s.toString();
    }

    public SpecialBlockFindResult areSpecialsSatisfiedBy(HashMap<Block, ArrayList<BlockPos>> found)
    {
        if(specialBlocks == null) return new SpecialBlockFindResult(true, null);
        for (SpecialBlockParams b : specialBlocks)
        {
            if(b.isRequired() && !found.containsKey(b.block()))
            {
                return new SpecialBlockFindResult(false, b.block());
            }
        }
        return new SpecialBlockFindResult(true, null);
    }
}
