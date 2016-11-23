package mchorse.metamorph.network.client;

import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.network.common.PacketAcquiredMorphs;
import net.minecraft.client.entity.EntityPlayerSP;

public class ClientHandlerAcquiredMorphs extends ClientMessageHandler<PacketAcquiredMorphs>
{
    @Override
    public void run(EntityPlayerSP player, PacketAcquiredMorphs message)
    {
        Morphing.get(player).setAcquiredMorphs(message.morphs);
    }
}