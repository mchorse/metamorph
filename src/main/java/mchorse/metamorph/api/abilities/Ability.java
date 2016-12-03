package mchorse.metamorph.api.abilities;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Ability's base class
 * 
 * All it does, makes onMorph and onDemorph methods empty (so I didn't had a 
 * need to create those methods like in every ability).
 */
public abstract class Ability implements IAbility
{
    @Override
    public void onMorph(EntityPlayer player)
    {}

    @Override
    public void onDemorph(EntityPlayer player)
    {}
}