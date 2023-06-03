package com.bloodforge.bloodworks.Particles;

import com.bloodforge.bloodworks.Common.Config.BloodworksCommonConfig;
import com.bloodforge.bloodworks.Globals;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ParticleHelper
{
    public static void DoStepParticle(ParticleOptions stepParticle, Level level, BlockPos pos, BlockState blockState, Entity stepperOnner)
    {
        double deltaMove = stepperOnner.getDeltaMovement().horizontalDistanceSqr();
        if (deltaMove > 0.001 && !stepperOnner.isSteppingCarefully())
        {
            Vec3 footPos = stepperOnner.position().add(0, 0.016, 0);
            Vec3 posRand = new Vec3((Globals.RAND.nextDouble() - 0.5), 0, (Globals.RAND.nextDouble() - 0.5));
            posRand = posRand.scale((stepperOnner.getBoundingBox().getXsize() + stepperOnner.getBoundingBox().getZsize()) / 2);
            posRand = posRand.add(footPos);

            double speedMult = stepperOnner.isSprinting() ? 4.0 : 1.0;
            if (BloodworksCommonConfig.PARTICLE_ENABLED_STEP.get() && Globals.RAND.nextFloat() > BloodworksCommonConfig.PARTICLE_DENSITY.get())
                return;
            level.addParticle(stepParticle, posRand.x, posRand.y, posRand.z, speedMult, 0, 0);
        }
    }
}