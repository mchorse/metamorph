package mchorse.vanilla_pack.abilities;

import mchorse.metamorph.api.abilities.Ability;
import net.minecraft.entity.EntityLivingBase;

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
        double speed = 1.25D;
        double maxSpeed = speed * 0.75;

        if (target.isInWater())
        {
            if (target.moveForward != 0 || target.moveStrafing != 0)
            {
                if (target.motionX <= maxSpeed && target.motionX >= -maxSpeed) target.motionX *= speed;
                if (target.motionZ <= maxSpeed && target.motionZ >= -maxSpeed) target.motionZ *= speed;
            }

            if (Math.abs(target.motionY) <= maxSpeed)
            {
                target.motionY *= speed;
            }

            if (target.motionY < 0 && !target.isSneaking())
            {
                target.motionY = 0;
            }
        }
    }
}