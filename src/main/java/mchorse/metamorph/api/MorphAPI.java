package mchorse.metamorph.api;

import java.util.List;

import mchorse.metamorph.Metamorph;
import mchorse.metamorph.api.events.AcquireMorphEvent;
import mchorse.metamorph.api.events.MorphEvent;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.network.Dispatcher;
import mchorse.metamorph.network.common.creative.PacketAcquireMorph;
import mchorse.metamorph.network.common.creative.PacketMorph;
import mchorse.metamorph.network.common.survival.PacketMorphPlayer;
import mchorse.metamorph.network.common.survival.PacketMorphState;
import mchorse.metamorph.network.common.survival.PacketSelectMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

        if (!force && !player.noClip && !Metamorph.morphInTightSpaces.get() && !EntityUtils.canPlayerMorphFit(player, morphing.getCurrentMorph(), morph))
        {
            if (!player.world.isRemote)
            {
                ((EntityPlayerMP) player).connection.sendPacket(new SPacketChat(new TextComponentTranslation("metamorph.gui.status.tight_space"), (byte) 2));
            }
            return false;
        }

        MorphEvent.Pre event = new MorphEvent.Pre(player, morph, force);

        if (MinecraftForge.EVENT_BUS.post(event))
        {
            return false;
        }

        boolean morphed = morphing.setCurrentMorph(event.morph, player, event.force);

        if (!player.world.isRemote && morphed)
        {
            Dispatcher.sendTo(new PacketMorph(morph), (EntityPlayerMP) player);
            Dispatcher.sendToTracked(player, new PacketMorphPlayer(player.getEntityId(), morph));
            Dispatcher.sendTo(new PacketMorphState(player, morphing), (EntityPlayerMP) player);
        }

        if (morphed)
        {
            MinecraftForge.EVENT_BUS.post(new MorphEvent.Post(player, event.morph, force));
        }

        return morphed;
    }
    
    /**
     * Request the server to survival morph the player.
     */
    @SideOnly(Side.CLIENT)
    public static boolean selectMorph(AbstractMorph morph)
    {
        int index = -1;
        if (morph != null)
        {
            EntityPlayer player = Minecraft.getMinecraft().player;
            IMorphing morphing = Morphing.get(player);
            List<AbstractMorph> acquiredMorphs = morphing.getAcquiredMorphs();
            for (int i = 0; i < acquiredMorphs.size(); i++)
            {
                AbstractMorph acquiredMorph = acquiredMorphs.get(i);
                if (acquiredMorph.equals(morph))
                {
                    index = i;
                    break;
                }
            }
            if (index == -1)
            {
                return false;
            }

            morphing.setLastSelectedMorph(morph);
        }

        Dispatcher.sendToServer(new PacketSelectMorph(index));
        return true;
    }

    /**
     * Request the server to survival demorph the player.
     */
    @SideOnly(Side.CLIENT)
    public static boolean selectDemorph()
    {
        return selectMorph(null);
    }

    public static boolean acquire(EntityPlayer player, AbstractMorph morph)
    {
        return acquire(player, morph, true);
    }

    /**
     * Make given player acquire a given morph. Usable on both sides, but it's 
     * better to use it on the server.
     * 
     * @return true, if player has acquired a morph
     */
    public static boolean acquire(EntityPlayer player, AbstractMorph morph, boolean notify)
    {
        if (morph == null)
        {
            return false;
        }

        AcquireMorphEvent.Pre event = new AcquireMorphEvent.Pre(player, morph);

        if (MinecraftForge.EVENT_BUS.post(event))
        {
            return false;
        }

        boolean acquired = Morphing.get(player).acquireMorph(event.morph);

        if (notify && !player.world.isRemote && acquired)
        {
            Dispatcher.sendTo(new PacketAcquireMorph(event.morph), (EntityPlayerMP) player);
        }

        if (acquired)
        {
            MinecraftForge.EVENT_BUS.post(new AcquireMorphEvent.Post(player, event.morph));
        }

        return acquired;
    }
}