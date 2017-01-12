package mchorse.metamorph.network;

import mchorse.metamorph.Metamorph;
import mchorse.metamorph.network.client.ClientHandlerAcquireMorph;
import mchorse.metamorph.network.client.ClientHandlerAcquiredMorphs;
import mchorse.metamorph.network.client.ClientHandlerFavoriteMorph;
import mchorse.metamorph.network.client.ClientHandlerMorph;
import mchorse.metamorph.network.client.ClientHandlerMorphPlayer;
import mchorse.metamorph.network.client.ClientHandlerRemoveMorph;
import mchorse.metamorph.network.common.PacketAcquireMorph;
import mchorse.metamorph.network.common.PacketAcquiredMorphs;
import mchorse.metamorph.network.common.PacketAction;
import mchorse.metamorph.network.common.PacketFavoriteMorph;
import mchorse.metamorph.network.common.PacketMorph;
import mchorse.metamorph.network.common.PacketMorphPlayer;
import mchorse.metamorph.network.common.PacketRemoveMorph;
import mchorse.metamorph.network.common.PacketSelectMorph;
import mchorse.metamorph.network.server.ServerHandlerAcquireMorph;
import mchorse.metamorph.network.server.ServerHandlerAction;
import mchorse.metamorph.network.server.ServerHandlerFavoriteMorph;
import mchorse.metamorph.network.server.ServerHandlerMorph;
import mchorse.metamorph.network.server.ServerHandlerRemoveMorph;
import mchorse.metamorph.network.server.ServerHandlerSelectMorph;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Network dispatcher
 *
 * @author Ernio (Ernest Sadowski)
 */
public class Dispatcher
{
    private static final SimpleNetworkWrapper DISPATCHER = NetworkRegistry.INSTANCE.newSimpleChannel(Metamorph.MODID);
    private static byte PACKET_ID;

    public static SimpleNetworkWrapper get()
    {
        return DISPATCHER;
    }

    public static void updateTrackers(Entity entity, IMessage message)
    {
        EntityTracker et = ((WorldServer) entity.worldObj).getEntityTracker();

        for (EntityPlayer player : et.getTrackingPlayers(entity))
        {
            DISPATCHER.sendTo(message, (EntityPlayerMP) player);
        }
    }

    public static void sendTo(IMessage message, EntityPlayerMP player)
    {
        DISPATCHER.sendTo(message, player);
    }

    public static void sendToServer(IMessage message)
    {
        DISPATCHER.sendToServer(message);
    }

    /**
     * Register all the networking messages and message handlers
     */
    public static void register()
    {
        /* Action */
        register(PacketAction.class, ServerHandlerAction.class, Side.SERVER);

        /* Morphing */
        register(PacketMorph.class, ClientHandlerMorph.class, Side.CLIENT);
        register(PacketMorph.class, ServerHandlerMorph.class, Side.SERVER);
        register(PacketMorphPlayer.class, ClientHandlerMorphPlayer.class, Side.CLIENT);

        register(PacketAcquireMorph.class, ClientHandlerAcquireMorph.class, Side.CLIENT);
        register(PacketAcquireMorph.class, ServerHandlerAcquireMorph.class, Side.SERVER);
        register(PacketAcquiredMorphs.class, ClientHandlerAcquiredMorphs.class, Side.CLIENT);

        register(PacketSelectMorph.class, ServerHandlerSelectMorph.class, Side.SERVER);

        /* Managing morphs */
        register(PacketFavoriteMorph.class, ClientHandlerFavoriteMorph.class, Side.CLIENT);
        register(PacketFavoriteMorph.class, ServerHandlerFavoriteMorph.class, Side.SERVER);

        register(PacketRemoveMorph.class, ClientHandlerRemoveMorph.class, Side.CLIENT);
        register(PacketRemoveMorph.class, ServerHandlerRemoveMorph.class, Side.SERVER);
    }

    private static <REQ extends IMessage, REPLY extends IMessage> void register(Class<REQ> message, Class<? extends IMessageHandler<REQ, REPLY>> handler, Side side)
    {
        DISPATCHER.registerMessage(handler, message, PACKET_ID++, side);
    }
}