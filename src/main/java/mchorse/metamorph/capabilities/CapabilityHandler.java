package mchorse.metamorph.capabilities;

import mchorse.metamorph.Metamorph;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.capabilities.morphing.MorphingProvider;
import mchorse.metamorph.capabilities.render.ModelProvider;
import mchorse.metamorph.network.Dispatcher;
import mchorse.metamorph.network.common.survival.PacketAcquiredMorphs;
import mchorse.metamorph.network.common.PacketBlacklist;
import mchorse.metamorph.network.common.creative.PacketMorph;
import mchorse.metamorph.network.common.survival.PacketMorphPlayer;
import mchorse.metamorph.network.common.survival.PacketMorphState;
import mchorse.metamorph.network.common.PacketSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
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
    public static final ResourceLocation MORPHING_CAP = new ResourceLocation(Metamorph.MOD_ID, "morphing_capability");
    public static final ResourceLocation MODEL_CAP = new ResourceLocation(Metamorph.MOD_ID, "model");

    /**
     * Attach capabilities
     */
    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof EntityLivingBase)
        {
            event.addCapability(MODEL_CAP, new ModelProvider());
        }

        if (event.getObject() instanceof EntityPlayer)
        {
            event.addCapability(MORPHING_CAP, new MorphingProvider());
        }
    }

    /**
     * When player logs in, sent him his server counter partner's values.
     */
    @SubscribeEvent
    public void playerLogsIn(PlayerLoggedInEvent event)
    {
        EntityPlayer player = event.player;
        IMorphing cap = Morphing.get(player);

        if (cap != null)
        {
            this.sendAcquiredMorphs(cap, player);

            /* Ensure that player was morphed */
            if (cap.isMorphed())
            {
                cap.getCurrentMorph().morph(player);
            }

            /* Send data */
            Dispatcher.sendTo(new PacketBlacklist(MorphManager.INSTANCE.activeBlacklist), (EntityPlayerMP) player);
            Dispatcher.sendTo(new PacketSettings(MorphManager.INSTANCE.activeSettings), (EntityPlayerMP) player);
            Dispatcher.sendTo(new PacketMorphState(player, cap), (EntityPlayerMP) player);
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

            Dispatcher.sendTo(new PacketMorphPlayer(target.getEntityId(), cap.getCurrentMorph()), player);
        }
    }

    /**
     * On player's spawn in the world (when player travels in other dimension 
     * and spawns there or when player dies and then respawns).
     * 
     * This method is responsible for sending morphing data on the client.
     */
    @SubscribeEvent
    public void onPlayerSpawn(EntityJoinWorldEvent event)
    {
        if (event.getEntity() instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) event.getEntity();

            if (!player.world.isRemote)
            {
                IMorphing morphing = Morphing.get(player);

                this.sendAcquiredMorphs(morphing, player);
                Dispatcher.sendTo(new PacketMorphState(player, morphing), (EntityPlayerMP) player);
            }
        }
    }

    /**
     * Copy data from dead player (or player returning from end) to the new player
     */
    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event)
    {
        EntityPlayer player = event.getEntityPlayer();
        IMorphing morphing = Morphing.get(player);
        IMorphing oldMorphing = Morphing.get(event.getOriginal());

        if (Metamorph.keepMorphs.get() || !event.isWasDeath())
        {
            morphing.copy(oldMorphing, player);
        }
    }

    /**
     * Send acquired morphs (and currently morphed morph) to the given player. 
     */
    private void sendAcquiredMorphs(IMorphing cap, EntityPlayer player)
    {
        EntityPlayerMP mp = (EntityPlayerMP) player;

        Dispatcher.sendTo(new PacketMorph(cap.getCurrentMorph()), mp);
        Dispatcher.sendTo(new PacketAcquiredMorphs(cap.getAcquiredMorphs()), mp);
    }
}