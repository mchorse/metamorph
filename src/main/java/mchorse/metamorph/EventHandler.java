package mchorse.metamorph;

import mchorse.metamorph.api.morph.Morph;
import mchorse.metamorph.api.morph.MorphManager;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.MorphingProvider;
import mchorse.metamorph.network.Dispatcher;
import mchorse.metamorph.network.common.PacketMorph;
import mchorse.metamorph.network.common.PacketMorphPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

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

        if (target.worldObj.isRemote || !(source instanceof EntityPlayer) || target instanceof EntityPlayer)
        {
            return;
        }

        EntityPlayer player = (EntityPlayer) source;
        IMorphing capability = player.getCapability(MorphingProvider.MORPHING_CAP, null);

        if (capability == null)
        {
            return;
        }

        String morph = EntityList.getEntityString(target);

        if (!MorphManager.INSTANCE.morphs.containsKey(morph))
        {
            System.out.println("Morph by key '" + morph + "' doesn't exist!");

            return;
        }

        System.out.println(capability.getAcquiredMorphs());

        if (capability.acquireMorph(morph))
        {
            player.addChatMessage(new TextComponentString("You gained §o§7" + morph + "§r morph!"));
        }

        capability.setCurrentMorph(morph, player.isCreative());

        if (capability.getCurrentMorphName().equals(morph))
        {
            Dispatcher.sendTo(new PacketMorph(morph), (EntityPlayerMP) player);
            Dispatcher.updateTrackers(player, new PacketMorphPlayer(player.getEntityId(), morph));
        }
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event)
    {
        if (event.phase == Phase.START)
        {
            return;
        }

        EntityPlayer player = event.player;
        IMorphing capability = player.getCapability(MorphingProvider.MORPHING_CAP, null);

        if (capability == null)
        {
            return;
        }

        Morph morph = capability.getCurrentMorph();

        if (morph == null)
        {
            return;
        }

        morph.update(player, capability);
    }
}