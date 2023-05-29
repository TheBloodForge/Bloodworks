package com.bloodforge.bloodworks.Blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class BlockFleshLight extends BlockOmniBase
{
    private boolean isLarge = false;
    public BlockFleshLight(boolean isLarge)
    {
        super(
                Properties
                .of(Material.STONE)
                .strength(4f, 5f)
                .sound(SoundType.SLIME_BLOCK)
                .noOcclusion()
                .lightLevel((BlockState b) -> isLarge?15:10),
                isLarge ? Block.box(0, 0, 0, 16, 6.1, 16) : Block.box(4, 0, 4, 12, 6, 12)
        );
        this.isLarge = isLarge;
    }

}
