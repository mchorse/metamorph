package mchorse.metamorph.api.abilities;

import mchorse.metamorph.api.IAbility;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Water breath ability
 * 
 * This ability grants its owner ability to stay in water and refill its air. 
 */
public class WaterBreath implements IAbility
{
    @Override
    public void update(EntityPlayer player)
    {
        if (player.isInWater())
        {
            player.setAir(300);
        }
    }
}