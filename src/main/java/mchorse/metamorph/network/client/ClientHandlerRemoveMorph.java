package mchorse.metamorph.network.client;

import mchorse.metamorph.ClientProxy;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.network.common.PacketRemoveMorph;
import net.minecraft.client.entity.EntityPlayerSP;

public class ClientHandlerRemoveMorph extends ClientMessageHandler<PacketRemoveMorph>
{
    @Override
    public void run(EntityPlayerSP player, PacketRemoveMorph message)
    {
        Morphing.get(player).remove(message.index);
        ClientProxy.overlay.remove(message.index);
    }
}