package mchorse.metamorph.network.server;

import java.util.List;

import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
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
        IMorphing capability = Morphing.get(player);
        List<AbstractMorph> morphs = capability.getAcquiredMorphs();

        if (message.index == -1)
        {
            capability.demorph(player);
        }

        if (message.index >= 0 && morphs.get(message.index) != null)
        {
            AbstractMorph morph = morphs.get(message.index);

            capability.setCurrentMorph(morph, player, false);
        }

        AbstractMorph morph = capability.getCurrentMorph();

        Dispatcher.sendTo(new PacketMorph(morph), player);
        Dispatcher.updateTrackers(player, new PacketMorphPlayer(player.getEntityId(), morph));
    }
}