package mchorse.metamorph.api.abilities;

import mchorse.metamorph.api.IAbility;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Glide ability
 * 
 * This ability makes player fall much slower.
 */
public class Glide implements IAbility
{
    @Override
    public void update(EntityPlayer player)
    {
        if (!player.onGround && player.motionY < 0.0D)
        {
            player.motionY *= 0.6D;
            player.fallDistance = 0.0F;
        }
    }
}