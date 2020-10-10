package mchorse.metamorph.network.server.survival;

import mchorse.mclib.network.ServerMessageHandler;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.network.Dispatcher;
import mchorse.metamorph.network.common.survival.PacketKeybind;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerKeybind extends ServerMessageHandler<PacketKeybind>
{
	@Override
	public void run(EntityPlayerMP player, PacketKeybind message)
	{
		Morphing.get(player).keybind(message.index, message.keybind);
		Dispatcher.sendTo(message, player);
	}
}