package com.wiggle1000.bloodworks.Blocks;

import com.wiggle1000.bloodworks.ClientUtils;
import com.wiggle1000.bloodworks.Particles.ParticleHelper;
import com.wiggle1000.bloodworks.Registry.ParticleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings({"NullableProblems", "Unused", "unused"})
public class GenericBlockBase extends Block
{
    private String selfQuoteText = "Clotted blood. Still a bit mushy..";
    private String useText = "Used for decoration";

    private boolean isHalfTransparent = false;

    public GenericBlockBase(Properties props)
    {
        super(props);
    }

    public GenericBlockBase(String selfQuote, String use, Properties props)
    {
        super(props);
        if(selfQuoteText != null) selfQuoteText = selfQuote;
        if(useText != null) useText = use;
    }

    public GenericBlockBase withGlasslikeProperties()
    {
        this.isHalfTransparent = true;
        return this;
    }

    public boolean skipRendering(BlockState p_53972_, BlockState p_53973_, Direction p_53974_) {
        if(!this.isHalfTransparent) return super.skipRendering(p_53972_, p_53973_, p_53974_);
        return p_53973_.is(this) ? true : super.skipRendering(p_53972_, p_53973_, p_53974_);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter blockGetter, List<Component> components, TooltipFlag tooltipFlag)
    {
        ClientUtils.AddChatComponents(components, selfQuoteText, useText);
        super.appendHoverText(stack, blockGetter, components, tooltipFlag);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState blockState, Entity stepperOnner)
    {
        super.stepOn(level, pos, blockState, stepperOnner);
        ParticleHelper.DoStepParticle(ParticleRegistry.PARTICLE_FLESH_STEP.get(), level, pos, blockState, stepperOnner);
    }
}