package mchorse.metamorph.network.client;

import mchorse.mclib.network.ClientMessageHandler;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.network.common.PacketBlacklist;
import net.minecraft.client.entity.EntityPlayerSP;

/**
 * Client handler blacklist loader 
 */
public class ClientHandlerBlacklist extends ClientMessageHandler<PacketBlacklist>
{
    @Override
    public void run(EntityPlayerSP player, PacketBlacklist message)
    {
        MorphManager.INSTANCE.setActiveBlacklist(message.blacklist);
    }
}
