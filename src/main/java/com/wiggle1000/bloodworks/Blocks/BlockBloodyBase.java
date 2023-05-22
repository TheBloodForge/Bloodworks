package com.wiggle1000.bloodworks.Blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

public class BlockBloodyBase extends Block {

    public BlockBloodyBase() {
        super(
            BlockBehaviour.Properties
                .of(Material.STONE)
                .strength(4f, 1200f)
                .sound(SoundType.SLIME_BLOCK)
        );
    }

}
