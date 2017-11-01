package mchorse.metamorph.network.client;

import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.network.common.PacketMorphState;
import mchorse.metamorph.network.common.PacketRemoveMorph;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerMorphState extends ClientMessageHandler<PacketMorphState>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketMorphState message)
    {
        Morphing.get(player).setSquidAir(message.squidAir);
    }
}
