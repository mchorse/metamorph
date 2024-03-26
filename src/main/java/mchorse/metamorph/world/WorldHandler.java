package mchorse.metamorph.world;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import mchorse.metamorph.Metamorph;
import mchorse.metamorph.util.ObfuscatedName;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Adds hook to prevent particles spawning in first person
 */
public class WorldHandler
{
    public static final ObfuscatedName EVENT_LISTENERS = new ObfuscatedName("field_73021_x");
    
    protected static boolean spawnClientParticles = true;

    protected static WeakHashMap<World, List<WorldEventListenerWrapper>> worldListenersClient = new WeakHashMap<>();
    protected static WeakHashMap<World, List<WorldEventListenerWrapper>> worldListenersServer = new WeakHashMap<>();
    
    public static List<WorldEventListenerWrapper> getListenersForWorld(World world)
    {
        WeakHashMap<World, List<WorldEventListenerWrapper>> listenersPerWorld = world.isRemote ? worldListenersClient : worldListenersServer;
        List<WorldEventListenerWrapper> ourListeners = listenersPerWorld.get(world);
        if (ourListeners == null)
        {
            ourListeners = new ArrayList<WorldEventListenerWrapper>();
            listenersPerWorld.put(world, ourListeners);
            try
            {
                Field eventListenersField = World.class.getDeclaredField(EVENT_LISTENERS.getName());
                eventListenersField.setAccessible(true);
                @SuppressWarnings("unchecked")
                List<IWorldEventListener> vanillaListeners = (List<IWorldEventListener>)eventListenersField.get(world);
                int listenerCount = vanillaListeners.size();
                for (int i = 0; i < listenerCount; ++i)
                {
                    WorldEventListenerWrapper wrapper = new WorldEventListenerWrapper(vanillaListeners.get(i));
                    vanillaListeners.set(i, wrapper);
                    ourListeners.add(wrapper);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return ourListeners;
    }

    public static void setEnableWorldParticles(World world, boolean enable) {
        List<WorldEventListenerWrapper> worldListeners = getListenersForWorld(world);
        for (WorldEventListenerWrapper listener : worldListeners) {
            listener.spawnParticles = enable;
        }
    }
    
    @SubscribeEvent
    public void onUpdateClient(ClientTickEvent event)
    {
        spawnClientParticles = Metamorph.spawnParticlesFirstPerson.get() || !ClientSide.isFirstPerson();
    }
    
    public static boolean shouldSpawnMorphParticles(Entity entity)
    {
        return spawnClientParticles || Metamorph.proxy.isDedicatedServer() || !ClientSide.isThePlayerByID(entity);
    }

    @SideOnly(Side.CLIENT)
    public static class ClientSide
    {
        public static boolean isFirstPerson()
        {
            return Minecraft.getMinecraft().gameSettings.thirdPersonView == 0;
        }
        
        public static boolean isThePlayerByID(Entity entity)
        {
            if (entity == null)
            {
                return false;
            }
            EntityPlayer thePlayer = Minecraft.getMinecraft().player;
            if (thePlayer == null)
            {
                return false;
            }
            return entity.getEntityId() == thePlayer.getEntityId();
        }
    }
}
