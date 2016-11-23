package mchorse.metamorph.network.server;

import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.network.common.PacketAction;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerAction extends ServerMessageHandler<PacketAction>
{
    @Override
    public void run(EntityPlayerMP player, PacketAction message)
    {
        IMorphing capability = Morphing.get(player);

        if (capability != null && capability.isMorphed())
        {
            capability.getCurrentMorph().action(player);
        }
    }
}