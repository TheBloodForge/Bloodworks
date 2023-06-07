package com.bloodforge.bloodworks.Items;

import com.bloodforge.bloodworks.Client.ClientUtils;
import com.bloodforge.bloodworks.Globals;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class BloodworksMusicDisc extends RecordItem
{
    public BloodworksMusicDisc(int comparatorValue, Supplier<SoundEvent> soundSupplier, int lengthInTicks) {
        super(comparatorValue, soundSupplier, new Item.Properties().tab(Globals.CREATIVE_TAB).stacksTo(1).rarity(Rarity.RARE), lengthInTicks);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag)
    {
        super.appendHoverText(itemStack, level, components, tooltipFlag);
        ClientUtils.AddChatComponents(components, itemStack);
    }
}
