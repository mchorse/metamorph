package mchorse.metamorph.network.server;

import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.network.Dispatcher;
import mchorse.metamorph.network.common.PacketAcquireMorph;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Server handler acquire morph
 * 
 * This handler is responsible for sending acquired morph for players in 
 * creative morph.
 */
public class ServerHandlerAcquireMorph extends ServerMessageHandler<PacketAcquireMorph>
{
    @Override
    public void run(EntityPlayerMP player, PacketAcquireMorph message)
    {
        if (!player.isCreative())
        {
            return;
        }

        if (Morphing.get(player).acquireMorph(message.morph))
        {
            Dispatcher.sendTo(new PacketAcquireMorph(message.morph), player);
        }
    }
}