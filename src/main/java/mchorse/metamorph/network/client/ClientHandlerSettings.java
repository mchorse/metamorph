package mchorse.metamorph.network.client;

import mchorse.mclib.network.ClientMessageHandler;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.network.common.PacketSettings;
import net.minecraft.client.entity.EntityPlayerSP;

public class ClientHandlerSettings extends ClientMessageHandler<PacketSettings>
{
    @Override
    public void run(EntityPlayerSP player, PacketSettings message)
    {
        MorphManager.INSTANCE.setActiveSettings(message.settings);
    }
}