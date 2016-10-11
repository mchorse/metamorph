package mchorse.metamorph.api.abilities;

import mchorse.metamorph.api.IAbilityAttack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

/**
 * Wither attack ability
 * 
 * This ability simple adds on a target wither effect
 */
public class WitherAttack implements IAbilityAttack
{
    @Override
    public void attack(Entity target)
    {
        if (target instanceof EntityLivingBase)
        {
            ((EntityLivingBase) target).addPotionEffect(new PotionEffect(MobEffects.WITHER, 200));
        }
    }
}