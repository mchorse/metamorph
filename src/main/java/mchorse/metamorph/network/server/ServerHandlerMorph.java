package mchorse.metamorph.network.server;

import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.network.Dispatcher;
import mchorse.metamorph.network.common.PacketMorph;
import mchorse.metamorph.network.common.PacketMorphPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerMorph extends ServerMessageHandler<PacketMorph>
{
    @Override
    public void run(EntityPlayerMP player, PacketMorph message)
    {
        IMorphing capability = Morphing.get(player);

        if (capability != null)
        {
            capability.setCurrentMorph(message.morph, player, false);

            Dispatcher.sendTo(message, player);
            Dispatcher.updateTrackers(player, new PacketMorphPlayer(player.getEntityId(), message.morph));
        }
    }
}