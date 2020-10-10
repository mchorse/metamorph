package mchorse.metamorph.network.server.survival;

import java.util.List;

import mchorse.mclib.network.ServerMessageHandler;
import mchorse.metamorph.api.MorphAPI;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import mchorse.metamorph.capabilities.morphing.Morphing;
import mchorse.metamorph.network.common.survival.PacketSelectMorph;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.GameType;

public class ServerHandlerSelectMorph extends ServerMessageHandler<PacketSelectMorph>
{
    @Override
    public void run(EntityPlayerMP player, PacketSelectMorph message)
    {
        if (player.interactionManager.getGameType() == GameType.ADVENTURE)
        {
            return;
        }

        int index = message.index;

        IMorphing capability = Morphing.get(player);
        List<AbstractMorph> morphs = capability.getAcquiredMorphs();
        AbstractMorph morph = null;

        if (!morphs.isEmpty() && index >= 0 && index < morphs.size())
        {
            morph = morphs.get(index);
        }

        MorphAPI.morph(player, MorphUtils.copy(morph), false);
    }
}