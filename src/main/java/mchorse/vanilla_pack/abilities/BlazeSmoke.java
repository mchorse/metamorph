package mchorse.vanilla_pack.abilities;

import java.util.Random;

import mchorse.metamorph.api.abilities.Ability;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;

/**
 * Blaze smoke ability
 * 
 * What it does, it basically spawns smoke like a blaze (420).
 */
public class BlazeSmoke extends Ability
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
                double y = target.posY + rand.nextDouble() * (double) target.height;
                double z = target.posZ + (rand.nextDouble() - 0.5D) * (double) target.width;

                target.worldObj.spawnParticle(EnumParticleTypes.SMOKE_LARGE, x, y, z, 0.0D, 0.0D, 0.0D, new int[0]);
            }
        }
    }
}