package mchorse.vanilla_pack.abilities;

import mchorse.metamorph.api.abilities.Ability;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Fly ability
 * 
 * Allows player to fly as in creative. Mostly used by flying (captain) morphs. 
 */
public class Fly extends Ability
{
    @Override
    public void onMorph(EntityLivingBase target)
    {
        if (target instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) target;

            if (!player.capabilities.allowFlying)
            {
                player.capabilities.allowFlying = true;
                player.sendPlayerAbilities();
            }
        }
    }

    @Override
    public void update(EntityLivingBase target)
    {
        this.onMorph(target);
    }

    @Override
    public void onDemorph(EntityLivingBase target)
    {
        if (target instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) target;

            if (!player.isCreative())
            {
                player.capabilities.allowFlying = false;
                player.capabilities.isFlying = false;
                player.sendPlayerAbilities();
            }
        }
    }
}