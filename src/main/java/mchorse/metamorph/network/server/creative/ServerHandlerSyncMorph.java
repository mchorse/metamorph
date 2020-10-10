package mchorse.metamorph.network.server.creative;

import mchorse.mclib.network.ServerMessageHandler;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.network.common.creative.PacketSyncMorph;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerSyncMorph extends ServerMessageHandler<PacketSyncMorph>
{
	@Override
	public void run(EntityPlayerMP player, PacketSyncMorph message)
	{
		IMorphing cap = Morphing.get(player);

		if (cap != null && message.morph != null)
		{
			cap.getAcquiredMorphs().set(message.index, message.morph);
		}
	}
}