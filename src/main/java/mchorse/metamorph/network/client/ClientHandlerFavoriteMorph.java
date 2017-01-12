package mchorse.metamorph.network.client;

import mchorse.metamorph.ClientProxy;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.network.common.PacketFavoriteMorph;
import net.minecraft.client.entity.EntityPlayerSP;

public class ClientHandlerFavoriteMorph extends ClientMessageHandler<PacketFavoriteMorph>
{
    @Override
    public void run(EntityPlayerSP player, PacketFavoriteMorph message)
    {
        Morphing.get(player).favorite(message.index);
        ClientProxy.overlay.favorite(message.index);
    }
}