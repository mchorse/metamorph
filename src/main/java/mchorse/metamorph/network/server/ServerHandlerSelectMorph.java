package mchorse.metamorph.network.server;

import java.util.List;

import mchorse.metamorph.api.MorphAPI;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.network.common.PacketSelectMorph;
import net.minecraft.entity.player.EntityPlayerMP;

public class ServerHandlerSelectMorph extends ServerMessageHandler<PacketSelectMorph>
{
    @Override
    public void run(EntityPlayerMP player, PacketSelectMorph message)
    {
        int index = message.index;

        IMorphing capability = Morphing.get(player);
        List<AbstractMorph> morphs = capability.getAcquiredMorphs();
        AbstractMorph morph = null;

        if (!morphs.isEmpty() && index >= 0 && index < morphs.size())
        {
            morph = morphs.get(index);
        }

        MorphAPI.morph(player, morph, false);
    }
}