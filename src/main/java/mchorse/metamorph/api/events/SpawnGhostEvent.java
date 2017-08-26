package mchorse.metamorph.api.events;

import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Morph spawn event
 * 
 * {@link SpawnGhostEvent.Pre} occurs when a player kills a monster and a ghost is
 * about to spawn from it. The event will not occur if the config option
 * prevent_kill_acquire is set to true, nor will it occur if the config option
 * prevent_ghosts is set to true while the player already has the morph.
 * 
 * {@link #player} is the player responsible for killing the monster which spawned
 * the morph.
 * 
 * {@link #morph} is an instance of (@link #AbstractMorph} which represents the
 * monster that was just killed. If you change this, a different morph ghost will
 * spawn. If you set this to null, no morph will spawn.
 * 
 * Canceling the event will prevent the ghost from spawning.
 * 
 * {@link SpawnGhostEvent.Post} is fired after the ghost successfully spawns.
 * 
 * @author asanetargoss
 */
public abstract class SpawnGhostEvent extends Event
{
    public EntityPlayer player;
    public AbstractMorph morph;

    public SpawnGhostEvent(EntityPlayer player, AbstractMorph morph)
    {
        this.player = player;
        this.morph = morph;
    }

    @Cancelable
    public static class Pre extends SpawnGhostEvent
    {
        public Pre(EntityPlayer player, AbstractMorph morph)
        {
            super(player, morph);
        }
    }

    public static class Post extends SpawnGhostEvent
    {
        public Post(EntityPlayer player, AbstractMorph morph)
        {
            super(player, morph);
        }
    }
}