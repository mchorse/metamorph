package mchorse.metamorph.network.server;

import mchorse.mclib.network.ServerMessageHandler;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.network.Dispatcher;
import mchorse.metamorph.network.common.PacketRemoveMorph;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerRemoveMorph extends ServerMessageHandler<PacketRemoveMorph>
{
    @Override
    public void run(EntityPlayerMP player, PacketRemoveMorph message)
    {
        if (Morphing.get(player).remove(message.index))
        {
            Dispatcher.sendTo(message, player);
        }
    }
}