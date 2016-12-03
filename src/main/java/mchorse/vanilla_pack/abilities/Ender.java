package mchorse.vanilla_pack.abilities;

import java.util.Random;

import mchorse.metamorph.api.abilities.Ability;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;

/**
 * Ender ability
 * 
 * All it does it basically spawns enderman-like particles in the world on 
 * client side.
 */
public class Ender extends Ability
{
    @Override
    public void update(EntityPlayer player)
    {
        if (player.worldObj.isRemote)
        {
            Random rand = player.getRNG();

            for (int i = 0; i < 2; ++i)
            {
                double x = player.posX + (rand.nextDouble() - 0.5D) * (double) player.width;
                double y = player.posY + rand.nextDouble() * (double) player.height - 0.25D;
                double z = player.posZ + (rand.nextDouble() - 0.5D) * (double) player.width;

                player.worldObj.spawnParticle(EnumParticleTypes.PORTAL, x, y, z, (rand.nextDouble() - 0.5D) * 2.0D, -rand.nextDouble(), (rand.nextDouble() - 0.5D) * 2.0D, new int[0]);
            }
        }
    }
}