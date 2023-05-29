package com.wiggle1000.bloodworks.Blocks;

import com.wiggle1000.bloodworks.ClientUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter blockGetter, List<Component> components, TooltipFlag tooltipFlag)
    {
        if(isLarge)
        {
            ClientUtils.AddChatComponents(components, stack);
            //ClientUtils.AddChatComponents(components, "...No comment.", "Used for decoration.");
        }
        else
        {
            ClientUtils.AddChatComponents(components, stack);
            //ClientUtils.AddChatComponents(components, "Wow, this joke is pretty on the nose.", "Used for decoration.");
        }
        super.appendHoverText(stack, blockGetter, components, tooltipFlag);
    }
}
