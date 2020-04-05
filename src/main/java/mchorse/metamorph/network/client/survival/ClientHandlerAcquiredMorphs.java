package mchorse.metamorph.network.client.survival;

import mchorse.mclib.network.ClientMessageHandler;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.network.common.survival.PacketAcquiredMorphs;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientHandlerAcquiredMorphs extends ClientMessageHandler<PacketAcquiredMorphs>
{
    @Override
    @SideOnly(Side.CLIENT)
    public void run(EntityPlayerSP player, PacketAcquiredMorphs message)
    {
        IMorphing morphing = Morphing.get(player);

        morphing.setAcquiredMorphs(message.morphs);
    }
}