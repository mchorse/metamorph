package mchorse.metamorph.network.server.survival;

import mchorse.mclib.network.ServerMessageHandler;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.network.Dispatcher;
import mchorse.metamorph.network.common.survival.PacketFavorite;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerFavorite extends ServerMessageHandler<PacketFavorite>
{
    @Override
    public void run(EntityPlayerMP player, PacketFavorite message)
    {
        Morphing.get(player).favorite(message.index);
        Dispatcher.sendTo(message, player);
    }
}