package mchorse.metamorph.network.client;

import mchorse.mclib.network.ClientMessageHandler;
import mchorse.metamorph.ClientProxy;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.network.common.PacketFavoriteMorph;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerFavoriteMorph extends ClientMessageHandler<PacketFavoriteMorph>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketFavoriteMorph message)
    {
        Morphing.get(player).favorite(message.index);
        ClientProxy.overlay.favorite(message.index);
    }
}