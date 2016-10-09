package mchorse.metamorph.api.abilities;

import mchorse.metamorph.api.IAbility;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Climbing ability
 * 
 * This ability makes player climb on the wall. Don't add this ability to the 
 * any other mobs except Spiders, otherwise player will turn into Spider man.
 */
public class Climb implements IAbility
{
    @Override
    public void update(EntityPlayer player)
    {
        if (player.isCollidedHorizontally)
        {
            player.fallDistance = 0.0F;
            player.motionY = player.isSneaking() ? 0 : 0.2D;
        }
    }
}