package mchorse.vanilla_pack.abilities;

import mchorse.metamorph.api.abilities.Ability;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Glide ability
 * 
 * This ability makes player fall much slower. Sneak to disable gliding effect.
 */
public class Glide extends Ability
{
    @Override
    public void update(EntityPlayer player)
    {
        if (!player.onGround && player.motionY < 0.0D && !player.capabilities.isFlying && !player.isElytraFlying() && !player.isSneaking())
        {
            player.motionY *= 0.6D;
            player.fallDistance = 0.0F;
        }
    }
}