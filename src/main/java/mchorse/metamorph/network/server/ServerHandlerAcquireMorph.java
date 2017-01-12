package mchorse.metamorph.network.server;

import mchorse.metamorph.api.MorphAPI;
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
        if (player.isCreative())
        {
            MorphAPI.acquire(player, message.morph);
        }
    }
}