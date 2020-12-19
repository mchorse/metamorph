package mchorse.metamorph.network.server.creative;

import mchorse.mclib.network.ServerMessageHandler;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.network.common.creative.PacketClearAcquired;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerClearAcquired extends ServerMessageHandler<PacketClearAcquired>
{
	@Override
	public void run(EntityPlayerMP player, PacketClearAcquired message)
	{
		IMorphing cap = Morphing.get(player);

		if (cap != null)
		{
			cap.removeAcquired();
		}
	}
}