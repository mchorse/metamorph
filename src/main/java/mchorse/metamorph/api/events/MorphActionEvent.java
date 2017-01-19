package mchorse.metamorph.api.events;

import mchorse.metamorph.api.abilities.IAction;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Morph action event
 * 
 * This event is fired on {@link MinecraftForge#EVENT_BUS}. It's fired when a 
 * player uses an action on the server side.
 * 
 * It's not cancel-able and not modify-able.
 */
public class MorphActionEvent extends Event
{
    public EntityPlayer player;
    public IAction action;
    public AbstractMorph morph;

    public MorphActionEvent(EntityPlayer player, IAction action, AbstractMorph morph)
    {
        this.player = player;
        this.action = action;
        this.morph = morph;
    }

    /**
     * Check whether this action is valid (and did take place, because if 
     * action is null, nothing will happen) 
     */
    public boolean isValid()
    {
        return this.action != null;
    }
}