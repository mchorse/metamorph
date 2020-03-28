package mchorse.metamorph.network.server;

import mchorse.mclib.network.ServerMessageHandler;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.network.common.PacketSyncAcquiredMorph;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerSyncAcquiredMorph extends ServerMessageHandler<PacketSyncAcquiredMorph>
{
	@Override
	public void run(EntityPlayerMP player, PacketSyncAcquiredMorph packet)
	{
		IMorphing cap = Morphing.get(player);

		if (cap != null && packet.morph != null)
		{
			cap.getAcquiredMorphs().set(packet.index, packet.morph);
		}
	}
}