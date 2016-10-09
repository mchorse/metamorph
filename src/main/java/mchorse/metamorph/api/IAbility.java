package mchorse.metamorph.api;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Ability interface
 * 
 * This interface should provide a method which is responsible for updating 
 * entity in such way that it provides some kind of ability.
 */
public interface IAbility
{
    /**
     * This method is responsible for updating a player based on ability's 
     * function.
     */
    public void update(EntityPlayer player);
}