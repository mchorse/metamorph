package mchorse.metamorph.network.client;

import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.MorphingProvider;
import mchorse.metamorph.network.common.PacketAcquireMorph;
import net.minecraft.client.entity.EntityPlayerSP;

public class ClientHandlerAcquireMorph extends ClientMessageHandler<PacketAcquireMorph>
{
    @Override
    public void run(EntityPlayerSP player, PacketAcquireMorph message)
    {
        IMorphing capability = player.getCapability(MorphingProvider.MORPHING_CAP, null);

        capability.acquireMorph(message.morph);
    }
}