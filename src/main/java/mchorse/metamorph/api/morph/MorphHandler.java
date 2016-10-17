package mchorse.metamorph.api.morph;

import java.util.ArrayList;
import java.util.List;

import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.MorphingProvider;
import mchorse.metamorph.network.Dispatcher;
import mchorse.metamorph.network.common.PacketAcquireMorph;
import mchorse.metamorph.network.common.PacketMorphPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

/**
 * Server event handler
 * 
 * This event handler (or rather listener) is responsible for morphings. In 
 * essence, there are few things going on over here:
 * 
 * 1. Acquiring morphs from killed entities
 * 2. Grant additional attack effect while morphed (more damage, explosions, 
 *    potion effects, etc.)
 * 3. Update morphs in the player's loop 
 */
public class MorphHandler
{
    public static List<Runnable> FUTURE_TASKS_CLIENT = new ArrayList<Runnable>();
    public static List<Runnable> FUTURE_TASKS_SERVER = new ArrayList<Runnable>();

    /**
     * When a player kills an entity, he gains a morph based on the properties 
     * of this entity.
     * 
     * I think I need to implement some mechanism or a callback to map some 
     * entities with the same name onto different morphs. 
     */
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

        if (capability.acquireMorph(morph))
        {
            Dispatcher.sendTo(new PacketAcquireMorph(morph), (EntityPlayerMP) player);

            player.addChatMessage(new TextComponentString("You gained §o§7" + morph + "§r morph!"));
        }

        capability.setCurrentMorph(morph, player, false);

        if (capability.getCurrentMorphName().equals(morph))
        {
            Dispatcher.sendTo(new PacketAcquireMorph(morph), (EntityPlayerMP) player);
            Dispatcher.updateTrackers(player, new PacketMorphPlayer(player.getEntityId(), morph));
        }
    }

    /**
     * When player is morphed, he can deal an damage or effect onto the enemy. 
     * 
     * For example, if player is morphed into the wither skeleton, he also 
     * grants a target wither potion effect. Pretty realistic, however I don't 
     * really know what that does. 
     */
    @SubscribeEvent
    public void onPlayerAttack(LivingAttackEvent event)
    {
        Entity source = event.getSource().getEntity();
        Entity target = event.getEntity();

        if (source instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) source;
            IMorphing capability = player.getCapability(MorphingProvider.MORPHING_CAP, null);

            if (capability == null || !capability.isMorphed())
            {
                return;
            }

            capability.getCurrentMorph().attack(target, player);
        }
    }

    /**
     * When player is morphed, its morphing abilities are executed over here.
     * 
     * Stuff like gliding, allergies, climbing, swiming and other stuff are 
     * get applied on the player over here.
     */
    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event)
    {
        if (event.phase == Phase.START)
        {
            return;
        }

        EntityPlayer player = event.player;
        IMorphing capability = player.getCapability(MorphingProvider.MORPHING_CAP, null);

        this.runFutureTasks(player);

        if (capability == null || !capability.isMorphed())
        {
            return;
        }

        capability.getCurrentMorph().update(player, capability);
    }

    /**
     * Run future tasks on both client and server. 
     */
    private void runFutureTasks(EntityPlayer player)
    {
        if (player.worldObj.isRemote && !FUTURE_TASKS_CLIENT.isEmpty())
        {
            FUTURE_TASKS_CLIENT.remove(0).run();
        }

        if (!player.worldObj.isRemote && !FUTURE_TASKS_SERVER.isEmpty())
        {
            FUTURE_TASKS_SERVER.remove(0).run();
        }
    }
}