package mchorse.metamorph.capabilities;

import mchorse.metamorph.Metamorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.MorphingProvider;
import mchorse.metamorph.network.Dispatcher;
import mchorse.metamorph.network.common.PacketAcquiredMorphs;
import mchorse.metamorph.network.common.PacketMorph;
import mchorse.metamorph.network.common.PacketMorphPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

/**
 * Capability handler class
 *
 * This class is responsible for managing capabilities, i.e. attaching
 * capabilities and syncing values on the client.
 */
public class CapabilityHandler
{
    public static final ResourceLocation MORPHING_CAP = new ResourceLocation(Metamorph.MODID, "morphing_capability");

    /**
     * Attach capabilities (well, only one, right now)
     */
    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent.Entity event)
    {
        if (!(event.getEntity() instanceof EntityPlayer)) return;

        event.addCapability(MORPHING_CAP, new MorphingProvider());
    }

    /**
     * When player logs in, sent him his server counter partner's values.
     */
    @SubscribeEvent
    public void playerLogsIn(PlayerLoggedInEvent event)
    {
        EntityPlayer player = event.player;
        IMorphing cap = player.getCapability(MorphingProvider.MORPHING_CAP, null);

        if (cap != null)
        {
            Dispatcher.sendTo(new PacketMorph(cap.getCurrentMorphName()), (EntityPlayerMP) player);
            Dispatcher.sendTo(new PacketAcquiredMorphs(cap.getAcquiredMorphs()), (EntityPlayerMP) player);

            /* Ensure that player was morphed */
            if (cap.isMorphed())
            {
                cap.getCurrentMorph().morph(player);
            }
        }
    }

    /**
     * When player starts tracking another player, server has to send its
     * morphing values.
     */
    @SubscribeEvent
    public void playerStartsTracking(StartTracking event)
    {
        if (event.getTarget() instanceof EntityPlayer)
        {
            Entity target = event.getTarget();
            EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
            IMorphing cap = target.getCapability(MorphingProvider.MORPHING_CAP, null);

            Dispatcher.sendTo(new PacketMorphPlayer(target.getEntityId(), cap.getCurrentMorphName()), player);
        }
    }
}