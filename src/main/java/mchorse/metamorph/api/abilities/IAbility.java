package mchorse.metamorph.api.abilities;

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

    /**
     * This method should be invoked when the player is about to get morphed.
     */
    public void onMorph(EntityPlayer player);

    /**
     * This method should be invoked when the player morphs into other morph 
     * without this ability. 
     */
    public void onDemorph(EntityPlayer player);
}