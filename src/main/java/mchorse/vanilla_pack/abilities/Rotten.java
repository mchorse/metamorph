package mchorse.vanilla_pack.abilities;

import mchorse.metamorph.api.abilities.Ability;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;

/**
 * Rotten ability
 * 
 * Prevents a character from being posion.
 * Mostly used for undead.
 */
public class Rotten extends Ability
{
    @Override
    public void update(EntityLivingBase target)
    {
        if (target.isPotionActive(MobEffects.POISON))
        {
            this.onMorph(target);
        }
    }

    @Override
    public void onMorph(EntityLivingBase target)
    {
        target.removePotionEffect(MobEffects.POISON);
    }
}