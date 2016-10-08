package mchorse.metamorph;

import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.MorphingProvider;
import mchorse.metamorph.network.Dispatcher;
import mchorse.metamorph.network.common.PacketMorph;
import mchorse.metamorph.network.common.PacketMorphPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Server event handler
 * 
 * This event handler (or rather listener) is responsible for adding new 
 * available morphings to the player.
 */
public class EventHandler
{
    @SubscribeEvent
    public void onPlayerKillEntity(LivingDeathEvent event)
    {
        Entity source = event.getSource().getEntity();
        Entity target = event.getEntity();

        if (source.worldObj.isRemote || !(source instanceof EntityPlayer) || target instanceof EntityPlayer)
        {
            return;
        }

        EntityPlayer player = (EntityPlayer) source;
        IMorphing capability = player.getCapability(MorphingProvider.MORPHING_CAP, null);

        if (capability == null)
        {
            return;
        }

        capability.setModel("chicken");

        Dispatcher.sendTo(new PacketMorph("chicken", ""), (EntityPlayerMP) player);
        Dispatcher.updateTrackers(player, new PacketMorphPlayer(player.getEntityId(), "chicken", ""));
    }
}