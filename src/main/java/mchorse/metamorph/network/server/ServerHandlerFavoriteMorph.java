package mchorse.metamorph.network.server;

import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.network.Dispatcher;
import mchorse.metamorph.network.common.PacketFavoriteMorph;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerFavoriteMorph extends ServerMessageHandler<PacketFavoriteMorph>
{
    @Override
    public void run(EntityPlayerMP player, PacketFavoriteMorph message)
    {
        Morphing.get(player).favorite(message.index);
        Dispatcher.sendTo(message, player);
    }
}