package mchorse.vanilla_pack.abilities;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Climbing ability
 * 
 * This ability makes player climb on the wall. Don't add this ability to the 
 * any other mobs except Spiders, otherwise player will turn into Spider man.
 */
public class Climb extends Ability
{
    @Override
    public void update(EntityPlayer player)
    {
        if (player.isCollidedHorizontally)
        {
            player.motionY = player.isSneaking() ? 0 : 0.2D;
        }

        player.fallDistance = 0.0F;
    }
}