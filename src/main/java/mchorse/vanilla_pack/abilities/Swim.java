package mchorse.vanilla_pack.abilities;

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
        double maxSpeed = speed * 0.75;

        if (player.isInWater())
        {
            if (player.moveForward != 0 || player.moveStrafing != 0)
            {
                if (player.motionX <= maxSpeed && player.motionX >= -maxSpeed) player.motionX *= speed;
                if (player.motionZ <= maxSpeed && player.motionZ >= -maxSpeed) player.motionZ *= speed;
            }

            if (Math.abs(player.motionY) <= maxSpeed)
            {
                player.motionY *= speed;
            }

            if (player.motionY < 0 && !player.isSneaking())
            {
                player.motionY = 0;
            }
        }
    }
}