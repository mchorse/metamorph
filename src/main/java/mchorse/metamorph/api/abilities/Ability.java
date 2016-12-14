package mchorse.metamorph.api.abilities;

import net.minecraft.entity.EntityLivingBase;

/**
 * Ability's base class
 * 
 * All it does, makes onMorph and onDemorph methods empty (so I didn't had a 
 * need to create those methods like in every ability).
 */
public abstract class Ability implements IAbility
{
    @Override
    public void onMorph(EntityLivingBase player)
    {}

    @Override
    public void onDemorph(EntityLivingBase player)
    {}
}