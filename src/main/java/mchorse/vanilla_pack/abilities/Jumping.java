package mchorse.vanilla_pack.abilities;

import mchorse.metamorph.api.abilities.Ability;
import net.minecraft.entity.EntityLivingBase;

/**
 * Jumping ability
 * 
 * Makes player jump whenever he moves and on the ground. Just like a slime!
 */
public class Jumping extends Ability
{
    @Override
    public void update(EntityLivingBase target)
    {
        boolean moving = target.moveStrafing != 0 || target.moveForward != 0;

        if (target.onGround && moving && target.motionY <= 0 && !target.isWet())
        {
            target.motionY += 0.5D;
        }
    }
}