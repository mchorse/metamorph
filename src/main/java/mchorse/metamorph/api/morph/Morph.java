package mchorse.metamorph.api.morph;

import mchorse.metamorph.api.IAbility;
import mchorse.metamorph.api.IAction;
import mchorse.metamorph.api.Model;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Morph class
 * 
 * This class is simply responsible for storing morph-related data like its 
 * abilities, action and the model in which it morphs into.
 */
public class Morph
{
    public IAbility[] abilities;
    public IAction action;
    public Model model;

    public void update(EntityPlayer player)
    {
        for (IAbility ability : abilities)
        {
            ability.update(player);
        }
    }
}