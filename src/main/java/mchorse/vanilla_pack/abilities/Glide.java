package mchorse.vanilla_pack.abilities;

import mchorse.metamorph.api.abilities.Ability;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Glide ability
 * 
 * This ability makes player fall much slower. Sneak to disable gliding effect.
 */
public class Glide extends Ability
{
    @Override
    public void update(EntityLivingBase target)
    {
        boolean isFlying = target instanceof EntityPlayer && ((EntityPlayer) target).capabilities.isFlying;

        if (!target.onGround && target.motionY < 0.0D && !isFlying && !target.isElytraFlying() && !target.isSneaking())
        {
            target.motionY *= 0.6D;
            target.fallDistance = 0.0F;
        }
    }
}