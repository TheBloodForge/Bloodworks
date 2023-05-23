package com.wiggle1000.bloodworks.Blocks;

import com.wiggle1000.bloodworks.Particles.ParticleHelper;
import com.wiggle1000.bloodworks.Registry.ParticleRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockIntestine extends BaseEntityBlock
{
    public BlockIntestine()
    {
        super(
                BlockBehaviour.Properties
                        .of(Material.STONE)
                        .strength(4f, 1200f)
                        .sound(SoundType.SLIME_BLOCK)
                        .noOcclusion()
        );
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter blockGetter, List<Component> components, TooltipFlag tooltipFlag)
    {
        if (Screen.hasShiftDown())
        {
            components.add(Component.literal("\"Clotted blood. Still a bit mushy...\"").withStyle(ChatFormatting.DARK_RED));
            components.add(Component.literal("Used for decoration!").withStyle(ChatFormatting.DARK_RED));
        } else {
            components.add(Component.literal("Press SHIFT for more info").withStyle(ChatFormatting.DARK_AQUA));
        }
        super.appendHoverText(stack, blockGetter, components, tooltipFlag);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState blockState, Entity stepperOnner)
    {
        super.stepOn(level, pos, blockState, stepperOnner);
        ParticleHelper.DoStepParticle(ParticleRegistry.PARTICLE_FLESH_STEP.get(), level, pos, blockState, stepperOnner);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_)
    {
        return null;
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState)
    {
        return RenderShape.INVISIBLE;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos)
    {
        return true;
    }
}
