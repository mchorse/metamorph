package mchorse.metamorph.api.abilities;

import mchorse.metamorph.api.IAbility;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Fire proof ability
 * 
 * This abilitiy grants you fire immunity. So basically you're fire proof.
 */
public class FireProof implements IAbility
{
    @Override
    public void update(EntityPlayer player)
    {
        if (!player.isImmuneToFire())
        {
            player.fireResistance = 1000000000;
        }

        player.extinguish();
    }
}