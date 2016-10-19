package mchorse.metamorph.api.abilities;

import net.minecraft.entity.player.EntityPlayer;

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
    public void update(EntityPlayer player)
    {
        double speed = 1.25D;

        if (player.isInWater())
        {
            boolean flag = false;

            if (player.moveForward != 0 || player.moveStrafing != 0)
            {
                if (player.motionX <= speed && player.motionX >= -speed) player.motionX *= speed;
                if (player.motionZ <= speed && player.motionZ >= -speed) player.motionZ *= speed;

                flag = true;
            }

            if (player.motionY <= speed * 1.2 && player.motionY > 0)
            {
                player.motionY *= speed * 1.2;
            }

            if (player.motionY < 0 && !player.isSneaking())
            {
                player.motionY = 0;
            }
        }
    }
}