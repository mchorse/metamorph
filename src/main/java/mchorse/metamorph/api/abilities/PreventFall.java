package mchorse.metamorph.api.abilities;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Prevent fall damage ability
 * 
 * This ability is responsible for prevent the fall damage, and it is doing it 
 * by modifying player's "fallDistance" field and setting it (or rather reseting) 
 * to 0.0F.
 */
public class PreventFall extends Ability
{
    @Override
    public void update(EntityPlayer player)
    {
        player.fallDistance = 0.0F;
    }
}