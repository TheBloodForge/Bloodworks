package com.bloodforge.bloodworks.Multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;

public class BlockMask
{
    public record RequiredBlockRecord(Block block, int min, int max){}

    ArrayList<Block> allowedBlocks;
    ArrayList<Block> disallowedBlocks;
    ArrayList<RequiredBlockRecord> requiredBlocks;
    ArrayList<Block> specialBlocks;

    public BlockMask()
    { }

    //Blocks to exclusively allow
    public BlockMask withWhitelisted(Block block)
    {
        if(allowedBlocks == null) allowedBlocks = new ArrayList<>();
        allowedBlocks.add(block);
        return this;
    }
    //Required means the same as special, but will cause a list of blocks to fail areRequirementsSatisfied if missing.
    public BlockMask withRequired(Block block, int minimum, int maximum)
    {
        if(requiredBlocks == null) requiredBlocks = new ArrayList<>();
        requiredBlocks.add(new RequiredBlockRecord(block, minimum, maximum));
        return this;
    }
    public BlockMask withRequired(Block block, int minimum)
    {
        if(requiredBlocks == null) requiredBlocks = new ArrayList<>();
        requiredBlocks.add(new RequiredBlockRecord(block, minimum, 99999));
        return this;
    }
    public BlockMask withRequired(Block block)
    {
        if(requiredBlocks == null) requiredBlocks = new ArrayList<>();
        requiredBlocks.add(new RequiredBlockRecord(block, 1, 99999));
        return this;
    }
    //Blocks to exclusively deny. If only this is set, the blacklist is active
    public BlockMask withBlacklisted(Block block)
    {
        if(disallowedBlocks == null) disallowedBlocks = new ArrayList<>();
        disallowedBlocks.add(block);
        return this;
    }

    //Special means "we care about this block, add it to the special array"
    public BlockMask withSpecial(Block block)
    {
        if(specialBlocks == null) specialBlocks = new ArrayList<>();
        specialBlocks.add(block);
        return this;
    }

    public BlockMaskCompareResult Compare(BlockState state)
    {
        boolean isWhitelist = false;
        boolean isBlacklist = false;
        boolean isRequired  = false;
        boolean isBlacklistOnly  = true;
        boolean isSpecial   = false;
        //if a list doesnt exist, we ignore it.
        if(allowedBlocks != null)
        {
            isWhitelist = allowedBlocks.contains(state.getBlock());
            isBlacklistOnly = false;
        }
        if(specialBlocks != null)
        {
            isSpecial = specialBlocks.contains(state.getBlock());
            isBlacklistOnly = false;
        }
        if(requiredBlocks != null)
        {
            isRequired = requiredBlocks.contains(state.getBlock());
            isBlacklistOnly = false;
        }
        if(disallowedBlocks != null)
        {
            isBlacklist = disallowedBlocks.contains(state.getBlock());
        }
        if(isBlacklistOnly) return new BlockMaskCompareResult(!isBlacklist, isWhitelist, isBlacklist, isSpecial, isRequired, state.getBlock());
        return new BlockMaskCompareResult((isWhitelist || isSpecial || isRequired) && !isBlacklist, isWhitelist, isBlacklist, isSpecial, isRequired, state.getBlock());
    }

    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder();
        if(allowedBlocks != null) {
            s.append("Whitelist[");
            for (Block b : allowedBlocks) {
                s.append(b.getDescriptionId()).append(", ");
            }
            s.append("]");
        }
        if(disallowedBlocks != null) {
            if(allowedBlocks != null) s.append(", ");
            s.append("Blacklist[");
            for (Block b : disallowedBlocks) {
                s.append(b.getDescriptionId()).append(", ");
            }
            s.append("]");
        }
        return s.toString();
    }

    public BlockMaskRequireResult areRequirementsSatisfiedBy(HashMap<Block, ArrayList<BlockPos>> found)
    {
        if (requiredBlocks == null) return new BlockMaskRequireResult(true, null);
        for (RequiredBlockRecord b : requiredBlocks)
        {
            if (!found.containsKey(b.block))
            {
                return new BlockMaskRequireResult(false, b.block);
            }
            int numFound = found.get(b.block).size();
            if(numFound < b.min || numFound > b.max)
            {
                return new BlockMaskRequireResult(false, b.block);
            }
        }
        return new BlockMaskRequireResult(true, null);
    }

    public record BlockMaskRequireResult(boolean OK, Block missing)
    {
        @Override
        public String toString() {
            if(OK)
            {
                return "Requirements met.";
            }
            return "A required block was not found: "+missing.getName()+".";
        }
    }
    public record BlockMaskCompareResult(boolean OK, boolean isWhitelisted, boolean isBlacklisted, boolean isSpecial, boolean isRequired, Block block)
    {
        @Override
        public String toString() {
            if(OK)
            {
                return "Block " + block.getName() + " fits mask.";
            }
            return "Block did not fit mask. {" +
                    "isWhitelisted=" + isWhitelisted +
                    ", isBlacklisted=" + isBlacklisted +
                    ", isSpecial=" + isSpecial +
                    ", isRequired=" + isRequired +
                    ", blockName=" + block.getName() +
                    '}';
        }
    }
}