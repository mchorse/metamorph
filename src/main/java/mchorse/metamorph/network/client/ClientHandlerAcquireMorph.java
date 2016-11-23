package mchorse.metamorph.network.client;

import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.network.common.PacketAcquireMorph;
import net.minecraft.client.entity.EntityPlayerSP;

public class ClientHandlerAcquireMorph extends ClientMessageHandler<PacketAcquireMorph>
{
    @Override
    public void run(EntityPlayerSP player, PacketAcquireMorph message)
    {
        IMorphing capability = Morphing.get(player);

        capability.acquireMorph(message.morph);
    }
}