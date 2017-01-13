package mchorse.metamorph.api;

import mchorse.metamorph.api.events.AcquireMorphEvent;
import mchorse.metamorph.api.events.MorphEvent;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.network.Dispatcher;
import mchorse.metamorph.network.common.PacketAcquireMorph;
import mchorse.metamorph.network.common.PacketMorph;
import mchorse.metamorph.network.common.PacketMorphPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;

/**
 * Morph API class
 * 
 * This class provides public API for morphing the player. Let me know which 
 * methods I may add to simplify your life :D
 * 
 * Acquired morphs and favorites are sent only to the owner players. So you 
 * can't access this information from the client side. However, you can hit me 
 * up, and prove me why I should send that information to the other players :D
 * 
 * Use this API on the server side, please. Thanks!
 */
public class MorphAPI
{
    /**
     * Demorph given player 
     */
    public static boolean demorph(EntityPlayer player)
    {
        return morph(player, null, false);
    }

    /**
     * Morph a player into given morph with given force flag. 
     * 
     * @return true, if player was morphed successfully
     */
    public static boolean morph(EntityPlayer player, AbstractMorph morph, boolean force)
    {
        IMorphing morphing = Morphing.get(player);

        if (morphing == null)
        {
            return false;
        }

        MorphEvent event = new MorphEvent(player, morph, force);

        if (MinecraftForge.EVENT_BUS.post(event))
        {
            return false;
        }

        boolean morphed = morphing.setCurrentMorph(event.morph, player, event.force);

        if (!player.worldObj.isRemote && morphed)
        {
            Dispatcher.sendTo(new PacketMorph(morph), (EntityPlayerMP) player);
            Dispatcher.updateTrackers(player, new PacketMorphPlayer(player.getEntityId(), morph));
        }

        return morphed;
    }

    /**
     * Make given player acquire a given morph. Usable on both sides, but it's 
     * better to use it on the server.
     * 
     * @return true, if player has acquired a morph
     */
    public static boolean acquire(EntityPlayer player, AbstractMorph morph)
    {
        if (morph == null)
        {
            return false;
        }

        AcquireMorphEvent event = new AcquireMorphEvent(player, morph);

        if (MinecraftForge.EVENT_BUS.post(event))
        {
            return false;
        }

        boolean acquired = Morphing.get(player).acquireMorph(event.morph);

        if (!player.worldObj.isRemote && acquired)
        {
            Dispatcher.sendTo(new PacketAcquireMorph(event.morph), (EntityPlayerMP) player);
        }

        return acquired;
    }
}