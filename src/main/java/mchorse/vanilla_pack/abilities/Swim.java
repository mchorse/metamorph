package mchorse.vanilla_pack.abilities;

import mchorse.metamorph.api.abilities.Ability;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;

/**
 * Swim ability
 * 
 * This ability makes player a better swimmer. What it does, it basically 
 * increases the swim speed and also gives player more control over vertical 
 * movement. 
 */
public class Swim extends Ability
{
    @Override
    public void update(EntityLivingBase target)
    {
        double speed = 0.35;

        if (target.isInWater())
        {
            if (target.moveForward != 0 || target.moveStrafing != 0)
            {
                float a = target.rotationYaw / 180 * (float) Math.PI;

                float f1 = MathHelper.sin(a);
                float f2 = MathHelper.cos(a);

                target.motionX = (double) (target.moveStrafing * f2 - target.moveForward * f1) * speed;
                target.motionZ = (double) (target.moveForward * f2 + target.moveStrafing * f1) * speed;
                target.motionY = -MathHelper.sin((float) (target.rotationPitch / 180 * Math.PI)) * target.moveForward * speed;
            }
            else
            {
                target.motionX *= 0.6;
                target.motionY *= 0.6;

                if (target.motionY < 0)
                {
                    target.motionY = 0;
                }
            }
        }
        else
        {
            target.attackEntityFrom(DamageSource.drown, target.worldObj.getDifficulty().equals(EnumDifficulty.PEACEFUL) ? 1.0F : 0.5F);
        }
    }
}