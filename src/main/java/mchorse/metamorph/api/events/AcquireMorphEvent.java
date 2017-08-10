package mchorse.metamorph.api.events;

import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.Morphing;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Acquire morph event
 * 
 * {@link AcquireMorphEvent.Pre} is fired when a player is about to acquire a morph. This is not 
 * necessarily means that player already has this morph. Use {@link #hasMorph()} 
 * method to figure out whether player already has given morph.
 * 
 * You can modify the morph property. This will result into player getting 
 * different morph. 
 * 
 * If you cancel this event, player won't acquire a morph, however, if player 
 * already has this morph, it will be completely useless.
 * 
 * {@link AcquireMorphEvent.Post} is fired after a player successfully acquires a new morph.
 */
public abstract class AcquireMorphEvent extends Event
{
    public EntityPlayer player;
    public AbstractMorph morph;

    public AcquireMorphEvent(EntityPlayer player, AbstractMorph morph)
    {
        this.player = player;
        this.morph = morph;
    }

    /**
     * Does given player already has this morph 
     */
    public boolean hasMorph()
    {
        return Morphing.get(this.player).acquiredMorph(this.morph);
    }

    /**
     * Fires before player acquires a morph. This event is {@link Cancelable}.
     * 
     * @author asanetargoss
     */
    @Cancelable
    public static class Pre extends AcquireMorphEvent
    {
        public Pre(EntityPlayer player, AbstractMorph morph)
        {
            super(player, morph);
        }
    }

    /**
     * Fires after player successfully acquires a morph. 
     */
    public static class Post extends AcquireMorphEvent
    {
        public Post(EntityPlayer player, AbstractMorph morph)
        {
            super(player, morph);
        }
    }
}