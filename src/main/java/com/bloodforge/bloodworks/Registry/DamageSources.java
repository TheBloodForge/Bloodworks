package com.bloodforge.bloodworks.Registry;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public class DamageSources
{
    public static final DamageSource MACERATION = new DamageSource("bloodworks_macerator")
    {
        @Override
        public Component getLocalizedDeathMessage(LivingEntity whatDied)
        {
            return Component.translatable("death.bloodworks.macerator", whatDied.getDisplayName());
        }
    };
}
