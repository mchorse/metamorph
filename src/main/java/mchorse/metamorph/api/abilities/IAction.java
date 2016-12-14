package mchorse.metamorph.api.abilities;

import net.minecraft.entity.EntityLivingBase;

/**
 * Action interface
 * 
 * Just like an ability, but cooler. This interface, instead of changing player's 
 * properties, it's actually does some kind of trick.
 */
public interface IAction
{
    /**
     * Execute an action. Depends on action's description, it can teleport 
     * player, emit explosion, or something else.  
     */
    public void execute(EntityLivingBase target);
}