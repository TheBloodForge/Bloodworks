package com.wiggle1000.bloodworks.Blocks;

import com.wiggle1000.bloodworks.ClientUtils;
import com.wiggle1000.bloodworks.Particles.ParticleHelper;
import com.wiggle1000.bloodworks.Registry.ParticleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings({"NullableProblems", "Unused"})
public class BlockBloodyStairsBase extends StairBlock
{
    public BlockBloodyStairsBase()
    {
        super(
                Blocks.COBBLESTONE_STAIRS::defaultBlockState,
                Properties
                        .of(Material.STONE)
                        .strength(3f, 5f)
                        .sound(SoundType.SLIME_BLOCK)
        );
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState blockState, Entity stepperOnner)
    {
        super.stepOn(level, pos, blockState, stepperOnner);
        ParticleHelper.DoStepParticle(ParticleRegistry.PARTICLE_FLESH_STEP.get(), level, pos, blockState, stepperOnner);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter blockGetter, List<Component> components, TooltipFlag tooltipFlag)
    {
        ClientUtils.AddChatComponents(components, "Clotted blood stairs. Still a bit mushy..", "Used for decoration.");
        super.appendHoverText(stack, blockGetter, components, tooltipFlag);
    }
}
