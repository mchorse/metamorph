package mchorse.metamorph.network;

import mchorse.mclib.network.AbstractDispatcher;
import mchorse.metamorph.Metamorph;
import mchorse.metamorph.network.client.ClientHandlerAcquireMorph;
import mchorse.metamorph.network.client.ClientHandlerAcquiredMorphs;
import mchorse.metamorph.network.client.ClientHandlerBlacklist;
import mchorse.metamorph.network.client.ClientHandlerFavorite;
import mchorse.metamorph.network.client.ClientHandlerKeybind;
import mchorse.metamorph.network.client.ClientHandlerMorph;
import mchorse.metamorph.network.client.ClientHandlerMorphPlayer;
import mchorse.metamorph.network.client.ClientHandlerMorphState;
import mchorse.metamorph.network.client.ClientHandlerRemoveMorph;
import mchorse.metamorph.network.client.ClientHandlerSettings;
import mchorse.metamorph.network.common.PacketAcquireMorph;
import mchorse.metamorph.network.common.PacketAcquiredMorphs;
import mchorse.metamorph.network.common.PacketAction;
import mchorse.metamorph.network.common.PacketBlacklist;
import mchorse.metamorph.network.common.PacketFavorite;
import mchorse.metamorph.network.common.PacketKeybind;
import mchorse.metamorph.network.common.PacketMorph;
import mchorse.metamorph.network.common.PacketMorphPlayer;
import mchorse.metamorph.network.common.PacketMorphState;
import mchorse.metamorph.network.common.PacketRemoveMorph;
import mchorse.metamorph.network.common.PacketSelectMorph;
import mchorse.metamorph.network.common.PacketSettings;
import mchorse.metamorph.network.common.PacketSyncMorph;
import mchorse.metamorph.network.server.ServerHandlerAcquireMorph;
import mchorse.metamorph.network.server.ServerHandlerAction;
import mchorse.metamorph.network.server.ServerHandlerFavorite;
import mchorse.metamorph.network.server.ServerHandlerKeybind;
import mchorse.metamorph.network.server.ServerHandlerMorph;
import mchorse.metamorph.network.server.ServerHandlerRemoveMorph;
import mchorse.metamorph.network.server.ServerHandlerSelectMorph;
import mchorse.metamorph.network.server.ServerHandlerSyncMorph;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Network dispatcher
 */
public class Dispatcher
{
    public static final AbstractDispatcher DISPATCHER = new AbstractDispatcher(Metamorph.MODID)
    {
        @Override
        public void register()
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
            register(PacketSyncMorph.class, ServerHandlerSyncMorph.class, Side.SERVER);

            register(PacketSelectMorph.class, ServerHandlerSelectMorph.class, Side.SERVER);

            /* Morph state */
            register(PacketMorphState.class, ClientHandlerMorphState.class, Side.CLIENT);

            /* Managing morphs */
            register(PacketFavorite.class, ClientHandlerFavorite.class, Side.CLIENT);
            register(PacketFavorite.class, ServerHandlerFavorite.class, Side.SERVER);

            register(PacketKeybind.class, ClientHandlerKeybind.class, Side.CLIENT);
            register(PacketKeybind.class, ServerHandlerKeybind.class, Side.SERVER);

            register(PacketRemoveMorph.class, ClientHandlerRemoveMorph.class, Side.CLIENT);
            register(PacketRemoveMorph.class, ServerHandlerRemoveMorph.class, Side.SERVER);

            /* Syncing data */
            register(PacketBlacklist.class, ClientHandlerBlacklist.class, Side.CLIENT);
            register(PacketSettings.class, ClientHandlerSettings.class, Side.CLIENT);
        }
    };

    /**
     * Send message to players who are tracking given entity
     */
    public static void sendToTracked(Entity entity, IMessage message)
    {
        DISPATCHER.sendToTracked(entity, message);
    }

    /**
     * Send message to given player
     */
    public static void sendTo(IMessage message, EntityPlayerMP player)
    {
        DISPATCHER.sendTo(message, player);
    }

    /**
     * Send message to the server
     */
    public static void sendToServer(IMessage message)
    {
        DISPATCHER.sendToServer(message);
    }

    /**
     * Register all the networking messages and message handlers
     */
    public static void register()
    {
        DISPATCHER.register();
    }
}