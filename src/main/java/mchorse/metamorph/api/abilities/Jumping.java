package mchorse.metamorph.api.abilities;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Jumping ability
 * 
 * Makes player jump whenever he moves and on the ground. Just like a slime!
 */
public class Jumping extends Ability
{
    @Override
    public void update(EntityPlayer player)
    {
        boolean moving = player.moveStrafing != 0 || player.moveForward != 0;

        if (player.onGround && moving && player.motionY <= 0)
        {
            player.motionY += 0.5D;
        }
    }
}