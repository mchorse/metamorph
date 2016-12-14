package mchorse.metamorph.api.abilities;

import net.minecraft.entity.EntityLivingBase;

/**
 * Ability interface
 * 
 * This interface should provide a method which is responsible for updating 
 * entity every tick, with ability to setup some behavior on player before 
 * morphing, and reset player, in given way, on player's demorph.
 */
public interface IAbility
{
    /**
     * This method is responsible for updating a player based on ability's 
     * function.
     */
    public void update(EntityLivingBase target);

    /**
     * This method should be invoked when the player is about to get morphed.
     */
    public void onMorph(EntityLivingBase target);

    /**
     * This method should be invoked when the player morphs into other morph 
     * without this ability. 
     */
    public void onDemorph(EntityLivingBase target);
}