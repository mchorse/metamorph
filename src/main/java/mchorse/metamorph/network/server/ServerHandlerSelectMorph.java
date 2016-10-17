package mchorse.metamorph.network.server;

import java.util.List;

import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.MorphingProvider;
import mchorse.metamorph.network.Dispatcher;
import mchorse.metamorph.network.common.PacketMorph;
import mchorse.metamorph.network.common.PacketMorphPlayer;
import mchorse.metamorph.network.common.PacketSelectMorph;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerSelectMorph extends ServerMessageHandler<PacketSelectMorph>
{
    @Override
    public void run(EntityPlayerMP player, PacketSelectMorph message)
    {
        IMorphing capability = player.getCapability(MorphingProvider.MORPHING_CAP, null);
        List<String> morphs = capability.getAcquiredMorphs();

        if (message.index == -1)
        {
            capability.demorph();
        }

        if (morphs.get(message.index) != null)
        {
            String morph = morphs.get(message.index);

            capability.setCurrentMorph(morph, player, false);
        }

        String morph = capability.getCurrentMorphName();

        Dispatcher.sendTo(new PacketMorph(morph), player);
        Dispatcher.updateTrackers(player, new PacketMorphPlayer(player.getEntityId(), morph));
    }
}