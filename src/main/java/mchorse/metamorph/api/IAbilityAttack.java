package mchorse.metamorph.api;

import net.minecraft.entity.Entity;

/**
 * Ability attack interface
 * 
 * This interface is responsible for adding some additional effect on the 
 * target (used primarly for wither)
 */
public interface IAbilityAttack
{
    public void attack(Entity target);
}