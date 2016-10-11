package mchorse.metamorph.api.abilities;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Fly ability
 * 
 * Allows player to fly as in creative. Mostly used by flying (captain) morphs. 
 */
public class Fly extends Ability
{
    @Override
    public void onMorph(EntityPlayer player)
    {
        if (!player.capabilities.allowFlying)
        {
            player.capabilities.allowFlying = true;
            player.sendPlayerAbilities();
        }
    }

    @Override
    public void update(EntityPlayer player)
    {}

    @Override
    public void onDemorph(EntityPlayer player)
    {
        if (!player.capabilities.isCreativeMode)
        {
            player.capabilities.allowFlying = false;
            player.sendPlayerAbilities();
        }
    }
}