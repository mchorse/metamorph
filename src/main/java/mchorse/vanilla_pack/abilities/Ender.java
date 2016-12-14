package mchorse.vanilla_pack.abilities;

import java.util.Random;

import mchorse.metamorph.api.abilities.Ability;
import net.minecraft.entity.EntityLivingBase;
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
    public void update(EntityLivingBase target)
    {
        if (target.worldObj.isRemote)
        {
            Random rand = target.getRNG();

            for (int i = 0; i < 2; ++i)
            {
                double x = target.posX + (rand.nextDouble() - 0.5D) * (double) target.width;
                double y = target.posY + rand.nextDouble() * (double) target.height - 0.25D;
                double z = target.posZ + (rand.nextDouble() - 0.5D) * (double) target.width;

                target.worldObj.spawnParticle(EnumParticleTypes.PORTAL, x, y, z, (rand.nextDouble() - 0.5D) * 2.0D, -rand.nextDouble(), (rand.nextDouble() - 0.5D) * 2.0D, new int[0]);
            }
        }
    }
}