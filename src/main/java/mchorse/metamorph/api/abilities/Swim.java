package mchorse.metamorph.api.abilities;

import mchorse.metamorph.api.IAbility;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Swim ability
 * 
 * This ability makes player a better swimmer. What it does, it basically 
 * increases the swim speed and also gives player more control over vertical 
 * movement. 
 */
public class Swim implements IAbility
{
    @Override
    public void update(EntityPlayer player)
    {
        double speed = 2.5D;

        if (player.isInWater())
        {
            player.motionX *= speed;
            player.motionZ *= speed;

            if (player.motionY < 0 && player.isSneaking())
            {
                player.motionY = 0;
            }
        }
    }
}