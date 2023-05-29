package com.bloodforge.bloodworks.Blocks;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

public class BlockBloodyWallBase extends WallBlock
{
    public BlockBloodyWallBase()
    {
        super
        (
                BlockBehaviour.Properties
                        .of(Material.STONE)
                        .strength(3f, 5f)
                        .sound(SoundType.SLIME_BLOCK)
        );
    }

}