package mchorse.metamorph.api.events;

import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Morph event
 * 
 * {@link MorphEvent.Pre} occurs when player gets morphed or demorphed. If player gets 
 * demorphed then {@link #morph} is null. Check for player's worldObj property 
 * to get on which side this event is triggered.
 * 
 * This event is cancelable. If it's get canceled, then player won't demorph 
 * or morph. If you'll reassign {@link #morph}, then player will apply morph 
 * which you assigned (or demorph if you assign {@link #morph} to null).
 * 
 * You can also modify {@link #force} parameter which is responsible for 
 * forcing morphing (if not forced, player will morph only in case if he has 
 * acquired morph like given).
 * 
 * {@link MorphEvent.Post} is fired after a player successfully morphs or demorphs.
 * 
 */
public abstract class MorphEvent extends Event
{
    public EntityPlayer player;
    public AbstractMorph morph;
    public boolean force;

    public MorphEvent(EntityPlayer player, AbstractMorph morph, boolean force)
    {
        this.player = player;
        this.morph = morph;
        this.force = force;
    }

    /**
     * Whether given player is about to demorph
     */
    public boolean isDemorphing()
    {
        return this.morph == null;
    }

    /**
     * Fires before player morphs. This event is {@link Cancelable}.
     * 
     * @author asanetargoss 
     */
    @Cancelable
    public static class Pre extends MorphEvent
    {
        public Pre(EntityPlayer player, AbstractMorph morph, boolean force)
        {
            super(player, morph, force);
        }
    }

    /**
     * Fires after a player successfully morphed
     * 
     * @author asanetargoss 
     */
    public static class Post extends MorphEvent
    {
        public Post(EntityPlayer player, AbstractMorph morph, boolean force)
        {
            super(player, morph, force);
        }
    }
}