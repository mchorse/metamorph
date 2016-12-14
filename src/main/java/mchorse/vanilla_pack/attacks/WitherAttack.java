package mchorse.vanilla_pack.attacks;

import mchorse.metamorph.api.abilities.IAttackAbility;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

/**
 * Wither attack ability
 * 
 * This ability simple adds on a target wither effect
 */
public class WitherAttack implements IAttackAbility
{
    @Override
    public void attack(Entity target, EntityLivingBase source)
    {
        if (target instanceof EntityLivingBase)
        {
            ((EntityLivingBase) target).addPotionEffect(new PotionEffect(MobEffects.WITHER, 200));
        }
    }
}