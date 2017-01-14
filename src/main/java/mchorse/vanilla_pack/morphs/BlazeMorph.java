package mchorse.vanilla_pack.morphs;

import java.util.Random;

import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.CustomMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;

/**
 * Blaze morph
 * 
 * This dude is responsible for making a morph look like {@link EntityBlaze} 
 * you know, particles, sounds and stuff.
 */
public class BlazeMorph extends CustomMorph
{
    @Override
    public void update(EntityLivingBase target, IMorphing cap)
    {
        super.update(target, cap);

        Random rand = target.getRNG();

        if (target.worldObj.isRemote && !target.isWet())
        {
            if (rand.nextInt(24) == 0 && !target.isSilent())
            {
                target.worldObj.playSound(target.posX + 0.5D, target.posY + 0.5D, target.posZ + 0.5D, SoundEvents.ENTITY_BLAZE_BURN, target.getSoundCategory(), 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
            }

            for (int i = 0; i < 2; ++i)
            {
                target.worldObj.spawnParticle(EnumParticleTypes.SMOKE_LARGE, target.posX + (rand.nextDouble() - 0.5D) * (double) target.width, target.posY + rand.nextDouble() * (double) target.height, target.posZ + (rand.nextDouble() - 0.5D) * (double) target.width, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public AbstractMorph clone()
    {
        BlazeMorph morph = new BlazeMorph();

        morph.name = this.name;

        morph.abilities = this.abilities;
        morph.attack = this.attack;
        morph.action = this.action;

        morph.model = this.model;
        morph.renderer = this.renderer;

        return morph;
    }
}