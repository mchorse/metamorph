package mchorse.metamorph.network.client;

import mchorse.metamorph.capabilities.morphing.MorphingProvider;
import mchorse.metamorph.network.common.PacketAcquiredMorphs;
import net.minecraft.client.entity.EntityPlayerSP;

public class ClientHandlerAcquiredMorphs extends ClientMessageHandler<PacketAcquiredMorphs>
{
    @Override
    public void run(EntityPlayerSP player, PacketAcquiredMorphs message)
    {
        player.getCapability(MorphingProvider.MORPHING_CAP, null).setAcquiredMorphs(message.morphs);
    }
}