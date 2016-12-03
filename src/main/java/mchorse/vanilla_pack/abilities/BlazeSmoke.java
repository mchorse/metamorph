package mchorse.vanilla_pack.abilities;

import java.util.Random;

import mchorse.metamorph.api.abilities.Ability;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;

/**
 * Blaze smoke ability
 * 
 * What it does, it basically spawns smoke like a blaze (420).
 */
public class BlazeSmoke extends Ability
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
                double y = player.posY + rand.nextDouble() * (double) player.height;
                double z = player.posZ + (rand.nextDouble() - 0.5D) * (double) player.width;

                player.worldObj.spawnParticle(EnumParticleTypes.SMOKE_LARGE, x, y, z, 0.0D, 0.0D, 0.0D, new int[0]);
            }
        }
    }
}