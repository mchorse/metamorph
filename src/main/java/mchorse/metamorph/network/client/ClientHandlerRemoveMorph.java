package mchorse.metamorph.network.client;

import mchorse.mclib.network.ClientMessageHandler;
import mchorse.metamorph.ClientProxy;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.network.common.PacketRemoveMorph;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerRemoveMorph extends ClientMessageHandler<PacketRemoveMorph>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketRemoveMorph message)
    {
        Morphing.get(player).remove(message.index);
        ClientProxy.overlay.remove(message.index);
    }
}