package mchorse.vanilla_pack.abilities;

import mchorse.metamorph.api.abilities.Ability;
import net.minecraft.entity.EntityLivingBase;

/**
 * Climbing ability
 * 
 * This ability makes player climb on the wall. Don't add this ability to the 
 * any other mobs except Spiders, otherwise player will turn into Spider man.
 */
public class Climb extends Ability
{
    @Override
    public void update(EntityLivingBase target)
    {
        if (target.isCollidedHorizontally)
        {
            target.motionY = target.isSneaking() ? 0 : 0.2D;
        }

        target.fallDistance = 0.0F;
    }
}